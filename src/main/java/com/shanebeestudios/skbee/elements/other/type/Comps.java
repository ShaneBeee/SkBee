package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

@SuppressWarnings("unchecked")
public class Comps {

    static {
        Comparators.registerComparator(NamespacedKey.class, String.class, (o1, o2) ->
            Relation.get(o1.toString().equalsIgnoreCase(o2)));

        if (!Comparators.exactComparatorExists(ItemType.class, Tag.class)) {
            // Add this comparator until Skript adds it!
            Comparators.registerComparator(ItemType.class, Tag.class, (itemType, tag) -> {
                if (Util.isMaterialTag(tag)) {
                    return Relation.get(((Tag<Material>) tag).isTagged(itemType.getMaterial()));
                }
                return Relation.NOT_EQUAL;
            });
        }

        if (!Comparators.exactComparatorExists(BlockData.class, Tag.class)) {
            // Add this comparator until Skript adds it!
            Comparators.registerComparator(BlockData.class, Tag.class, (blockData, tag) -> {
                if (Util.isMaterialTag(tag)) {
                    return Relation.get(((Tag<Material>) tag).isTagged(blockData.getMaterial()));
                }
                return Relation.NOT_EQUAL;
            });
        }

        if (!Comparators.exactComparatorExists(EntityData.class, Tag.class)) {
            // Add this comparator until Skript adds it!
            Comparators.registerComparator(EntityData.class, Tag.class, (entityData, tag) -> {
                if (Util.isEntityTypeTag(tag)) {
                    EntityType bukkitEntityType = EntityUtils.toBukkitEntityType(entityData);
                    return Relation.get(((Tag<EntityType>) tag).isTagged(bukkitEntityType));
                }
                return Relation.NOT_EQUAL;
            });
        }

        if (!Comparators.exactComparatorExists(EntityType.class, Tag.class)) {
            Comparators.registerComparator(EntityType.class, Tag.class, (entityType, tag) -> {
                if (Util.isEntityTypeTag(tag)) {
                    return Relation.get(((Tag<EntityType>) tag).isTagged(entityType));
                }
                return Relation.NOT_EQUAL;
            });
        }
    }

}
