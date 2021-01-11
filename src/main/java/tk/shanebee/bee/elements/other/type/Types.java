package tk.shanebee.bee.elements.other.type;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.JavaFunction;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Color;
import ch.njol.util.StringUtils;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Tag;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.api.util.ParticleUtil;
import tk.shanebee.bee.api.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Types {


    static {
        // == TYPES ==

        if (Skript.isRunningMinecraft(1, 13)) {
            // Only register if no other addons have registered this class
            if (Classes.getExactClassInfo(Particle.class) == null) {
                Classes.registerClass(new ClassInfo<>(Particle.class, "particle")
                        .user("particles?")
                        .name("Particle")
                        .description("Represents a particle which can be used in the 'Particle Spawn' effect. Some particles require extra data,",
                                "these are distinguished by their data type within the square brackets.")
                        .usage(ParticleUtil.getNamesAsString())
                        .examples("play 1 of soul at location of player",
                                "play 10 of dust using dustOption(green, 10) at location of player",
                                "play 3 of item using player's tool at location of player",
                                "play 1 of block using dirt at location of player")
                        .since("1.9.0")
                        .parser(new Parser<Particle>() {

                            @Nullable
                            @Override
                            public Particle parse(String s, ParseContext context) {
                                return ParticleUtil.parse(s.replace(" ", "_"));
                            }

                            @Override
                            public String toString(Particle particle, int flags) {
                                return ParticleUtil.getName(particle);
                            }

                            @Override
                            public String toVariableNameString(Particle particle) {
                                return "particle:" + toString(particle, 0);
                            }

                            @Override
                            public String getVariableNamePattern() {
                                return "particle://s";
                            }
                        }));
            } else {
                Util.log("It looks like another addon registered 'particle' already. ");
                Util.log("You may have to use their particles in SkBee's 'particle spawn' effect.");
            }

            Classes.registerClass(new ClassInfo<>(DustOptions.class, "dustoption")
                    .name(ClassInfo.NO_DOC).user("dust options?"));

            Classes.registerClass(new ClassInfo<>(MaterialChoice.class, "materialchoice")
                    .name("Material Choice")
                    .user("material choices?")
                    .description("Represents a set of materials/minecraft tags which can be used in some recipes. ",
                            "Requires Minecraft 1.13+")
                    .usage("see material choice expression")
                    .examples("set {_a} to material choice of diamond sword, diamond shovel and diamond hoe",
                            "set {_a} to material choice of every sword",
                            "set {_a} to material choice of minecraft tag \"doors\"")
                    .since("INSERT VERSION")
                    .parser(new Parser<MaterialChoice>() {

                        @Override
                        public boolean canParse(ParseContext context) {
                            return false;
                        }

                        @Override
                        public @NotNull String toString(@NotNull MaterialChoice matChoice, int flags) {
                            return matChoiceToString(matChoice);
                        }

                        @Override
                        public String toVariableNameString(MaterialChoice matChoice) {
                            return "materialchoice:" + toString(matChoice, 0);
                        }

                        @Override
                        public String getVariableNamePattern() {
                            return "materialchoice://s";
                        }
                    }));

            Classes.registerClass(new ClassInfo<>(Tag.class, "minecrafttag")
                    .name("Minecraft Tag")
                    .user("(minecraft )?tags?")
                    .description("Represents a tag provided by Minecraft. Requires Minecraft 1.13+")
                    .usage("see Minecraft tag expression")
                    .examples("set {_i} to minecraft tag \"doors\"",
                            "set {_tag} to minecraft tag \"trapdoors\"",
                            "set {_tags::*} to minecraft tags \"wall_signs\" and \"wooden_doors\"",
                            "set {_tag} to \"minecraft:climbable\"", "",
                            "loop minecraft tags:",
                            "\tsend \"-%loop-value%\" to console")
                    .since("INSERT VERSION")
                    .parser(new Parser<Tag>() {

                        @Override
                        public boolean canParse(ParseContext context) {
                            return false;
                        }

                        @Override
                        public String toString(Tag tag, int flags) {
                            return tag.getKey().toString();
                        }

                        @Override
                        public String toVariableNameString(Tag tag) {
                            return "minecrafttag:" + toString(tag, 0);
                        }

                        @Override
                        public String getVariableNamePattern() {
                            return "minecrafttag://s";
                        }
                    }));
        }

        // == FUNCTIONS ==

        if (Skript.isRunningMinecraft(1, 13)) {
            // Function to create DustOptions
            Functions.registerFunction(new JavaFunction<DustOptions>("dustOption", new Parameter[]{
                    new Parameter<>("color", Classes.getExactClassInfo(Color.class), true, null),
                    new Parameter<>("size", Classes.getExactClassInfo(Number.class), true, null)
            }, Classes.getExactClassInfo(DustOptions.class), true) {
                @Nullable
                @Override
                public DustOptions[] execute(FunctionEvent e, Object[][] params) {
                    return new DustOptions[]{new DustOptions(((Color) params[0][0]).asBukkitColor(), ((Number) params[1][0]).floatValue())};
                }
            }.description("Creates a new dust option to be used with 'dust' particle. Color can either be a regular color or an RGB color using",
                    "Skript's rgb() function. Size is the size the particle will be.")
                    .examples("set {_c} to dustOption(red, 1.5)", "set {_c} to dustOption(rgb(1, 255, 1), 3)")
                    .since("1.9.0"));
        }
    }

    private static String matChoiceToString(MaterialChoice materialChoice) {
        List<String> itemTypes = new ArrayList<>();
        materialChoice.getChoices().forEach(material -> {
            itemTypes.add(new ItemType(material).toString());
        });
        return String.format("MaterialChoice{choices=[%s]}", StringUtils.join(itemTypes, ", "));
    }

}
