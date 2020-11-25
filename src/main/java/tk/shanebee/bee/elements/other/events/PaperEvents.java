package tk.shanebee.bee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.EntityZapEvent;
import com.destroystokyo.paper.event.entity.ExperienceOrbMergeEvent;
import com.destroystokyo.paper.event.entity.SkeletonHorseTrapEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PaperEvents {

    static {
        // == PLAYER EVENTS == //

        // Player Armor Change Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerArmorChangeEvent")) {
            Skript.registerEvent("Armor Change Event", SimpleEvent.class, PlayerArmorChangeEvent.class, "player change armor")
                    .description("Called when the player themselves change their armor items. Requires Paper 1.12.2+")
                    .examples("on player change armor:",
                            "\tset helmet of player to pumpkin")
                    .since("1.3.1");
        }

        // Player Recipe Book Click Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent")) {
            Skript.registerEvent("Recipe Book Click Event", SimpleEvent.class, PlayerRecipeBookClickEvent.class, "[player] recipe book click")
                    .description("Called when the player clicks on a recipe in their recipe book. Requires Paper 1.15+")
                    .examples("on recipe book click:",
                            "\tif event-string = \"minecraft:diamond_sword\":",
                            "\t\tcancel event")
                    .since("1.5.0");

            EventValues.registerEventValue(PlayerRecipeBookClickEvent.class, String.class, new Getter<String, PlayerRecipeBookClickEvent>() {
                @Nullable
                @Override
                public String get(@NotNull PlayerRecipeBookClickEvent event) {
                    return event.getRecipe().toString();
                }
            }, 0);
        }

        // Player shear entity event
        if (Skript.classExists("org.bukkit.event.player.PlayerShearEntityEvent")) {
            Skript.registerEvent("Shear Entity", SimpleEvent.class, PlayerShearEntityEvent.class, "[player] shear entity")
                    .description("Called when a player shears an entity. Requires Minecraft 1.9.4+")
                    .examples("on player shear entity:")
                    .since("1.8.0");
        }

        // Player Pickup XP Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent")) {
            Skript.registerEvent("Player Pickup Experience Orb", SimpleEvent.class, PlayerPickupExperienceEvent.class, "player pickup (experience|xp) [orb]")
                    .description("Fired when a player is attempting to pick up an experience orb. Requires Paper 1.12.2+")
                    .examples("on player pickup xp:",
                            "\tadd 10 to level of player")
                    .since("1.8.0");
        }

        // Player Elytra Boost Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerElytraBoostEvent")) {
            Skript.registerEvent("Player Elytra Boost", SimpleEvent.class, PlayerElytraBoostEvent.class, "[player] elytra boost")
                    .description("Fired when a player boosts elytra flight with a firework. Requires Paper 1.13.2+")
                    .examples("on elytra boost:",
                            "\tpush player forward at speed 50")
                    .since("1.8.0");
            EventValues.registerEventValue(PlayerElytraBoostEvent.class, ItemType.class, new Getter<ItemType, PlayerElytraBoostEvent>() {
                @Override
                public ItemType get(PlayerElytraBoostEvent e) {
                    return new ItemType(e.getItemStack());
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

            EventValues.registerEventValue(EntityPathfindEvent.class, Location.class, new Getter<Location, EntityPathfindEvent>() {
                @Nullable
                @Override
                public Location get(@NotNull EntityPathfindEvent event) {
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
            EventValues.registerEventValue(EntityZapEvent.class, Location.class, new Getter<Location, EntityZapEvent>() {
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

        // == BLOCK EVENTS == //

        // Anvil Damaged Event
        if (Skript.classExists("com.destroystokyo.paper.event.block.AnvilDamagedEvent")) {
            Skript.registerEvent("Anvil Damaged Event", SimpleEvent.class, AnvilDamagedEvent.class, "anvil damage")
                    .description("Called when an anvil is damaged from being used. Requires Paper 1.13+")
                    .examples("on anvil damage:",
                            "\tloop viewers of event-inventory:",
                            "\t\tif loop-player has permission \"no.anvil.break\"",
                            "\t\t\tcancel event")
                    .since("1.5.0");
            EventValues.registerEventValue(AnvilDamagedEvent.class, Inventory.class, new Getter<Inventory, AnvilDamagedEvent>() {
                @Nullable
                @Override
                public Inventory get(@NotNull AnvilDamagedEvent event) {
                    return event.getInventory();
                }
            }, 0);
            EventValues.registerEventValue(AnvilDamagedEvent.class, Block.class, new Getter<Block, AnvilDamagedEvent>() {
                @Nullable
                @Override
                public Block get(AnvilDamagedEvent event) {
                    return event.getViewers().get(0).getTargetBlockExact(10);
                }
            }, 0);
            EventValues.registerEventValue(AnvilDamagedEvent.class, Player.class, new Getter<Player, AnvilDamagedEvent>() {
                @Nullable
                @Override
                public Player get(AnvilDamagedEvent event) {
                    return ((Player) event.getViewers().get(0));
                }
            }, 0);
        }

        // Beacon Effect Event
        if (Skript.classExists("com.destroystokyo.paper.event.block.BeaconEffectEvent")) {
            Skript.registerEvent("Beacon Effect", SimpleEvent.class, BeaconEffectEvent.class, "beacon effect")
                    .description("Called when a beacon effect is being applied to a player. Requires Paper 1.9+")
                    .examples("on beacon effect:",
                            "\tif event-player does not have permission \"my.server.beacons\":",
                            "\t\tcancel event")
                    .since("INSERT VERSION");
            EventValues.registerEventValue(BeaconEffectEvent.class, Player.class, new Getter<Player, BeaconEffectEvent>() {
                @Override
                public Player get(BeaconEffectEvent e) {
                    return e.getPlayer();
                }
            }, 0);
            // TODO These two values will make more sense in the future (Currently have a PR for potion effects in Skript)
            EventValues.registerEventValue(BeaconEffectEvent.class, PotionEffectType.class, new Getter<PotionEffectType, BeaconEffectEvent>() {
                @Override
                public PotionEffectType get(BeaconEffectEvent e) {
                    return e.getEffect().getType();
                }
            }, 0);
            EventValues.registerEventValue(BeaconEffectEvent.class, PotionEffect.class, new Getter<PotionEffect, BeaconEffectEvent>() {
                @Nullable
                @Override
                public PotionEffect get(BeaconEffectEvent e) {
                    return e.getEffect();
                }
            }, 0);
        }
    }

}
