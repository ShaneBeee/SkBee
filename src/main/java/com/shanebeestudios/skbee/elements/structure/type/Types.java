package com.shanebeestudios.skbee.elements.structure.type;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.Bukkit;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Types {

    private static final StructureManager STRUCTURE_MANAGER = SkBee.getPlugin().getStructureManager();

    public static void register(Registration reg) {
        reg.newType(StructureWrapper.class, "structure")
            .user("structures?")
            .name("Structure")
            .description("Represents a structure that can be saved or pasted into a world.",
                "In Minecraft these are actually called [**Structure Templates**](https://minecraft.wiki/w/Structure_file).",
                "These can also be placed using the `/place template` command.",
                "Use the 'Structure-Object' expression to get a new/existing structure object.",
                "When using `all structures`, this will only show structures that have been currently loaded into the game.")
            .since("1.12.0")
            .supplier(() -> {
                List<StructureWrapper> structures = new ArrayList<>();
                Bukkit.getStructureManager().getStructures().forEach((namespacedKey, structure) ->
                    structures.add(StructureWrapper.wrap(structure, namespacedKey)));
                return structures.stream().sorted(Comparator.comparing(sw -> sw.getKey().toString())).iterator();
            })
            .parser(new Parser<>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(StructureWrapper structure, int flags) {
                    return "structure '" + structure.getKey() + "'";
                }

                @Override
                public String toVariableNameString(StructureWrapper structure) {
                    return toString(structure, 0);
                }
            })
            .changer(new Changer<>() {
                @Override
                public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
                    if (mode == ChangeMode.DELETE) return CollectionUtils.array();
                    return null;
                }

                @Override
                public void change(StructureWrapper[] what, Object @Nullable [] delta, ChangeMode mode) {
                    if (mode == ChangeMode.DELETE) {
                        for (StructureWrapper structureWrapper : what) {
                            STRUCTURE_MANAGER.deleteStructure(structureWrapper);
                        }
                    }
                }
            })
            .register();

        if (Classes.getExactClassInfo(Mirror.class) == null) {
            reg.newEnumType(Mirror.class, "mirror")
                .user("mirrors?")
                .name("Structure - Mirror")
                .description("Represents the different states of mirroring for a structure. Requires MC 1.17.1+")
                .examples("set structure mirror of structure {_s} to front back")
                .since("1.12.0")
                .register();
        }

        if (Classes.getExactClassInfo(StructureRotation.class) == null) {
            reg.newEnumType(StructureRotation.class, "structurerotation")
                .user("structure ?rotations?")
                .name("Structure - Rotation")
                .description("Represents the different states of rotation for a structure. Requires MC 1.17.1+")
                .examples("set structure rotation of structure {_s} to clockwise 90")
                .since("1.12.0")
                .register();
        }
    }

}
