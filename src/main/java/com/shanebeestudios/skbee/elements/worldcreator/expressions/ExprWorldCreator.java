package com.shanebeestudios.skbee.elements.worldcreator.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldCreator;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("World Creator")
@Description("Create a new world creator. This will be used to create a new world. Name will be the name of your new world. " +
        "You can not use the name of one of the default worlds, or a world created by another plugin, such as MultiVerse. " +
        "Copy will create a fresh new world with the same seed and settings. Clone will create a carbon copy of your world, if the world " +
        "is large, this process may take a while. " +
        "After creating a world creator you will need to load the world.")
@Examples({"set {_w} to a new world creator named \"my-world\"",
        "set environment of {_w} to nether",
        "load world from creator {_w}",
        "",
        "set {_clone} to a new world creator named \"world-clone\" to clone world \"world\"",
        "load world from creator {_clone}"})
@Since("1.8.0")
public class ExprWorldCreator extends SimpleExpression<BeeWorldCreator> {

    static {
        Skript.registerExpression(ExprWorldCreator.class, BeeWorldCreator.class, ExpressionType.SIMPLE,
                "[a] [new] world creator (with name|named) %string%",
                "[a] [new] world creator (with name|named) %string% to (0¦copy|1¦clone) %world%");
    }

    private int pattern;
    private Expression<String> name;
    private Expression<World> world;
    private boolean clone;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.name = (Expression<String>) exprs[0];
        this.world = pattern == 1 ? (Expression<World>) exprs[1] : null;
        this.clone = pattern == 1 && parseResult.mark == 1;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected BeeWorldCreator[] get(@NotNull Event e) {
        String name = this.name.getSingle(e);
        if (name == null) {
            return null;
        }
        if (pattern == 0) {
            return new BeeWorldCreator[]{new BeeWorldCreator(name)};
        } else if (pattern == 1 && this.world != null) {
            World world = this.world.getSingle(e);
            if (world == null) return null;
            return new BeeWorldCreator[]{new BeeWorldCreator(world, name, this.clone)};
        }
        return null;
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
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String creator = String.format("new world creator named '%s'", this.name.toString(e, d));
        String copy = this.pattern == 1 ? " to " + (this.clone ? "clone " : "copy ") + this.world.toString(e, d) : "";
        return creator + copy;
    }

}
