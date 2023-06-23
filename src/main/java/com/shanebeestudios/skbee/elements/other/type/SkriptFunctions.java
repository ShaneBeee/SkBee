package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.registrations.Classes;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.eclipse.jdt.annotation.Nullable;

@SuppressWarnings("unused")
public class SkriptFunctions {

    static {
        if (Types.HAS_ARMOR_TRIM) {
            ClassInfo<TrimMaterial> TRIM_MATERIAL = Classes.getExactClassInfo(TrimMaterial.class);
            ClassInfo<TrimPattern> TRIM_PATTERN = Classes.getExactClassInfo(TrimPattern.class);
            ClassInfo<ArmorTrim> ARMOR_TRIM = Classes.getExactClassInfo(ArmorTrim.class);
            if (TRIM_MATERIAL != null && TRIM_PATTERN != null && ARMOR_TRIM != null) {

                Functions.registerFunction(new SimpleJavaFunction<>("armorTrim", new Parameter[]{
                        new Parameter<>("trimMaterial", TRIM_MATERIAL, true, null),
                        new Parameter<>("trimPattern", TRIM_PATTERN, true, null)

                }, ARMOR_TRIM, true) {
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public @Nullable ArmorTrim[] executeSimple(Object[][] params) {
                        TrimMaterial trimMaterial = ((TrimMaterial) params[0][0]);
                        TrimPattern trimPattern = ((TrimPattern) params[1][0]);
                        return new ArmorTrim[]{new ArmorTrim(trimMaterial, trimPattern)};
                    }
                }.description("Create an armor trim that may be applied to an item.")
                        .examples("set {_trim} to armorTrim(gold_material, eye_pattern)")
                        .since("INSERT VERSION"));
            }
        }
    }
}
