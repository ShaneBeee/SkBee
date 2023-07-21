package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.jetbrains.annotations.Nullable;

public class EvtPreSpawn extends SkriptEvent {

    private static final boolean HAS_PRE_CREATURE_SPAWN_EVENT = Skript.classExists("com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent");
    private static final boolean HAS_PRE_SPAWNER_SPAWN_EVENT = Skript.classExists("com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent");
    private static final boolean HAS_PRE_PHANTOM_SPAWN_EVENT = Skript.classExists("com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent");

    static {
        // Paper - PreCreatureSpawnEvent
        if (HAS_PRE_CREATURE_SPAWN_EVENT) {
            Skript.registerEvent("Pre Creature Spawn", EvtPreSpawn.class, PreCreatureSpawnEvent.class,
                            "pre [creature] spawn[ing] [of %entitydatas%]")
                    .description("Called before an entity is spawned into the world.",
                            "Note: The spawning entity does not exist when this event is called only the entitytype exists.",
                            "\nevent-spawnreason = the reason the entity is spawned.",
                            "\nevent-location = the location the spawned entity will appear.",
                            "\nevent-entitytype = the type of entity being spawned.")
                    .examples("on pre spawn of a pig:",
                            "\tbroadcast \"a %event-entitytype% is spawning in\"")
                    .since("INSERT VERSION");

            EventValues.registerEventValue(PreCreatureSpawnEvent.class, Location.class, new Getter<>() {
                @Override
                public Location get(PreCreatureSpawnEvent event) {
                    return event.getSpawnLocation();
                }
            }, EventValues.TIME_NOW);

            EventValues.registerEventValue(PreCreatureSpawnEvent.class, EntityData.class, new Getter<>() {
                @Override
                public EntityData<?> get(PreCreatureSpawnEvent event) {
                    return EntityUtils.toSkriptEntityData(event.getType());
                }
            }, EventValues.TIME_NOW);

            EventValues.registerEventValue(PreCreatureSpawnEvent.class, SpawnReason.class, new Getter<>() {
                @Override
                public SpawnReason get(PreCreatureSpawnEvent event) {
                    return event.getReason();
                }
            }, EventValues.TIME_NOW);
        }

        // Paper - PreSpawnerSpawnEvent
        if (HAS_PRE_SPAWNER_SPAWN_EVENT) {
            Skript.registerEvent("Pre Spawner Spawn", EvtPreSpawn.class, PreSpawnerSpawnEvent.class,
                            "pre spawner spawn[ing] [of %entitydatas%]")
                    .description("Called before an entity is spawned via a spawner.",
                            "Note: The spawned entity does not exist when this event is called only the entitytype exists.",
                            "\nView the pre creature spawn event for more event values.",
                            "\nevent-block = the block location of the spawner spawning the entity.")
                    .examples("on pre spawner spawn of a zombie:",
                            "\tbroadcast \"%event-entitytype% is spawning in\"")
                    .since("INSERT VERSION");

            EventValues.registerEventValue(PreSpawnerSpawnEvent.class, Block.class, new Getter<>() {
                @Override
                public Block get(PreSpawnerSpawnEvent event) {
                    return event.getSpawnerLocation().getBlock();
                }
            }, EventValues.TIME_NOW);
        }

        // Paper - PhantomPreSpawnEvent
        if (HAS_PRE_PHANTOM_SPAWN_EVENT) {
            Skript.registerEvent("Pre Phantom Spawn", EvtPreSpawn.class, PhantomPreSpawnEvent.class,
                    "pre phantom spawn[ing]")
                    .description("Called before a phantom is spawned for an entity.",
                            "Note: The phantom entity does not exist when this event is called only the entitytype exists.",
                            "\nView the pre creature spawn event for more event values.",
                            "\nevent-entity = the entity the spawned phantom is spawning for.")
                    .examples("on pre phantom spawn:",
                            "\tbroadcast \"Watch out %event-entity% a phantom is coming!\"")
                    .since("INSERT VERSION");

            EventValues.registerEventValue(PhantomPreSpawnEvent.class, Entity.class, new Getter<>() {
                @Override
                @Nullable
                public Entity get(PhantomPreSpawnEvent event) {
                    return event.getSpawningEntity();
                }
            }, EventValues.TIME_NOW);
        }

    }

    private EntityData[] spawnedEntities = null;

    @Override
    public boolean init(Literal<?>[] literals, int matchedPattern, ParseResult parseResult) {
        this.spawnedEntities = literals[0] == null ? null : ((Literal<EntityData<?>>) literals[0]).getAll();
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (this.spawnedEntities == null) return true;
        if (event instanceof PreCreatureSpawnEvent preCreatureSpawnEvent) {
            EntityData<?> spawnedEntity = EntityUtils.toSkriptEntityData(preCreatureSpawnEvent.getType());
            for (EntityData<?> entityData : this.spawnedEntities) {
                if (entityData.isSupertypeOf(spawnedEntity))
                    return true;
            }
        }
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "pre spawn" + (spawnedEntities != null ? " of " + Classes.toString(spawnedEntities, false) : "");
    }

}
