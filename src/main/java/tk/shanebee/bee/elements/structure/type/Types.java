package tk.shanebee.bee.elements.structure.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.api.util.EnumUtils;
import tk.shanebee.bee.elements.structure.StructureBee;

public class Types {

    static {
        if (Skript.classExists("org.bukkit.structure.Structure")) {
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

                        @Override
                        public String getVariableNamePattern() {
                            return "";
                        }
                    }));

            EnumUtils<Mirror> mirror = new EnumUtils<>(Mirror.class);
            Classes.registerClass(new ClassInfo<>(Mirror.class, "mirror")
                    .user("mirrors?")
                    .name("Structure - Mirror")
                    .description("Represents the different states of mirroring for a structure. Requires MC 1.17.1+")
                    .examples("set mirror of structure {_s} to front back")
                    .usage(mirror.getAllNames())
                    .since("1.12.0")
                    .parser(new Parser<Mirror>() {
                        @Nullable
                        @Override
                        public Mirror parse(String string, ParseContext context) {
                            return mirror.parse(string);
                        }

                        @Override
                        public String toString(Mirror m, int flags) {
                            return mirror.toString(m, flags);
                        }

                        @Override
                        public String toVariableNameString(Mirror m) {
                            return m.name();
                        }

                        @Override
                        public String getVariableNamePattern() {
                            return "\\S+";
                        }
                    }));

            EnumUtils<StructureRotation> rotation = new EnumUtils<>(StructureRotation.class);
            Classes.registerClass(new ClassInfo<>(StructureRotation.class, "rotation")
                    .user("rotations?")
                    .name("Structure - Rotation")
                    .description("Represents the different states of rotation for a structure. Requires MC 1.17.1+")
                    .examples("set rotation of structure {_s} to clockwise 90")
                    .usage(rotation.getAllNames())
                    .since("1.12.0")
                    .parser(new Parser<StructureRotation>() {
                        @Nullable
                        @Override
                        public StructureRotation parse(String string, ParseContext context) {
                            return rotation.parse(string);
                        }

                        @Override
                        public String toString(StructureRotation structureRotation, int flags) {
                            return rotation.toString(structureRotation, flags);
                        }

                        @Override
                        public String toVariableNameString(StructureRotation structureRotation) {
                            return structureRotation.name();
                        }

                        @Override
                        public String getVariableNamePattern() {
                            return "\\S+";
                        }
                    }));
        }
    }

}
