package com.shanebeestudios.skbee.elements.nbt.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("NBT - Spawn Entity with NBT")
@Description("Spawn an entity at a location with NBT.")
@Examples({"set {_n} to nbt compound from \"{NoAI:1b}\"",
        "spawn sheep at player with nbt {_n}",
        "spawn 1 of zombie at player with nbt nbt compound from \"{NoGravity:1b}\""})
@Since("1.0.0")
public class EffSpawnEntityNBT extends Effect {

    static {
        Skript.registerEffect(EffSpawnEntityNBT.class,
                "spawn %entitytypes% [%directions% %locations%] with [nbt] %nbtcompound%",
                "spawn %number% of %entitytypes% [%directions% %locations%] with [nbt] %nbtcompound%");
    }

    @SuppressWarnings("null")
    private Expression<Location> locations;
    @SuppressWarnings("null")
    private Expression<EntityType> types;
    private Expression<NBTCompound> nbt;
    @Nullable
    private Expression<Number> amount;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        amount = matchedPattern == 0 ? null : (Expression<Number>) (exprs[0]);
        types = (Expression<EntityType>) exprs[matchedPattern];
        locations = Direction.combine((Expression<? extends Direction>) exprs[1 + matchedPattern], (Expression<? extends Location>) exprs[2 + matchedPattern]);
        nbt = (Expression<NBTCompound>) exprs[3 + matchedPattern];
        return true;
    }

    @Override
    public void execute(final @NotNull Event event) {
        NBTCompound compound = this.nbt.getSingle(event);
        if (compound == null) return;

        final Number a = amount != null ? amount.getSingle(event) : 1;
        if (a == null)
            return;
        final EntityType[] et = types.getArray(event);
        for (final Location loc : locations.getArray(event)) {
            assert loc != null : locations;
            for (final EntityType type : et) {
                for (int i = 0; i < a.doubleValue() * type.getAmount(); i++) {
                    Entity spawn = type.data.spawn(loc, entity -> NBTApi.addNBTToEntity(entity, compound));
                    SkriptUtils.setLastSpawned(spawn);
                }
            }
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "spawn " + (amount != null ? amount.toString(e, debug) + " " : "") + types.toString(e, debug) +
                " " + locations.toString(e, debug) + " " + nbt.toString(e, debug);
    }

}
