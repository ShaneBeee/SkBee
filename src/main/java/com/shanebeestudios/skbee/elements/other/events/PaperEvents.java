package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Experience;
import ch.njol.skript.util.Getter;
import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.entity.EntityZapEvent;
import com.destroystokyo.paper.event.entity.ExperienceOrbMergeEvent;
import com.destroystokyo.paper.event.entity.SkeletonHorseTrapEvent;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("unused")
public class PaperEvents {

    static {
        // == PLAYER EVENTS == //

        // Player Recipe Book Click Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent")) {
            Skript.registerEvent("Recipe Book Click Event", SimpleEvent.class, PlayerRecipeBookClickEvent.class, "[player] recipe book click")
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
            Skript.registerEvent("Player Pickup Experience Orb", SimpleEvent.class, PlayerPickupExperienceEvent.class,
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
            Skript.registerEvent("Player Elytra Boost", SimpleEvent.class, PlayerElytraBoostEvent.class, "[player] elytra boost")
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
            Skript.registerEvent("Player Stop Using Item", SimpleEvent.class, PlayerStopUsingItemEvent.class, "[player] stop using item")
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
            Skript.registerEvent("Beacon - Player Change Effect", SimpleEvent.class, PlayerChangeBeaconEffectEvent.class, "[player] change beacon [potion] effect[s]", "beacon [potion] effect change")
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
            Skript.registerEvent("Player Chunk Load", SimpleEvent.class, PlayerChunkLoadEvent.class,
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
            Skript.registerEvent("Player Chunk Unload", SimpleEvent.class, PlayerChunkUnloadEvent.class,
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
            Skript.registerEvent("Entity Pathfind Event", SimpleEvent.class, EntityPathfindEvent.class, "entity start[s] pathfinding")
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
            Skript.registerEvent("Skeleton Horse Trap Event", SimpleEvent.class, SkeletonHorseTrapEvent.class, "skeleton horse trap")
                    .description("Called when a player gets close to a skeleton horse and triggers the lightning trap. Requires Paper 1.13+")
                    .examples("on skeleton horse trap:",
                            "\tloop all players in radius 10 around event-entity:",
                            "\t\tif loop-player is an op:",
                            "\t\t\tcancel event")
                    .since("1.5.0");
        }

        // Entity Zap Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.EntityZapEvent")) {
            Skript.registerEvent("Entity Zap", SimpleEvent.class, EntityZapEvent.class, "entity (zap|struck by lightning)")
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
            Skript.registerEvent("Entity Knockback", SimpleEvent.class, EntityKnockbackByEntityEvent.class, "entity knockback")
                    .description("Fired when an Entity is knocked back by the hit of another Entity. " +
                            "If this event is cancelled, the entity is not knocked back. Requires Paper 1.12.2+")
                    .examples("on entity knockback:", "\tif event-entity is a cow:", "\t\tcancel event")
                    .since("1.8.0");
        }

        // Experience Orb Merge Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.ExperienceOrbMergeEvent")) {
            Skript.registerEvent("Experience Orb Merge", SimpleEvent.class, ExperienceOrbMergeEvent.class, "(experience|[e]xp) orb merge")
                    .description("Fired anytime the server is about to merge 2 experience orbs into one. Requires Paper 1.12.2+")
                    .examples("on xp merge:",
                            "\tcancel event")
                    .since("1.8.0");
        }

        // Entity Add To World Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.EntityAddToWorldEvent")) {
            Skript.registerEvent("Entity Add to World", SimpleEvent.class, EntityAddToWorldEvent.class,
                            "entity add[ed] to world")
                    .description("Fired any time an entity is being added to the world for any reason.",
                            "Not to be confused with entity spawn event. This will fire anytime a chunk is reloaded too. Requires a PaperMC server.")
                    .examples("on entity added to world:",
                            "\tdelete event-entity")
                    .since("2.7.2");
        }

        // Entity Removed from World Event
        if (Skript.classExists("com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent")) {
            Skript.registerEvent("Entity Remove from World", SimpleEvent.class, EntityRemoveFromWorldEvent.class,
                            "entity remove[d] from world")
                    .description("Fired any time an entity is being removed from a world for any reason. Requires a PaperMC server.")
                    .examples("on entity removed from world:",
                            "\tbroadcast \"a lonely %event-entity% left the world.\"")
                    .since("2.7.2");
        }

        // == BLOCK EVENTS == //

        // Beacon Effect Event
        if (Skript.classExists("com.destroystokyo.paper.event.block.BeaconEffectEvent")) {
            Skript.registerEvent("Beacon Effect", SimpleEvent.class, BeaconEffectEvent.class, "beacon effect")
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
            Skript.registerEvent("Beacon Deactivation", SimpleEvent.class, BeaconDeactivatedEvent.class, "beacon (deactivate|deactivation)")
                    .description("Called when a beacon is deactivated from breaking or losing required amount blocks.")
                    .examples("on beacon deactivation:",
                            "\tbroadcast \"%event-block% is no longer activated, :cry:\"");

        }

        // Beacon Activated Event
        if (Skript.classExists("io.papermc.paper.event.block.BeaconActivatedEvent")) {
            Skript.registerEvent("Beacon Activation", SimpleEvent.class, BeaconActivatedEvent.class, "beacon (activate|activation)")
                    .description("Called when a beacon is successfully activated by having correct amount of blocks.")
                    .examples("on beacon activation",
                            "\tset primary effect of event-block to strength")
                    .since("2.16.0");
        }

    }

}
