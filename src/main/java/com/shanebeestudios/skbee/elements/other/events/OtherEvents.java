package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.slot.Slot;
import com.shanebeestudios.skbee.api.event.EntityBlockInteractEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class OtherEvents {

    static {
        Skript.registerEvent("Block Physical Interact Event", SimpleEvent.class, EntityBlockInteractEvent.class,
                        "block (interact|trample)")
                .description("Called when an entity physically interacts with a block, for example,",
                        " entities trampling farmland and villagers opening doors.")
                .examples("on block interact:",
                        "\tif event-entity is a villager:",
                        "\t\tcancel event")
                .since("1.5.0");

        EventValues.registerEventValue(EntityBlockInteractEvent.class, Block.class, new Getter<>() {
            @Nullable
            @Override
            public Block get(EntityBlockInteractEvent event) {
                return event.getBlock();
            }
        }, 0);

        Skript.registerEvent("Anvil Prepare Event", SimpleEvent.class, PrepareAnvilEvent.class, "anvil prepare")
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

        EventValues.registerEventValue(PrepareAnvilEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(PrepareAnvilEvent event) {
                return (Player) event.getView().getPlayer();
            }
        }, 0);

        EventValues.registerEventValue(PrepareAnvilEvent.class, Slot.class, new Getter<>() {
            @Override
            public Slot get(PrepareAnvilEvent event) {
                return new Slot() {
                    @Nullable
                    @Override
                    public ItemStack getItem() {
                        return event.getResult();
                    }

                    @Override
                    public void setItem(@Nullable ItemStack item) {
                        event.setResult(item);
                    }

                    @Override
                    public int getAmount() {
                        return event.getResult().getAmount();
                    }

                    @Override
                    public void setAmount(int amount) {
                        event.getResult().setAmount(amount);
                    }

                    @Override
                    public boolean isSameSlot(Slot o) {
                        ItemStack item = o.getItem();
                        return item != null && item.isSimilar(event.getResult());
                    }

                    @Override
                    public String toString(@Nullable Event e, boolean debug) {
                        return "anvil inventory result slot";
                    }
                };
            }
        }, 0);

        EventValues.registerEventValue(PrepareAnvilEvent.class, Inventory.class, new Getter<>() {
            @Override
            public Inventory get(PrepareAnvilEvent event) {
                return event.getInventory();
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
            public @Nullable Entity get(EntityBreedEvent breedEvent) {
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

        // Inventory Move Item Event
        Skript.registerEvent("Inventory Move Item", SimpleEvent.class, InventoryMoveItemEvent.class,
                        "inventory move item")
                .description("Called when some entity or block (e.g. hopper) tries to move items directly from one inventory to another.",
                        "\nNOTE: This has nothing to do with a player's inventory!!!",
                        "\nWhen this event is called, the initiator may already have removed the item from the source inventory and is ready to move it into the destination inventory.",
                        "\nIf this event is cancelled, the items will be returned to the source inventory, if needed.",
                        "\nIf this event is not cancelled, the initiator will try to put the ItemStack into the destination inventory.",
                        "If this is not possible and the ItemStack has not been modified, the source inventory slot will be restored to its former state. Otherwise any additional items will be discarded.",
                        "\nevent-inventory = Inventory that initiated the transfer.",
                        "\npast event-inventory = Inventory that the ItemStack is being taken from.",
                        "\nfuture event-inventory = Inventory that the ItemStack is being put into.")
                .examples("on inventory move item:",
                        "\tif type of past event-inventory = hopper inventory:",
                        "\t\tcancel event")
                .since("2.5.2");

        EventValues.registerEventValue(InventoryMoveItemEvent.class, Inventory.class, new Getter<>() {
            @Override
            public @NotNull Inventory get(InventoryMoveItemEvent event) {
                return event.getInitiator();
            }
        }, 0);

        EventValues.registerEventValue(InventoryMoveItemEvent.class, Inventory.class, new Getter<>() {
            @Override
            public @NotNull Inventory get(InventoryMoveItemEvent event) {
                return event.getSource();
            }
        }, -1);

        EventValues.registerEventValue(InventoryMoveItemEvent.class, Inventory.class, new Getter<>() {
            @Override
            public @NotNull Inventory get(InventoryMoveItemEvent event) {
                return event.getDestination();
            }
        }, 1);

        EventValues.registerEventValue(InventoryMoveItemEvent.class, ItemStack.class, new Getter<>() {
            @Override
            public @NotNull ItemStack get(InventoryMoveItemEvent event) {
                return event.getItem();
            }
        }, 0);

        // Entity Transform Event
        Skript.registerEvent("Entity Transform", SimpleEvent.class, EntityTransformEvent.class,
                        "entity transform")
                .description("Called when an entity is about to be replaced by another entity.",
                        "Examples include a villager struck by lightning turning into a witch,",
                        "zombie drowning and becoming a drowned,",
                        "slime splitting into other slimes.",
                        "\nevent-entity = entity that is going to transform",
                        "\nfuture event-entity = entity after transformation (only returns one entity)",
                        "\nevent-transformreason = reason for the transformation",
                        "\ntransformed entit(y|ies) = entity or entities after transformation (can be multiple entities)")
                .examples("on entity transform:",
                        "\tif event-entity is a villager:",
                        "\t\tcancel event")
                .since("2.5.3");

        EventValues.registerEventValue(EntityTransformEvent.class, Entity.class, new Getter<>() {
            @Override
            public Entity get(EntityTransformEvent event) {
                return event.getTransformedEntity();
            }
        }, 1);

        EventValues.registerEventValue(EntityTransformEvent.class, TransformReason.class, new Getter<>() {
            @Override
            public TransformReason get(EntityTransformEvent event) {
                return event.getTransformReason();
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

        // OTHER EVENT VALUES
        // Click Events
        EventValues.registerEventValue(PlayerInteractEvent.class, BlockFace.class, new Getter<>() {
            @Override
            public @Nullable BlockFace get(PlayerInteractEvent event) {
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

    }

}
