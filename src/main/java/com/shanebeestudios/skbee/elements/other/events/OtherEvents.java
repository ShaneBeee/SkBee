package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventConverter;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.BlockStateBlock;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.skript.util.slot.Slot;
import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.shanebeestudios.skbee.api.event.EntityBlockInteractEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converter;

import java.util.Locale;

public class OtherEvents extends SimpleEvent {

    public static void register(Registration reg) {
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

        EventValues.registerEventValue(EntityBlockInteractEvent.class, Block.class, EntityBlockInteractEvent::getBlock, EventValues.TIME_NOW);

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
            .since("1.11.0");

        EventValues.registerEventValue(PrepareAnvilEvent.class, Slot.class, event -> new Slot() {
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
        EventValues.registerEventValue(PrepareAnvilEvent.class, Player.class, event -> (Player) event.getView().getPlayer(), EventValues.TIME_NOW);

        // Player shear entity event
        reg.newEvent(OtherEvents.class, PlayerShearEntityEvent.class, "[player] shear entity")
            .name("Shear Entity")
            .description("Called when a player shears an entity. Requires Minecraft 1.9.4+")
            .examples("on player shear entity:")
            .since("1.8.0")
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

        EventValues.registerEventValue(EntityChangeBlockEvent.class, BlockData.class, new Converter<>() {
            @Override
            public @NotNull BlockData convert(EntityChangeBlockEvent event) {
                return event.getBlockData();
            }
        }, EventValues.TIME_NOW);

        // Block Damage Abort Event
        reg.newEvent(OtherEvents.class, BlockDamageAbortEvent.class,
                "block damage abort")
            .name("Block Damage Abort")
            .description("Called when a player stops damaging a Block. Requires MC 1.18.x+")
            .examples("on block damage abort:",
                "\tsend \"get back to work\"")
            .since("2.8.3")
            .register();

        EventValues.registerEventValue(BlockDamageAbortEvent.class, Player.class, BlockDamageAbortEvent::getPlayer, EventValues.TIME_NOW);

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

        EventValues.registerEventValue(EntityAirChangeEvent.class, Number.class, event -> {
            if (event.getEntity() instanceof LivingEntity livingEntity) return livingEntity.getRemainingAir();
            return 0;
        }, EventValues.TIME_PAST);
        EventValues.registerEventValue(EntityAirChangeEvent.class, Timespan.class, event -> {
            int ticks = 0;
            if (event.getEntity() instanceof LivingEntity livingEntity) {
                ticks = livingEntity.getRemainingAir();
            }
            return Timespan.fromTicks(ticks);
        }, EventValues.TIME_PAST);
        EventValues.registerEventValue(EntityAirChangeEvent.class, Number.class, new EventConverter<>() {
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
        EventValues.registerEventValue(EntityAirChangeEvent.class, Timespan.class, new EventConverter<>() {
            @Override
            public void set(EntityAirChangeEvent event, @Nullable Timespan value) {
                int amount = value != null ? (int) value.getAs(TimePeriod.TICK) : 0;
                event.setAmount(amount);
            }

            @Override
            public Timespan convert(EntityAirChangeEvent event) {
                return new Timespan(TimePeriod.TICK, event.getAmount());
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(SpawnerSpawnEvent.class, Block.class, event -> {
            CreatureSpawner spawner = event.getSpawner();
            if (spawner == null) return null;
            return spawner.getBlock();
        }, EventValues.TIME_NOW);

        // OTHER EVENT VALUES
        // Click Events
        EventValues.registerEventValue(PlayerInteractEvent.class, BlockFace.class, PlayerInteractEvent::getBlockFace, EventValues.TIME_NOW);

        // Projectile Hit Event
        EventValues.registerEventValue(ProjectileHitEvent.class, BlockFace.class, ProjectileHitEvent::getHitBlockFace, EventValues.TIME_NOW);

        EventValues.registerEventValue(BlockPlaceEvent.class, BlockFace.class, event -> {
            Block placed = event.getBlockPlaced();
            Block against = event.getBlockAgainst();
            return against.getFace(placed);
        }, EventValues.TIME_NOW);

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

        EventValues.registerEventValue(EntitySpellCastEvent.class, Spellcaster.Spell.class, EntitySpellCastEvent::getSpell, EventValues.TIME_NOW);

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

        EventValues.registerEventValue(EntityShootBowEvent.class, Projectile.class, event -> {
            if (event.getProjectile() instanceof Projectile projectile) return projectile;
            return null;
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(EntityShootBowEvent.class, ItemType.class, event -> {
            ItemStack consumable = event.getConsumable();
            if (consumable != null) return new ItemType(consumable);
            return null;
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(EntityShootBowEvent.class, ItemStack.class, EntityShootBowEvent::getConsumable, EventValues.TIME_NOW);

        // Moisture Change Event
        reg.newEvent(OtherEvents.class, MoistureChangeEvent.class, "moisture change")
            .name("Moisture Change")
            .description("Called when the moisture level of a farmland block changes.")
            .examples("on moisture change:",
                "\tcancel event",
                "\tset event-block to farmland[moisture=7]")
            .since("3.0.0")
            .register();

        EventValues.registerEventValue(MoistureChangeEvent.class, Block.class, new Converter<>() {
            @Override
            public @NotNull Block convert(MoistureChangeEvent event) {
                return new BlockStateBlock(event.getNewState());
            }
        }, EventValues.TIME_FUTURE);

        // Block Explode Event
        reg.newEvent(OtherEvents.class, BlockExplodeEvent.class, "block explode")
            .name("Block Explode")
            .description("Called when a block explodes interacting with blocks.",
                "The event isn't called if the gamerule MOB_GRIEFING is disabled as no block interaction will occur.",
                "The Block returned by this event is not necessarily the block that caused the explosion,",
                "just the block at the location where the explosion originated.",
                "\n`past event-itemtype` will return the type of the block which exploded.",
                "\n`past event-blockdata` will return the blockdata of the block which exploded.")
            .examples("")
            .since("3.2.0")
            .register();

        EventValues.registerEventValue(BlockExplodeEvent.class, BlockData.class, event -> event.getBlock().getBlockData(), EventValues.TIME_NOW);
        EventValues.registerEventValue(BlockExplodeEvent.class, BlockData.class, new Converter<>() {
            @Override
            public @NotNull BlockData convert(BlockExplodeEvent event) {
                BlockState explodedBlockState = event.getExplodedBlockState();
                return explodedBlockState.getBlockData();
            }
        }, EventValues.TIME_PAST);
        EventValues.registerEventValue(BlockExplodeEvent.class, ItemType.class, event -> new ItemType(event.getBlock().getType()), EventValues.TIME_NOW);
        EventValues.registerEventValue(BlockExplodeEvent.class, ItemType.class, new Converter<>() {
            @Override
            public @NotNull ItemType convert(BlockExplodeEvent event) {
                BlockState explodedBlockState = event.getExplodedBlockState();
                return new ItemType(explodedBlockState.getType());
            }
        }, EventValues.TIME_PAST);

        // Leash Events
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

        EventValues.registerEventValue(PlayerLeashEntityEvent.class, Entity.class, PlayerLeashEntityEvent::getEntity, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerLeashEntityEvent.class, Entity.class, PlayerLeashEntityEvent::getLeashHolder, EventValues.TIME_FUTURE);
        EventValues.registerEventValue(PlayerLeashEntityEvent.class, Player.class, PlayerLeashEntityEvent::getPlayer, EventValues.TIME_NOW);

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

        EventValues.registerEventValue(EntityUnleashEvent.class, String.class, event -> event.getReason().name().toLowerCase(Locale.ROOT), EventValues.TIME_NOW);
        EventValues.registerEventValue(EntityUnleashEvent.class, Player.class, event -> {
            if (event instanceof PlayerUnleashEntityEvent playerUnleashEntityEvent)
                return playerUnleashEntityEvent.getPlayer();
            return null;
        }, EventValues.TIME_NOW);

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

        EventValues.registerEventValue(EntityRemoveEvent.class, EntityRemoveEvent.Cause.class, EntityRemoveEvent::getCause, EventValues.TIME_NOW);

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

        EventValues.registerEventValue(PlayerSetSpawnEvent.class, PlayerSetSpawnEvent.Cause.class, PlayerSetSpawnEvent::getCause, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerSetSpawnEvent.class, Location.class, event -> event.getPlayer().getRespawnLocation(), EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerSetSpawnEvent.class, Location.class, PlayerSetSpawnEvent::getLocation, EventValues.TIME_FUTURE);

        // Unknown Command Event
        reg.newEvent(OtherEvents.class, UnknownCommandEvent.class, "unknown command")
            .name("Unknown Command")
            .description("This event is fired when a player executes a command that is not defined.",
                "`event-string` = The command that was sent.",
                "`event-sender/player` = Who sent the command.")
            .examples("")
            .since("3.10.0")
            .register();

        EventValues.registerEventValue(UnknownCommandEvent.class, String.class, UnknownCommandEvent::getCommandLine);
        EventValues.registerEventValue(UnknownCommandEvent.class, CommandSender.class, UnknownCommandEvent::getSender);
    }

}
