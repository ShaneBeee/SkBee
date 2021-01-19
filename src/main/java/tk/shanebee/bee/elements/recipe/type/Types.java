package tk.shanebee.bee.elements.recipe.type;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import org.bukkit.Tag;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Types {

    static {
        if (Skript.isRunningMinecraft(1, 13)) {
            Classes.registerClass(new ClassInfo<>(RecipeChoice.MaterialChoice.class, "materialchoice")
                    .name("Material Choice")
                    .user("material choices?")
                    .description("Represents a set of materials/minecraft tags which can be used in some recipes. ",
                            "Requires Minecraft 1.13+")
                    .usage("see material choice expression")
                    .examples("set {_a} to material choice of diamond sword, diamond shovel and diamond hoe",
                            "set {_a} to material choice of every sword",
                            "set {_a} to material choice of minecraft tag \"doors\"")
                    .since("1.10.0")
                    .parser(new Parser<RecipeChoice.MaterialChoice>() {

                        @Override
                        public boolean canParse(ParseContext context) {
                            return false;
                        }

                        @Override
                        public @NotNull String toString(@NotNull RecipeChoice.MaterialChoice matChoice, int flags) {
                            return matChoiceToString(matChoice);
                        }

                        @Override
                        public String toVariableNameString(RecipeChoice.MaterialChoice matChoice) {
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
                    .since("1.10.0")
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
    }

    private static String matChoiceToString(RecipeChoice.MaterialChoice materialChoice) {
        List<String> itemTypes = new ArrayList<>();
        materialChoice.getChoices().forEach(material -> itemTypes.add(new ItemType(material).toString()));
        return String.format("MaterialChoice{choices=[%s]}", StringUtils.join(itemTypes, ", "));
    }
}
