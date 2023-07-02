package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
@Name("Recipe - Shaped/Shapeless")
@Description({"Register a new shaped/shapeless recipe for a specific item using custom ingredients.",
        "Recipes support items and material choices for ingredients. Material choices allow you to use Minecraft tags or lists of items.",
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens, a single colon and underscores,",
        "NOT SPACES!!! By default, if no namespace is provided, recipes will start with the namespace \"skbee:\",",
        "this can be changed in the config to whatever you want. IDs are used for recipe discovery/unlocking recipes for players.",
        "You may also include an optional group for recipes. These will group the recipes together in the recipe book.",
        "<b>NOTE:</b> Recipes with 4 or less ingredients will be craftable in the player's crafting grid."})
@Examples({"on load:",
        "\tregister new shaped recipe for elytra using air, iron chestplate, air, air, iron chestplate and air with id \"my_recipes:elytra\"",
        "\tset {_s} to emerald named \"&3Strong Emerald\"",
        "\tregister new shaped recipe for {_s} using emerald, emerald, air, emerald, emerald and air with id \"strong_emerald\"",
        "\tregister new shaped recipe for diamond chestplate named \"&3Strong Emerald Chestplate\" using {_s}, air, {_s}, " +
                "{_s}, {_s}, {_s}, {_s}, {_s} and {_s} with id \"strong_emerald_chestplate\"", "",
        "\tset {_a} to material choice of every plank",
        "\tregister new shaped recipe for jigsaw block using {_a}, {_a}, {_a}, {_a}, {_a}, {_a}, {_a}, {_a} and {_a} with id \"jigsaw\""})
@RequiredPlugins("1.13+")
@Since("1.0.0")
public class EffCraftingRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        Skript.registerEffect(EffCraftingRecipe.class,
                "register [a] [new] shaped recipe for %itemstack% (using|with ingredients) %reicpechoices/itemtypes% with id %string% [in group %-string%]",
                "register [a] [new] shappeless recipe for %itemstack% (using|with ingredients) %reicpechoices/itemtypes% with id %string% [in group %-string%]");
    }

    @SuppressWarnings("null")
    private Expression<ItemStack> result;
    private Expression<Object> ingredients;
    private Expression<String> id;
    private Expression<String> group;
    private boolean shaped;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        result = (Expression<ItemStack>) exprs[0];
        ingredients = (Expression<Object>) exprs[1];
        id = (Expression<String>) exprs[2];
        group = (Expression<String>) exprs[3];
        shaped = parseResult.mark == 0;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemStack result = this.result.getSingle(event);
        Object[] ingredients = this.ingredients.getAll(event);

        if (result == null) {
            RecipeUtil.error("Error registering crafting recipe - result is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (ingredients == null) {
            RecipeUtil.error("Error registering crafting recipe - ingredients is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }

        String group = this.group != null ? this.group.getSingle(event) : null;
        String id = this.id.getSingle(event);
        NamespacedKey key = RecipeUtil.getKey(id);
        if (key == null) {
            RecipeUtil.error("Current Item: §6'" + toString(event, true) + "'");
            return;
        }

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);

        if (shaped)
            registerShaped(result, ingredients, key, group);
        else
            registerShapeless(result, ingredients, key, group);
    }

    private void registerShaped(ItemStack result, Object[] ingredients, NamespacedKey key, String group) {

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        if (group != null && !group.isBlank()) recipe.setGroup(group);

        Character[] oldChar = new Character[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Character[] keyChar = new Character[9];
        for (int i = 0; i < 9; i++) {
            RecipeChoice recipeChoice = ingredients.length > i ? RecipeUtil.getRecipeChoice(ingredients[i]) : null;
            if (ingredients.length - 1 < i) {
                keyChar[i] = ' ';
            } else if (recipeChoice == null) {
                keyChar[i] = ' ';
            } else {
                keyChar[i] = oldChar[i];
            }
        }

        String one, two, three = null;
        if (ingredients.length > 4) {
            one = "" + keyChar[0] + keyChar[1] + keyChar[2];
            two = "" + keyChar[3] + keyChar[4] + keyChar[5];
            three = "" + keyChar[6] + keyChar[7] + keyChar[8];
        } else {
            one = "" + keyChar[0] + keyChar[1];
            two = "" + keyChar[2] + keyChar[3];
        }

        List<String> shape = new ArrayList<>();
        if (!one.isBlank()) shape.add(one);
        if (!two.isBlank()) shape.add(two);
        if (three != null && !three.isBlank()) shape.add(two);
        recipe.shape(shape.toArray(new String[0]));

        for (int i = 0; i < ingredients.length; i++) {
            RecipeChoice recipeChoice = RecipeUtil.getRecipeChoice(ingredients[i]);
            if (recipeChoice == null) continue;
            recipe.setIngredient(keyChar[i], recipeChoice);
        }

        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logShapedRecipe(recipe);
        }
    }

    private void registerShapeless(ItemStack result, Object[] ingredients, NamespacedKey key, String group) {
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        if (group != null && !group.isBlank()) recipe.setGroup(group);

        for (Object ingredient : ingredients) {
            RecipeChoice recipeChoice = RecipeUtil.getExactChoice(ingredient);
            if (recipeChoice == null) {
                if (config.SETTINGS_DEBUG) {
                    RecipeUtil.warn("ERROR LOADING RECIPE: &7(&b" + key.getKey() + "&7)");
                    RecipeUtil.warn("Invalid item &b" + ingredient.toString() + "&e found, this item will be removed from the recipe.");
                }
                continue;
            }
            recipe.addIngredient(recipeChoice);
        }
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logShapelessRecipe(recipe);
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return String.format("Register new %s recipe for %s using %s with id '%s' %s",
                shaped ? "shaped" : "shapeless",
                result.toString(event, debug),
                ingredients.toString(event, debug),
                id.toString(event, debug),
                this.group != null ? "in group " + this.group.toString(event, debug) : "");
    }

}
