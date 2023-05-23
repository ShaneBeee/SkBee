package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import org.bukkit.event.Event;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - RecipeChoice")
@Description({"Gets a material choice or exact choice using a set of items or singular item",
        "material choice will only care for the material type, where exact choice will care for additional information like item names"})
@Examples({"set {_materialChoice} to material choice using every weapon and any tool",
        "set {_exactChoice} to exact choice using {_item1}, {_item2}, {_item3}",
        "",
        "set {_materialChoice} to material choice using {_materialChoice}, all armor and turtle helmet",})
@Since("INSERT VERSION")
public class ExprRecipeChoice extends SimpleExpression<RecipeChoice> {

    private static final boolean MINECRAFT_TAGS_ENABLED = SkBee.getPlugin().getPluginConfig().ELEMENTS_MINECRAFT_TAG;

    private Expression<Object> choices;
    private boolean exactRecipe;

    static {
        Skript.registerExpression(ExprRecipeChoice.class, RecipeChoice.class, ExpressionType.SIMPLE,
                "exact [recipe[ ]]choice (of|using) %itemtypes/recipechoices%",
                "material [recipe[ ]]choice (of|using) %itemtypes/recipechoices" + (MINECRAFT_TAGS_ENABLED ? "/minecrafttags" : "") + "%");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        exactRecipe = matchedPattern == 0;
        choices = (Expression<Object>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected RecipeChoice[] get(Event event) {
        RecipeChoice recipeChoice = null;
        if (exactRecipe) {
            recipeChoice = RecipeUtil.getExactChoice(choices.getArray(event));
        } else {
            recipeChoice = RecipeUtil.getMaterialChoice(choices.getArray(event));
        }
        if (recipeChoice == null) return null;
        return new RecipeChoice[]{recipeChoice};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends RecipeChoice> getReturnType() {
        return RecipeChoice.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return (exactRecipe ? "exact" : "material") + " choice using " + choices.toString(event, debug);
    }

}
