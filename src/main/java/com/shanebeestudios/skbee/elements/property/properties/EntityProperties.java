package com.shanebeestudios.skbee.elements.property.properties;

import ch.njol.skript.aliases.ItemType;
import com.shanebeestudios.skbee.api.property.Property;
import com.shanebeestudios.skbee.api.property.PropertyRegistry;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Sittable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"deprecation", "unused"})
public class EntityProperties {

    static {
        PropertyRegistry.registerProperty("aggressive", new Property<>(Mob.class, Boolean.class) {
                @Override
                public Boolean get(Mob mob) {
                    return mob.isAggressive();
                }

                @Override
                public void set(Mob mob, Boolean value) {
                    mob.setAggressive(value);
                }
            })
            .description("Whether the mob is aggressive. This will not work on all mobs, only mobs that can actually be aggressive.")
            .examples("set aggressive property of event-mob to true")
            .since("3.10.0");

        PropertyRegistry.registerProperty("health", new Property<>(Entity.class, Number.class) {
                @Override
                public Number get(Entity entity) {
                    if (entity instanceof Damageable damageable) return damageable.getHealth();
                    return 0;
                }

                @Override
                public void set(Entity entity, Number value) {
                    if (entity instanceof Damageable damageable) damageable.setHealth(value.doubleValue());
                }

                @Override
                public void add(Entity entity, Number value) {
                    if (entity instanceof Damageable damageable) {
                        damageable.setHealth(damageable.getHealth() + value.doubleValue());
                    }
                }

                @Override
                public void remove(Entity entity, Number value) {
                    if (entity instanceof Damageable damageable) {
                        double health = damageable.getHealth();
                        health -= value.doubleValue();
                        damageable.setHealth(Math.max(health, 0));
                    }
                }

                @Override
                public void reset(Entity entity) {
                    if (entity instanceof Damageable damageable && entity instanceof Attributable attributable) {
                        AttributeInstance instance = attributable.getAttribute(Attribute.MAX_HEALTH);
                        if (instance != null) {
                            double baseValue = instance.getBaseValue();
                            damageable.setHealth(baseValue);
                        } else {
                            Util.log("NULL???");
                        }
                    } else {
                        Util.log("Not instance?!?!?!");
                    }
                }
            })
            .description("Represents the health of an entity.")
            .examples("set {_h} to health property of player",
                "set health property of player to 10",
                "add 1 to health property of player",
                "remove 1 from health property of target entity")
            .since("3.10.0");

        PropertyRegistry.registerProperty("inventory contents", new Property<>(InventoryHolder.class, ItemType[].class) {
                @Override
                public ItemType[] get(InventoryHolder inventoryHolder) {
                    List<ItemType> items = new ArrayList<>();
                    for (@Nullable ItemStack content : inventoryHolder.getInventory().getContents()) {
                        if (content == null) continue;
                        items.add(new ItemType(content));
                    }
                    return items.toArray(new ItemType[0]);
                }

                @Override
                public void set(InventoryHolder inventoryHolder, ItemType[] itemTypes) {
                    ItemStack[] items = new ItemStack[itemTypes.length];
                    for (int i = 0; i < itemTypes.length; i++) {
                        items[i] = itemTypes[i].getRandom();
                    }
                    inventoryHolder.getInventory().setContents(items);
                }

                @Override
                public void add(InventoryHolder inventoryHolder, ItemType[] itemTypes) {
                    Inventory inventory = inventoryHolder.getInventory();
                    for (ItemType itemType : itemTypes) {
                        itemType.addTo(inventory);
                    }
                }

                @Override
                public void remove(InventoryHolder inventoryHolder, ItemType[] itemTypes) {
                    Inventory inventory = inventoryHolder.getInventory();
                    for (ItemType itemType : itemTypes) {
                        itemType.removeFrom(inventory);
                    }
                }

                @Override
                public void delete(InventoryHolder inventoryHolder) {
                    inventoryHolder.getInventory().clear();
                }
            })
            .description("Represents the contents of an object that holds an inventory.")
            .examples("set {_i::*} to inventory contents property of player",
                "add an apple to inventory contents property of player",
                "remove all diamonds from inventory contents property of player")
            .since("3.10.0");

        PropertyRegistry.registerProperty("name", new Property<>(Entity.class, String.class) {
                @Override
                public String get(Entity object) {
                    return object.getName();
                }

                @Override
                public void set(Entity object, String value) {
                    object.setCustomName(value);
                }

                @Override
                public void delete(Entity object) {
                    object.setCustomName(null);
                }
            })
            .description("Represents the name of an entity.")
            .since("3.10.0");

        PropertyRegistry.registerProperty("persistence required",  new Property<>(LivingEntity.class, Boolean.class) {
            @Override
            public Boolean get(LivingEntity livingEntity) {
                return !livingEntity.getRemoveWhenFarAway();
            }

                @Override
                public void set(LivingEntity livingEntity, Boolean value) {
                    livingEntity.setRemoveWhenFarAway(!value);
                }
            })
            .description("Prevent mobs from despawning naturally.",
                "See the [Despawning](https://minecraft.wiki/w/Mob_spawning#Despawning) section McWiki for further details about despawning.",
                "A silly side effect of this is that some mobs (such as sheep) will stop their random stroll goal when more than " +
                    "32 blocks away from a player, setting this to true will prevent that and the mob will forever roam the lands.")
            .examples("set persistence required property of all mobs to true")
            .since("3.10.0");

        PropertyRegistry.registerProperty("sitting", new Property<>(Sittable.class, Boolean.class) {
                @Override
                public Boolean get(Sittable sittable) {
                    return sittable.isSitting();
                }

                @Override
                public void set(Sittable sittable, Boolean value) {
                    sittable.setSitting(value);
                }
            })
            .description("Whether an entity is sitting. Currently supports Camel, Cat, Fox, Panda, Parrot, Wolf.")
            .examples("set sitting property of target entity to true",
                "if sitting property of event-mob is false:")
            .since("3.10.0");
    }

}
