package com.shanebeestudios.skbee.elements.structure.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(StructureWrapper.class, "structure")
                .user("structures?")
                .name("Structure")
                .description("Represents a structure that can be saved or pasted into a world. Requires MC 1.17.1+",
                        "Use the 'Structure-Object' expression to get a new/existing structure object.")
                .since("1.12.0")
                .parser(new Parser<StructureWrapper>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(StructureWrapper structure, int flags) {
                        return structure.toString();
                    }

                    @Override
                    public String toVariableNameString(StructureWrapper structure) {
                        return structure.toString();
                    }

                    public String getVariableNamePattern() {
                        return "";
                    }
                }));

        EnumWrapper<Mirror> MIRROR_ENUM = new EnumWrapper<>(Mirror.class);
        Classes.registerClass(MIRROR_ENUM.getClassInfo("mirror")
                .user("mirrors?")
                .name("Structure - Mirror")
                .description("Represents the different states of mirroring for a structure. Requires MC 1.17.1+")
                .examples("set structure mirror of structure {_s} to front back")
                .since("1.12.0"));

        EnumWrapper<StructureRotation> ROTATION_ENUM = new EnumWrapper<>(StructureRotation.class);
        Classes.registerClass(ROTATION_ENUM.getClassInfo("structurerotation")
                .user("structure ?rotations?")
                .name("Structure - Rotation")
                .description("Represents the different states of rotation for a structure. Requires MC 1.17.1+")
                .examples("set structure rotation of structure {_s} to clockwise 90")
                .since("1.12.0"));
    }

}
