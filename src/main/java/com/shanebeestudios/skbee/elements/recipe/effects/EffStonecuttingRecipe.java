package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

@SuppressWarnings({"ConstantConditions", "NullableProblems"})
@Name("Recipe - StoneCutting")
@Description({"Register a new stone cutting recipe.",
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens, a single colon and underscores,",
        "NOT SPACES!!! By default, if no namespace is provided, recipes will start with the namespace \"skbee:\",",
        "this can be changed in the config to whatever you want. IDs are used for recipe discovery/unlocking recipes for players.",
        "You may also include an optional group for recipes. These will group the recipes together in the recipe book.",
        "Requires MC 1.13+"})
@Examples({"on skript load:", "\tregister new stone cutting recipe for diamond using diamond ore with id \"cutting_diamond\""})
@RequiredPlugins("1.14+")
@Since("1.0.0")
public class EffStonecuttingRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        Skript.registerEffect(EffStonecuttingRecipe.class,
                "register [new] stone[ ]cutt(ing|er) recipe for %itemstack% (using|with ingredient) %recipechoice/itemtype% with id %string% [in group %-string%]");
    }

    @SuppressWarnings("null")
    private Expression<ItemStack> result;
    private Expression<Object> ingredient;
    private Expression<String> key;
    private Expression<String> group;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.result = (Expression<ItemStack>) exprs[0];
        this.ingredient = (Expression<Object>) exprs[1];
        this.key = (Expression<String>) exprs[2];
        this.group = (Expression<String>) exprs[3];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemStack result = this.result.getSingle(event);
        RecipeChoice recipeChoice = RecipeUtil.getRecipeChoice(this.ingredient.getSingle(event));

        if (result == null) {
            Skript.error("Error registering stonecutting recipe - result is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (ingredient == null) {
            Skript.error("Error registering stonecutting recipe - ingredient is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }

        String group = this.group != null ? this.group.getSingle(event) : null;

        NamespacedKey key = RecipeUtil.getKey(this.key.getSingle(event));
        if (key == null) {
            RecipeUtil.error("Current Item: §6'" + toString(event, true) + "'");
            return;
        }

        StonecuttingRecipe recipe = new StonecuttingRecipe(key, result, recipeChoice);
        if (group != null && !group.isBlank()) recipe.setGroup(group);

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logRecipe(recipe, recipe.getInputChoice());
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        String group = this.group != null ? " in group " + this.group.toString(event, debug) : "";
        return "register new stone cutting recipe for " + result.toString(event, debug) + " using " + ingredient.toString(event, debug) + group;
    }

}
