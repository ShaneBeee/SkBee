package com.shanebeestudios.skbee.elements.other.type;

import org.bukkit.NamespacedKey;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

public class Comps {

    static {
        Comparators.registerComparator(NamespacedKey.class, String.class, (o1, o2) ->
            Relation.get(o1.toString().equalsIgnoreCase(o2)));
    }

}
