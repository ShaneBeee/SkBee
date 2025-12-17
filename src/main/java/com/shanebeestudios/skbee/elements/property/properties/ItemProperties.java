package com.shanebeestudios.skbee.elements.property.properties;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import com.shanebeestudios.skbee.api.property.Property;
import com.shanebeestudios.skbee.api.property.PropertyRegistry;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.tag.DamageTypeTags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class ItemProperties {

    static {
        PropertyRegistry.registerProperty("blast resistance", new Property<>(ItemType.class, Float.class) {
                @Override
                public Float get(ItemType itemType) {
                    return itemType.getMaterial().getBlastResistance();
                }
            })
            .description("Represents the blast resistance of a block.")
            .examples("if blast resistance property of target block > 1:")
            .since("3.10.0");

        PropertyRegistry.registerProperty("fire resistant", new Property<>(ItemType.class, Boolean.class) {
                @Override
                public Boolean get(ItemType itemType) {
                    return itemType.getItemMeta().getDamageResistant() == DamageTypeTags.IS_FIRE;
                }

                @Override
                public void set(ItemType itemType, Boolean value) {
                    ItemMeta itemMeta = itemType.getItemMeta();
                    itemMeta.setDamageResistant(DamageTypeTags.IS_FIRE);
                    itemType.setItemMeta(itemMeta);
                }
            })
            .description("Represents if an item is fire resistant. If true, it will not burn in fire or lava.")
            .examples("set fire resistant property of {_i} to true")
            .since("3.10.0");

        if (Skript.classExists("org.bukkit.inventory.ItemRarity")) {
            List<String> rarityNames = new ArrayList<>();
            for (ItemRarity value : ItemRarity.values()) {
                rarityNames.add("\"" + value.name().toLowerCase(Locale.ROOT) + "\"");
            }
            PropertyRegistry.registerProperty("item rarity", new Property<>(ItemType.class, String.class) {
                    @SuppressWarnings("DataFlowIssue")
                    @Override
                    public @Nullable String get(ItemType itemType) {
                        ItemMeta itemMeta = itemType.getItemMeta();
                        ItemRarity rarity;
                        if (itemMeta.hasRarity()) {
                            rarity = itemMeta.getRarity();
                        } else {
                            rarity = itemType.getMaterial().asItemType().getItemRarity();
                        }
                        if (rarity == null) return null;
                        return rarity.toString().toLowerCase(Locale.ROOT);
                    }

                    @Override
                    public void set(ItemType itemType, String value) {
                        ItemRarity itemRarity;
                        try {
                            itemRarity = ItemRarity.valueOf(value.toUpperCase(Locale.ROOT));
                        } catch (IllegalArgumentException ignore) {
                            itemRarity = ItemRarity.COMMON;
                        }
                        ItemMeta itemMeta = itemType.getItemMeta();
                        itemMeta.setRarity(itemRarity);
                        itemType.setItemMeta(itemMeta);
                    }

                    @Override
                    public void delete(ItemType itemType) {
                        ItemMeta itemMeta = itemType.getItemMeta();
                        itemMeta.setRarity(null);
                        itemType.setItemMeta(itemMeta);
                    }
                })
                .description("Represents the different item rarities of an item (represented as a string).",
                    "Options are: " + String.join(", ", rarityNames), "Requires MC 1.20.5+")
                .examples("set item rarity property of player's tool to \"uncommon\"",
                    "set {_rarity} to item rarity property of player's tool",
                    "if item rarity property of player's tool = \"epic\":")
                .since("3.10.0");
        }

        PropertyRegistry.registerProperty("unbreakable", new Property<>(ItemType.class, Boolean.class) {
                @Override
                public Boolean get(ItemType itemType) {
                    return itemType.getItemMeta().isUnbreakable();
                }

                @Override
                public void set(ItemType itemType, Boolean value) {
                    ItemMeta itemMeta = itemType.getItemMeta();
                    itemMeta.setUnbreakable(value);
                    itemType.setItemMeta(itemMeta);
                }
            })
            .description("Represents if the item is unbreakable.")
            .examples("set unbreakable property of player's tool to true",
                "set unbreakable property of player's tool to false")
            .since("3.10.0");
    }

}
