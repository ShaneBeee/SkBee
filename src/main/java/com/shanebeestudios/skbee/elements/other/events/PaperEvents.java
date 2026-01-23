package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventConverter;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Experience;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.EntityZapEvent;
import com.destroystokyo.paper.event.entity.ExperienceOrbMergeEvent;
import com.destroystokyo.paper.event.entity.SkeletonHorseTrapEvent;
import com.destroystokyo.paper.event.entity.SlimePathfindEvent;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import io.papermc.paper.connection.PlayerCommonConnection;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.connection.PlayerConnection;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import io.papermc.paper.event.packet.UncheckedSignChangeEvent;
import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings({"unused", "unchecked", "UnstableApiUsage"})
public class PaperEvents extends SimpleEvent {

    private static final boolean HAS_CONFIG = Skript.classExists("io.papermc.paper.connection.PlayerConfigurationConnection");

    static {
        // == PLAYER EVENTS == //

        // Player Recipe Book Click Event
        Skript.registerEvent("Recipe Book Click Event", PaperEvents.class, PlayerRecipeBookClickEvent.class, "[player] recipe book click")
            .description("Called when the player clicks on a recipe in their recipe book. Requires Paper 1.15+")
            .examples("on recipe book click:",
                "\tif event-string = \"minecraft:diamond_sword\":",
                "\t\tcancel event")
            .since("1.5.0");

        EventValues.registerEventValue(PlayerRecipeBookClickEvent.class, String.class, event -> event.getRecipe().toString(), EventValues.TIME_NOW);

        // Player Pickup XP Event
        Skript.registerEvent("Player Pickup Experience Orb", PaperEvents.class, PlayerPickupExperienceEvent.class,
                "player pickup (experience|xp) [orb]")
            .description("Fired when a player is attempting to pick up an experience orb. Requires Paper 1.12.2+",
                "\n`event-experience` represents the experience picked up (This is Skript's version of XP) (can be set).",
                "\n`event-number` represents the experience picked up as a number (can be set).",
                "\n`event-entity` represents the experience orb entity.")
            .examples("on player pickup xp:",
                "\tadd 10 to level of player")
            .since("1.8.0");

        EventValues.registerEventValue(PlayerPickupExperienceEvent.class, Experience.class, new EventConverter<>() {
            @Override
            public void set(PlayerPickupExperienceEvent event, @Nullable Experience value) {
                if (value == null) return;
                event.getExperienceOrb().setExperience(value.getXP());
            }

            @Override
            public Experience convert(PlayerPickupExperienceEvent event) {
                return new Experience(event.getExperienceOrb().getExperience());
            }
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerPickupExperienceEvent.class, Number.class, new EventConverter<>() {
            @Override
            public void set(PlayerPickupExperienceEvent event, @Nullable Number value) {
                if (value == null) return;
                event.getExperienceOrb().setExperience(value.intValue());
            }

            @Override
            public Number convert(PlayerPickupExperienceEvent event) {
                return event.getExperienceOrb().getExperience();
            }
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerPickupExperienceEvent.class, Entity.class, PlayerPickupExperienceEvent::getExperienceOrb, EventValues.TIME_NOW);

        // Player Elytra Boost Event
        Skript.registerEvent("Player Elytra Boost", PaperEvents.class, PlayerElytraBoostEvent.class, "[player] elytra boost")
            .description("Fired when a player boosts elytra flight with a firework. Requires Paper 1.13.2+")
            .examples("on elytra boost:",
                "\tpush player forward at speed 50")
            .since("1.8.0");
        EventValues.registerEventValue(PlayerElytraBoostEvent.class, ItemType.class, e -> new ItemType(e.getItemStack()), EventValues.TIME_NOW);

        // Player Stop Using Item Event
        Skript.registerEvent("Player Stop Using Item", PaperEvents.class, PlayerStopUsingItemEvent.class, "[player] stop using item")
            .description("Called when the server detects a player stopping using an item.",
                "Examples of this are letting go of the interact button when holding a bow, an edible item, or a spyglass.",
                "event-number is the number of ticks the item was held for. Requires Paper 1.18+.")
            .examples("on player stop using item:",
                "\tif event-item is a spyglass:",
                "\t\tkill player")
            .since("1.17.0");
        EventValues.registerEventValue(PlayerStopUsingItemEvent.class, ItemType.class, event -> new ItemType(event.getItem()), EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerStopUsingItemEvent.class, ItemStack.class, PlayerStopUsingItemEvent::getItem);
        EventValues.registerEventValue(PlayerStopUsingItemEvent.class, Number.class, PlayerStopUsingItemEvent::getTicksHeldFor, EventValues.TIME_NOW);

        // Player Chunk Load Event
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

        EventValues.registerEventValue(PlayerChunkLoadEvent.class, Player.class, PlayerChunkLoadEvent::getPlayer, EventValues.TIME_NOW);

        // Player Chunk Unload Event
        Skript.registerEvent("Player Chunk Unload", PaperEvents.class, PlayerChunkUnloadEvent.class,
                "player chunk unload")
            .description("Is called when a Player receives a chunk unload packet.",
                "Should only be used for packet/clientside related stuff. Not intended for modifying server side.",
                "\nRequires a PaperMC server.")
            .examples("on player chunk unload:",
                "\tsend \"looks like you lost your chunk cowboy!\" to player")
            .since("2.6.1");

        EventValues.registerEventValue(PlayerChunkUnloadEvent.class, Player.class, PlayerChunkUnloadEvent::getPlayer, EventValues.TIME_NOW);

        // Player Custom Click Event
        if (Skript.classExists("io.papermc.paper.event.player.PlayerCustomClickEvent")) {
            Skript.registerEvent("Player Custom Click Event", PaperEvents.class, PlayerCustomClickEvent.class,
                    "[player] custom (click|payload)")
                .description("This event is fired for any custom click events.",
                    "This is primarily used for dialogs and text component click events with custom payloads.",
                    "Requires Paper 1.21.6+",
                    "",
                    "**Event Values:**",
                    "- `event-[offline]player` = The player/offlineplayer who sent the payload.",
                    "- `event-audience` = The audience who sent the payload.",
                    "- `event-uuid` = The uuid of the player who sent the payload.`",
                    "- `event-string` = The name of the player (used when a player isn't available yet).",
                    "- `event-namespacedkey` = The key used to identify the custom payload.",
                    "- `event-nbt` = The nbt compound passed from the custom payload click.")
                .examples("on custom click:",
                    "\tif event-namespacedkey = \"test:key\":",
                    "\t\tset {_nbt} to event-nbt",
                    "\t\tset {_blah} to string tag \"blah\" of {_nbt}",
                    "\t\tsend \"YourData: %{_blah}%\" to player")
                .since("3.13.0");

            EventValues.registerEventValue(PlayerCustomClickEvent.class, UUID.class, from -> {
                PlayerCommonConnection connection = from.getCommonConnection();
                if (connection instanceof PlayerGameConnection gameConnection) {
                    return gameConnection.getPlayer().getUniqueId();
                } else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                    return configConnection.getProfile().getId();
                }
                return null;
            });
            EventValues.registerEventValue(PlayerCustomClickEvent.class, OfflinePlayer.class, event -> {
                PlayerCommonConnection connection = event.getCommonConnection();
                if (connection instanceof PlayerGameConnection gameConnection)
                    return gameConnection.getPlayer();
                else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                    UUID uuid = configConnection.getProfile().getId();
                    if (uuid != null) {
                        return Bukkit.getOfflinePlayer(uuid);
                    }
                }
                return null;
            });
            EventValues.registerEventValue(PlayerCustomClickEvent.class, Audience.class, event -> {
                PlayerCommonConnection connection = event.getCommonConnection();
                if (connection instanceof PlayerGameConnection gameConnection)
                    return gameConnection.getPlayer();
                else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                    return configConnection.getAudience();
                }
                return null;
            });
            EventValues.registerEventValue(PlayerCustomClickEvent.class, String.class, event -> {
                PlayerCommonConnection connection = event.getCommonConnection();
                if (connection instanceof PlayerGameConnection gameConnection)
                    return gameConnection.getPlayer().getName();
                else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                    return configConnection.getProfile().getName();
                }
                return null;
            });
            EventValues.registerEventValue(PlayerCustomClickEvent.class, NBTCompound.class, event -> {
                BinaryTagHolder tag = event.getTag();
                if (tag == null) return null;

                return (NBTCompound) NBT.parseNBT(tag.string());
            });
            EventValues.registerEventValue(PlayerCustomClickEvent.class, NamespacedKey.class, event -> NamespacedKey.fromString(event.getIdentifier().asString()));
            EventValues.registerEventValue(PlayerCustomClickEvent.class, PlayerConnection.class, PlayerCustomClickEvent::getCommonConnection);
        }

        // UncheckedSignChangeEvent
        if (Skript.classExists("io.papermc.paper.event.packet.UncheckedSignChangeEvent")) {
            Skript.registerEvent("Unchecked Sign Change", PaperEvents.class, UncheckedSignChangeEvent.class, "unchecked sign change")
                .description("Called when a client attempts to modify a sign, but the location at which the sign should be edited has not yet been checked for the existence of a real sign.",
                    "This event is used for client side sign changes.",
                    "`event-text components` = The lines from the sign (will include all 4 lines, reglardless if they were changed).",
                    "`event-location` = The location of the client side sign block.")
                .examples("")
                .since("3.11.3");

            EventValues.registerEventValue(UncheckedSignChangeEvent.class, ComponentWrapper[].class, from -> {
                ComponentWrapper[] comps = new ComponentWrapper[4];
                for (int i = 0; i < 4; i++) {
                    comps[i] = ComponentWrapper.fromComponent(from.lines().get(i));
                }
                return comps;
            }, EventValues.TIME_NOW);
            EventValues.registerEventValue(UncheckedSignChangeEvent.class, Location.class, from -> {
                BlockPosition editedBlockPosition = from.getEditedBlockPosition();
                return editedBlockPosition.toLocation(from.getPlayer().getWorld());
            }, EventValues.TIME_NOW);
        }

        // == ENTITY EVENTS == //

        // Entity Pathfind Event
        Skript.registerEvent("Entity Pathfind Event", PaperEvents.class, new Class[]{EntityPathfindEvent.class, SlimePathfindEvent.class}, "entity start[s] pathfinding")
            .description("Called when an Entity decides to start moving towards a location. This event does not fire for the entities " +
                "actual movement. Only when it is choosing to start moving to a location. Requires Paper.")
            .examples("on entity starts pathfinding:",
                "\tif event-entity is a sheep:",
                "\t\tcancel event")
            .since("1.5.0");

        EventValues.registerEventValue(EntityPathfindEvent.class, Location.class, EntityPathfindEvent::getLoc, EventValues.TIME_NOW);

        // Skeleton Horse Trap Event
        Skript.registerEvent("Skeleton Horse Trap Event", PaperEvents.class, SkeletonHorseTrapEvent.class, "skeleton horse trap")
            .description("Called when a player gets close to a skeleton horse and triggers the lightning trap. Requires Paper 1.13+")
            .examples("on skeleton horse trap:",
                "\tloop all players in radius 10 around event-entity:",
                "\t\tif loop-player is an op:",
                "\t\t\tcancel event")
            .since("1.5.0");

        // Entity Zap Event
        Skript.registerEvent("Entity Zap", PaperEvents.class, EntityZapEvent.class, "entity (zap|struck by lightning)")
            .description("Fired when lightning strikes an entity. Requires Paper 1.10.2+")
            .examples("on entity zap:",
                "\tif event-entity is a pig:",
                "\t\tspawn 3 zombie pigmen at event-location")
            .since("1.8.0");
        EventValues.registerEventValue(EntityZapEvent.class, Location.class, e -> e.getEntity().getLocation(), EventValues.TIME_NOW);

        // Entity Knockback Event
        Skript.registerEvent("Entity Knockback", PaperEvents.class, EntityKnockbackByEntityEvent.class, "entity knockback")
            .description("Fired when an Entity is knocked back by the hit of another Entity. " +
                "If this event is cancelled, the entity is not knocked back. Requires Paper 1.12.2+")
            .examples("on entity knockback:", "\tif event-entity is a cow:", "\t\tcancel event")
            .since("1.8.0");

        // Experience Orb Merge Event
        Skript.registerEvent("Experience Orb Merge", PaperEvents.class, ExperienceOrbMergeEvent.class, "(experience|[e]xp) orb merge")
            .description("Fired anytime the server is about to merge 2 experience orbs into one. Requires Paper 1.12.2+")
            .examples("on xp merge:",
                "\tcancel event")
            .since("1.8.0");

        // Entity Add To World Event
        Skript.registerEvent("Entity Add to World", PaperEvents.class, EntityAddToWorldEvent.class,
                "entity add[ed] to world")
            .description("Fired any time an entity is being added to the world for any reason.",
                "Not to be confused with entity spawn event. This will fire anytime a chunk is reloaded too. Requires a PaperMC server.")
            .examples("on entity added to world:",
                "\tdelete event-entity")
            .since("2.7.2");

        // == BLOCK EVENTS == //

        // EntityInsideBlockEvent
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

        EventValues.registerEventValue(EntityInsideBlockEvent.class, Block.class, EntityInsideBlockEvent::getBlock, EventValues.TIME_NOW);

        // PlayerAttemptPickupItemEvent
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

        EventValues.registerEventValue(PlayerAttemptPickupItemEvent.class, Number.class, PlayerAttemptPickupItemEvent::getRemaining, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerAttemptPickupItemEvent.class, Number.class, event -> event.getItem().getItemStack().getAmount(), EventValues.TIME_PAST);
        EventValues.registerEventValue(PlayerAttemptPickupItemEvent.class, Item.class, PlayerAttemptPickupItemEvent::getItem, EventValues.TIME_NOW);

        // PlayerTrackEntityEvent
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

        EventValues.registerEventValue(PlayerTrackEntityEvent.class, Entity.class, PlayerTrackEntityEvent::getEntity, EventValues.TIME_NOW);

        // PlayerFailMoveEvent
        Skript.registerEvent("Player Fail Move", PaperEvents.class, PlayerFailMoveEvent.class, "player fail move")
            .description("Called when a player attempts to move, but is prevented from doing so by the server.",
                "Requires PaperMC and Skript 2.11+.",
                "`event-failmovereason` = The reason they failed to move.",
                "`event-location` = The location they moved from.",
                "`future event-location` = The location they moved to.",
                "`event-boolean` = Whether the player is allowed to move (can be set).",
                "`future event-boolean` = Whether to log warning to console (can be set).")
            .examples("on player fail move:",
                "\tset event-boolean to true",
                "\tset future event-boolean to false",
                "\tif event-failmovereason = clipped_into_block:",
                "\t\tpush player up with speed 1")
            .since("3.11.0");

        EventValues.registerEventValue(PlayerFailMoveEvent.class, PlayerFailMoveEvent.FailReason.class, PlayerFailMoveEvent::getFailReason);
        EventValues.registerEventValue(PlayerFailMoveEvent.class, Location.class, PlayerFailMoveEvent::getFrom, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerFailMoveEvent.class, Location.class, PlayerFailMoveEvent::getTo, EventValues.TIME_FUTURE);
        EventValues.registerEventValue(PlayerFailMoveEvent.class, Boolean.class, new EventConverter<>() {
            @Override
            public void set(PlayerFailMoveEvent event, @Nullable Boolean allowed) {
                event.setAllowed(Boolean.TRUE.equals(allowed));
            }

            @Override
            public Boolean convert(PlayerFailMoveEvent event) {
                return event.isAllowed();
            }
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerFailMoveEvent.class, Boolean.class, new EventConverter<>() {
            @Override
            public void set(PlayerFailMoveEvent event, @Nullable Boolean allowed) {
                event.setLogWarning(Boolean.TRUE.equals(allowed));
            }

            @Override
            public Boolean convert(PlayerFailMoveEvent event) {
                return event.getLogWarning();
            }
        }, EventValues.TIME_FUTURE);

        Skript.registerEvent("Player Client Load World", PaperEvents.class, PlayerClientLoadedWorldEvent.class, "player client load world")
            .description("Called when the player's client has officially loaded into the world, this is called after the join event.",
                "This event may also be called when a player timeouts if the player fails to load into the world.")
            .examples("on client load world:", "\tbroadcast \"%player% has loaded into the world%\"")
            .since("INSERT VERSION");

        // SERVER EVENTS
        // Tick Start/End Event
        Skript.registerEvent("Tick Start Event", PaperEvents.class, ServerTickStartEvent.class, "server tick start")
            .description("Called each time the server starts its main tick loop.",
                "`event-number` = The current tick number.")
            .examples("")
            .since("3.10.0");

        Skript.registerEvent("Tick End Event", PaperEvents.class, ServerTickEndEvent.class, "server tick end")
            .description("Called when the server has finished ticking the main loop.",
                "There may be time left after this event is called, and before the next tick starts.",
                "`event-numbers` = Represents different numbers in this event, in this order:",
                "- Current tick number (starts from 0 when the server starts and counts up).",
                "- Tick duration (in milliseconds) (How long the tick took to tick).",
                "- Time remaining (in milliseconds) (How long til the next tick executes).",
                "- Time remaining (in nanoseconds) (How long til the next tick executes).")
            .examples("")
            .since("3.10.0");

        EventValues.registerEventValue(ServerTickStartEvent.class, Integer.class, ServerTickStartEvent::getTickNumber);
        EventValues.registerEventValue(ServerTickEndEvent.class, Number[].class,
            from -> new Number[]{
                from.getTickNumber(),
                from.getTickDuration(),
                from.getTimeRemaining() / 1_000_000,
                from.getTimeRemaining()
            });

        // Server Resources Reloaded Event
        Skript.registerEvent("Server Resources Reloaded", PaperEvents.class, ServerResourcesReloadedEvent.class,
                "server resources reload[ed]")
            .description("Called when resources such as datapacks are reloaded (e.g. /minecraft:reload).",
                "Intended for use to re-register custom recipes, advancements that may be lost during a reload like this.",
                "This can also be used after SkBriggy commands are loaded (since they appear to wipe recipes).")
            .examples("function loadRecipes():",
                "\tregister shaped recipe:",
                "\t\t...",
                "",
                "on skript load:",
                "\t# Load recipes when the server starts",
                "\tloadRecipes()",
                "",
                "on server resources reload:",
                "\t# Reload recipes when datapacks get reloaded",
                "\tloadRecipes()")
            .since("3.15.0");
    }

}
