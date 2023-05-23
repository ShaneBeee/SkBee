package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
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
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Recipe - Shaped/Shapeless")
@Description({"Register a new shaped/shapeless recipe for a specific item using custom ingredients.",
        "Recipes support items and material choices for ingredients. Material choices allow you to use Minecraft tags or lists of items.",
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens, a single colon and underscores,",
        "NOT SPACES!!! By default, if no namespace is provided, recipes will start with the namespace \"skbee:\",",
        "this can be changed in the config to whatever you want. IDs are used for recipe discovery/unlocking recipes for players.",
        "You may also include an optional group for recipes. These will group the recipes together in the recipe book.",
        "<b>NOTE:</b> Recipes with 4 or less ingredients will be craftable in the player's crafting grid.",
        "Requires MC 1.19+ to use categories"})
@Examples({"on load:",
        "\tset {_item} to emerald named \"&aWeak Emerald\"",
        "\tregister new shapeless recipe for 4 emeralds using {_item} with id \"ores:emerald_from_weak_emerald\"",
        "\tregister new shapeless recipe for {_item} using {_item} with id \"ores:weak_emerald\" in group \"ores\"",
        "\tregister new shapeless recipe for emerald named \"&2Strong Emerald\" using {_item},{_item},{_item},{_item} with id \"ores:weak_emerald\" in category misc category",
        "",
        "on load:",
        "\tset {_item} to diamond named \"&bWeak Diamond\"",
        "\tregister new shaped recipe for 4 diamonds using {_item} with id \"ores:diamond_from_weak_diamond\"",
        "\tregister new shaped recipe for {_item} using diamond,diamond,diamond,diamond with id \"ores:weak_diamond\" in group \"ores\"",
        "\tregister new shaped recipe for diamond named \"&9Strong Diamond\" using {_item},{_item},{_item},{_item} with id \"ores:strong_diamond\" in group \"ores\" in category misc category"})
@Since("1.0.0")
public class EffCraftingRecipe extends Effect {

    private static final boolean CRAFTING_CATEGORY_EXISTS = Skript.classExists("org.bukkit.inventory.recipe.CraftingBookCategory");
    private static final boolean USE_EXPERIMENTAL_SYNTAX = SkBee.getPlugin().getPluginConfig().RECIPE_EXPERIMENTAL_SYNTAX;
    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        String STRING_PATTERN = USE_EXPERIMENTAL_SYNTAX ? "with (key|id) %namespacedkey%" : "with id %string%";
        String register = "register [a] [new] ";
        String recipeForUsingID = " recipe for %itemstack% (using|with ingredients) %itemtypes/recipechoices% " + STRING_PATTERN;
        String inGroup = " [[and] (in|with) group %-string%]";
        String inCategory = CRAFTING_CATEGORY_EXISTS ? " [[and] in [category] %-craftingcategory%]" : "";
        Skript.registerEffect(EffCraftingRecipe.class,
                register + "shaped" + recipeForUsingID + inGroup + inCategory,
                register + "shapeless" + recipeForUsingID + inGroup + inCategory);
    }

    private Expression<ItemStack> result;
    private Expression<Object> ingredients;
    private Expression<Object> keyID;
    private Expression<String> group;
    private Expression<CraftingBookCategory> category;
    private boolean shaped;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        result = (Expression<ItemStack>) exprs[0];
        ingredients = (Expression<Object>) exprs[1];
        keyID = (Expression<Object>) exprs[2];
        group = (Expression<String>) exprs[3];
        category = (Expression<CraftingBookCategory>) exprs[4];
        shaped = matchedPattern == 0;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemStack result = this.result.getSingle(event);
        Object[] ingredients = this.ingredients.getAll(event);
        NamespacedKey key = RecipeUtil.getKey(this.keyID.getSingle(event));

        if (result == null) {
            RecipeUtil.error("Error registering crafting recipe - result is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        } else if (ingredients.length == 0) {
            RecipeUtil.error("Error registering crafting recipe - ingredient is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        } else if (ingredients.length > 9) {
            RecipeUtil.error("Error registering crafting recipe - ingredient is too large");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        } else if (key == null) {
            RecipeUtil.error("Error registering crafting recipe - key is null");
            RecipeUtil.error("Current Item: §6'" + this.toString(event, true) + "'");
            return;
        }
        String group = this.group != null ? this.group.getSingle(event) : null;
        CraftingBookCategory category = this.category != null ? this.category.getSingle(event) : null;
        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        if (shaped)
            registerShaped(result, ingredients, key, group, category);
        else
            registerShapeless(result, ingredients, key, group, category);
    }

    private void registerShaped(ItemStack result, Object[] ingredients, NamespacedKey key, @Nullable String group, @Nullable CraftingBookCategory category) {

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        if (group != null) recipe.setGroup(group);
        if (category != null) recipe.setCategory(category);

        // Character[ShapeKey]
        Character[] oldChar = new Character[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
        Character[] keyChar = new Character[9];
        for (int i = 0; i < 9; i++) {
            RecipeChoice recipeChoice = ingredients.length > i ? RecipeUtil.getRecipeChoice(ingredients[i]) : null;
            if (ingredients.length - 1 < i) {
                keyChar[i] = ' ';
            } else if (RecipeUtil.getRecipeChoice(recipeChoice) == null) {
                keyChar[i] = ' ';
            } else {
                keyChar[i] = oldChar[i];
            }
        }
        String one, two, three = null;
        if (ingredients.length > 4) {
            one = String.valueOf(keyChar[0]) + keyChar[1] + keyChar[2];
            two = String.valueOf(keyChar[3]) + keyChar[4] + keyChar[5];
            three = String.valueOf(keyChar[6]) + keyChar[7] + keyChar[8];
        } else {
            one = String.valueOf(keyChar[0]) + keyChar[1];
            two = String.valueOf(keyChar[2]) + keyChar[3];
        }

        // Only register a row if it has a key in it
        List<String> shape = new ArrayList<>();
        if (!one.isBlank()) shape.add(one);
        if (!two.isBlank()) shape.add(two);
        if (three != null && !three.isBlank()) shape.add(three);
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

    private void registerShapeless(ItemStack result, Object[] ingredients, NamespacedKey key, @Nullable String group, @Nullable CraftingBookCategory category) {
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        if (group != null) recipe.setGroup(group);
        if (category != null) recipe.setCategory(category);

        for (Object ing : ingredients) {
            RecipeChoice ingredient = RecipeUtil.getRecipeChoice(ing);
            if (ingredient == null) {
                if (config.SETTINGS_DEBUG) {
                    RecipeUtil.warn("ERROR LOADING RECIPE: &7(&b" + key.getKey() + "&7)");
                    RecipeUtil.warn("Non recipe choice &b" + ing + "&e found, this ingredient will be removed from the recipe.");
                }
                continue;
            }
            recipe.addIngredient(ingredient);
        }
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logShapelessRecipe(recipe);
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return String.format("register new %s for %s using %s with id %s%s%s",
                shaped ? "shaped" : "shapeless",
                result.toString(event, debug),
                ingredients.toString(event, debug),
                keyID.toString(event, debug),
                this.group != null ? " and in group " + this.group.toString(event, debug) : "",
                this.category != null && CRAFTING_CATEGORY_EXISTS ? " in category" + this.category.toString(event, debug) : "");
    }

}
