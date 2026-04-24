package com.shanebeestudios.skbee.elements.other.events.other;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.Timespan;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.EntityZapEvent;
import com.destroystokyo.paper.event.entity.ExperienceOrbMergeEvent;
import com.destroystokyo.paper.event.entity.SkeletonHorseTrapEvent;
import com.destroystokyo.paper.event.entity.SlimePathfindEvent;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.event.EntityBlockInteractEvent;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

import java.util.Locale;

public class EntityEvents extends SimpleEvent {

    @SuppressWarnings("unchecked")
    public static void register(Registration reg) {
        // Entity Add To World Event
        reg.newEvent(EntityEvents.class, EntityAddToWorldEvent.class,
                "entity add[ed] to world")
            .name("Entity Add to World")
            .description("Fired any time an entity is being added to the world for any reason.",
                "Not to be confused with entity spawn event. This will fire anytime a chunk is reloaded too. Requires a PaperMC server.")
            .examples("on entity added to world:",
                "\tdelete event-entity")
            .since("2.7.2")
            .register();

        reg.newEventValue(EntityAddToWorldEvent.class, World.class)
            .converter(EntityAddToWorldEvent::getWorld)
            .register();

        // Entity Change Air Event
        reg.newEvent(EntityEvents.class, EntityAirChangeEvent.class,
                "[entity] air change")
            .name("Entity Air Change")
            .description("Called when the amount of air an entity has remaining changes.")
            .examples("on entity air change:",
                "\tif event-entity is a player:",
                "\t\tcancel event")
            .since("2.8.4")
            .register();

        reg.newEventValue(EntityAirChangeEvent.class, Number.class)
            .description("The amount of air the entity had left before the event (measured in ticks).")
            .time(EventValue.Time.PAST)
            .converter(event -> {
                if (event.getEntity() instanceof LivingEntity livingEntity) return livingEntity.getRemainingAir();
                return 0;
            })
            .register();
        reg.newEventValue(EntityAirChangeEvent.class, Timespan.class)
            .description("The amount of air the entity had left before the event (as a time span).")
            .time(EventValue.Time.PAST)
            .converter(event -> {
                int ticks = 0;
                if (event.getEntity() instanceof LivingEntity livingEntity) {
                    ticks = livingEntity.getRemainingAir();
                }
                return new Timespan(Timespan.TimePeriod.TICK, Math.max(ticks, 0));
            })
            .register();
        reg.newEventValue(EntityAirChangeEvent.class, Number.class)
            .description("The amount of air the entity will have left (measured in ticks).")
            .converter(EntityAirChangeEvent::getAmount)
            .changer(Changer.ChangeMode.SET, (event, value) ->
                event.setAmount(value != null ? value.intValue() : 0))
            .register();
        reg.newEventValue(EntityAirChangeEvent.class, Timespan.class)
            .converter(event -> new Timespan(Timespan.TimePeriod.TICK, Math.max(event.getAmount(), 0)))
            .changer(Changer.ChangeMode.SET, (event, value) -> {
                int amount = value != null ? (int) value.getAs(Timespan.TimePeriod.TICK) : 0;
                event.setAmount(amount);
            })
            .description("The amount of air the entity will have left (as a time span).")
            .register();

        // Entity Block Interact Event
        reg.newEvent(EntityEvents.class, EntityBlockInteractEvent.class,
                "block (interact|trample)")
            .name("Block Physical Interact Event")
            .description("Called when an entity physically interacts with a block, for example," +
                " entities trampling farmland and villagers opening doors.")
            .examples("on block trample:",
                "\tif type of event-block is farmland:",
                "\t\tcancel event")
            .since("1.5.0")
            .register();

        reg.newEventValue(EntityBlockInteractEvent.class, Block.class)
            .description("The block which was interacted with.")
            .converter(EntityBlockInteractEvent::getBlock)
            .register();

        // Entity Change Block Event
        reg.newEvent(EntityEvents.class, EntityChangeBlockEvent.class,
                "entity change block")
            .name("Entity Change Block")
            .description("Called when any Entity changes a block and a more specific event is not available.",
                "Skript does partially have this event, but this version of it opens up ALL possibilities with this event.",
                "\nevent-entity = the entity which changed the block",
                "\nevent-block = the block that changed",
                "\nevent-blockdata = the blockdata the block has changed into")
            .examples("on entity change block:",
                "\tif event-entity is a villager:",
                "\t\tif event-block is a composter:",
                "\t\t\theal event-entity")
            .since("2.5.3")
            .register();

        reg.newEventValue(EntityChangeBlockEvent.class, BlockData.class)
            .converter(EntityChangeBlockEvent::getBlockData)
            .register();

        // EntityInsideBlockEvent
        reg.newEvent(EntityEvents.class, EntityInsideBlockEvent.class, "entity inside block")
            .name("Entity Inside Block")
            .description("Called when an entity enters the hitbox of a block.",
                "Only called for blocks that react when an entity is inside.",
                "If cancelled, any action that would have resulted from that entity being in the block will not happen (such as extinguishing an entity in a cauldron).",
                "Currently called for: Big dripleaf, Bubble column, Buttons, Cactus, Campfire, Cauldron, Crops, Ender Portal, Fires, Frogspawn, Honey, Hopper, Detector rails,",
                "Nether portals, Pitcher crop, Powdered snow, Pressure plates, Sweet berry bush, Tripwire, Waterlily, Web, Wither rose")
            .examples("on entity inside block:",
                "\tif event-block is a cactus:",
                "\t\tcancel event",
                "\t\tbroadcast \"OUCHIE\"")
            .since("3.4.0")
            .register();

        reg.newEventValue(EntityInsideBlockEvent.class, Block.class)
            .converter(EntityInsideBlockEvent::getBlock)
            .register();

        // Entity Pathfind Event
        reg.newEvent(EntityEvents.class, new Class[]{EntityPathfindEvent.class, SlimePathfindEvent.class}, "entity start[s] pathfinding")
            .name("Entity Pathfind")
            .description("Called when an Entity decides to start moving towards a location. This event does not fire for the entities " +
                "actual movement. Only when it is choosing to start moving to a location. Requires Paper.")
            .examples("on entity starts pathfinding:",
                "\tif event-entity is a sheep:",
                "\t\tcancel event")
            .since("1.5.0")
            .register();

        reg.newEventValue(EntityPathfindEvent.class, Location.class)
            .converter(EntityPathfindEvent::getLoc)
            .register();

        // Entity Place Event
        reg.newEvent(EntityEvents.class, EntityPlaceEvent.class,
                "entity place", "player place entity")
            .name("Entity Place")
            .description("Triggered when an entity is created in the world by a player \"placing\" an item on a block.",
                "Note that this event is currently only fired for four specific placements: armor stands, boats, minecarts, and end crystals.")
            .since("INSERT VERSION")
            .register();

        reg.newEventValue(EntityPlaceEvent.class, Block.class)
            .description("Gets the block the entity was placed on.")
            .converter(EntityPlaceEvent::getBlock)
            .register();
        reg.newEventValue(EntityPlaceEvent.class, BlockFace.class)
            .description("Gets the face of the block the entity was placed on.")
            .converter(EntityPlaceEvent::getBlockFace)
            .register();
        reg.newEventValue(EntityPlaceEvent.class, EquipmentSlot.class)
            .description("Gets the hand used to place the entity.")
            .converter(EntityPlaceEvent::getHand)
            .register();
        reg.newEventValue(EntityPlaceEvent.class, Player.class)
            .description("Gets the player who placed the entity.")
            .converter(EntityPlaceEvent::getPlayer)
            .register();

        // Entity Pose Change Event
        reg.newEvent(EntityEvents.class, EntityPoseChangeEvent.class,
                "entity pose change", "entity changed pose")
            .name("Entity Pose Change")
            .description("Called when an entity changes their pose.")
            .examples("on entity pose change:",
                "\tbroadcast \"%event-entity% changed their pose from %past event-pose% to %event-pose%\"")
            .since("3.20.0")
            .register();

        reg.newEventValue(EntityPoseChangeEvent.class, Pose.class)
            .description("The previous pose of the entity.")
            .time(EventValue.Time.PAST)
            .converter(from -> from.getEntity().getPose())
            .register();
        reg.newEventValue(EntityPoseChangeEvent.class, Pose.class)
            .description("The new pose once this event finishes.")
            .converter(EntityPoseChangeEvent::getPose)
            .register();

        // Entity Remove Event
        reg.newEvent(EntityEvents.class, EntityRemoveEvent.class,
                "entity remove[d] [from world]")
            .name("Entity Remove from World")
            .description("Fired any time an entity is being removed from a world for any reason.")
            .examples("on entity removed from world:",
                "\tbroadcast \"a lonely %event-entity% left the world.\"")
            .since("2.7.2")
            .register();

        reg.newEventValue(EntityRemoveEvent.class, EntityRemoveEvent.Cause.class)
            .description("The reason the entity was removed.")
            .converter(EntityRemoveEvent::getCause)
            .register();

        // Entity Spell Cast Event
        reg.newEvent(EntityEvents.class, EntitySpellCastEvent.class,
                "[entity] spell cast")
            .name("Entity Spell Cast")
            .description("Called when a Spellcaster casts a spell.")
            .examples("on spell cast:",
                "\tif event-entity is an evoker:",
                "\t\tif event-spell is fangs:",
                "\t\t\tcancel event")
            .since("2.14.0")
            .register();

        reg.newEventValue(EntitySpellCastEvent.class, Spellcaster.Spell.class)
            .converter(EntitySpellCastEvent::getSpell)
            .register();

        // Entity Unleash Event
        reg.newEvent(EntityEvents.class, EntityUnleashEvent.class, "entity unleash")
            .name("Entity Unleash")
            .description("Called immediately prior to an entity being unleashed.",
                "Cancelling this event when either the leashed entity dies, the entity changes dimension, or",
                "the client has disconnected the leash will have no effect.",
                "\n`event-string` = The reason for unleashing.",
                "Options are \"distance\" (When the entity's leashholder is more than 10 blocks away),",
                "\"holder_gone\" (When the entity's leashholder has died or logged out, and so is unleashed),",
                "\"player_unleash\" (When the entity's leashholder attempts to unleash it), \"unknown\"")
            .examples("on entity unleash:",
                "\tif event-entity is a cow:",
                "\t\tif event-string = \"distance\":",
                "\t\t\tcancel event")
            .since("3.2.0")
            .register();

        reg.newEventValue(EntityUnleashEvent.class, String.class)
            .converter(event -> event.getReason().name().toLowerCase(Locale.ROOT))
            .register();
        reg.newEventValue(EntityUnleashEvent.class, Player.class)
            .converter(event -> {
                if (event instanceof PlayerUnleashEntityEvent playerUnleashEntityEvent)
                    return playerUnleashEntityEvent.getPlayer();
                return null;
            })
            .register();

        // Entity Zap Event
        reg.newEvent(EntityEvents.class, EntityZapEvent.class, "entity (zap|struck by lightning)")
            .name("Entity Zap")
            .description("Fired when lightning strikes an entity. Requires Paper 1.10.2+")
            .examples("on entity zap:",
                "\tif event-entity is a pig:",
                "\t\tspawn 3 zombie pigmen at event-location")
            .since("1.8.0")
            .register();
        reg.newEventValue(EntityZapEvent.class, Location.class)
            .converter(event -> event.getEntity().getLocation())
            .register();

        // Experience Orb Merge Event
        reg.newEvent(EntityEvents.class, ExperienceOrbMergeEvent.class, "(experience|[e]xp) orb merge")
            .name("Experience Orb Merge")
            .description("Fired anytime the server is about to merge 2 experience orbs into one.")
            .examples("on xp merge:",
                "\tcancel event")
            .since("1.8.0")
            .register();

        reg.newEventValue(ExperienceOrbMergeEvent.class, ExperienceOrb.class)
            .description("The orb that is subject to being removed and merged into the target orb.")
            .converter(ExperienceOrbMergeEvent::getMergeSource)
            .patterns("merge-source")
            .register();

        reg.newEventValue(ExperienceOrbMergeEvent.class, ExperienceOrb.class)
            .description("The orb that will absorb the other experience orb.")
            .converter(ExperienceOrbMergeEvent::getMergeTarget)
            .patterns("merge-target")
            .register();

        // Skeleton Horse Trap Event
        reg.newEvent(EntityEvents.class, SkeletonHorseTrapEvent.class, "skeleton horse trap")
            .name("Skeleton Horse Trap Event")
            .description("Called when a player gets close to a skeleton horse and triggers the lightning trap. Requires Paper 1.13+")
            .examples("on skeleton horse trap:",
                "\tloop all players in radius 10 around event-entity:",
                "\t\tif loop-player is an op:",
                "\t\t\tcancel event")
            .since("1.5.0")
            .register();

        reg.newEventValue(SkeletonHorseTrapEvent.class, Player[].class)
            .converter(event -> event.getEligibleHumans().toArray(new Player[0]))
            .patterns("eligible-players")
            .register();
    }

}
