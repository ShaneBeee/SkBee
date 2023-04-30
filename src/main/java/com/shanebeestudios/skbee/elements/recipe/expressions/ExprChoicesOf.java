package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.Ingredient;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Recipe - Choices")
@Description("Gets the choices of an ingredient or recipe choice")
@Examples({"set {_mChoice} to material choice using every sword",
        "send choices of {_mChoice}",
        "",
        "set {_ingredients::*} to ingredients of recipe with id \"minecraft:crafting_table\"",
        "send choices of {_ingredients::*}"})
@Since("INSERT VERSION")
public class ExprChoicesOf extends PropertyExpression<Object, ItemStack> {

    static {
        register(ExprChoicesOf.class, ItemStack.class, "choices", "recipechoices/ingredients");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr(exprs[0]);
        return true;
    }

    @Override
    protected ItemStack[] get(Event event, Object[] objects) {
        List<ItemStack> items = new ArrayList<>();
        for (Object object : objects) {
            items.addAll(getChoices(object));
        }
        return items.toArray(new ItemStack[0]);
    }

    private List<ItemStack> getChoices(Object object) {
        List<ItemStack> items = new ArrayList<>();
        if (object instanceof Ingredient ingredient) {
            items.addAll(getChoices(ingredient.recipeChoice()));
        } else if (object instanceof ExactChoice exactChoice) {
            items.addAll(exactChoice.getChoices());
        } else if (object instanceof MaterialChoice materialChoice) {
            materialChoice.getChoices().forEach(material -> items.add(new ItemStack(material)));
        } else {
            items.add(new ItemStack(Material.AIR));
        }
        return items;
    }

    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "choices of " + getExpr().toString(event, debug);
    }

}
