package com.shanebeestudios.skbee.elements.property.properties;

import ch.njol.skript.aliases.ItemType;
import com.shanebeestudios.skbee.api.property.Property;
import com.shanebeestudios.skbee.api.property.PropertyRegistry;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"deprecation", "unused"})
public class EntityProperties {

    static {
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
            })
            .description("Represents the health of an entity.")
            .examples("set {_h} to health property of player",
                "set health property of player to 10",
                "add 1 to health property of player",
                "remove 1 from health property of target entity")
            .since("INSERT VERSION");

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
            .since("INSERT VERSION");

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
            .since("INSERT VERSION");
    }

}
