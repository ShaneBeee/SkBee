package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Experience;
import ch.njol.skript.util.Getter;
import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.EntityZapEvent;
import com.destroystokyo.paper.event.entity.ExperienceOrbMergeEvent;
import com.destroystokyo.paper.event.entity.SkeletonHorseTrapEvent;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("unused")
public class PaperEvents extends SimpleEvent {

    static {
        // == PLAYER EVENTS == //

        // Player Recipe Book Click Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent")) {
            Skript.registerEvent("Recipe Book Click Event", PaperEvents.class, PlayerRecipeBookClickEvent.class, "[player] recipe book click")
                    .description("Called when the player clicks on a recipe in their recipe book. Requires Paper 1.15+")
                    .examples("on recipe book click:",
                            "\tif event-string = \"minecraft:diamond_sword\":",
                            "\t\tcancel event")
                    .since("1.5.0");

            EventValues.registerEventValue(PlayerRecipeBookClickEvent.class, String.class, new Getter<>() {
                @Override
                public @NotNull String get(@NotNull PlayerRecipeBookClickEvent event) {
                    return event.getRecipe().toString();
                }
            }, 0);
        }

        // Player Pickup XP Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent")) {
            Skript.registerEvent("Player Pickup Experience Orb", PaperEvents.class, PlayerPickupExperienceEvent.class,
                            "player pickup (experience|xp) [orb]")
                    .description("Fired when a player is attempting to pick up an experience orb. Requires Paper 1.12.2+",
                            "\n`event-experience` represents the experience picked up (This is Skript's version of XP).",
                            "\n`event-number` represents the experience picked up as a number.",
                            "\n`event-entity` represents the experience orb entity.")
                    .examples("on player pickup xp:",
                            "\tadd 10 to level of player")
                    .since("1.8.0");

            EventValues.registerEventValue(PlayerPickupExperienceEvent.class, Experience.class, new Getter<>() {
                @Override
                public Experience get(PlayerPickupExperienceEvent event) {
                    return new Experience(event.getExperienceOrb().getExperience());
                }
            }, 0);

            EventValues.registerEventValue(PlayerPickupExperienceEvent.class, Number.class, new Getter<>() {
                @Override
                public Number get(PlayerPickupExperienceEvent event) {
                    return event.getExperienceOrb().getExperience();
                }
            }, 0);

            EventValues.registerEventValue(PlayerPickupExperienceEvent.class, Entity.class, new Getter<>() {
                @Override
                public Entity get(PlayerPickupExperienceEvent event) {
                    return event.getExperienceOrb();
                }
            }, 0);
        }

        // Player Elytra Boost Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerElytraBoostEvent")) {
            Skript.registerEvent("Player Elytra Boost", PaperEvents.class, PlayerElytraBoostEvent.class, "[player] elytra boost")
                    .description("Fired when a player boosts elytra flight with a firework. Requires Paper 1.13.2+")
                    .examples("on elytra boost:",
                            "\tpush player forward at speed 50")
                    .since("1.8.0");
            EventValues.registerEventValue(PlayerElytraBoostEvent.class, ItemType.class, new Getter<>() {
                @Override
                public ItemType get(PlayerElytraBoostEvent e) {
                    return new ItemType(e.getItemStack());
                }
            }, 0);
        }

        // Player Stop Using Item Event
        if (Skript.classExists("io.papermc.paper.event.player.PlayerStopUsingItemEvent")) {
            Skript.registerEvent("Player Stop Using Item", PaperEvents.class, PlayerStopUsingItemEvent.class, "[player] stop using item")
                    .description("Called when the server detects a player stopping using an item.",
                            "Examples of this are letting go of the interact button when holding a bow, an edible item, or a spyglass.",
                            "event-number is the number of ticks the item was held for. Requires Paper 1.18+.")
                    .examples("on player stop using item:",
                            "\tif event-item is a spyglass:",
                            "\t\tkill player")
                    .since("1.17.0");
            EventValues.registerEventValue(PlayerStopUsingItemEvent.class, ItemType.class, new Getter<>() {
                @Override
                public ItemType get(PlayerStopUsingItemEvent event) {
                    return new ItemType(event.getItem());
                }
            }, 0);
            EventValues.registerEventValue(PlayerStopUsingItemEvent.class, Number.class, new Getter<>() {
                @Override
                public Number get(PlayerStopUsingItemEvent event) {
                    return event.getTicksHeldFor();
                }
            }, 0);
        }

        // Player Change Beacon Effect Event
        if (Skript.classExists("io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent")) {
            Skript.registerEvent("Beacon - Player Change Effect", PaperEvents.class, PlayerChangeBeaconEffectEvent.class, "[player] change beacon [potion] effect[s]", "beacon [potion] effect change")
                    .description("Called when a player changes the current potion effects of a beacon.")
                    .examples("on beacon potion effect change:",
                            "\tprimary beacon effect of event-block is jump boost",
                            "\tset primary beacon effect of event-block to levitation")
                    .since("2.16.0");

            EventValues.registerEventValue(PlayerChangeBeaconEffectEvent.class, Block.class, new Getter<>() {
                @Override
                public Block get(PlayerChangeBeaconEffectEvent event) {
                    return event.getBeacon();
                }
            }, EventValues.TIME_NOW);

        }

        // Player Chunk Load Event
        if (Skript.classExists("io.papermc.paper.event.packet.PlayerChunkLoadEvent")) {
            Skript.registerEvent("Player Chunk Load", PaperEvents.class, PlayerChunkLoadEvent.class,
                            "player chunk (send|load)")
                    .description("Is called when a Player receives a Chunk.",
                            "Can for example be used for spawning a fake entity when the player receives a chunk. ",
                            "Should only be used for packet/clientside related stuff. Not intended for modifying server side state.",
                            "\nRequires a PaperMC server.")
                    .examples("on player chunk send:",
                            "\tloop all blocks in event-chunk:",
                            "\t\tif loop-block is diamond ore:",
                            "\t\t\tmake player see loop-block as stone")
                    .since("2.6.1");

            EventValues.registerEventValue(PlayerChunkLoadEvent.class, Player.class, new Getter<>() {
                @Override
                public @Nullable Player get(PlayerChunkLoadEvent event) {
                    return event.getPlayer();
                }
            }, 0);
        }

        // Player Chunk Unload Event
        if (Skript.classExists("io.papermc.paper.event.packet.PlayerChunkUnloadEvent")) {
            Skript.registerEvent("Player Chunk Unload", PaperEvents.class, PlayerChunkUnloadEvent.class,
                            "player chunk unload")
                    .description("Is called when a Player receives a chunk unload packet.",
                            "Should only be used for packet/clientside related stuff. Not intended for modifying server side.",
                            "\nRequires a PaperMC server.")
                    .examples("on player chunk unload:",
                            "\tsend \"looks like you lost your chunk cowboy!\" to player")
                    .since("2.6.1");

            EventValues.registerEventValue(PlayerChunkUnloadEvent.class, Player.class, new Getter<>() {
                @Override
                public @Nullable Player get(PlayerChunkUnloadEvent event) {
                    return event.getPlayer();
                }
            }, 0);
        }

        // == ENTITY EVENTS == //

        // Entity Pathfind Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.EntityPathfindEvent")) {
            Skript.registerEvent("Entity Pathfind Event", PaperEvents.class, EntityPathfindEvent.class, "entity start[s] pathfinding")
                    .description("Called when an Entity decides to start moving towards a location. This event does not fire for the entities " +
                            "actual movement. Only when it is choosing to start moving to a location. Requires Paper.")
                    .examples("on entity starts pathfinding:",
                            "\tif event-entity is a sheep:",
                            "\t\tcancel event")
                    .since("1.5.0");

            EventValues.registerEventValue(EntityPathfindEvent.class, Location.class, new Getter<>() {
                @Override
                public @NotNull Location get(@NotNull EntityPathfindEvent event) {
                    return event.getLoc();
                }
            }, 0);
        }

        // Skeleton Horse Trap Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.SkeletonHorseTrapEvent")) {
            Skript.registerEvent("Skeleton Horse Trap Event", PaperEvents.class, SkeletonHorseTrapEvent.class, "skeleton horse trap")
                    .description("Called when a player gets close to a skeleton horse and triggers the lightning trap. Requires Paper 1.13+")
                    .examples("on skeleton horse trap:",
                            "\tloop all players in radius 10 around event-entity:",
                            "\t\tif loop-player is an op:",
                            "\t\t\tcancel event")
                    .since("1.5.0");
        }

        // Entity Zap Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.EntityZapEvent")) {
            Skript.registerEvent("Entity Zap", PaperEvents.class, EntityZapEvent.class, "entity (zap|struck by lightning)")
                    .description("Fired when lightning strikes an entity. Requires Paper 1.10.2+")
                    .examples("on entity zap:",
                            "\tif event-entity is a pig:",
                            "\t\tspawn 3 zombie pigmen at event-location")
                    .since("1.8.0");
            EventValues.registerEventValue(EntityZapEvent.class, Location.class, new Getter<>() {
                @Override
                public Location get(EntityZapEvent e) {
                    return e.getEntity().getLocation();
                }
            }, 0);
        }

        // Entity Knockback Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent")) {
            Skript.registerEvent("Entity Knockback", PaperEvents.class, EntityKnockbackByEntityEvent.class, "entity knockback")
                    .description("Fired when an Entity is knocked back by the hit of another Entity. " +
                            "If this event is cancelled, the entity is not knocked back. Requires Paper 1.12.2+")
                    .examples("on entity knockback:", "\tif event-entity is a cow:", "\t\tcancel event")
                    .since("1.8.0");
        }

        // Experience Orb Merge Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.ExperienceOrbMergeEvent")) {
            Skript.registerEvent("Experience Orb Merge", PaperEvents.class, ExperienceOrbMergeEvent.class, "(experience|[e]xp) orb merge")
                    .description("Fired anytime the server is about to merge 2 experience orbs into one. Requires Paper 1.12.2+")
                    .examples("on xp merge:",
                            "\tcancel event")
                    .since("1.8.0");
        }

        // Entity Add To World Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.EntityAddToWorldEvent")) {
            Skript.registerEvent("Entity Add to World", PaperEvents.class, EntityAddToWorldEvent.class,
                            "entity add[ed] to world")
                    .description("Fired any time an entity is being added to the world for any reason.",
                            "Not to be confused with entity spawn event. This will fire anytime a chunk is reloaded too. Requires a PaperMC server.")
                    .examples("on entity added to world:",
                            "\tdelete event-entity")
                    .since("2.7.2");
        }

        // == BLOCK EVENTS == //

        // Beacon Effect Event
        if (Skript.classExists("com.destroystokyo.paper.event.block.BeaconEffectEvent")) {
            Skript.registerEvent("Beacon Effect", PaperEvents.class, BeaconEffectEvent.class, "beacon effect")
                    .description("Called when a beacon effect is being applied to a player. Requires Paper 1.9+")
                    .examples("on beacon effect:",
                            "\tif event-player does not have permission \"my.server.beacons\":",
                            "\t\tcancel event")
                    .since("1.8.4");
            EventValues.registerEventValue(BeaconEffectEvent.class, Player.class, new Getter<>() {
                @Override
                public Player get(BeaconEffectEvent e) {
                    return e.getPlayer();
                }
            }, EventValues.TIME_NOW);

            EventValues.registerEventValue(BeaconEffectEvent.class, PotionEffectType.class, new Getter<>() {
                @Override
                public PotionEffectType get(BeaconEffectEvent e) {
                    return e.getEffect().getType();
                }
            }, EventValues.TIME_NOW);
            EventValues.registerEventValue(BeaconEffectEvent.class, PotionEffect.class, new Getter<>() {
                @Override
                public @NotNull PotionEffect get(BeaconEffectEvent e) {
                    return e.getEffect();
                }
            }, EventValues.TIME_NOW);
        }

        // Beacon Deactivated Event
        if (Skript.classExists("io.papermc.paper.event.block.BeaconDeactivatedEvent")) {
            Skript.registerEvent("Beacon Deactivation", PaperEvents.class, BeaconDeactivatedEvent.class, "beacon (deactivate|deactivation)")
                    .description("Called when a beacon is deactivated from breaking or losing required amount blocks.")
                    .examples("on beacon deactivation:",
                            "\tbroadcast \"%event-block% is no longer activated, :cry:\"");

        }

        // Beacon Activated Event
        if (Skript.classExists("io.papermc.paper.event.block.BeaconActivatedEvent")) {
            Skript.registerEvent("Beacon Activation", PaperEvents.class, BeaconActivatedEvent.class, "beacon (activate|activation)")
                    .description("Called when a beacon is successfully activated by having correct amount of blocks.")
                    .examples("on beacon activation",
                            "\tset primary effect of event-block to strength")
                    .since("2.16.0");
        }

        if (Skript.classExists("io.papermc.paper.event.entity.EntityInsideBlockEvent")) {
            Skript.registerEvent("Entity Inside Block", PaperEvents.class, EntityInsideBlockEvent.class, "entity inside block")
                    .description("Called when an entity enters the hitbox of a block.",
                            "Only called for blocks that react when an entity is inside.",
                            "If cancelled, any action that would have resulted from that entity being in the block will not happen (such as extinguishing an entity in a cauldron).",
                            "Currently called for: Big dripleaf, Bubble column, Buttons, Cactus, Campfire, Cauldron, Crops, Ender Portal, Fires, Frogspawn, Honey, Hopper, Detector rails,",
                            "Nether portals, Pitcher crop, Powdered snow, Pressure plates, Sweet berry bush, Tripwire, Waterlily, Web, Wither rose")
                    .examples("on entity inside block:",
                            "\tif event-block is a cactus:",
                            "\t\tcancel event",
                            "\t\tbroadcast \"OUCHIE\"")
                    .since("3.4.0");

            EventValues.registerEventValue(EntityInsideBlockEvent.class, Block.class, new Getter<>() {
                @Override
                public Block get(EntityInsideBlockEvent event) {
                    return event.getBlock();
                }
            }, EventValues.TIME_NOW);
        }

        // PlayerAttemptPickupItemEvent
        if (Skript.classExists("org.bukkit.event.player.PlayerAttemptPickupItemEvent")) {
            Skript.registerEvent("Player Attempt Item Pickup", PaperEvents.class, PlayerAttemptPickupItemEvent.class, "player attempt item pickup")
                    .description("Called when a player attempts to pick an item up from the ground. Requires PaperMC.",
                            "`event-number` = Represents the amount that will remain on the ground, if any.",
                            "`past event-number` = Represents the item amount of the dropped item before pickup.",
                            "`event-dropped item` = Represents the dropped item entity that is attempting to pickup.")
                    .examples("on player attempt item pickup:",
                            "\tif event-number > 0:",
                            "\t\twait 1 tick",
                            "\t\tadd (item of event-dropped item) to enderchest of player",
                            "\t\tkill event-dropped item")
                    .since("3.5.0");

            EventValues.registerEventValue(PlayerAttemptPickupItemEvent.class, Number.class, new Getter<>() {
                @Override
                public Number get(PlayerAttemptPickupItemEvent event) {
                    return event.getRemaining();
                }
            }, EventValues.TIME_NOW);

            EventValues.registerEventValue(PlayerAttemptPickupItemEvent.class, Number.class, new Getter<>() {
                @Override
                public Number get(PlayerAttemptPickupItemEvent event) {
                    return event.getItem().getItemStack().getAmount();
                }
            }, EventValues.TIME_PAST);

            EventValues.registerEventValue(PlayerAttemptPickupItemEvent.class, Item.class, new Getter<>() {
                @Override
                public Item get(PlayerAttemptPickupItemEvent event) {
                    return event.getItem();
                }
            }, EventValues.TIME_NOW);
        }

        // PlayerTrackEntityEvent
        if (Skript.classExists("io.papermc.paper.event.player.PlayerTrackEntityEvent")) {
            Skript.registerEvent("Player Track Entity", PaperEvents.class, PlayerTrackEntityEvent.class, "player track entity")
                .description("Called when a Player tracks an Entity (This means the entity is sent to the client).",
                    "If cancelled entity is not shown to the player and interaction in both directions is not possible.",
                    "(This is copied from Paper javadocs and does not seem true. When testing on a zombie, the zombie still attacked me)",
                    "Adding or removing entities from the world at the point in time this event is called is completely unsupported and should be avoided.",
                    "Requires PaperMC 1.19+.")
                .examples("on player track entity:",
                    "\tif event-entity is a zombie:",
                    "\t\tcancel event")
                .since("3.5.1");

            EventValues.registerEventValue(PlayerTrackEntityEvent.class, Entity.class, new Getter<>() {
                @Override
                public Entity get(PlayerTrackEntityEvent event) {
                    return event.getEntity();
                }
            }, EventValues.TIME_NOW);
        }

    }

}
