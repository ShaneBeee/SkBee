package com.shanebeestudios.skbee.elements.recipe.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.recipe.Ingredient;
import com.shanebeestudios.skbee.api.recipe.RecipeType;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(RecipeChoice.class, "recipechoice")
                .name("Recipe Choice")
                .user("recipe choices?")
                .description("Represents a set of materials/minecraft tags/itemstacks which can be used in most recipes")
                .usage("see material choice expression")
                .examples("set {_a} to material choice of diamond sword, diamond shovel and diamond hoe",
                        "set {_a} to material choice of every sword",
                        "set {_a} to material choice of minecraft tag \"doors\"")
                .since("1.10.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(@NotNull RecipeChoice recipeChoice, int flags) {
                        return RecipeUtil.recipeChoiceToString(recipeChoice);
                    }

                    @Override
                    public String toVariableNameString(RecipeChoice recipeChoice) {
                        return "recipechoice: " + toString(recipeChoice, 0);
                    }
                }));

        Classes.registerClass(new ClassInfo<>(Ingredient.class, "ingredient")
                .user("ingredients?")
                .name("Recipe - Ingredient")
                .description("Represents an ingredient for a recipe. See expression for more details.")
                .since("INSERT VERSION")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Ingredient ingredient, int flags) {
                        return ingredient.toString();
                    }

                    @Override
                    public @NotNull String toVariableNameString(Ingredient ingredient) {
                        return "ingredient:'" + toString(ingredient, 0) + "'";
                    }
                }));

        Classes.registerClass(new ClassInfo<>(Recipe.class, "recipe")
                .name("Recipes - Recipe")
                .user("recipes?")
                .description("Represents a Minecraft recipe which is used to collect information.")
                .examples("set {_recipe} to recipe with id \"minecraft:oak_door\"", "set {_recipes::*} to recipes from id \"someplugin:custom_recipe\", \"myrecipe\"")
                .usage("See recipe from id expression")
                .since("INSERT VERSION")
                .parser(new Parser<>() {

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
                .since("2.6.0")
                .usage(RECIPE_TYPE_ENUM.getAllNames())
                .parser(RECIPE_TYPE_ENUM.getParser()));

        if (Skript.classExists("org.bukkit.inventory.recipe.CookingBookCategory")) {
            EnumUtils<CookingBookCategory> COOKING_BOOK_CATEGORY_ENUM = new EnumUtils<>(CookingBookCategory.class, null, "category");
            Classes.registerClass(new ClassInfo<>(CookingBookCategory.class, "cookingcategory")
                    .user("cooking ?categor(y|ies)")
                    .name("Recipes - Cooking Category")
                    .description("Represents the type of cooking recipe book categories.")
                    .since("INSERT VERSION")
                    .usage(COOKING_BOOK_CATEGORY_ENUM.getAllNames())
                    .parser(COOKING_BOOK_CATEGORY_ENUM.getParser()));
        }

        if (Skript.classExists("org.bukkit.inventory.recipe.CraftingBookCategory")) {
            EnumUtils<CraftingBookCategory> CRAFTING_BOOK_CATEGORY_ENUM = new EnumUtils<>(CraftingBookCategory.class, null, "category");
            Classes.registerClass(new ClassInfo<>(CraftingBookCategory.class, "craftingcategory")
                    .user("crafting ?categor(y|ies)")
                    .name("Recipes - Crafting Category")
                    .description("Represents the type of crafting recipe book categories.")
                    .since("INSERT VERSION")
                    .usage(CRAFTING_BOOK_CATEGORY_ENUM.getAllNames())
                    .parser(CRAFTING_BOOK_CATEGORY_ENUM.getParser()));
        }

    }

}
