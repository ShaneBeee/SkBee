package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventConverter;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.BlockStateBlock;
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
import com.shanebeestudios.skbee.api.event.EntityBlockInteractEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converter;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SuppressWarnings({"UnstableApiUsage", "removal"})
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

        reg.newEventValue(BlockDamageAbortEvent.class, Player.class, BlockDamageAbortEvent::getPlayer)
            .register();

        // Block Explode Event
        reg.newEvent(OtherEvents.class, BlockExplodeEvent.class, "block explode")
            .name("Block Explode")
            .description("Called when a block explodes interacting with blocks.",
                "The event isn't called if the gamerule MOB_GRIEFING is disabled as no block interaction will occur.",
                "The Block returned by this event is not necessarily the block that caused the explosion,",
                "just the block at the location where the explosion originated.",
                "`event-blocks` = The blocks which exploded (can be set).",
                "`past event-itemtype` will return the type of the block which exploded.",
                "`past event-blockdata` will return the blockdata of the block which exploded.")
            .examples("")
            .since("3.2.0")
            .register();

        reg.registerEventValue(BlockExplodeEvent.class, BlockData.class, event -> event.getBlock().getBlockData(), EventValues.TIME_NOW);
        reg.registerEventValue(BlockExplodeEvent.class, BlockData.class, new Converter<>() {
            @Override
            public @NotNull BlockData convert(BlockExplodeEvent event) {
                BlockState explodedBlockState = event.getExplodedBlockState();
                return explodedBlockState.getBlockData();
            }
        }, EventValues.TIME_PAST);
        reg.registerEventValue(BlockExplodeEvent.class, ItemType.class, event -> new ItemType(event.getBlock().getType()), EventValues.TIME_NOW);
        reg.registerEventValue(BlockExplodeEvent.class, ItemType.class, new Converter<>() {
            @Override
            public @NotNull ItemType convert(BlockExplodeEvent event) {
                BlockState explodedBlockState = event.getExplodedBlockState();
                return new ItemType(explodedBlockState.getType());
            }
        }, EventValues.TIME_PAST);
        reg.registerEventValue(BlockExplodeEvent.class, Block[].class, new EventConverter<>() {
            @Override
            public void set(BlockExplodeEvent event, @Nullable Block[] value) {
                event.blockList().clear();
                if (value != null && value.length > 0) {
                    event.blockList().addAll(List.of(value));
                }
            }

            @Override
            public @Nullable Block[] convert(BlockExplodeEvent from) {
                return from.blockList().toArray(new Block[0]);
            }
        });

        // Moisture Change Event
        reg.newEvent(OtherEvents.class, MoistureChangeEvent.class, "moisture change")
            .name("Moisture Change")
            .description("Called when the moisture level of a farmland block changes.")
            .examples("on moisture change:",
                "\tcancel event",
                "\tset event-block to farmland[moisture=7]")
            .since("3.0.0")
            .register();

        reg.registerEventValue(MoistureChangeEvent.class, Block.class, new Converter<>() {
            @Override
            public @NotNull Block convert(MoistureChangeEvent event) {
                return new BlockStateBlock(event.getNewState());
            }
        }, EventValues.TIME_FUTURE);
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

        // Entity Change Air Event
        reg.newEvent(OtherEvents.class, EntityAirChangeEvent.class,
                "[entity] air change")
            .name("Entity Air Change")
            .description("Called when the amount of air an entity has remaining changes.",
                "\n`event-number` = The amount of air the entity will have left (measured in ticks) (can be set).",
                "\n`event-timespan` = The amount of air the entity will have left (as a time span) (can be set).",
                "\n`past event-number` = The amount of air the entity had left before the event (measured in ticks).",
                "\n`past event-timespan` = The amount of air the entity had left before the event (as a time span).")
            .examples("on entity air change:",
                "\tif event-entity is a player:",
                "\t\tcancel event")
            .since("2.8.4")
            .register();

        reg.registerEventValue(EntityAirChangeEvent.class, Number.class, event -> {
            if (event.getEntity() instanceof LivingEntity livingEntity) return livingEntity.getRemainingAir();
            return 0;
        }, EventValues.TIME_PAST);
        reg.registerEventValue(EntityAirChangeEvent.class, Timespan.class, event -> {
            int ticks = 0;
            if (event.getEntity() instanceof LivingEntity livingEntity) {
                ticks = livingEntity.getRemainingAir();
            }
            return new Timespan(TimePeriod.TICK, Math.max(ticks, 0));
        }, EventValues.TIME_PAST);
        reg.registerEventValue(EntityAirChangeEvent.class, Number.class, new EventConverter<>() {
            @Override
            public void set(EntityAirChangeEvent event, @Nullable Number value) {
                int amount = value != null ? value.intValue() : 0;
                event.setAmount(amount);
            }

            @Override
            public Number convert(EntityAirChangeEvent event) {
                return event.getAmount();
            }
        }, EventValues.TIME_NOW);
        reg.registerEventValue(EntityAirChangeEvent.class, Timespan.class, new EventConverter<>() {
            @Override
            public void set(EntityAirChangeEvent event, @Nullable Timespan value) {
                int amount = value != null ? (int) value.getAs(TimePeriod.TICK) : 0;
                event.setAmount(amount);
            }

            @Override
            public Timespan convert(EntityAirChangeEvent event) {
                return new Timespan(TimePeriod.TICK, Math.max(event.getAmount(), 0));
            }
        }, EventValues.TIME_NOW);

        // Entity Block Interact Event
        reg.newEvent(OtherEvents.class, EntityBlockInteractEvent.class,
                "block (interact|trample)")
            .name("Block Physical Interact Event")
            .description("Called when an entity physically interacts with a block, for example,",
                " entities trampling farmland and villagers opening doors.")
            .examples("on block trample:",
                "\tif type of event-block is farmland:",
                "\t\tcancel event")
            .since("1.5.0")
            .register();

        reg.registerEventValue(EntityBlockInteractEvent.class, Block.class, EntityBlockInteractEvent::getBlock, EventValues.TIME_NOW);

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

        if (!Util.IS_RUNNING_SKRIPT_2_15) {
            reg.registerEventValue(EntityChangeBlockEvent.class, BlockData.class, new Converter<>() {
                @Override
                public @NotNull BlockData convert(EntityChangeBlockEvent event) {
                    return event.getBlockData();
                }
            }, EventValues.TIME_NOW);
        }

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

        reg.registerEventValue(EntityInsideBlockEvent.class, Block.class, EntityInsideBlockEvent::getBlock, EventValues.TIME_NOW);

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

        reg.registerEventValue(EntityPathfindEvent.class, Location.class, EntityPathfindEvent::getLoc, EventValues.TIME_NOW);

        // Entity Pose Change Event
        reg.newEvent(OtherEvents.class, EntityPoseChangeEvent.class,
                "entity pose change", "entity changed pose")
            .name("Entity Pose Change")
            .description("Called when an entity changes their pose.",
                "`event-pose` = The new pose once this event finishes.",
                "`past event-pose` = The previous pose of the entity.")
            .examples("on entity pose change:",
                "\tbroadcast \"%event-entity% changed their pose from %past event-pose% to %event-pose%\"")
            .since("INSERT VERSION")
            .register();

        reg.registerEventValue(EntityPoseChangeEvent.class, Pose.class,
            from -> from.getEntity().getPose(), EventValues.TIME_PAST);
        reg.registerEventValue(EntityPoseChangeEvent.class, Pose.class,
            EntityPoseChangeEvent::getPose, EventValues.TIME_NOW);

        // Entity Remove Event
        reg.newEvent(OtherEvents.class, EntityRemoveEvent.class,
                "entity remove[d] [from world]")
            .name("Entity Remove from World")
            .description("Fired any time an entity is being removed from a world for any reason.",
                "Requires a PaperMC server or Spigot 1.20.4+ server.",
                "`event-entityremovecause` = The reason the entity was removed (requires MC 1.20.4+).")
            .examples("on entity removed from world:",
                "\tbroadcast \"a lonely %event-entity% left the world.\"")
            .since("2.7.2")
            .register();

        reg.registerEventValue(EntityRemoveEvent.class, EntityRemoveEvent.Cause.class, EntityRemoveEvent::getCause, EventValues.TIME_NOW);

        // Entity Shoot Bow Event
        reg.newEvent(OtherEvents.class, EntityShootBowEvent.class,
                "entity shoot bow")
            .name("Entity Shoot Bow")
            .description("Called when a LivingEntity shoots a bow/crossbow firing an arrow.",
                "`event-entity` = Entity which shot the bow.",
                "`event-projectile` = The projectile which was shot.",
                "`event-item[type]` = The item which will be consumed from the entity's inventory (if any).")
            .examples("on entity shoot bow:",
                "\tif name of shot bow != \"Mr Bow\":",
                "\t\tcancel event",
                "on entity shoot bow:",
                "\tif gamemode of player = survival:",
                "\t\tgive player 1 of event-item")
            .since("2.16.0")
            .register();

        reg.registerEventValue(EntityShootBowEvent.class, Projectile.class, event -> {
            if (event.getProjectile() instanceof Projectile projectile) return projectile;
            return null;
        }, EventValues.TIME_NOW);
        reg.registerEventValue(EntityShootBowEvent.class, ItemType.class, event -> {
            ItemStack consumable = event.getConsumable();
            if (consumable != null) return new ItemType(consumable);
            return null;
        }, EventValues.TIME_NOW);

        if (!Util.IS_RUNNING_SKRIPT_2_15) {
            reg.registerEventValue(EntityShootBowEvent.class, ItemStack.class,
                EntityShootBowEvent::getConsumable, EventValues.TIME_NOW);
        }

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

        reg.registerEventValue(EntitySpellCastEvent.class, Spellcaster.Spell.class, EntitySpellCastEvent::getSpell, EventValues.TIME_NOW);

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

        reg.registerEventValue(EntityUnleashEvent.class, String.class, event -> event.getReason().name().toLowerCase(Locale.ROOT), EventValues.TIME_NOW);
        reg.registerEventValue(EntityUnleashEvent.class, Player.class, event -> {
            if (event instanceof PlayerUnleashEntityEvent playerUnleashEntityEvent)
                return playerUnleashEntityEvent.getPlayer();
            return null;
        }, EventValues.TIME_NOW);

        // Entity Zap Event
        reg.newEvent(OtherEvents.class, EntityZapEvent.class, "entity (zap|struck by lightning)")
            .name("Entity Zap")
            .description("Fired when lightning strikes an entity. Requires Paper 1.10.2+")
            .examples("on entity zap:",
                "\tif event-entity is a pig:",
                "\t\tspawn 3 zombie pigmen at event-location")
            .since("1.8.0")
            .register();
        reg.registerEventValue(EntityZapEvent.class, Location.class, e -> e.getEntity().getLocation(), EventValues.TIME_NOW);

        // Experience Orb Merge Event
        reg.newEvent(OtherEvents.class, ExperienceOrbMergeEvent.class, "(experience|[e]xp) orb merge")
            .name("Experience Orb Merge")
            .description("Fired anytime the server is about to merge 2 experience orbs into one. Requires Paper 1.12.2+")
            .examples("on xp merge:",
                "\tcancel event")
            .since("1.8.0")
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
    }

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

            reg.registerEventValue(UncheckedSignChangeEvent.class, ComponentWrapper[].class, from -> {
                ComponentWrapper[] comps = new ComponentWrapper[4];
                for (int i = 0; i < 4; i++) {
                    comps[i] = ComponentWrapper.fromComponent(from.lines().get(i));
                }
                return comps;
            }, EventValues.TIME_NOW);
            reg.registerEventValue(UncheckedSignChangeEvent.class, Location.class, from -> {
                BlockPosition editedBlockPosition = from.getEditedBlockPosition();
                return editedBlockPosition.toLocation(from.getPlayer().getWorld());
            }, EventValues.TIME_NOW);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void playerEvents(Registration reg) {
        // PlayerAttemptPickupItemEvent
        reg.newEvent(OtherEvents.class, PlayerAttemptPickupItemEvent.class, "player attempt item pickup")
            .name("Player Attempt Item Pickup")
            .description("Called when a player attempts to pick an item up from the ground. Requires PaperMC.",
                "`event-number` = Represents the amount that will remain on the ground, if any.",
                "`past event-number` = Represents the item amount of the dropped item before pickup.",
                "`event-dropped item` = Represents the dropped item entity that is attempting to pickup.")
            .examples("on player attempt item pickup:",
                "\tif event-number > 0:",
                "\t\twait 1 tick",
                "\t\tadd (item of event-dropped item) to enderchest of player",
                "\t\tkill event-dropped item")
            .since("3.5.0")
            .register();

        reg.registerEventValue(PlayerAttemptPickupItemEvent.class, Number.class, PlayerAttemptPickupItemEvent::getRemaining, EventValues.TIME_NOW);
        reg.registerEventValue(PlayerAttemptPickupItemEvent.class, Number.class, event -> event.getItem().getItemStack().getAmount(), EventValues.TIME_PAST);
        reg.registerEventValue(PlayerAttemptPickupItemEvent.class, Item.class, PlayerAttemptPickupItemEvent::getItem, EventValues.TIME_NOW);

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

        reg.registerEventValue(PrepareAnvilEvent.class, Slot.class, event -> new Slot() {
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
        }, EventValues.TIME_NOW);
        reg.registerEventValue(PrepareAnvilEvent.class, Player.class, event -> (Player) event.getView().getPlayer(), EventValues.TIME_NOW);

        // Player Chunk Load Event
        reg.newEvent(OtherEvents.class, PlayerChunkLoadEvent.class,
                "player chunk (send|load)")
            .name("Player Chunk Load")
            .description("Is called when a Player receives a Chunk.",
                "Can for example be used for spawning a fake entity when the player receives a chunk. ",
                "Should only be used for packet/clientside related stuff. Not intended for modifying server side state.",
                "\nRequires a PaperMC server.")
            .examples("on player chunk send:",
                "\tloop all blocks in event-chunk:",
                "\t\tif loop-block is diamond ore:",
                "\t\t\tmake player see loop-block as stone")
            .since("2.6.1")
            .register();

        reg.registerEventValue(PlayerChunkLoadEvent.class, Player.class, PlayerChunkLoadEvent::getPlayer, EventValues.TIME_NOW);

        // Player Chunk Unload Event
        reg.newEvent(OtherEvents.class, PlayerChunkUnloadEvent.class,
                "player chunk unload")
            .name("Player Chunk Unload")
            .description("Is called when a Player receives a chunk unload packet.",
                "Should only be used for packet/clientside related stuff. Not intended for modifying server side.",
                "\nRequires a PaperMC server.")
            .examples("on player chunk unload:",
                "\tsend \"looks like you lost your chunk cowboy!\" to player")
            .since("2.6.1")
            .register();

        reg.registerEventValue(PlayerChunkUnloadEvent.class, Player.class, PlayerChunkUnloadEvent::getPlayer, EventValues.TIME_NOW);


        // Player Custom Click Event
        if (Skript.classExists("io.papermc.paper.event.player.PlayerCustomClickEvent")) {
            reg.newEvent(OtherEvents.class, PlayerCustomClickEvent.class,
                    "[player] custom (click|payload)")
                .name("Player Custom Click Event")
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
                .since("3.13.0")
                .register();

            reg.registerEventValue(PlayerCustomClickEvent.class, UUID.class, from -> {
                PlayerCommonConnection connection = from.getCommonConnection();
                if (connection instanceof PlayerGameConnection gameConnection) {
                    return gameConnection.getPlayer().getUniqueId();
                } else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                    return configConnection.getProfile().getId();
                }
                return null;
            });
            reg.registerEventValue(PlayerCustomClickEvent.class, OfflinePlayer.class, event -> {
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
            reg.registerEventValue(PlayerCustomClickEvent.class, Audience.class, event -> {
                PlayerCommonConnection connection = event.getCommonConnection();
                if (connection instanceof PlayerGameConnection gameConnection)
                    return gameConnection.getPlayer();
                else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                    return configConnection.getAudience();
                }
                return null;
            });
            reg.registerEventValue(PlayerCustomClickEvent.class, String.class, event -> {
                PlayerCommonConnection connection = event.getCommonConnection();
                if (connection instanceof PlayerGameConnection gameConnection)
                    return gameConnection.getPlayer().getName();
                else if (HAS_CONFIG && connection instanceof PlayerConfigurationConnection configConnection) {
                    return configConnection.getProfile().getName();
                }
                return null;
            });
            reg.registerEventValue(PlayerCustomClickEvent.class, NBTCompound.class, event -> {
                BinaryTagHolder tag = event.getTag();
                if (tag == null) return null;

                return (NBTCompound) NBT.parseNBT(tag.string());
            });
            reg.registerEventValue(PlayerCustomClickEvent.class, NamespacedKey.class, event -> NamespacedKey.fromString(event.getIdentifier().asString()));
            reg.registerEventValue(PlayerCustomClickEvent.class, PlayerConnection.class, PlayerCustomClickEvent::getCommonConnection);
        }

        // Player Elytra Boost Event
        reg.newEvent(OtherEvents.class, PlayerElytraBoostEvent.class, "[player] elytra boost")
            .name("Player Elytra Boost")
            .description("Fired when a player boosts elytra flight with a firework. Requires Paper 1.13.2+")
            .examples("on elytra boost:",
                "\tpush player forward at speed 50")
            .since("1.8.0")
            .register();
        reg.registerEventValue(PlayerElytraBoostEvent.class, ItemType.class, e -> new ItemType(e.getItemStack()), EventValues.TIME_NOW);

        // PlayerFailMoveEvent
        reg.newEvent(OtherEvents.class, PlayerFailMoveEvent.class, "player fail move")
            .name("Player Fail Move")
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
            .since("3.11.0")
            .register();

        reg.registerEventValue(PlayerFailMoveEvent.class, PlayerFailMoveEvent.FailReason.class, PlayerFailMoveEvent::getFailReason);
        reg.registerEventValue(PlayerFailMoveEvent.class, Location.class, PlayerFailMoveEvent::getFrom, EventValues.TIME_NOW);
        reg.registerEventValue(PlayerFailMoveEvent.class, Location.class, PlayerFailMoveEvent::getTo, EventValues.TIME_FUTURE);
        reg.registerEventValue(PlayerFailMoveEvent.class, Boolean.class, new EventConverter<>() {
            @Override
            public void set(PlayerFailMoveEvent event, @Nullable Boolean allowed) {
                event.setAllowed(Boolean.TRUE.equals(allowed));
            }

            @Override
            public Boolean convert(PlayerFailMoveEvent event) {
                return event.isAllowed();
            }
        }, EventValues.TIME_NOW);
        reg.registerEventValue(PlayerFailMoveEvent.class, Boolean.class, new EventConverter<>() {
            @Override
            public void set(PlayerFailMoveEvent event, @Nullable Boolean allowed) {
                event.setLogWarning(Boolean.TRUE.equals(allowed));
            }

            @Override
            public Boolean convert(PlayerFailMoveEvent event) {
                return event.getLogWarning();
            }
        }, EventValues.TIME_FUTURE);

        // Player Leash Entity Event
        reg.newEvent(OtherEvents.class, PlayerLeashEntityEvent.class, "player leash entity")
            .name("Player Leash")
            .description("Called immediately prior to a creature being leashed by a player.",
                "\n`event-entity` = Entity which got leashed.",
                "\n`future event-entity` = The entity the leashed entity is leashed to (could be a player or leash hitch on a fence).",
                "\n`event-player` = Player whom leashed the entity.")
            .examples("on player leash entity:",
                "\tkill event-entity")
            .since("3.2.0")
            .register();

        if (!Util.IS_RUNNING_SKRIPT_2_15) {
            reg.registerEventValue(PlayerLeashEntityEvent.class, Entity.class, PlayerLeashEntityEvent::getEntity, EventValues.TIME_NOW);
            reg.registerEventValue(PlayerLeashEntityEvent.class, Player.class, PlayerLeashEntityEvent::getPlayer, EventValues.TIME_NOW);
        }
        reg.registerEventValue(PlayerLeashEntityEvent.class, Entity.class, PlayerLeashEntityEvent::getLeashHolder, EventValues.TIME_FUTURE);

        // Player Pickup XP Event
        reg.newEvent(OtherEvents.class, PlayerPickupExperienceEvent.class,
                "player pickup (experience|xp) [orb]")
            .name("Player Pickup Experience Orb")
            .description("Fired when a player is attempting to pick up an experience orb. Requires Paper 1.12.2+",
                "\n`event-experience` represents the experience picked up (This is Skript's version of XP) (can be set).",
                "\n`event-number` represents the experience picked up as a number (can be set).",
                "\n`event-entity` represents the experience orb entity.")
            .examples("on player pickup xp:",
                "\tadd 10 to level of player")
            .since("1.8.0")
            .register();

        reg.registerEventValue(PlayerPickupExperienceEvent.class, Experience.class, new EventConverter<>() {
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
        reg.registerEventValue(PlayerPickupExperienceEvent.class, Number.class, new EventConverter<>() {
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
        reg.registerEventValue(PlayerPickupExperienceEvent.class, Entity.class, PlayerPickupExperienceEvent::getExperienceOrb, EventValues.TIME_NOW);

        // Player Recipe Book Click Event
        reg.newEvent(OtherEvents.class, PlayerRecipeBookClickEvent.class, "[player] recipe book click")
            .name("Recipe Book Click Event")
            .description("Called when the player clicks on a recipe in their recipe book. Requires Paper 1.15+")
            .examples("on recipe book click:",
                "\tif event-string = \"minecraft:diamond_sword\":",
                "\t\tcancel event")
            .since("1.5.0")
            .register();

        reg.registerEventValue(PlayerRecipeBookClickEvent.class, String.class, event -> event.getRecipe().toString(), EventValues.TIME_NOW);

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

        reg.registerEventValue(PlayerSetSpawnEvent.class, PlayerSetSpawnEvent.Cause.class, PlayerSetSpawnEvent::getCause, EventValues.TIME_NOW);
        reg.registerEventValue(PlayerSetSpawnEvent.class, Location.class, event -> event.getPlayer().getRespawnLocation(), EventValues.TIME_NOW);
        reg.registerEventValue(PlayerSetSpawnEvent.class, Location.class, PlayerSetSpawnEvent::getLocation, EventValues.TIME_FUTURE);

        // Player shear entity event
        reg.newEvent(OtherEvents.class, PlayerShearEntityEvent.class, "[player] shear entity")
            .name("Shear Entity")
            .description("Called when a player shears an entity. Requires Minecraft 1.9.4+")
            .examples("on player shear entity:")
            .since("1.8.0")
            .register();

        // Player Stop Using Item Event
        reg.newEvent(OtherEvents.class, PlayerStopUsingItemEvent.class, "[player] stop using item")
            .name("Player Stop Using Item")
            .description("Called when the server detects a player stopping using an item.",
                "Examples of this are letting go of the interact button when holding a bow, an edible item, or a spyglass.",
                "event-number is the number of ticks the item was held for. Requires Paper 1.18+.")
            .examples("on player stop using item:",
                "\tif event-item is a spyglass:",
                "\t\tkill player")
            .since("1.17.0")
            .register();

        if (!Util.IS_RUNNING_SKRIPT_2_15) {
            reg.registerEventValue(PlayerStopUsingItemEvent.class, ItemType.class, event -> new ItemType(event.getItem()), EventValues.TIME_NOW);
        }
        reg.registerEventValue(PlayerStopUsingItemEvent.class, ItemStack.class, PlayerStopUsingItemEvent::getItem);
        reg.registerEventValue(PlayerStopUsingItemEvent.class, Number.class, PlayerStopUsingItemEvent::getTicksHeldFor, EventValues.TIME_NOW);

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

        reg.registerEventValue(PlayerTrackEntityEvent.class, Entity.class, PlayerTrackEntityEvent::getEntity, EventValues.TIME_NOW);

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
                "There may be time left after this event is called, and before the next tick starts.",
                "`event-numbers` = Represents different numbers in this event, in this order:",
                "- Current tick number (starts from 0 when the server starts and counts up).",
                "- Tick duration (in milliseconds) (How long the tick took to tick).",
                "- Time remaining (in milliseconds) (How long til the next tick executes).",
                "- Time remaining (in nanoseconds) (How long til the next tick executes).")
            .examples("")
            .since("3.10.0")
            .register();

        reg.newEvent(OtherEvents.class, ServerTickStartEvent.class, "server tick start")
            .name("Tick Start Event")
            .description("Called each time the server starts its main tick loop.",
                "`event-number` = The current tick number.")
            .examples("")
            .since("3.10.0")
            .register();

        reg.registerEventValue(ServerTickStartEvent.class, Integer.class, ServerTickStartEvent::getTickNumber);
        reg.registerEventValue(ServerTickEndEvent.class, Number[].class,
            from -> new Number[]{
                from.getTickNumber(),
                from.getTickDuration(),
                from.getTimeRemaining() / 1_000_000,
                from.getTimeRemaining()
            });

        // Unknown Command Event
        reg.newEvent(OtherEvents.class, UnknownCommandEvent.class, "unknown command")
            .name("Unknown Command")
            .description("This event is fired when a player executes a command that is not defined.",
                "`event-string` = The command that was sent.",
                "`event-sender/player` = Who sent the command.")
            .examples("")
            .since("3.10.0")
            .register();

        reg.registerEventValue(UnknownCommandEvent.class, String.class, UnknownCommandEvent::getCommandLine);
        reg.registerEventValue(UnknownCommandEvent.class, CommandSender.class, UnknownCommandEvent::getSender);

    }

    private static void otherEventValues(Registration reg) {
        if (!Util.IS_RUNNING_SKRIPT_2_15) {
            reg.registerEventValue(SpawnerSpawnEvent.class, Block.class, event -> {
                CreatureSpawner spawner = event.getSpawner();
                if (spawner == null) return null;
                return spawner.getBlock();
            }, EventValues.TIME_NOW);
        }

        // Click Events
        reg.registerEventValue(PlayerInteractEvent.class, BlockFace.class, PlayerInteractEvent::getBlockFace, EventValues.TIME_NOW);

        // Projectile Hit Event
        reg.registerEventValue(ProjectileHitEvent.class, BlockFace.class, ProjectileHitEvent::getHitBlockFace, EventValues.TIME_NOW);

        reg.registerEventValue(BlockPlaceEvent.class, BlockFace.class, event -> {
            Block placed = event.getBlockPlaced();
            Block against = event.getBlockAgainst();
            return against.getFace(placed);
        }, EventValues.TIME_NOW);
    }

}
