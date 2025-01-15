package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.sections.EffSecSpawn;
import ch.njol.skript.util.Direction;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.EffectSection;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.RegionAccessor;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

// Derived from https://github.com/SkriptLang/Skript/blob/master/src/main/java/ch/njol/skript/sections/EffSecSpawn.java
@Name("Minecraft - Spawn Entity")
@Description({"Spawn an entity from a Minecraft Key/EntityType.",
    "This is both a section and an effect (used just like Skript's spawn sec/effect). Requires Minecraft 1.20.2+"})
@Examples({"# Spawn from MinecraftEntityType",
    "mc spawn sheep at player",
    "mc spawn minecraft:cow at player",
    "mc spawn minecraft:wind_charge at player",
    "",
    "# Spawn from String",
    "mc spawn \"sheep\" at player",
    "mc spawn \"minecraft:cow\" at location(1,100,1)",
    "mc spawn \"minecraft:wind_charge\" above target block",
    "",
    "# Spawn from Minecraft Key",
    "set {_key} to mc key from \"minecraft:sheep\"",
    "mc spawn {_key} above target block",
    "mc spawn (mc key from \"sheep\") at {_location}",
    "mc spawn (minecraft key from \"minecraft:breeze\") at location(1,100,1, world \"world_nether\")",
    "",
    "# Spawn Using Section",
    "le spawn sheep at player:",
    "\tset ai of entity to false",
    "mc spawn minecraft:armor_stand at player:",
    "\tset gravity of entity to false",
    "mc spawn \"minecraft:breeze\" at player:",
    "\tset max health of entity to 100",
    "\tset health of entity to 100"})
@Since("3.5.0")
public class SecSpawnMinecraftEntity extends EffectSection {

    static {
        // Bukkit changed from a Bukkit Consumer to Java Consumer in 1.20.2
        if (Skript.methodExists(RegionAccessor.class, "spawn", Location.class, Class.class, Consumer.class)) {
            Skript.registerSection(SecSpawnMinecraftEntity.class,
                "(minecraft|mc|skbee|le) spawn [%number% of] %minecraftentitytypes/namespacedkeys/strings% [%directions% %locations%]");
        }
    }

    private Expression<Number> amount;
    private Expression<?> entityType;
    private Expression<Location> location;
    private Trigger trigger;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.amount = (Expression<Number>) exprs[0];
        this.entityType = exprs[1];
        this.location = Direction.combine((Expression<? extends Direction>) exprs[2], (Expression<? extends Location>) exprs[3]);
        if (sectionNode != null) {
            AtomicBoolean delayed = new AtomicBoolean(false);
            Runnable afterLoading = () -> delayed.set(!getParser().getHasDelayBefore().isFalse());
            this.trigger = loadCode(sectionNode, "spawn", afterLoading, EffSecSpawn.SpawnEvent.class);
            if (delayed.get()) {
                Skript.error("Delays can't be used within a Spawn Effect Section");
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Consumer<? extends Entity> consumer = getConsumer(event);
        Number numberAmount = this.amount != null ? this.amount.getOptionalSingle(event).orElse(1) : 1;

        double amount = numberAmount.doubleValue();
        for (Location location : this.location.getArray(event)) {
            World world = location.getWorld();
            if (world == null) continue;

            for (Object entityType : this.entityType.getArray(event)) {
                Class<? extends Entity> entityClass = getEntityClass(entityType);
                if (entityClass == null) continue;

                for (int i = 0; i < amount; i++) {
                    EffSecSpawn.lastSpawned = world.spawn(location, entityClass, (Consumer) consumer);
                }
            }
        }
        return super.walk(event, false);
    }

    @Nullable
    private Consumer<? extends Entity> getConsumer(Event event) {
        Consumer<? extends Entity> consumer = null;
        if (this.trigger != null) {
            consumer = entity -> {
                EffSecSpawn.SpawnEvent spawnEvent = new EffSecSpawn.SpawnEvent(entity);
                // Copy the local variables from the calling code to this section
                Variables.setLocalVariables(spawnEvent, Variables.copyLocalVariables(event));
                TriggerItem.walk(this.trigger, spawnEvent);
                // And copy our (possibly modified) local variables back to the calling code
                Variables.setLocalVariables(event, Variables.copyLocalVariables(spawnEvent));
                // Clear spawnEvent's local variables as it won't be done automatically
                Variables.removeLocals(spawnEvent);
            };
        }
        return consumer;
    }

    @Nullable
    private Class<? extends Entity> getEntityClass(Object object) {
        if (object instanceof EntityType et) return et.getEntityClass();
        else if (object instanceof NamespacedKey key) {
            EntityType et = Registry.ENTITY_TYPE.get(key);
            if (et != null) return et.getEntityClass();
        } else if (object instanceof String string) {
            NamespacedKey key = Util.getNamespacedKey(string, false);
            if (key != null) return getEntityClass(key);
        }
        error("Couldn't get entity from '" + Classes.toString(object) + "'");
        return null;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String amount = this.amount != null ? (this.amount + " of ") : "";
        String type = this.entityType.toString(e, d);
        String loc = this.location.toString(e, d);
        return "minecraft spawn " + amount + type + loc;
    }

}
