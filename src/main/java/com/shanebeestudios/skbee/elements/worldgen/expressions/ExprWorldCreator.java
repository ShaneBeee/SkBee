package com.shanebeestudios.skbee.elements.worldgen.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.util.legacy.LegacyUtils;
import com.shanebeestudios.skbee.api.worldgen.BeeWorldCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprWorldCreator extends SimpleExpression<BeeWorldCreator> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprWorldCreator.class, BeeWorldCreator.class,
                "[a] [new] world creator (with name|named) %string%",
                "[a] [new] world creator with key %namespacedkey/string%",
                "[a] [new] world creator (with name|named) %string% to (copy|:clone) %world% [no-save:without saving]",
                "[a] [new] world creator with key %namespacedkey/string% to (copy|:clone) %world% [no-save:without saving]")
            .name("World Creator")
            .description("Create a new world creator. This will be used to create a new world.",
                "After creating a world creator you will need to load the world.",
                "",
                "**Name/Key**:",
                " - Name: This is the name of your new world (As of Minecraft 26.1, names are discouraged and keys are suggested.",
                " - Key: This is the key of your new world, and is used to identify it.",
                "   - As of Minecraft 26.1, the key will determine where your world folder is saved within the `world/dimensions` folder.",
                "",
                "**Other**:",
                " - Copy will create a fresh new world with the same seed and settings.",
                " - Clone will create a carbon copy of your world, if the world is large, this process may take a while. " +
                    "The file copying process will happen on another thread, so this won't freeze your server.",
                " - `without saving` = Optionally prevent saving of the world that will be cloned. Saving freezes the server for a bit " +
                    "so this can help speed up the process... if you don't need that world saved at this moment in time.")
            .examples("set {_w} to a new world creator with key \"my_stuff:my_world\"",
                "set environment of {_w} to nether",
                "load world from creator {_w}",
                "",
                "set {_clone} to a new world creator with key \"clones:world\" to clone world \"world\" without saving",
                "load world from creator {_clone}")
            .since("1.8.0")
            .register();
    }

    private int pattern;
    private Expression<String> name;
    private Expression<?> key;
    private Expression<World> world;
    private boolean clone;
    private boolean save;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (this.pattern == 0 || this.pattern == 2) {
            this.name = (Expression<String>) exprs[0];
            if (LegacyUtils.IS_RUNNING_MC_26_1_1) {
                Skript.warning("Named worlds are highly discouraged in Minecraft 26.1+ and you should be using a key instead.");
            }
        } else {
            this.key = exprs[0];
        }
        this.world = this.pattern > 1 ? (Expression<World>) exprs[1] : null;
        this.clone = this.pattern > 1 && parseResult.hasTag("clone");
        this.save = !parseResult.hasTag("no-save");
        return true;
    }

    @Override
    protected BeeWorldCreator @Nullable [] get(@NotNull Event e) {
        String name = null;
        NamespacedKey key = null;
        World world = null;
        if (this.name != null) name = this.name.getSingle(e);
        if (this.key != null) {
            Object o = this.key.getSingle(e);
            if (o instanceof NamespacedKey) {
                key = (NamespacedKey) o;
            } else if (o instanceof String s) {
                key = Util.getNamespacedKey(s, false);
            }
        }
        if (this.world != null) world = this.world.getSingle(e);

        if (name == null && key == null) {
            return null;
        }
        if (this.pattern > 1 && world == null) {
            return null;
        }
        BeeWorldCreator beeWorldCreator = new BeeWorldCreator(world, name, key, this.clone);
        if (this.pattern > 1) {
            beeWorldCreator.setSaveClone(this.save);
        }

        return new BeeWorldCreator[]{beeWorldCreator};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends BeeWorldCreator> getReturnType() {
        return BeeWorldCreator.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String key;
        if (this.key != null) {
            key = " with key " + this.key.toString(e, d);
        } else {
            key = " named " + this.name.toString(e, d);
        }

        String copy = this.pattern == 1 ? " to " + (this.clone ? "clone " : "copy ") + this.world.toString(e, d) : "";
        String withoutSaving = this.save ? "" : " without saving";
        return "new world creator " + key + copy + withoutSaving;
    }

}
