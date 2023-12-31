package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtSpawnerSpawn extends SkriptEvent {

    static {
        Skript.registerEvent("Spawner Spawn", EvtSpawnerSpawn.class, SpawnerSpawnEvent.class,
                        "spawner spawn[ing] [of %entitydatas%]")
                .description("Called whenever an entity is spawned via a spawner.")
                .examples("on spawner spawn of zombie:",
                        "\treset spawner timer of event-block")
                .since("2.16.0");

        EventValues.registerEventValue(SpawnerSpawnEvent.class, Block.class, new Getter<>() {
            @Override
            public @Nullable Block get(SpawnerSpawnEvent event) {
                CreatureSpawner spawner = event.getSpawner();
                if (spawner != null) return spawner.getBlock();
                return null;
            }
        }, EventValues.TIME_NOW);

    }

    private Literal<EntityData<?>> spawnedEntities;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Literal<?>[] literals, int matchedPattern, ParseResult parseResult) {
        this.spawnedEntities = (Literal<EntityData<?>>) literals[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        if (this.spawnedEntities == null) return true;
        if (event instanceof SpawnerSpawnEvent spawnerEvent) {
            Entity spawnedEntity = spawnerEvent.getEntity();

            return this.spawnedEntities.check(event, entityData -> entityData.isInstance(spawnedEntity));
        }
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "spawner spawn" + (this.spawnedEntities != null ? " of " + this.spawnedEntities.toString(e, d) : "");
    }

}
