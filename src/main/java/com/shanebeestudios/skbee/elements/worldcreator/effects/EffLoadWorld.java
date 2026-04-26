package com.shanebeestudios.skbee.elements.worldcreator.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.legacy.LegacyUtils;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldConfig;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldCreator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EffLoadWorld extends Effect {

    private static BeeWorldConfig BEE_WORLD_CONFIG;

    public static void register(Registration reg) {
        BEE_WORLD_CONFIG = SkBee.getPlugin().getBeeWorldConfig();
        reg.newEffect(EffLoadWorld.class,
                "load world from [[world] creator] %worldcreator%",
                "load world %namespacedkey%",
                "unload [world] %world% [and (save|1:(do not|don't) save)]",
                "delete world file for [world] %string%",
                "delete world file for world with key %namespacedkey%")
            .name("Load/Unload/Delete World")
            .description("Load a world from a world creator.",
                "Worlds created/loaded with a world creator, are saved in the 'plugins/SkBee/worlds.yml' file",
                "and automatically loaded on server start if auto-load is enabled in the config.",
                "If you wish to import a world, just use a world creator with a name that matches that world folder.",
                "You can load a world from a name (assuming this world is in your world directory and not loaded).",
                "You can unload a world with an option to save/not save (defaults to saving).",
                "You can also delete a world, but only a custom world.")
            .examples("set {_w} to a new world creator named \"my-world\"",
                "load world from world creator {_w}", "",
                "load world \"my-world\"",
                "unload world world(\"my-world\")")
            .since("1.8.0")
            .register();
    }

    private int pattern;
    private Expression<BeeWorldCreator> creator;
    private Expression<String> worldName;
    private Expression<NamespacedKey> worldKey;
    private Expression<World> world;
    private boolean save;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.creator = this.pattern == 0 ? (Expression<BeeWorldCreator>) exprs[0] : null;
        this.worldKey = this.pattern == 1 || this.pattern == 4 ? (Expression<NamespacedKey>) exprs[0] : null;
        this.worldName = this.pattern == 3 ? (Expression<String>) exprs[0] : null;
        this.world = this.pattern == 2 ? (Expression<World>) exprs[0] : null;
        this.save = this.pattern == 2 && parseResult.mark != 1;

        if (this.pattern == 3 && LegacyUtils.IS_RUNNING_MC_26_1_1) {
            Skript.error("A world file cannot be deleted by name in Minecraft 26.1+");
            return false;
        }
        if (this.pattern == 4 && !LegacyUtils.IS_RUNNING_MC_26_1_1) {
            Skript.error("A world file cannot be deleted by key in Minecraft 1.21.11 or below.");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        // not doing anything here since we're walking
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        if (pattern == 0) {
            BeeWorldCreator worldCreator = this.creator.getSingle(event);
            if (worldCreator != null) {
                // Let's save you guys for later after the world has loaded
                Object localVars = Variables.removeLocals(event);

                worldCreator.loadWorld().thenAccept(world1 -> {
                    // re-set local variables
                    if (localVars != null) Variables.setLocalVariables(event, localVars);

                    // walk next trigger
                    if (next != null) TriggerItem.walk(next, event);

                    // remove local vars as we're now done
                    Variables.removeLocals(event);
                }).exceptionally(t -> {
                    // If for some reason the loader fails
                    // walk the next trigger and throw a Skript exception
                    if (next != null) TriggerItem.walk(next, event);
                    throw Skript.exception(t);
                });
                return null;

            }
        } else if (pattern == 1) {
            if (this.worldKey != null) {
                NamespacedKey worldName = this.worldKey.getSingle(event);
                if (worldName != null) {
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        new WorldCreator(worldName).createWorld();
                    }
                }
            }
        } else if (pattern == 2) {
            if (this.world == null) return next;
            World world = this.world.getSingle(event);
            if (world == null) return next;

            unloadWorld(world);
        } else if (pattern == 3) {
            if (this.worldName == null) return next;

            String worldName = this.worldName.getSingle(event);
            if (worldName == null) return next;

            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                // Kick players and unload the world before deleting
                if (!unloadWorld(world)) {
                    // if world could not unload, we don't want to delete it
                    return next;
                }
            }
            BEE_WORLD_CONFIG.deleteWorld(worldName);
        } else if (pattern == 4) {
            if (this.worldKey == null) return next;

            NamespacedKey worldKey = this.worldKey.getSingle(event);
            if (worldKey == null) return next;

            World world = Bukkit.getWorld(worldKey);
            if (world != null) {
                // Kick players and unload the world before deleting
                if (!unloadWorld(world)) {
                    // if world could not unload, we don't want to delete it
                    return next;
                }
            }
            BEE_WORLD_CONFIG.deleteWorld(worldKey);
        }
        return next;
    }

    private boolean unloadWorld(@NotNull World world) {
        World mainWorld = Bukkit.getWorlds().getFirst();
        if (world == mainWorld) {
            // We can't unload the main world
            return false;
        }
        // Teleport remaining players out of this world to be safe
        world.getPlayers().forEach(player -> player.teleport(mainWorld.getSpawnLocation()));

        return Bukkit.unloadWorld(world, this.save);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        switch (this.pattern) {
            case 1 -> {
                return String.format("load world %s", this.worldKey.toString(e, d));
            }
            case 2 -> {
                String save = this.save ? "and save" : "without saving";
                return String.format("unload world %s %s", this.world.toString(e, d), save);
            }
            case 3 -> {
                return String.format("delete world file for %s", this.worldName.toString(e, d));
            }
            case 4 -> {
                return String.format("delete world file for %s", this.worldKey.toString(e, d));
            }
            default -> {
                return String.format("load world from creator %s", this.creator.toString(e, d));
            }
        }
    }

}
