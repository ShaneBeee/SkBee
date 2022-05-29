package com.shanebeestudios.skbee.elements.structure.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.structure.BlockStateBee;
import com.shanebeestudios.skbee.api.structure.StructureBee;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(StructureBee.class, "structure")
                .user("structures?")
                .name("Structure")
                .description("Represents a structure that can be saved or pasted into a world. Requires MC 1.17.1+",
                        "Use the 'Structure-Object' expression to get a new/existing structure object.")
                .since("1.12.0")
                .parser(new Parser<StructureBee>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(StructureBee structure, int flags) {
                        return structure.toString();
                    }

                    @Override
                    public String toVariableNameString(StructureBee structure) {
                        return structure.toString();
                    }

                    public String getVariableNamePattern() {
                        return "";
                    }
                }));

        EnumUtils<Mirror> MIRROR_ENUM = new EnumUtils<>(Mirror.class);
        Classes.registerClass(new ClassInfo<>(Mirror.class, "mirror")
                .user("mirrors?")
                .name("Structure - Mirror")
                .description("Represents the different states of mirroring for a structure. Requires MC 1.17.1+")
                .examples("set mirror of structure {_s} to front back")
                .usage(MIRROR_ENUM.getAllNames())
                .since("1.12.0")
                .parser(MIRROR_ENUM.getParser()));

        EnumUtils<StructureRotation> ROTATION_ENUM = new EnumUtils<>(StructureRotation.class);
        Classes.registerClass(new ClassInfo<>(StructureRotation.class, "structurerotation")
                .user("structure ?rotations?")
                .name("Structure - Rotation")
                .description("Represents the different states of rotation for a structure. Requires MC 1.17.1+")
                .examples("set rotation of structure {_s} to clockwise 90")
                .usage(ROTATION_ENUM.getAllNames())
                .since("1.12.0")
                .parser(ROTATION_ENUM.getParser()));

        Classes.registerClass(new ClassInfo<>(BlockStateBee.class, "blockstate")
                .user("blockstates?")
                .name("BlockState")
                .description("Represents the blockstate of a block saved in a structure. Requires MC 1.17.1+")
                .since("1.12.3"));
    }

}
