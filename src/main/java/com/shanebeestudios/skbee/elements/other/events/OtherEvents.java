package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.BlockStateBlock;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.slot.Slot;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.shanebeestudios.skbee.api.event.EntityBlockInteractEvent;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
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
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@SuppressWarnings({"unused", "removal"})
public class OtherEvents extends SimpleEvent {

    static {
        Skript.registerEvent("Block Physical Interact Event", OtherEvents.class, EntityBlockInteractEvent.class,
                "block (interact|trample)")
            .description("Called when an entity physically interacts with a block, for example,",
                " entities trampling farmland and villagers opening doors.")
            .examples("on block trample:",
                "\tif type of event-block is farmland:",
                "\t\tcancel event")
            .since("1.5.0");

        EventValues.registerEventValue(EntityBlockInteractEvent.class, Block.class, new Getter<>() {
            @Nullable
            @Override
            public Block get(EntityBlockInteractEvent event) {
                return event.getBlock();
            }
        }, EventValues.TIME_NOW);

        // Prepare Anvil Event
        Skript.registerEvent("Anvil Prepare Event", OtherEvents.class, PrepareAnvilEvent.class, "[skbee] anvil prepare")
            .description("Called when a player attempts to combine 2 items in an anvil.",
                "'event-slot' represents the result slot, can be used to get or set.")
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

        EventValues.registerEventValue(PrepareAnvilEvent.class, Slot.class, new Getter<>() {
            @Override
            public Slot get(PrepareAnvilEvent event) {
                return new Slot() {
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
                };
            }
        }, EventValues.TIME_NOW);

        if (Util.IS_RUNNING_MC_1_21) {
            EventValues.registerEventValue(PrepareAnvilEvent.class, Player.class, new Getter<>() {
                @Override
                public Player get(PrepareAnvilEvent event) {
                    return (Player) event.getView().getPlayer();
                }
            }, EventValues.TIME_NOW);
        }

        // Player shear entity event
        Skript.registerEvent("Shear Entity", OtherEvents.class, PlayerShearEntityEvent.class, "[player] shear entity")
            .description("Called when a player shears an entity. Requires Minecraft 1.9.4+")
            .examples("on player shear entity:")
            .since("1.8.0");

        // Entity Change Block Event
        Skript.registerEvent("Entity Change Block", OtherEvents.class, EntityChangeBlockEvent.class,
                "entity change block")
            .description("Called when any Entity changes a block and a more specific event is not available.",
                "Skript does partially have this event, but this version of it opens up ALL possibilities with this event.",
                "\nevent-entity = the entity which changed the block",
                "\nevent-block = the block that changed",
                "\nevent-blockdata = the blockdata the block has changed into")
            .examples("on entity change block:",
                "\tif event-entity is a villager:",
                "\t\tif event-block is a composter:",
                "\t\t\theal event-entity")
            .since("2.5.3");

        EventValues.registerEventValue(EntityChangeBlockEvent.class, BlockData.class, new Getter<>() {
            @Override
            public @NotNull BlockData get(EntityChangeBlockEvent event) {
                return event.getBlockData();
            }
        }, EventValues.TIME_NOW);

        // Block Damage Abort Event
        if (Skript.classExists("org.bukkit.event.block.BlockDamageAbortEvent")) {
            Skript.registerEvent("Block Damage Abort", OtherEvents.class, BlockDamageAbortEvent.class,
                    "block damage abort")
                .description("Called when a player stops damaging a Block. Requires MC 1.18.x+")
                .examples("on block damage abort:",
                    "\tsend \"get back to work\"")
                .since("2.8.3");

            EventValues.registerEventValue(BlockDamageAbortEvent.class, Player.class, new Getter<>() {
                @Override
                public Player get(BlockDamageAbortEvent event) {
                    return event.getPlayer();
                }
            }, EventValues.TIME_NOW);
        }

        Skript.registerEvent("Entity Air Change", OtherEvents.class, EntityAirChangeEvent.class,
                "[entity] air change")
            .description("Called when the amount of air an entity has remaining changes.",
                "\n`event-number` = The amount of air the entity will have left (measured in ticks).",
                "\n`event-timespan` = The amount of air the entity will have left (as a time span).",
                "\n`past event-number` = The amount of air the entity had left before the event (measured in ticks).",
                "\n`past event-timespan` = The amount of air the entity had left before the event (as a time span).")
            .examples("on entity air change:",
                "\tif event-entity is a player:",
                "\t\tcancel event")
            .since("2.8.4");

        EventValues.registerEventValue(EntityAirChangeEvent.class, Number.class, new Getter<>() {
            @Override
            public Number get(EntityAirChangeEvent event) {
                if (event.getEntity() instanceof LivingEntity livingEntity) return livingEntity.getRemainingAir();
                return 0;
            }
        }, EventValues.TIME_PAST);

        EventValues.registerEventValue(EntityAirChangeEvent.class, Timespan.class, new Getter<>() {
            @Override
            public Timespan get(EntityAirChangeEvent event) {
                int ticks = 0;
                if (event.getEntity() instanceof LivingEntity livingEntity) {
                    ticks = livingEntity.getRemainingAir();
                }
                return Timespan.fromTicks(ticks);
            }
        }, EventValues.TIME_PAST);

        EventValues.registerEventValue(EntityAirChangeEvent.class, Number.class, new Getter<>() {
            @Override
            public Number get(EntityAirChangeEvent event) {
                return event.getAmount();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(EntityAirChangeEvent.class, Timespan.class, new Getter<>() {
            @Override
            public Timespan get(EntityAirChangeEvent event) {
                return Timespan.fromTicks(event.getAmount());
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(SpawnerSpawnEvent.class, Block.class, new Getter<>() {
            @Override
            public @Nullable Block get(SpawnerSpawnEvent event) {
                CreatureSpawner spawner = event.getSpawner();
                if (spawner == null) return null;
                return spawner.getBlock();
            }
        }, EventValues.TIME_NOW);

        // OTHER EVENT VALUES
        // Click Events
        EventValues.registerEventValue(PlayerInteractEvent.class, BlockFace.class, new Getter<>() {
            @Override
            public BlockFace get(PlayerInteractEvent event) {
                return event.getBlockFace();
            }
        }, EventValues.TIME_NOW);

        // Projectile Hit Event
        EventValues.registerEventValue(ProjectileHitEvent.class, BlockFace.class, new Getter<>() {
            @Override
            public @Nullable BlockFace get(ProjectileHitEvent event) {
                return event.getHitBlockFace();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(BlockPlaceEvent.class, BlockFace.class, new Getter<>() {
            @Override
            public @Nullable BlockFace get(BlockPlaceEvent event) {
                Block placed = event.getBlockPlaced();
                Block against = event.getBlockAgainst();
                return against.getFace(placed);
            }
        }, EventValues.TIME_NOW);

        // Entity Spell Cast Event
        Skript.registerEvent("Spell Cast", OtherEvents.class, EntitySpellCastEvent.class,
                "[entity] spell cast")
            .description("Called when a Spellcaster casts a spell.")
            .examples("on spell cast:",
                "\tif event-entity is an evoker:",
                "\t\tif event-spell is fangs:",
                "\t\t\tcancel event")
            .since("2.14.0");

        EventValues.registerEventValue(EntitySpellCastEvent.class, Spellcaster.Spell.class, new Getter<>() {
            @Override
            public Spellcaster.Spell get(EntitySpellCastEvent event) {
                return event.getSpell();
            }
        }, EventValues.TIME_NOW);

        // Entity Shoot Bow Event
        Skript.registerEvent("Entity Shoot Bow", OtherEvents.class, EntityShootBowEvent.class,
                "entity shoot bow")
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
            .since("2.16.0");

        EventValues.registerEventValue(EntityShootBowEvent.class, Projectile.class, new Getter<>() {
            @Override
            public @Nullable Projectile get(EntityShootBowEvent event) {
                if (event.getProjectile() instanceof Projectile projectile) return projectile;
                return null;
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(EntityShootBowEvent.class, ItemType.class, new Getter<>() {
            @Override
            public @Nullable ItemType get(EntityShootBowEvent event) {
                ItemStack consumable = event.getConsumable();
                if (consumable != null) return new ItemType(consumable);
                return null;
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(EntityShootBowEvent.class, ItemStack.class, new Getter<>() {
            @Override
            public @Nullable ItemStack get(EntityShootBowEvent event) {
                return event.getConsumable();
            }
        }, EventValues.TIME_NOW);

        // Entity Teleport Event
        Skript.registerEvent("Entity Teleport", OtherEvents.class, EntityTeleportEvent.class, "entity teleport")
            .description("Thrown when a non-player entity is teleported from one location to another.",
                "This may be as a result of natural causes (Enderman, Shulker), pathfinding (Wolf), or commands (/teleport).",
                "\n`past event-location` = Location teleported from.",
                "\n`event-location` = Location teleported to.")
            .examples("on entity teleport:",
                "\tif event-entity is an enderman:",
                "\t\tcancel event")
            .since("2.18.0");

        EventValues.registerEventValue(EntityTeleportEvent.class, Location.class, new Getter<>() {
            @Override
            public Location get(EntityTeleportEvent event) {
                return event.getFrom();
            }
        }, EventValues.TIME_PAST);

        EventValues.registerEventValue(EntityTeleportEvent.class, Location.class, new Getter<>() {
            @Override
            public Location get(EntityTeleportEvent event) {
                return event.getTo();
            }
        }, EventValues.TIME_NOW);

        // Moisture Change Event
        Skript.registerEvent("Moisture Change", OtherEvents.class, MoistureChangeEvent.class, "moisture change")
            .description("Called when the moisture level of a farmland block changes.")
            .examples("on moisture change:",
                "\tcancel event",
                "\tset event-block to farmland[moisture=7]")
            .since("3.0.0");

        EventValues.registerEventValue(MoistureChangeEvent.class, Block.class, new Getter<>() {
            @Override
            public @NotNull Block get(MoistureChangeEvent event) {
                return new BlockStateBlock(event.getNewState());
            }
        }, EventValues.TIME_FUTURE);

        // Block Explode Event
        Skript.registerEvent("Block Explode", OtherEvents.class, BlockExplodeEvent.class, "block explode")
            .description("Called when a block explodes interacting with blocks.",
                "The event isn't called if the gamerule MOB_GRIEFING is disabled as no block interaction will occur.",
                "The Block returned by this event is not necessarily the block that caused the explosion,",
                "just the block at the location where the explosion originated.",
                "\n`past event-itemtype` will return the type of the block which exploded.",
                "\n`past event-blockdata` will return the blockdata of the block which exploded.")
            .examples("")
            .since("3.2.0");

        EventValues.registerEventValue(BlockExplodeEvent.class, BlockData.class, new Getter<>() {
            @Override
            public BlockData get(BlockExplodeEvent event) {
                return event.getBlock().getBlockData();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(BlockExplodeEvent.class, BlockData.class, new Getter<>() {
            @Override
            public @NotNull BlockData get(BlockExplodeEvent event) {
                BlockState explodedBlockState = event.getExplodedBlockState();
                return explodedBlockState.getBlockData();
            }
        }, EventValues.TIME_PAST);

        EventValues.registerEventValue(BlockExplodeEvent.class, ItemType.class, new Getter<>() {
            @Override
            public ItemType get(BlockExplodeEvent event) {
                return new ItemType(event.getBlock().getType());
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(BlockExplodeEvent.class, ItemType.class, new Getter<>() {
            @Override
            public @NotNull ItemType get(BlockExplodeEvent event) {
                BlockState explodedBlockState = event.getExplodedBlockState();
                return new ItemType(explodedBlockState.getType());
            }
        }, EventValues.TIME_PAST);

        // Leash Events
        Skript.registerEvent("Player Leash", OtherEvents.class, PlayerLeashEntityEvent.class, "player leash entity")
            .description("Called immediately prior to a creature being leashed by a player.",
                "\n`event-entity` = Entity which got leashed.",
                "\n`future event-entity` = The entity the leashed entity is leashed to (could be a player or leash hitch on a fence).",
                "\n`event-player` = Player whom leashed the entity.")
            .examples("on player leash entity:",
                "\tkill event-entity")
            .since("3.2.0");

        EventValues.registerEventValue(PlayerLeashEntityEvent.class, Entity.class, new Getter<>() {
            @Override
            public Entity get(PlayerLeashEntityEvent event) {
                return event.getEntity();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(PlayerLeashEntityEvent.class, Entity.class, new Getter<>() {
            @Override
            public Entity get(PlayerLeashEntityEvent event) {
                return event.getLeashHolder();
            }
        }, EventValues.TIME_FUTURE);

        EventValues.registerEventValue(PlayerLeashEntityEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(PlayerLeashEntityEvent event) {
                return event.getPlayer();
            }
        }, EventValues.TIME_NOW);

        Skript.registerEvent("Entity Unleash", OtherEvents.class, EntityUnleashEvent.class, "entity unleash")
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
            .since("3.2.0");

        EventValues.registerEventValue(EntityUnleashEvent.class, String.class, new Getter<>() {
            @Override
            public String get(EntityUnleashEvent event) {
                return event.getReason().name().toLowerCase(Locale.ROOT);
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(EntityUnleashEvent.class, Player.class, new Getter<>() {
            @Override
            public @Nullable Player get(EntityUnleashEvent event) {
                if (event instanceof PlayerUnleashEntityEvent playerUnleashEntityEvent)
                    return playerUnleashEntityEvent.getPlayer();
                return null;
            }
        }, EventValues.TIME_NOW);

        // Entity Remove Event
        Class<? extends Event> eventClass = null;
        boolean bukkitRemoveEvent = false;
        if (Skript.classExists("org.bukkit.event.entity.EntityRemoveEvent")) {
            eventClass = EntityRemoveEvent.class;
            bukkitRemoveEvent = true;
        } else if (Skript.classExists("com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent")) {
            eventClass = EntityRemoveFromWorldEvent.class;
        }
        if (eventClass != null) {
            Skript.registerEvent("Entity Remove from World", OtherEvents.class, eventClass,
                    "entity remove[d] [from world]")
                .description("Fired any time an entity is being removed from a world for any reason.",
                    "Requires a PaperMC server or Spigot 1.20.4+ server.",
                    "`event-entityremovecause` = The reason the entity was removed (requires MC 1.20.4+).")
                .examples("on entity removed from world:",
                    "\tbroadcast \"a lonely %event-entity% left the world.\"")
                .since("2.7.2");

            if (bukkitRemoveEvent) {
                EventValues.registerEventValue(EntityRemoveEvent.class, EntityRemoveEvent.Cause.class, new Getter<>() {
                    @Override
                    public EntityRemoveEvent.Cause get(EntityRemoveEvent event) {
                        return event.getCause();
                    }
                }, EventValues.TIME_NOW);
            }
        }

        // Player Spawn Change Event
        if (Skript.classExists("org.bukkit.event.player.PlayerSpawnChangeEvent")) {
            Skript.registerEvent("Player Spawn Change", OtherEvents.class, PlayerSpawnChangeEvent.class, "player spawn change")
                .description("This event is fired when the spawn point of the player is changed.")
                .examples("on player spawn change:",
                    "\tif event-playerspawnchangereason = bed or respawn_anchor:",
                    "\t\tcancel event",
                    "\t\tsend \"Nope... sorry!\"")
                .since("3.4.0");

            EventValues.registerEventValue(PlayerSpawnChangeEvent.class, PlayerSpawnChangeEvent.Cause.class, new Getter<>() {
                @Override
                public PlayerSpawnChangeEvent.Cause get(PlayerSpawnChangeEvent event) {
                    return event.getCause();
                }
            }, EventValues.TIME_NOW);

            EventValues.registerEventValue(PlayerSpawnChangeEvent.class, Location.class, new Getter<>() {
                @SuppressWarnings("deprecation")
                @Override
                public @Nullable Location get(PlayerSpawnChangeEvent event) {
                    return event.getPlayer().getBedSpawnLocation();
                }
            }, EventValues.TIME_NOW);

            EventValues.registerEventValue(PlayerSpawnChangeEvent.class, Location.class, new Getter<>() {
                @Override
                public @Nullable Location get(PlayerSpawnChangeEvent event) {
                    return event.getNewSpawn();
                }
            }, EventValues.TIME_FUTURE);
        }

        if (Skript.classExists("org.bukkit.event.command.UnknownCommandEvent")) {
            Skript.registerEvent("Unknown Command", OtherEvents.class, UnknownCommandEvent.class, "unknown command")
                .description("This event is fired when a player executes a command that is not defined.",
                    "`event-string` = The command that was sent.",
                    "`event-sender/player` = Who sent the command.")
                .examples("")
                .since("3.10.0");

            EventValues.registerEventValue(UnknownCommandEvent.class, String.class, UnknownCommandEvent::getCommandLine);
            EventValues.registerEventValue(UnknownCommandEvent.class, CommandSender.class, UnknownCommandEvent::getSender);
        }
    }

}
