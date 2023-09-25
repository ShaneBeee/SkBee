package com.shanebeestudios.skbee.elements.other.type;

import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.skriptlang.skript.lang.comparator.Comparator;
import org.skriptlang.skript.lang.comparator.Relation;

public class Comparators {

    static {
        if (Types.HAS_ARMOR_TRIM) {
            register(TrimPattern.class, TrimPattern.class, (pattern1,pattern2) -> Relation.get(pattern1.equals(pattern2)));
            register(TrimMaterial.class, TrimMaterial.class, (material1, material2) -> Relation.get(material1.equals(material2)));
        }
    }

    private static <T1, T2> void register(Class<T1> class1, Class<T2> class2, Comparator<T1, T2> comparator) {
        org.skriptlang.skript.lang.comparator.Comparators.registerComparator(class1, class2, comparator);
    }

}
