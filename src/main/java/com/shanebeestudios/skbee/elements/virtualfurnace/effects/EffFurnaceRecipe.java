package com.shanebeestudios.skbee.elements.virtualfurnace.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.vf.api.RecipeManager;
import com.shanebeestudios.vf.api.recipe.FurnaceRecipe;
import com.shanebeestudios.vf.api.util.Util;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"ConstantConditions", "NullableProblems"})
@Name("VirtualFurnace - Furnace Recipe")
@Description("Register recipes for virtual furnaces. Alternatively you can just register all vanilla-like recipes for" +
        "your virtual furnaces. ")
@Examples({"on load:",
        "\tregister virtual furnace recipe for cooked chicken using raw chicken with cooktime 15 seconds",
        "on load:",
        "\tregister all virtual furnace recipes"})
@Since("1.3.0")
public class EffFurnaceRecipe extends Effect {

    private static final RecipeManager RECIPE_MANAGER = SkBee.getPlugin().getVirtualFurnaceAPI().getRecipeManager();

    static {
        Skript.registerEffect(EffFurnaceRecipe.class,
                "register virtual furnace recipe for %itemtype% using %itemtype% [with cooktime %-timespan%]",
                "register all virtual (furnace|1:smoker|2:blast furnace) recipes");
    }

    private Expression<ItemType> ingredient;
    private Expression<ItemType> result;
    private Expression<Timespan> cookTime;
    private boolean vanilla;
    private int type;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        vanilla = pattern == 1;
        type = parseResult.mark;
        if (!vanilla) {
            ingredient = (Expression<ItemType>) exprs[1];
            result = (Expression<ItemType>) exprs[0];
            cookTime = (Expression<Timespan>) exprs[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (vanilla) {
            List<FurnaceRecipe> recipes;
            switch (type) {
                default:
                case 0:
                    recipes = FurnaceRecipe.getVanillaFurnaceRecipes();
                    break;
                case 1:
                    recipes = FurnaceRecipe.getVanillaSmokingRecipes();
                    break;
                case 2:
                    recipes = FurnaceRecipe.getVanillaBlastingRecipes();
            }
            for (FurnaceRecipe recipe : recipes) {
                RECIPE_MANAGER.registerFurnaceRecipe(recipe);
            }
        } else {
            Material ing = this.ingredient.getSingle(event).getMaterial();
            Material result = this.result.getSingle(event).getMaterial();
            int cook = this.cookTime != null ? (int) this.cookTime.getSingle(event).getTicks() : 200;
            String key = "recipe_" + ing.toString() + "_" + result.toString() + "_" + cook;
            FurnaceRecipe recipe = new FurnaceRecipe(Util.getKey(key), ing, result, cook);
            RECIPE_MANAGER.registerFurnaceRecipe(recipe);
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "register new virtual furnace recipe using " + this.ingredient.toString(e, d) + " with result "
                + this.result.toString(e, d) + (this.cookTime != null ? " with cook time " + this.cookTime.toString(e, d) : "");
    }

}
