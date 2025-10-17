package com.shanebeestudios.skbee.elements.nbt.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Name("NBT - Spawn Entity with NBT")
@Description({"Spawn an entity at a location with NBT.",
    "The ability to spawn falling was added in 2.10.0 as a temp effect until Skript properly handles falling blocks",
    "with block data."})
@Examples({"set {_n} to nbt compound from \"{NoAI:1b}\"",
    "spawn sheep at player with nbt {_n}",
    "spawn 1 of zombie at player with nbt nbt compound from \"{NoGravity:1b}\"",
    "spawn an armor stand at player with nbt from \"{Small:1b,NoBasePlate:1b,Marker:1b}\"",
    "spawn falling snow[layers=3] at target block with nbt from \"{HurtEntities:1b}\""})
@Since("1.0.0")
public class EffSpawnEntityNBT extends Effect {

    static {
        Skript.registerEffect(EffSpawnEntityNBT.class,
            "spawn %entitytypes% [%directions% %locations%] with [nbt] %nbtcompound%",
            "spawn %number% of %entitytypes% [%directions% %locations%] with [nbt] %nbtcompound%",
            "spawn falling %blockdata% [%directions% %locations%] with [nbt] %nbtcompound%");
    }

    @SuppressWarnings("null")
    private Expression<Location> locations;
    @Nullable
    private Expression<EntityType> entityTypes;
    private Expression<NBTCompound> nbt;
    @Nullable
    private Expression<Number> amount;
    @Nullable
    private Expression<BlockData> blockdata;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        this.amount = matchedPattern != 1 ? null : (Expression<Number>) (exprs[0]);
        this.entityTypes = matchedPattern != 2 ? (Expression<EntityType>) exprs[matchedPattern] : null;
        if (matchedPattern == 2) {
            this.locations = Direction.combine((Expression<? extends Direction>) exprs[1], (Expression<? extends Location>) exprs[2]);
        } else {
            this.locations = Direction.combine((Expression<? extends Direction>) exprs[1 + matchedPattern], (Expression<? extends Location>) exprs[2 + matchedPattern]);
        }
        this.nbt = (Expression<NBTCompound>) exprs[matchedPattern == 2 ? 3 : 3 + matchedPattern];
        this.blockdata = matchedPattern == 2 ? (Expression<BlockData>) exprs[0] : null;
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(final @NotNull Event event) {
        NBTCompound compound = this.nbt.getSingle(event);
        if (compound == null) return;

        final Number numberAmount = this.amount != null ? this.amount.getSingle(event) : 1;
        if (numberAmount == null) return;

        int amount = numberAmount.intValue();

        for (final Location loc : this.locations.getArray(event)) {
            assert loc != null : this.locations;
            if (this.entityTypes != null) {
                for (final EntityType entityType : this.entityTypes.getArray(event)) {
                    for (int i = 0; i < amount * entityType.getAmount(); i++) {
                        Entity spawn;
                        if (Util.IS_RUNNING_SKRIPT_2_13) {
                            spawn = entityType.data.spawn(loc, entity -> NBTApi.addNBTToEntity(entity, compound));

                        } else {
                            spawn = oldSpawn(entityType, loc, compound);
                        }
                        SkriptUtils.setLastSpawned(spawn);
                    }
                }
            } else if (this.blockdata != null) {
                BlockData blockData = this.blockdata.getSingle(event);
                if (blockData == null) return;

                World world = loc.getWorld();
                FallingBlock fallingBlock = world.spawnFallingBlock(loc, blockData);
                NBTApi.addNBTToEntity(fallingBlock, compound);
                SkriptUtils.setLastSpawned(fallingBlock);
            }
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String locAndNBT = " " + this.locations.toString(e, d) + " " + this.nbt.toString(e, d);
        if (this.blockdata != null) {
            return "spawn falling " + this.blockdata.toString(e, d) + locAndNBT;
        } else {
            assert this.entityTypes != null;
            return "spawn " + (this.amount != null ? this.amount.toString(e, d) + " " : "") +
                this.entityTypes.toString(e, d) + locAndNBT;
        }
    }

    // TODO remove once Skript 2.13 is the min version for SkBee
    private static final Method SPAWN_METHOD;

    static {
        try {
            if (Util.IS_RUNNING_SKRIPT_2_13) {
                SPAWN_METHOD = null;
            } else {
                //noinspection removal,JavaReflectionMemberAccess
                SPAWN_METHOD = EntityData.class.getMethod("spawn", Location.class, org.bukkit.util.Consumer.class);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    @SuppressWarnings({"removal"})
    private static Entity oldSpawn(EntityType entityType, Location location, NBTCompound compound) {
        org.bukkit.util.Consumer<Entity> consumer = (entity -> NBTApi.addNBTToEntity(entity, compound));
        try {
            Object spawnedEntity = SPAWN_METHOD.invoke(entityType.data, location, consumer);
            return (Entity) spawnedEntity;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

}
