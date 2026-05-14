package com.shanebeestudios.skbee.elements.other.events.other;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.Experience;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.skript.util.slot.Slot;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.github.shanebeee.skr.Registration;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import io.papermc.paper.connection.PlayerCommonConnection;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.connection.PlayerConnection;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.event.player.PlayerItemGroupCooldownEvent;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerEvents extends SimpleEvent {

    private static final boolean HAS_CONFIG = Skript.classExists("io.papermc.paper.connection.PlayerConfigurationConnection");

    @SuppressWarnings("UnstableApiUsage")
    public static void register(Registration reg) {
        // EntityExhaustionEvent
        reg.newEvent(PlayerEvents.class, EntityExhaustionEvent.class, "player exhaustion")
            .name("Player Exhaustion")
            .description("Called when a human entity experiences exhaustion.",
                "An exhaustion level greater than 4.0 causes a decrease in saturation by 1.")
            .since("INSERT VERSION")
            .register();

        reg.newEventValue(EntityExhaustionEvent.class, Number.class)
            .description("Represents the amount of exhaustion to add to the player's current exhaustion.")
            .patterns("exhaustion")
            .converter(EntityExhaustionEvent::getExhaustion)
            .changer(ChangeMode.SET, (event, value) -> event.setExhaustion(value.floatValue()))
            .register();

        // PlayerAttemptPickupItemEvent
        reg.newEvent(PlayerEvents.class, PlayerAttemptPickupItemEvent.class, "player attempt item pickup")
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
        reg.newEvent(PlayerEvents.class, PrepareAnvilEvent.class, "[skbee] anvil prepare")
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
        reg.newEvent(PlayerEvents.class, PlayerChunkLoadEvent.class,
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
        reg.newEvent(PlayerEvents.class, PlayerChunkUnloadEvent.class,
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
            reg.newEvent(PlayerEvents.class, PlayerCustomClickEvent.class,
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
        reg.newEvent(PlayerEvents.class, PlayerElytraBoostEvent.class, "[player] elytra boost")
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
        reg.newEvent(PlayerEvents.class, PlayerFailMoveEvent.class, "player fail move")
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
        reg.newEvent(PlayerEvents.class, PlayerLeashEntityEvent.class, "player leash entity")
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

        // Player Item Group Cooldown Event
        reg.newEvent(PlayerEvents.class, PlayerItemGroupCooldownEvent.class,
                "player item group cooldown", "player item cooldown")
            .name("Player Item Group Cooldown")
            .description("Fired when a player receives an item cooldown.")
            .since("INSERT VERSION")
            .register();

        reg.newEventValue(PlayerItemGroupCooldownEvent.class, Timespan.class)
            .description("Represents the cooldown.")
            .converter(event -> new Timespan(TimePeriod.TICK, event.getCooldown()))
            .changer(ChangeMode.SET, (event, timespan) -> event.setCooldown((int) timespan.getAs(TimePeriod.TICK)))
            .register();
        reg.newEventValue(PlayerItemGroupCooldownEvent.class, NamespacedKey.class)
            .description("Represents the cooldown group as defined by an item's UseCooldownComponent.")
            .patterns("cooldown-group", "group")
            .converter(PlayerItemGroupCooldownEvent::getCooldownGroup)
            .register();

        // Player Pickup XP Event
        reg.newEvent(PlayerEvents.class, PlayerPickupExperienceEvent.class,
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
        reg.newEvent(PlayerEvents.class, PlayerRecipeBookClickEvent.class, "[player] recipe book click")
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
        reg.newEvent(PlayerEvents.class, PlayerSetSpawnEvent.class, "player spawn change")
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
        reg.newEvent(PlayerEvents.class, PlayerShearEntityEvent.class, "[player] shear entity")
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
        reg.newEvent(PlayerEvents.class, PlayerStopUsingItemEvent.class, "[player] stop using item")
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

    }


}
