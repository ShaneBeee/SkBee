package com.shanebeestudios.skbee.elements.recipe.type;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.recipe.RecipeType;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import org.bukkit.Keyed;
import org.bukkit.Tag;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Types {

    static {
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

                    public String getVariableNamePattern() {
                        return "materialchoice://s";
                    }
                }));

        Classes.registerClass(new ClassInfo<>(Recipe.class, "recipe")
                .name("Recipes - Recipe")
                .user("recipes?")
                .description("Represents a minecraft recipe which is used to collect information",
                        "Requires Minecraft 1.13+")
                .examples("set {_recipe} to recipe with id \"minecraft:oak_door\"", "set {_recipes::*} to recipes from id \"someplugin:custom_recipe\", \"myrecipe\"")
                .usage("See recipe from id expression")
                .since("INSERT VERSION")
                .parser(new Parser<Recipe>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(Recipe recipe, int flags) {
                        if (recipe instanceof Keyed recipeKey) {
                            return recipeKey.getKey().toString();
                        }
                        return recipe.toString();
                    }

                    @Override
                    public String toVariableNameString(Recipe recipe) {
                        return "recipe:" + toString(recipe, 0);
                    }
                }));

        EnumUtils<RecipeType> RECIPE_TYPE_ENUM = new EnumUtils<>(RecipeType.class);
        Classes.registerClass(new ClassInfo<>(RecipeType.class, "recipetype")
                .user("recipe ?types?")
                .name("Recipe Type")
                .description("Represents the types of recipes.")
                .usage(RECIPE_TYPE_ENUM.getAllNames())
                .since("2.6.0")
                .parser(RECIPE_TYPE_ENUM.getParser()));
    }

    private static String matChoiceToString(RecipeChoice.MaterialChoice materialChoice) {
        List<String> itemTypes = new ArrayList<>();
        materialChoice.getChoices().forEach(material -> itemTypes.add(new ItemType(material).toString()));
        return String.format("MaterialChoice{choices=[%s]}", StringUtils.join(itemTypes, ", "));
    }

}
