package com.shanebeestudios.skbee.elements.structure.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.Bukkit;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(StructureWrapper.class, "structure")
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
            }));

        if (Classes.getExactClassInfo(Mirror.class) == null) {
            EnumWrapper<Mirror> MIRROR_ENUM = new EnumWrapper<>(Mirror.class);
            Classes.registerClass(MIRROR_ENUM.getClassInfo("mirror")
                .user("mirrors?")
                .name("Structure - Mirror")
                .description("Represents the different states of mirroring for a structure. Requires MC 1.17.1+")
                .examples("set structure mirror of structure {_s} to front back")
                .since("1.12.0"));
        }

        if (Classes.getExactClassInfo(StructureRotation.class) == null) {
            EnumWrapper<StructureRotation> ROTATION_ENUM = new EnumWrapper<>(StructureRotation.class);
            Classes.registerClass(ROTATION_ENUM.getClassInfo("structurerotation")
                .user("structure ?rotations?")
                .name("Structure - Rotation")
                .description("Represents the different states of rotation for a structure. Requires MC 1.17.1+")
                .examples("set structure rotation of structure {_s} to clockwise 90")
                .since("1.12.0"));
        }
    }

}
