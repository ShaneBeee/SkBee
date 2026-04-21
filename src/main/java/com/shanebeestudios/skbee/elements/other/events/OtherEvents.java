package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.Experience;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.skript.util.slot.Slot;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.EntityZapEvent;
import com.destroystokyo.paper.event.entity.ExperienceOrbMergeEvent;
import com.destroystokyo.paper.event.entity.SkeletonHorseTrapEvent;
import com.destroystokyo.paper.event.entity.SlimePathfindEvent;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.event.EntityBlockInteractEvent;
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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;
import org.skriptlang.skript.lang.converter.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class OtherEvents extends SimpleEvent {

    private static final boolean HAS_CONFIG = Skript.classExists("io.papermc.paper.connection.PlayerConfigurationConnection");

    public static void register(Registration reg) {
        blockEvents(reg);
        entityEvents(reg);
        packetEvents(reg);
        playerEvents(reg);
        serverEvents(reg);

        otherEventValues(reg);
    }

    private static void blockEvents(Registration reg) {
        // Block Damage Abort Event
        reg.newEvent(OtherEvents.class, BlockDamageAbortEvent.class,
                "block damage abort")
            .name("Block Damage Abort")
            .description("Called when a player stops damaging a Block. Requires MC 1.18.x+")
            .examples("on block damage abort:",
                "\tsend \"get back to work\"")
            .since("2.8.3")
            .register();

        reg.newEventValue(BlockDamageAbortEvent.class, Player.class)
            .converter(BlockDamageAbortEvent::getPlayer)
            .register();

        // Block Explode Event
        reg.newEvent(OtherEvents.class, BlockExplodeEvent.class, "block explode")
            .name("Block Explode")
            .description("Called when a block explodes interacting with blocks.",
                "The event isn't called if the gamerule MOB_GRIEFING is disabled as no block interaction will occur.",
                "The Block returned by this event is not necessarily the block that caused the explosion,",
                "just the block at the location where the explosion originated.")
            .examples("")
            .since("3.2.0")
            .register();

        reg.newEventValue(BlockExplodeEvent.class, BlockData.class)
            .converter(event -> event.getBlock().getBlockData())
            .register();
        reg.newEventValue(BlockExplodeEvent.class, BlockData.class)
            .description("The blockdata of the block which exploded.")
            .time(EventValue.Time.PAST)
            .converter(new Converter<>() {
                @Override
                public @NotNull BlockData convert(BlockExplodeEvent event) {
                    BlockState explodedBlockState = event.getExplodedBlockState();
                    return explodedBlockState.getBlockData();
                }
            })
            .register();
        reg.newEventValue(BlockExplodeEvent.class, ItemType.class)
            .converter(event -> new ItemType(event.getBlock().getType()))
            .register();
        reg.newEventValue(BlockExplodeEvent.class, ItemType.class)
            .description("The item type of the block which exploded.")
            .time(EventValue.Time.PAST)
            .converter(new Converter<>() {
                @Override
                public @NotNull ItemType convert(BlockExplodeEvent event) {
                    BlockState explodedBlockState = event.getExplodedBlockState();
                    return new ItemType(explodedBlockState.getType());
                }
            })
            .register();
        reg.newEventValue(BlockExplodeEvent.class, Block[].class)
            .description("The blocks which exploded.")
            .patterns("exploded-blocks")
            .converter(from -> from.blockList().toArray(new Block[0]))
            .changer(ChangeMode.SET, (event, value) -> {
                event.blockList().clear();
                if (value != null && value.length > 0) {
                    event.blockList().addAll(List.of(value));
                }
            })
            .changer(ChangeMode.ADD, (event, value) -> {
                if (value != null) {
                    for (Block block : value) {
                        event.blockList().add(block);
                    }
                }
            })
            .changer(ChangeMode.REMOVE, (event, value) -> {
                if (value != null) {
                    for (Block block : value) {
                        event.blockList().remove(block);
                    }
                }
            })
            .changer(ChangeMode.DELETE, (event, value) -> event.blockList().clear())
            .register();

        // Moisture Change Event
        reg.newEvent(OtherEvents.class, MoistureChangeEvent.class, "moisture change")
            .name("Moisture Change")
            .description("Called when the moisture level of a farmland block changes.")
            .examples("on moisture change:",
                "\tcancel event",
                "\tset event-block to farmland[moisture=7]")
            .since("3.0.0")
            .register();

        reg.newEventValue(MoistureChangeEvent.class, Block.class)
            .time(EventValue.Time.FUTURE)
            .converter(MoistureChangeEvent::getBlock)
            .register();
    }

    @SuppressWarnings("unchecked")
    private static void entityEvents(Registration reg) {
        // Entity Add To World Event
        reg.newEvent(OtherEvents.class, EntityAddToWorldEvent.class,
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
        reg.newEvent(OtherEvents.class, EntityAirChangeEvent.class,
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
                return new Timespan(TimePeriod.TICK, Math.max(ticks, 0));
            })
            .register();
        reg.newEventValue(EntityAirChangeEvent.class, Number.class)
            .description("The amount of air the entity will have left (measured in ticks).")
            .converter(EntityAirChangeEvent::getAmount)
            .changer(ChangeMode.SET, (event, value) ->
                event.setAmount(value != null ? value.intValue() : 0))
            .register();
        reg.newEventValue(EntityAirChangeEvent.class, Timespan.class)
            .converter(event -> new Timespan(TimePeriod.TICK, Math.max(event.getAmount(), 0)))
            .changer(ChangeMode.SET, (event, value) -> {
                int amount = value != null ? (int) value.getAs(TimePeriod.TICK) : 0;
                event.setAmount(amount);
            })
            .description("The amount of air the entity will have left (as a time span).")
            .register();

        // Entity Block Interact Event
        reg.newEvent(OtherEvents.class, EntityBlockInteractEvent.class,
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
        reg.newEvent(OtherEvents.class, EntityChangeBlockEvent.class,
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
        reg.newEvent(OtherEvents.class, EntityInsideBlockEvent.class, "entity inside block")
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
        reg.newEvent(OtherEvents.class, new Class[]{EntityPathfindEvent.class, SlimePathfindEvent.class}, "entity start[s] pathfinding")
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
        reg.newEvent(OtherEvents.class, EntityPlaceEvent.class,
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
        reg.newEvent(OtherEvents.class, EntityPoseChangeEvent.class,
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
        reg.newEvent(OtherEvents.class, EntityRemoveEvent.class,
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

        // Entity Shoot Bow Event
        reg.newEvent(OtherEvents.class, EntityShootBowEvent.class,
                "entity shoot bow")
            .name("Entity Shoot Bow")
            .description("Called when a LivingEntity shoots a bow/crossbow firing an arrow.")
            .examples("on entity shoot bow:",
                "\tif name of shot bow != \"Mr Bow\":",
                "\t\tcancel event",
                "",
                "on entity shoot bow:",
                "\tif gamemode of player = survival:",
                "\t\tgive player 1 of event-item",
                "",
                "on entity shoot bow:",
                "\tif event-shooter is a player:",
                "\t\tif event-force < 1:",
                "\t\t\tcancel event")
            .since("2.16.0")
            .register();

        reg.newEventValue(EntityShootBowEvent.class, Number.class)
            .description("The force of the bow/crossbow firing the arrow (value between 0.0 and 1.0).")
            .converter(EntityShootBowEvent::getForce)
            .patterns("force")
            .register();
        reg.newEventValue(EntityShootBowEvent.class, Projectile.class)
            .description("The projectile which was shot.")
            .converter(event -> {
                if (event.getProjectile() instanceof Projectile projectile) return projectile;
                return null;
            })
            .register();
        reg.newEventValue(EntityShootBowEvent.class, Entity.class)
            .description("The entity which fired the bow.")
            .patterns("shooter")
            .converter(EntityShootBowEvent::getEntity)
            .register();
        reg.newEventValue(EntityShootBowEvent.class, ItemType.class)
            .description("The ItemType which will be consumed from the entity's inventory (if any).")
            .converter(event -> {
                ItemStack consumable = event.getConsumable();
                if (consumable != null) return new ItemType(consumable);
                return null;
            })
            .register();
        reg.newEventValue(EntityShootBowEvent.class, ItemStack.class)
            .description("The ItemStack which will be consumed from the entity's inventory (if any).")
            .converter(EntityShootBowEvent::getConsumable)
            .register();
        reg.newEventValue(EntityShootBowEvent.class, ItemType.class)
            .description("The bow (ItemType) which shot the entity.")
            .patterns("bow-itemtype")
            .converter(event -> {
                ItemStack consumable = event.getBow();
                if (consumable != null) return new ItemType(consumable);
                return null;
            })
            .register();
        reg.newEventValue(EntityShootBowEvent.class, ItemStack.class)
            .description("The bow (ItemStack) which shot the entity.")
            .patterns("bow-item", "bow-itemstack")
            .converter(EntityShootBowEvent::getBow)
            .register();

        // Entity Spell Cast Event
        reg.newEvent(OtherEvents.class, EntitySpellCastEvent.class,
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
        reg.newEvent(OtherEvents.class, EntityUnleashEvent.class, "entity unleash")
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
        reg.newEvent(OtherEvents.class, EntityZapEvent.class, "entity (zap|struck by lightning)")
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
        reg.newEvent(OtherEvents.class, ExperienceOrbMergeEvent.class, "(experience|[e]xp) orb merge")
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
        reg.newEvent(OtherEvents.class, SkeletonHorseTrapEvent.class, "skeleton horse trap")
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

    @SuppressWarnings("UnstableApiUsage")
    private static void packetEvents(Registration reg) {
        // UncheckedSignChangeEvent
        if (Skript.classExists("io.papermc.paper.event.packet.UncheckedSignChangeEvent")) {
            reg.newEvent(OtherEvents.class, UncheckedSignChangeEvent.class, "unchecked sign change")
                .name("Unchecked Sign Change")
                .description("Called when a client attempts to modify a sign, but the location at which the sign should be edited has not yet been checked for the existence of a real sign.",
                    "This event is used for client side sign changes.",
                    "`event-text components` = The lines from the sign (will include all 4 lines, reglardless if they were changed).",
                    "`event-location` = The location of the client side sign block.")
                .examples("")
                .since("3.11.3")
                .register();

            reg.newEventValue(UncheckedSignChangeEvent.class, ComponentWrapper[].class)
                .converter(from -> {
                    ComponentWrapper[] comps = new ComponentWrapper[4];
                    for (int i = 0; i < 4; i++) {
                        comps[i] = ComponentWrapper.fromComponent(from.lines().get(i));
                    }
                    return comps;
                })
                .register();
            reg.newEventValue(UncheckedSignChangeEvent.class, Location.class)
                .converter(from -> {
                    BlockPosition editedBlockPosition = from.getEditedBlockPosition();
                    return editedBlockPosition.toLocation(from.getPlayer().getWorld());
                })
                .register();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void playerEvents(Registration reg) {
        // PlayerAttemptPickupItemEvent
        reg.newEvent(OtherEvents.class, PlayerAttemptPickupItemEvent.class, "player attempt item pickup")
            .name("Player Attempt Item Pickup")
            .description("Called when a player attempts to pick an item up from the ground.")
            .examples("on player attempt item pickup:",
                "\tif event-number > 0:",
                "\t\twait 1 tick",
                "\t\tadd (item of event-dropped item) to enderchest of player",
                "\t\tkill event-dropped item")
            .since("3.5.0")
            .register();

        reg.newEventValue(PlayerAttemptPickupItemEvent.class, Number.class)
            .description("Represents the amount that will remain on the ground, if any.")
            .converter(PlayerAttemptPickupItemEvent::getRemaining)
            .patterns("remaining")
            .register();
        reg.newEventValue(PlayerAttemptPickupItemEvent.class, Number.class)
            .description("Represents the item amount of the dropped item before pickup.")
            .time(EventValue.Time.PAST)
            .converter(event -> event.getItem().getItemStack().getAmount())
            .register();
        reg.newEventValue(PlayerAttemptPickupItemEvent.class, Item.class)
            .description("Represents the dropped item entity that is attempting to pickup.")
            .converter(PlayerAttemptPickupItemEvent::getItem)
            .register();

        // Prepare Anvil Event
        reg.newEvent(OtherEvents.class, PrepareAnvilEvent.class, "[skbee] anvil prepare")
            .description("Called when a player attempts to combine 2 items in an anvil.",
                "'event-slot' represents the result slot, can be used to get or set.")
            .name("Anvil Prepare Event")
            .examples("on anvil prepare:",
                "\tif slot 0 of event-inventory is a diamond sword:",
                "\t\tif slot 1 of event-inventory is an enchanted book:",
                "\t\t\tif stored enchants of slot 1 of event-inventory contains sharpness 5:",
                "\t\t\t\tset {_i} to slot 0 of event-inventory",
                "\t\t\t\tadd \"&aOOOOOOO\" and \"&bAHHHHHH\" to lore of {_i}",
                "\t\t\t\tenchant {_i} with sharpness 6",
                "\t\t\t\tset event-slot to {_i}",
                "\t\t\t\tset repair cost of event-inventory to 30")
            .since("1.11.0")
            .register();

        reg.newEventValue(PrepareAnvilEvent.class, Slot.class)
            .converter(event -> new Slot() {
                final ItemStack result = event.getResult();

                @Nullable
                @Override
                public ItemStack getItem() {
                    return result;
                }

                @Override
                public void setItem(@Nullable ItemStack item) {
                    event.setResult(item);
                }

                @Override
                public int getAmount() {
                    if (result != null) return result.getAmount();
                    return 0;
                }

                @Override
                public void setAmount(int amount) {
                    if (result != null) result.setAmount(amount);
                }

                @Override
                public boolean isSameSlot(@NotNull Slot o) {
                    ItemStack item = o.getItem();
                    return item != null && item.isSimilar(result);
                }

                @Override
                public @NotNull String toString(@Nullable Event e, boolean debug) {
                    return "anvil inventory result slot";
                }
            })
            .register();
        reg.newEventValue(PrepareAnvilEvent.class, Player.class)
            .converter(event -> (Player) event.getView().getPlayer())
            .register();

        // Player Chunk Load Event
        reg.newEvent(OtherEvents.class, PlayerChunkLoadEvent.class,
                "player chunk (send|load)")
            .name("Player Chunk Load")
            .description("Is called when a Player receives a Chunk.",
                "Can for example be used for spawning a fake entity when the player receives a chunk. ",
                "Should only be used for packet/clientside related stuff. Not intended for modifying server side state.")
            .examples("on player chunk send:",
                "\tloop all blocks in event-chunk:",
                "\t\tif loop-block is diamond ore:",
                "\t\t\tmake player see loop-block as stone")
            .since("2.6.1")
            .register();

        reg.newEventValue(PlayerChunkLoadEvent.class, Player.class)
            .converter(PlayerChunkLoadEvent::getPlayer)
            .register();

        // Player Chunk Unload Event
        reg.newEvent(OtherEvents.class, PlayerChunkUnloadEvent.class,
                "player chunk unload")
            .name("Player Chunk Unload")
            .description("Is called when a Player receives a chunk unload packet.",
                "Should only be used for packet/clientside related stuff. Not intended for modifying server side.")
            .examples("on player chunk unload:",
                "\tsend \"looks like you lost your chunk cowboy!\" to player")
            .since("2.6.1")
            .register();

        reg.newEventValue(PlayerChunkUnloadEvent.class, Player.class)
            .converter(PlayerChunkUnloadEvent::getPlayer)
            .register();

        // Player Custom Click Event
        if (Skript.classExists("io.papermc.paper.event.player.PlayerCustomClickEvent")) {
            reg.newEvent(OtherEvents.class, PlayerCustomClickEvent.class,
                    "[player] custom (click|payload)")
                .name("Player Custom Click Event")
                .description("This event is fired for any custom click events.",
                    "This is primarily used for dialogs and text component click events with custom payloads.")
                .examples("on custom click:",
                    "\tif event-namespacedkey = \"test:key\":",
                    "\t\tset {_nbt} to event-nbt",
                    "\t\tset {_blah} to string tag \"blah\" of {_nbt}",
                    "\t\tsend \"YourData: %{_blah}%\" to player")
                .since("3.13.0")
                .register();

            reg.newEventValue(PlayerCustomClickEvent.class, UUID.class)
                .description("The UUID of the player who sent the payload.")
                .converter(from -> {
                    PlayerCommonConnection connection = from.getCommonConnection();
                    if (connection instanceof PlayerGameConnection gameConnection) {
                        return gameConnection.getPlayer().getUniqueId();
                    } else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                        return configConnection.getProfile().getId();
                    }
                    return null;
                })
                .register();
            reg.newEventValue(PlayerCustomClickEvent.class, OfflinePlayer.class)
                .description("The player/offlineplayer who sent the payload")
                .converter(event -> {
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
                })
                .register();
            reg.newEventValue(PlayerCustomClickEvent.class, Audience.class)
                .description("The audience who sent the payload.")
                .converter(event -> {
                    PlayerCommonConnection connection = event.getCommonConnection();
                    if (connection instanceof PlayerGameConnection gameConnection)
                        return gameConnection.getPlayer();
                    else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                        return configConnection.getAudience();
                    }
                    return null;
                })
                .register();
            reg.newEventValue(PlayerCustomClickEvent.class, String.class)
                .description("The name of the player (used when a player isn't available yet).")
                .converter(event -> {
                    PlayerCommonConnection connection = event.getCommonConnection();
                    if (connection instanceof PlayerGameConnection gameConnection)
                        return gameConnection.getPlayer().getName();
                    else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                        return configConnection.getProfile().getName();
                    }
                    return null;
                })
                .register();
            reg.newEventValue(PlayerCustomClickEvent.class, NBTCompound.class)
                .description("The nbt compound passed from the custom payload click.")
                .converter(event -> {
                    BinaryTagHolder tag = event.getTag();
                    if (tag == null) return null;

                    return (NBTCompound) NBT.parseNBT(tag.string());
                })
                .register();
            reg.newEventValue(PlayerCustomClickEvent.class, NamespacedKey.class)
                .description("The key used to identify the custom payload.")
                .converter(event -> NamespacedKey.fromString(event.getIdentifier().asString()))
                .register();
            reg.newEventValue(PlayerCustomClickEvent.class, PlayerConnection.class)
                .description("The connection of the player who sent the payload.")
                .converter(PlayerCustomClickEvent::getCommonConnection)
                .register();
        }

        // Player Elytra Boost Event
        reg.newEvent(OtherEvents.class, PlayerElytraBoostEvent.class, "[player] elytra boost")
            .name("Player Elytra Boost")
            .description("Fired when a player boosts elytra flight with a firework.")
            .examples("on elytra boost:",
                "\tpush player forward at speed 50")
            .since("1.8.0")
            .register();
        reg.newEventValue(PlayerElytraBoostEvent.class, ItemType.class)
            .converter(e -> new ItemType(e.getItemStack()))
            .register();

        // PlayerFailMoveEvent
        reg.newEvent(OtherEvents.class, PlayerFailMoveEvent.class, "player fail move")
            .name("Player Fail Move")
            .description("Called when a player attempts to move, but is prevented from doing so by the server.")
            .examples("on player fail move:",
                "\tset event-boolean to true",
                "\tset future event-boolean to false",
                "\tif event-failmovereason = clipped_into_block:",
                "\t\tpush player up with speed 1")
            .since("3.11.0")
            .register();

        reg.newEventValue(PlayerFailMoveEvent.class, PlayerFailMoveEvent.FailReason.class)
            .description("The reason they failed to move.")
            .converter(PlayerFailMoveEvent::getFailReason)
            .register();
        reg.newEventValue(PlayerFailMoveEvent.class, Location.class)
            .description("The location they moved from.")
            .converter(PlayerFailMoveEvent::getFrom)
            .register();
        reg.newEventValue(PlayerFailMoveEvent.class, Location.class)
            .description("The location they moved to.")
            .time(EventValue.Time.FUTURE)
            .converter(PlayerFailMoveEvent::getTo)
            .register();
        reg.newEventValue(PlayerFailMoveEvent.class, Boolean.class)
            .description("Whether the player is allowed to move.")
            .converter(PlayerFailMoveEvent::isAllowed)
            .changer(ChangeMode.SET, PlayerFailMoveEvent::setAllowed)
            .register();
        reg.newEventValue(PlayerFailMoveEvent.class, Boolean.class)
            .description("Whether to log warning to console.")
            .time(EventValue.Time.FUTURE)
            .converter(PlayerFailMoveEvent::getLogWarning)
            .changer(ChangeMode.SET, PlayerFailMoveEvent::setLogWarning)
            .register();

        // Player Leash Entity Event
        reg.newEvent(OtherEvents.class, PlayerLeashEntityEvent.class, "player leash entity")
            .name("Player Leash")
            .description("Called immediately prior to a creature being leashed by a player.")
            .examples("on player leash entity:",
                "\tkill event-entity")
            .since("3.2.0")
            .register();

        reg.newEventValue(PlayerLeashEntityEvent.class, Entity.class)
            .description("Entity which got leashed.")
            .converter(PlayerLeashEntityEvent::getEntity)
            .register();
        reg.newEventValue(PlayerLeashEntityEvent.class, Player.class)
            .description("Player whom leashed the entity.")
            .converter(PlayerLeashEntityEvent::getPlayer)
            .register();
        reg.newEventValue(PlayerLeashEntityEvent.class, Entity.class)
            .description("The entity the leashed entity is leashed to (could be a player or leash hitch on a fence).")
            .time(EventValue.Time.FUTURE)
            .converter(PlayerLeashEntityEvent::getLeashHolder)
            .register();

        // Player Pickup XP Event
        reg.newEvent(OtherEvents.class, PlayerPickupExperienceEvent.class,
                "player pickup (experience|xp) [orb]")
            .name("Player Pickup Experience Orb")
            .description("Fired when a player is attempting to pick up an experience orb.")
            .examples("on player pickup xp:",
                "\tadd 10 to level of player")
            .since("1.8.0")
            .register();

        reg.newEventValue(PlayerPickupExperienceEvent.class, Experience.class)
            .description("Represents the experience picked up (This is Skript's version of XP).")
            .converter(event -> new Experience(event.getExperienceOrb().getExperience()))
            .changer(ChangeMode.SET, (event, value) -> {
                if (value == null) return;
                event.getExperienceOrb().setExperience(value.getXP());
            })
            .register();
        reg.newEventValue(PlayerPickupExperienceEvent.class, Number.class)
            .description("represents the experience picked up as a number.")
            .converter(event -> event.getExperienceOrb().getExperience())
            .changer(ChangeMode.SET, (event, value) -> {
                if (value == null) return;
                event.getExperienceOrb().setExperience(value.intValue());
            })
            .register();
        reg.newEventValue(PlayerPickupExperienceEvent.class, Entity.class)
            .description("Represents the experience orb entity.")
            .converter(PlayerPickupExperienceEvent::getExperienceOrb)
            .register();

        // Player Recipe Book Click Event
        reg.newEvent(OtherEvents.class, PlayerRecipeBookClickEvent.class, "[player] recipe book click")
            .name("Recipe Book Click Event")
            .description("Called when the player clicks on a recipe in their recipe book.")
            .examples("on recipe book click:",
                "\tif event-string = \"minecraft:diamond_sword\":",
                "\t\tcancel event")
            .since("1.5.0")
            .register();

        reg.newEventValue(PlayerRecipeBookClickEvent.class, String.class)
            .converter(event -> event.getRecipe().toString())
            .register();

        // Player Spawn Change Event
        reg.newEvent(OtherEvents.class, PlayerSetSpawnEvent.class, "player spawn change")
            .name("Player Spawn Change")
            .description("This event is fired when the spawn point of the player is changed.")
            .examples("on player spawn change:",
                "\tif event-playerspawnchangereason = bed or respawn_anchor:",
                "\t\tcancel event",
                "\t\tsend \"Nope... sorry!\"")
            .since("3.4.0")
            .register();

        reg.newEventValue(PlayerSetSpawnEvent.class, PlayerSetSpawnEvent.Cause.class)
            .converter(PlayerSetSpawnEvent::getCause)
            .register();
        reg.newEventValue(PlayerSetSpawnEvent.class, Location.class)
            .description("The current respawn location of the player.")
            .converter(event -> event.getPlayer().getRespawnLocation())
            .register();
        reg.newEventValue(PlayerSetSpawnEvent.class, Location.class)
            .description("The location that the spawn is set to.")
            .time(EventValue.Time.FUTURE)
            .converter(PlayerSetSpawnEvent::getLocation)
            .register();

        // Player shear entity event
        reg.newEvent(OtherEvents.class, PlayerShearEntityEvent.class, "[player] shear entity")
            .name("Shear Entity")
            .description("Called when a player shears an entity.")
            .examples("on player shear entity:")
            .since("1.8.0")
            .register();

        reg.newEventValue(PlayerShearEntityEvent.class, Entity.class)
            .description("The entity that is getting sheared.")
            .converter(PlayerShearEntityEvent::getEntity)
            .register();
        reg.newEventValue(PlayerShearEntityEvent.class, ItemStack.class)
            .description("The item that was used to shear the entity.")
            .converter(PlayerShearEntityEvent::getItem)
            .register();
        reg.newEventValue(PlayerShearEntityEvent.class, ItemType.class)
            .description("The item that was used to shear the entity.")
            .converter(event -> new ItemType(event.getItem()))
            .register();
        reg.newEventValue(PlayerShearEntityEvent.class, EquipmentSlot.class)
            .description("The hand the player used to shear.")
            .converter(PlayerShearEntityEvent::getHand)
            .register();
        reg.newEventValue(PlayerShearEntityEvent.class, ItemStack[].class)
            .description("Get a list of drops for this shearing.")
            .converter(event -> event.getDrops().toArray(new ItemStack[0]))
            .changer(ChangeMode.SET, (event, value) -> event.setDrops(Arrays.asList(value)))
            .register();
        reg.newEventValue(PlayerShearEntityEvent.class, ItemType[].class)
            .description("Get a list of drops for this shearing.")
            .converter(event -> event.getDrops().stream().map(ItemType::new).toArray(ItemType[]::new))
            .changer(ChangeMode.SET, (event, value) -> {
                List<ItemStack> items = new ArrayList<>();
                for (ItemType itemType : value) {
                    items.add(itemType.getRandom());
                }
                event.setDrops(items);
            })
            .register();

        // Player Stop Using Item Event
        reg.newEvent(OtherEvents.class, PlayerStopUsingItemEvent.class, "[player] stop using item")
            .name("Player Stop Using Item")
            .description("Called when the server detects a player stopping using an item.",
                "Examples of this are letting go of the interact button when holding a bow, an edible item, or a spyglass.")
            .examples("on player stop using item:",
                "\tif event-item is a spyglass:",
                "\t\tkill player")
            .since("1.17.0")
            .register();

        reg.newEventValue(PlayerStopUsingItemEvent.class, ItemType.class)
            .converter(event -> new ItemType(event.getItem()))
            .register();
        reg.newEventValue(PlayerStopUsingItemEvent.class, ItemStack.class)
            .converter(PlayerStopUsingItemEvent::getItem)
            .register();
        reg.newEventValue(PlayerStopUsingItemEvent.class, Number.class)
            .description("The number of ticks the item was held for.")
            .converter(PlayerStopUsingItemEvent::getTicksHeldFor)
            .register();
        reg.newEventValue(PlayerStopUsingItemEvent.class, Timespan.class)
            .description("The span of time the item was held for.")
            .converter(event -> new Timespan(TimePeriod.TICK, event.getTicksHeldFor()))
            .register();

        // PlayerTrackEntityEvent
        reg.newEvent(OtherEvents.class, PlayerTrackEntityEvent.class, "player track entity")
            .name("Player Track Entity")
            .description("Called when a Player tracks an Entity (This means the entity is sent to the client).",
                "If cancelled entity is not shown to the player and interaction in both directions is not possible.",
                "(This is copied from Paper javadocs and does not seem true. When testing on a zombie, the zombie still attacked me)",
                "Adding or removing entities from the world at the point in time this event is called is completely unsupported and should be avoided.",
                "Requires PaperMC 1.19+.")
            .examples("on player track entity:",
                "\tif event-entity is a zombie:",
                "\t\tcancel event")
            .since("3.5.1")
            .register();

        reg.newEventValue(PlayerTrackEntityEvent.class, Entity.class)
            .converter(PlayerTrackEntityEvent::getEntity)
            .register();

    }

    private static void serverEvents(Registration reg) {
        // Server Resources Reloaded Event
        reg.newEvent(OtherEvents.class, ServerResourcesReloadedEvent.class,
                "server resources reload[ed]")
            .name("Server Resources Reloaded")
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
            .since("3.15.0")
            .register();

        // Server Tick End/Start Event
        reg.newEvent(OtherEvents.class, ServerTickEndEvent.class, "server tick end")
            .name("Tick End Event")
            .description("Called when the server has finished ticking the main loop.",
                "There may be time left after this event is called, and before the next tick starts.")
            .examples("")
            .since("3.10.0")
            .register();

        reg.newEventValue(ServerTickEndEvent.class, Number.class)
            .description("The current tick number.")
            .converter(ServerTickEndEvent::getTickNumber)
            .patterns("tick-number")
            .register();
        reg.newEventValue(ServerTickEndEvent.class, Timespan.class)
            .description("Time of how long this tick took.")
            .converter(event -> new Timespan(TimePeriod.MILLISECOND, (long) event.getTickDuration()))
            .patterns("tick-duration")
            .register();
        reg.newEventValue(ServerTickEndEvent.class, Timespan.class)
            .description("Amount of time remaining before the next tick should start.")
            .converter(event -> new Timespan(TimePeriod.MILLISECOND, event.getTimeRemaining() / 1_000_000))
            .patterns("tick-remaining")
            .register();

        reg.newEvent(OtherEvents.class, ServerTickStartEvent.class, "server tick start")
            .name("Tick Start Event")
            .description("Called each time the server starts its main tick loop.")
            .examples("")
            .since("3.10.0")
            .register();
        reg.newEventValue(ServerTickStartEvent.class, Number.class)
            .description("The current tick number.")
            .converter(ServerTickStartEvent::getTickNumber)
            .patterns("tick-number")
            .register();

        // Unknown Command Event
        reg.newEvent(OtherEvents.class, UnknownCommandEvent.class, "unknown command")
            .name("Unknown Command")
            .description("This event is fired when a player executes a command that is not defined.")
            .examples("")
            .since("3.10.0")
            .register();

        reg.newEventValue(UnknownCommandEvent.class, String.class)
            .description("The command that was sent.")
            .converter(UnknownCommandEvent::getCommandLine)
            .register();
        reg.newEventValue(UnknownCommandEvent.class, CommandSender.class)
            .description("Who sent the command.")
            .converter(UnknownCommandEvent::getSender)
            .register();
        reg.newEventValue(UnknownCommandEvent.class, ComponentWrapper.class)
            .description("The message that will be returned.")
            .converter(event -> ComponentWrapper.fromComponent(event.message()))
            .patterns("message")
            .register();
    }

    private static void otherEventValues(Registration reg) {
        reg.newEventValue(SpawnerSpawnEvent.class, Block.class)
            .converter(event -> {
                CreatureSpawner spawner = event.getSpawner();
                if (spawner == null) return null;
                return spawner.getBlock();
            })
            .register();

        // Click Events
        reg.newEventValue(PlayerInteractEvent.class, BlockFace.class)
            .converter(PlayerInteractEvent::getBlockFace)
            .register();

        // Projectile Hit Event
        reg.newEventValue(ProjectileHitEvent.class, BlockFace.class)
            .converter(ProjectileHitEvent::getHitBlockFace)
            .register();

        reg.newEventValue(BlockPlaceEvent.class, BlockFace.class)
            .converter(event -> {
                Block placed = event.getBlockPlaced();
                Block against = event.getBlockAgainst();
                return against.getFace(placed);
            })
            .register();
    }

}
