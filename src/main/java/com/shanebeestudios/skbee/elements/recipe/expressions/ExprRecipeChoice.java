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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - RecipeChoice")
@Description("Create a recipe choice for exact match or material matching")
@Examples({"on load:",
        "\tset {rc::every_sword} to material choice of every sword",
        "\tset {rc::every_sword_named} to exact choice of every sword named \"sample test\""})
@Since("1.10.0 (MaterialChoice), INSERT VERSION (ExactChoice)")
public class ExprRecipeChoice extends SimpleExpression<RecipeChoice> {

    static {
        String tagEnabled = SkBee.getPlugin().getPluginConfig().ELEMENTS_MINECRAFT_TAG ? "/minecrafttags" : "";
        Skript.registerExpression(ExprRecipeChoice.class, RecipeChoice.class, ExpressionType.SIMPLE,
                "material[ ]choice (from|using|of) %recipechoices/itemtypes" + tagEnabled + "%",
                "exact[ ]choice (from|using|of) %recipechoices/itemtypes%");
    }

    private boolean useExactChoice;
    private Expression<Object> choices;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parse) {
        useExactChoice = matchedPattern == 1;
        choices = (Expression<Object>) exprs[0];
        return true;
    }

    @Override
    protected RecipeChoice[] get(Event event) {
        Object[] objects = choices.getArray(event);
        RecipeChoice recipeChoice = useExactChoice ? RecipeUtil.getExactChoice(objects) : RecipeUtil.getMaterialChoice(objects);
        if (recipeChoice == null) return null;
        return new RecipeChoice[]{recipeChoice};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends RecipeChoice> getReturnType() {
        return RecipeChoice.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return String.format("%s choice using %s",
                useExactChoice ? "exact" : "material",
                choices.toString(event, debug));
    }

}
