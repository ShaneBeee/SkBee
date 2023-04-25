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
import com.shanebeestudios.skbee.api.recipe.Ingredient;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Ingredient")
@Description({"Represents an ingredient for a recipe. This is a key value system.",
        "\nKey: a single character, must be a number of letter (Variables will not work here).",
        "\nValue: an item or material choice (see material choice expression for more info)."})
@Examples({"set {_ing} to 1:diamond sword",
        "set {_ing} to d:diamond",
        "set {_ing} to w:white wool",
        "set {_ing} to m:material choice of every sword"})
@Since("INSERT VERSION")
public class ExprRecipeIngredient extends SimpleExpression<Ingredient> {

    static {
        Skript.registerExpression(ExprRecipeIngredient.class, Ingredient.class, ExpressionType.PATTERN_MATCHES_EVERYTHING,
                "<[a-zA-Z0-9]>\\:%itemtype/itemstack/materialchoice%");
    }

    private char key;
    private Expression<Object> item;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.key = parseResult.regexes.get(0).group().charAt(0);
        this.item = (Expression<Object>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Ingredient[] get(Event event) {
        Object item = this.item.getSingle(event);
        return new Ingredient[]{new Ingredient(key, item)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Ingredient> getReturnType() {
        return Ingredient.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "ingredient: " + this.key + ":" + this.item.toString(e, d);
    }

}
