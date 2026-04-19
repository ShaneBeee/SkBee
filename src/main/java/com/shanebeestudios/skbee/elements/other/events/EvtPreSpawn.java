package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import com.github.shanebeee.skr.Registration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtPreSpawn extends SkriptEvent {

    public static void register(Registration reg) {
        // Paper - PreCreatureSpawnEvent
        reg.newEvent(EvtPreSpawn.class, PreCreatureSpawnEvent.class,
                "pre [creature] spawn[ing] [of %entitydatas%]")
            .name("Pre Creature Spawn")
            .description("Called before an entity is spawned into the world.",
                "Note: The spawning entity does not exist when this event is called only the entitytype exists.",
                "This event is called very frequently, and can cause server lag, use it sparingly.")
            .examples("on pre spawn of a pig:",
                "\tbroadcast \"a %event-entitytype% is spawning in\"")
            .since("2.16.0")
            .register();

        reg.newEventValue(PreCreatureSpawnEvent.class, Location.class)
            .description("The location the spawned entity will appear.")
            .converter(PreCreatureSpawnEvent::getSpawnLocation)
            .register();
        reg.newEventValue(PreCreatureSpawnEvent.class, EntityData.class)
            .description("The type of entity being spawned.")
            .converter(event -> EntityUtils.toSkriptEntityData(event.getType()))
            .register();
        reg.newEventValue(PreCreatureSpawnEvent.class, EntityType.class)
            .description("The Minecraft EntityType being spawned.")
            .converter(PreCreatureSpawnEvent::getType)
            .register();
        reg.newEventValue(PreCreatureSpawnEvent.class, SpawnReason.class)
            .description("The reason the entity is spawned.")
            .converter(PreCreatureSpawnEvent::getReason)
            .register();

        // Paper - PreSpawnerSpawnEvent
        reg.newEvent(EvtPreSpawn.class, PreSpawnerSpawnEvent.class,
                "pre spawner spawn[ing] [of %entitydatas%]")
            .name("Pre Spawner Spawn")
            .description("Called before an entity is spawned via a spawner.",
                "Note: The spawned entity does not exist when this event is called only the entitytype exists.",
                "View the pre creature spawn event for more event values.")
            .examples("on pre spawner spawn of a zombie:",
                "\tbroadcast \"%event-entitytype% is spawning in\"")
            .since("2.16.0")
            .register();

        reg.newEventValue(PreSpawnerSpawnEvent.class, Block.class)
            .description("The block location of the spawner spawning the entity.")
            .converter(event -> event.getSpawnerLocation().getBlock())
            .register();

        // Paper - PhantomPreSpawnEvent
        reg.newEvent(EvtPreSpawn.class, PhantomPreSpawnEvent.class,
                "pre phantom spawn[ing]")
            .name("Pre Phantom Spawn")
            .description("Called before a phantom is spawned for an entity.",
                "Note: The phantom entity does not exist when this event is called only the entitytype exists.",
                "View the pre creature spawn event for more event values.")
            .examples("on pre phantom spawn:",
                "\tbroadcast \"Watch out %event-entity% a phantom is coming!\"")
            .since("2.16.0")
            .register();

        reg.newEventValue(PhantomPreSpawnEvent.class, Entity.class)
            .description("The entity the spawned phantom is spawning for.")
            .converter(PhantomPreSpawnEvent::getSpawningEntity)
            .register();

    }

    private Literal<EntityData<?>> spawnedEntities;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Literal<?>[] literals, int matchedPattern, ParseResult parseResult) {
        if (literals.length > 0) {
            this.spawnedEntities = (Literal<EntityData<?>>) literals[0];
        }
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
