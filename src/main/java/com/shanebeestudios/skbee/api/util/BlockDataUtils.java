package com.shanebeestudios.skbee.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlockDataUtils {

    /**
     * Get an array of BlockData keys/values
     *
     * @param blockData BlockData to get keys/values
     * @return Array of BlockData keys/values
     */
    public static String @Nullable [] getBlockDataTagValues(BlockData blockData) {
        String[] splits1 = blockData.getAsString().split("\\[");
        if (splits1.length >= 2) {
            String[] splits2 = splits1[1].split("]");

            return splits2[0].split(",");
        }
        return null;
    }

    /**
     * Get the tags of a BlockData
     *
     * @param blockData BlockData to get tags from
     * @return All tags of BlockData
     */
    public static String @Nullable [] getBlockDataTags(BlockData blockData) {
        String[] splitData = getBlockDataTagValues(blockData);
        List<String> tags = new ArrayList<>();
        if (splitData == null) return null;

        for (String splitDatum : splitData) {
            tags.add(splitDatum.split("=")[0]);
        }
        return tags.toArray(new String[0]);
    }

    /**
     * Get the Block form of a Material
     * <p>Some Items (such as POTATO) have a different block form (POTATOES)</p>
     *
     * @param material Material to convert to Block form
     * @return Block form of Material
     */
    public static Material getBlockForm(Material material) {
        if (material.isBlock()) return material;
        return switch (material) {
            case WHEAT_SEEDS -> Material.WHEAT;
            case POTATO -> Material.POTATOES;
            case CARROT -> Material.CARROTS;
            case BEETROOT_SEEDS -> Material.BEETROOTS;
            case PUMPKIN_SEEDS -> Material.PUMPKIN_STEM;
            case MELON_SEEDS -> Material.MELON_STEM;
            case SWEET_BERRIES -> Material.SWEET_BERRY_BUSH;
            default -> material;
        };
    }

    /**
     * Get the value of a BlockData tag
     *
     * @param blockData BlockData to grab value from
     * @param tag       Tag to grab value
     * @return Value of tag from BlockData
     */
    public static @Nullable Object getBlockDataValueFromTag(BlockData blockData, String tag) {
        String[] sp = getBlockDataTagValues(blockData);
        if (sp != null) {
            for (String string : sp) {
                String[] s = string.split("=");
                if (s[0].equals(tag)) {
                    String value = s[1];
                    if (value == null) return null;

                    if (MathUtil.isBoolean(value)) {
                        return Boolean.valueOf(value);
                    } else if (MathUtil.isNumber(value)) {
                        return Integer.parseInt(value);
                    } else {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Set the tag of a BlockData
     *
     * @param oldBlockData Old version of BlockData to change
     * @param tag          Tag to change
     * @param value        New value of tag
     * @return Updated BlockData with new value
     */
    public static BlockData setBlockDataTag(BlockData oldBlockData, String tag, Object value) {
        if (oldBlockData.getAsString().contains("[")) {
            String newData = oldBlockData.getMaterial().getKey() + "[" + tag.toLowerCase(Locale.ROOT) + "=" + value + "]";
            try {
                BlockData blockData = Bukkit.createBlockData(newData);
                return oldBlockData.merge(blockData);
            } catch (IllegalArgumentException ex) {
                Util.debug("Could not parse block data: %s", newData);
            }
        }
        return oldBlockData;
    }

}
