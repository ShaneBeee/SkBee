package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtPreSpawn extends SkriptEvent {

    static {
        // Paper - PreCreatureSpawnEvent
        Skript.registerEvent("Pre Creature Spawn", EvtPreSpawn.class, PreCreatureSpawnEvent.class,
                "pre [creature] spawn[ing] [of %entitydatas%]")
            .description("Called before an entity is spawned into the world. Requires a PaperMC server.",
                "\nNote: The spawning entity does not exist when this event is called only the entitytype exists.",
                "This event is called very frequently, and can cause server lag, use it sparingly.",
                "\n`event-spawnreason` = the reason the entity is spawned.",
                "\n`event-location` = the location the spawned entity will appear.",
                "\n`event-entitytype` = the type of entity being spawned.")
            .examples("on pre spawn of a pig:",
                "\tbroadcast \"a %event-entitytype% is spawning in\"")
            .since("2.16.0");

        EventValues.registerEventValue(PreCreatureSpawnEvent.class, Location.class, PreCreatureSpawnEvent::getSpawnLocation, EventValues.TIME_NOW);
        EventValues.registerEventValue(PreCreatureSpawnEvent.class, EntityData.class, event -> EntityUtils.toSkriptEntityData(event.getType()), EventValues.TIME_NOW);
        EventValues.registerEventValue(PreCreatureSpawnEvent.class, SpawnReason.class, PreCreatureSpawnEvent::getReason, EventValues.TIME_NOW);

        // Paper - PreSpawnerSpawnEvent
        Skript.registerEvent("Pre Spawner Spawn", EvtPreSpawn.class, PreSpawnerSpawnEvent.class,
                "pre spawner spawn[ing] [of %entitydatas%]")
            .description("Called before an entity is spawned via a spawner. Requires a PaperMC server.",
                "\nNote: The spawned entity does not exist when this event is called only the entitytype exists.",
                "\nView the pre creature spawn event for more event values.",
                "\n`event-block` = the block location of the spawner spawning the entity.")
            .examples("on pre spawner spawn of a zombie:",
                "\tbroadcast \"%event-entitytype% is spawning in\"")
            .since("2.16.0");

        EventValues.registerEventValue(PreSpawnerSpawnEvent.class, Block.class, event -> event.getSpawnerLocation().getBlock(), EventValues.TIME_NOW);

        // Paper - PhantomPreSpawnEvent
        Skript.registerEvent("Pre Phantom Spawn", EvtPreSpawn.class, PhantomPreSpawnEvent.class,
                "pre phantom spawn[ing]")
            .description("Called before a phantom is spawned for an entity. Requires a PaperMC server.",
                "\nNote: The phantom entity does not exist when this event is called only the entitytype exists.",
                "\nView the pre creature spawn event for more event values.",
                "\n`event-entity` = the entity the spawned phantom is spawning for.")
            .examples("on pre phantom spawn:",
                "\tbroadcast \"Watch out %event-entity% a phantom is coming!\"")
            .since("2.16.0");

        EventValues.registerEventValue(PhantomPreSpawnEvent.class, Entity.class, event -> event.getSpawningEntity(), EventValues.TIME_NOW);

    }

    private Literal<EntityData<?>> spawnedEntities;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Literal<?>[] literals, int matchedPattern, ParseResult parseResult) {
        this.spawnedEntities = (Literal<EntityData<?>>) literals[0];
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (this.spawnedEntities == null) return true;
        if (event instanceof PreCreatureSpawnEvent preCreatureSpawnEvent) {
            EntityData<?> spawnedEntity = EntityUtils.toSkriptEntityData(preCreatureSpawnEvent.getType());
            return this.spawnedEntities.check(event, entityData -> entityData.isSupertypeOf(spawnedEntity));
        }
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "pre spawn" + (spawnedEntities != null ? " of " + this.spawnedEntities.toString(e, d) : "");
    }

}
