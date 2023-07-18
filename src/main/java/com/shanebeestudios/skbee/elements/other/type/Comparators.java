package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.classes.Comparator;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.block.data.BlockData;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Comparators {

    static {
        // PotionEffectType comparator
        register(PotionEffectType.class, PotionEffectType.class, new Comparator<>() {
            @Override
            public @NotNull Relation compare(PotionEffectType effect1, PotionEffectType effect2) {
                return Relation.get(effect1.equals(effect2));
            }

            @Override
            public boolean supportsOrdering() {
                return false;
            }
        });

        if (!Util.isRunningSkript27()) {
            // BlockData comparator -- Fixed in 2.7
            register(BlockData.class, BlockData.class, new Comparator<>() {
                @Override
                public @NotNull Relation compare(BlockData data1, BlockData data2) {
                    return Relation.get(data1.matches(data2));
                }

                @Override
                public boolean supportsOrdering() {
                    return false;
                }
            });
        }
    }

    // Shortcut method
    private static <T1, T2> void register(final Class<T1> class1, final Class<T2> class2, final Comparator<T1, T2> c) {
        ch.njol.skript.registrations.Comparators.registerComparator(class1, class2, c);
    }

}
