package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.RegionAccessor;
import org.bukkit.UnsafeValues;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
@Name("Biome Key of Location")
@Description({"Get/set the biome of a block/location using a NamespacedKey. Requires Paper 1.19+.",
    "**NOTES**:",
    "- This will support custom biomes.",
    "- When setting this will not immediately visually update the biome to players, you will need to use the `refresh %chunk%` effect."})
@Examples({"set biome key of block at player to mc key \"minecraft:plains\"",
    "set biome key of blocks in chunk at player to mc key \"wythers:ancient_taiga\"",
    "refresh chunk at player # forces biomes to be re-sent to the player.",
    "set {_key} to biome key of block at player",
    "set {_keys::*} to biome keys of blocks in chunk at player"})
@Since("3.6.0")
public class ExprBiomeKeyLocation extends SimplePropertyExpression<Location, NamespacedKey> {

    private static final UnsafeValues UNSAFE = Bukkit.getUnsafe();

    static {
        if (Skript.methodExists(UnsafeValues.class, "getBiomeKey", RegionAccessor.class, int.class, int.class, int.class)) {
            register(ExprBiomeKeyLocation.class, NamespacedKey.class, "biome key[s]", "locations");
        }
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (Biome.class.isInterface()) {
            Skript.warning("Deprecated, you can just set the biome now.");
        }
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @SuppressWarnings("removal")
    @Override
    public @Nullable NamespacedKey convert(Location from) {
        World world = from.getWorld();
        if (world != null) {
            return UNSAFE.getBiomeKey(world, from.getBlockX(), from.getBlockY(), from.getBlockZ());
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(NamespacedKey.class);
        return null;
    }

    @SuppressWarnings("removal")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof NamespacedKey key) {
            for (Location loc : getExpr().getArray(event)) {
                World world = loc.getWorld();
                if (world != null) {
                    try {
                        UNSAFE.setBiomeKey(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), key);
                    } catch (IllegalStateException ignore) {

                    }
                }
            }
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "biome key";
    }

    @Override
    public @NotNull Class<? extends NamespacedKey> getReturnType() {
        return NamespacedKey.class;
    }

}
