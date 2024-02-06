package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.BlockStateBlock;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.slot.Slot;
import com.shanebeestudios.skbee.api.event.EntityBlockInteractEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.Event;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@SuppressWarnings("unused")
public class OtherEvents {

    static {
        Skript.registerEvent("Block Physical Interact Event", SimpleEvent.class, EntityBlockInteractEvent.class,
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
        }, 0);

        // Prepare Anvil Event
        Skript.registerEvent("Anvil Prepare Event", SimpleEvent.class, PrepareAnvilEvent.class, "[skbee] anvil prepare")
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
        }, 0);

        EventValues.registerEventValue(PrepareAnvilEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(PrepareAnvilEvent event) {
                return (Player) event.getView().getPlayer();
            }
        }, 0);

        // Player shear entity event
        Skript.registerEvent("Shear Entity", SimpleEvent.class, PlayerShearEntityEvent.class, "[player] shear entity")
                .description("Called when a player shears an entity. Requires Minecraft 1.9.4+")
                .examples("on player shear entity:")
                .since("1.8.0");

        // Entity Breed Event
        Skript.registerEvent("Entity Breed", SimpleEvent.class, EntityBreedEvent.class,
                        "entity breed")
                .description("Called when one Entity breeds with another Entity.")
                .examples("on entity breed:", "\nif breeding mother is a sheep:",
                        "\n\nkill breeding player")
                .since("1.17.0");

        EventValues.registerEventValue(EntityBreedEvent.class, Player.class, new Getter<>() {
            @Override
            public @Nullable Player get(EntityBreedEvent breedEvent) {
                LivingEntity breeder = breedEvent.getBreeder();
                if (breeder instanceof Player player) {
                    return player;
                }
                return null;
            }
        }, 0);

        EventValues.registerEventValue(EntityBreedEvent.class, Entity.class, new Getter<>() {
            @Override
            public Entity get(EntityBreedEvent breedEvent) {
                return breedEvent.getEntity();
            }
        }, 0);

        EventValues.registerEventValue(EntityBreedEvent.class, ItemType.class, new Getter<>() {
            @Override
            public @Nullable ItemType get(EntityBreedEvent breedEvent) {
                ItemStack bredWith = breedEvent.getBredWith();
                if (bredWith != null) {
                    return new ItemType(bredWith);
                }
                return null;
            }
        }, 0);

        // Entity Change Block Event
        Skript.registerEvent("Entity Change Block", SimpleEvent.class, EntityChangeBlockEvent.class,
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
        }, 0);

        // Player Command Send Event
        Skript.registerEvent("Player Command Send", SimpleEvent.class, PlayerCommandSendEvent.class,
                        "player command send")
                .description("This event is called when the list of available server commands is sent to the player.",
                        "Commands may be removed from display using this event, but implementations are not required to securely",
                        "remove all traces of the command. If secure removal of commands is required,",
                        "then the command should be assigned a permission which is not granted to the player.")
                .examples("on player command send:",
                        "\tremove \"ver\" and \"version\" from player command map")
                .since("2.5.3");

        // Block Drop Item Event
        Skript.registerEvent("Block Drop Item", SimpleEvent.class, BlockDropItemEvent.class,
                        "block drop item")
                .description("This event is called if a block broken by a player drops an item. ")
                .examples("")
                .since("2.6.0");

        EventValues.registerEventValue(BlockDropItemEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(BlockDropItemEvent event) {
                return event.getPlayer();
            }
        }, 0);

        // Block Damage Abort Event
        if (Skript.classExists("org.bukkit.event.block.BlockDamageAbortEvent")) {
            Skript.registerEvent("Block Damage Abort", SimpleEvent.class, BlockDamageAbortEvent.class,
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

        Skript.registerEvent("Entity Air Change", SimpleEvent.class, EntityAirChangeEvent.class,
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
                return Timespan.fromTicks_i(ticks);
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
                return Timespan.fromTicks_i(event.getAmount());
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(SpawnerSpawnEvent.class, Block.class, new Getter<>() {
            @Override
            public Block get(SpawnerSpawnEvent event) {
                return event.getSpawner().getBlock();
            }
        }, EventValues.TIME_NOW);

        // OTHER EVENT VALUES
        // Click Events
        EventValues.registerEventValue(PlayerInteractEvent.class, BlockFace.class, new Getter<>() {
            @Override
            public BlockFace get(PlayerInteractEvent event) {
                return event.getBlockFace();
            }
        }, 0);

        // Projectile Hit Event
        EventValues.registerEventValue(ProjectileHitEvent.class, BlockFace.class, new Getter<>() {
            @Override
            public @Nullable BlockFace get(ProjectileHitEvent event) {
                return event.getHitBlockFace();
            }
        }, 0);

        EventValues.registerEventValue(BlockPlaceEvent.class, BlockFace.class, new Getter<>() {
            @Override
            public @Nullable BlockFace get(BlockPlaceEvent event) {
                Block placed = event.getBlockPlaced();
                Block against = event.getBlockAgainst();
                return against.getFace(placed);
            }
        }, 0);

        // Entity Spell Cast Event
        Skript.registerEvent("Spell Cast", SimpleEvent.class, EntitySpellCastEvent.class,
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
        }, 0);

        // Entity Shoot Bow Event
        Skript.registerEvent("Entity Shoot Bow", SimpleEvent.class, EntityShootBowEvent.class,
                        "entity shoot bow")
                .description("Called when a LivingEntity shoots a bow firing an arrow.")
                .examples("on entity shoot bow:",
                        "\tif name of shot bow != \"Mr Bow\":",
                        "\t\tcancel event")
                .since("2.16.0");

        EventValues.registerEventValue(EntityShootBowEvent.class, Projectile.class, new Getter<>() {
            @Override
            public @Nullable Projectile get(EntityShootBowEvent event) {
                if (event.getProjectile() instanceof Projectile projectile) return projectile;
                return null;
            }
        }, EventValues.TIME_NOW);

        // Bell Ring Event
        if (Skript.classExists("org.bukkit.event.block.BellRingEvent")) {
            Skript.registerEvent("Bell Ring", SimpleEvent.class, BellRingEvent.class, "bell ring")
                    .description("Called when a bell is being rung. Requires Minecraft 1.19.4+")
                    .examples("on bell ring:",
                            "\tkill all mobs in radius 5 of event-block")
                    .since("2.16.0");

            EventValues.registerEventValue(BellRingEvent.class, Entity.class, new Getter<>() {
                @Override
                public @Nullable Entity get(BellRingEvent event) {
                    return event.getEntity();
                }
            }, EventValues.TIME_NOW);

            EventValues.registerEventValue(BellRingEvent.class, BlockFace.class, new Getter<>() {
                @Override
                public @NotNull BlockFace get(BellRingEvent event) {
                    return event.getDirection();
                }
            }, EventValues.TIME_NOW);
        }

        // Entity Teleport Event
        Skript.registerEvent("Entity Teleport", SimpleEvent.class, EntityTeleportEvent.class, "entity teleport")
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
        Skript.registerEvent("Moisture Change", SimpleEvent.class, MoistureChangeEvent.class, "moisture change")
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
        Skript.registerEvent("Block Explode", SimpleEvent.class, BlockExplodeEvent.class, "block explode")
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
            public @Nullable BlockData get(BlockExplodeEvent event) {
                BlockState explodedBlockState = event.getExplodedBlockState();
                if (explodedBlockState == null) return null;
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
            public @Nullable ItemType get(BlockExplodeEvent event) {
                BlockState explodedBlockState = event.getExplodedBlockState();
                if (explodedBlockState == null) return null;
                return new ItemType(explodedBlockState.getType());
            }
        }, EventValues.TIME_PAST);

        // Leash Events
        Skript.registerEvent("Player Leash", SimpleEvent.class, PlayerLeashEntityEvent.class, "player leash entity")
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

        Skript.registerEvent("Entity Unleash", SimpleEvent.class, EntityUnleashEvent.class, "entity unleash")
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
    }

}
