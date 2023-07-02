package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import org.bukkit.event.Event;
import org.bukkit.inventory.RecipeChoice;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("RecipeChoice - Choices")
@Description("Get all choices of a recipe choice")
@Examples({"broadcast choices of material choice using every sword",
        "broadcast choices of exact choice using every sword named \"Sample Text\""})
@Since("INSERT VERSION")
public class ExprChoicesOf extends PropertyExpression<RecipeChoice, ItemType> {

    static {
        // Currently little to no use of this expression, until ExprIngredientsOfRecipe change to return RecipeChoice
        register(ExprChoicesOf.class, ItemType.class, "choices", "recipechoices");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<RecipeChoice>) exprs[0]);
        return true;
    }

    @Override
    protected ItemType[] get(Event event, RecipeChoice[] recipeChoices) {
        List<ItemType> choices = new ArrayList<>();
        for (RecipeChoice recipeChoice : getExpr().getArray(event)) {
            choices.addAll(RecipeUtil.getChoices(recipeChoice));
        }
        return choices.toArray(new ItemType[0]);
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "choices of " + getExpr().toString(event, debug);
    }
}
