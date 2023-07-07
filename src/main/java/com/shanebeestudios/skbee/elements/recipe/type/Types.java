package com.shanebeestudios.skbee.elements.recipe.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.recipe.RecipeType;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public class Types {

    static {

        Classes.registerClass(new ClassInfo<>(RecipeChoice.class, "recipechoice")
                .user("recipe ?choices?")
                .name("Recipe - RecipeChoice")
                .description("Represents a set of minecraft tags, materials and itemstacks. Which can be used in all recipes.")
                .examples("set {_exactChoice} to exact choice from redstone named \"&4Red Diamond\" and lapis named \"&9Bluer Diamond\"",
                        "set {_materialChoice} to material choice using every sword",
                        "set {_materialChoice} to material choice using minecraft tag \"planks\"")
                .since("1.10.0 (MaterialChoice), INSERT VERSION (ExactChoice)")
                .usage("see the recipe choice expression")
                .parser(new Parser<RecipeChoice>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    @NotNull
                    public String toString(RecipeChoice recipeChoice, int i) {
                        return RecipeUtil.recipeChoiceToString(recipeChoice);
                    }

                    @Override
                    @NotNull
                    public String toVariableNameString(RecipeChoice recipeChoice) {
                        return toString(recipeChoice, 0);
                    }

                }));


        EnumWrapper<RecipeType> RECIPE_TYPE_ENUM = new EnumWrapper<>(RecipeType.class);
        Classes.registerClass(new ClassInfo<>(RecipeType.class, "recipetype")
                .user("recipe ?types?")
                .name("Recipe Type")
                .description("Represents the types of recipes.")
                .usage(RECIPE_TYPE_ENUM.getAllNames())
                .since("2.6.0")
                .parser(RECIPE_TYPE_ENUM.getParser()));

    }

}
