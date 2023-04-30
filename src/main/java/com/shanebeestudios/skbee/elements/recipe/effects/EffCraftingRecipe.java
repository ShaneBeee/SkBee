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
        "\tregister new shaped recipe for elytra using air, iron chestplate, air, air, iron chestplate and air with id \"my_recipes:elytra\"",
        "\tset {_s} to emerald named \"&3Strong Emerald\"",
        "\tregister new shaped recipe for {_s} using emerald, emerald, air, emerald, emerald and air with id \"strong_emerald\"",
        "\tregister new shaped recipe for diamond chestplate named \"&3Strong Emerald Chestplate\" using {_s}, air, {_s}, " +
                "{_s}, {_s}, {_s}, {_s}, {_s} and {_s} with id \"strong_emerald_chestplate\"", "",
        "\tset {_a} to material choice of every plank",
        "\tregister new shaped recipe for jigsaw block using {_a}, {_a}, {_a}, {_a}, {_a}, {_a}, {_a}, {_a} and {_a} with id \"jigsaw\""})
@Since("1.0.0")
public class EffCraftingRecipe extends Effect {

    private static final boolean CRAFTING_CATEGORY_EXISTS = Skript.classExists("org.bukkit.inventory.recipe.CraftingBookCategory");

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        String register = "register [a] [new] ";
        String recipeForUsingID = " recipe for %itemstack% (using|with ingredients) %recipechoices% (using|with (id|key)) %string/namespacedkey%";
        String inGroup = " [[and ](in|with) group %-string%]";
        String inCategory = CRAFTING_CATEGORY_EXISTS ? " [[and ]in category %-craftingcategory%]" : "";
        Skript.registerEffect(EffCraftingRecipe.class,
                register + "shaped" + recipeForUsingID + inGroup + inCategory,
                register + "shapeless" + recipeForUsingID + inGroup + inCategory);
    }

    @SuppressWarnings("null")
    private Expression<ItemStack> result;
    private Expression<RecipeChoice> ingredients;
    private Expression<Object> keyID;
    private Expression<String> group;
    private Expression<CraftingBookCategory> category;
    private boolean shaped;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        result = (Expression<ItemStack>) exprs[0];
        ingredients = (Expression<RecipeChoice>) exprs[1];
        keyID = (Expression<Object>) exprs[2];
        group = (Expression<String>) exprs[3];
        category = (Expression<CraftingBookCategory>) exprs[4];
        shaped = matchedPattern == 0;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemStack result = this.result.getSingle(event);
        RecipeChoice[] ingredients = this.ingredients.getAll(event);
        NamespacedKey key = RecipeUtil.getKey(this.keyID.getSingle(event));

        if (result == null) {
            RecipeUtil.error("Error registering crafting recipe - result is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (ingredients == null) {
            RecipeUtil.error("Error registering crafting recipe - ingredient is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (key == null) {
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

    private void registerShaped(ItemStack result, RecipeChoice[] ingredients, NamespacedKey key, @Nullable String group, @Nullable CraftingBookCategory category) {

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        if (group != null) recipe.setGroup(group);
        if (category != null) recipe.setCategory(category);

        // Character[ShapeKey]
        Character[] oldChar = new Character[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
        Character[] keyChar = new Character[9];
        for (int i = 0; i < 9; i++) {
            RecipeChoice recipeChoice = ingredients.length > i ? ingredients[i] : null;
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

        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logShapedRecipe(recipe);
        }
        Bukkit.addRecipe(recipe);
    }

    private void registerShapeless(ItemStack result, RecipeChoice[] ingredients, NamespacedKey key, @Nullable String group, @Nullable CraftingBookCategory category) {
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        if (group != null) recipe.setGroup(group);
        if (category != null) recipe.setCategory(category);

        for (RecipeChoice ing : ingredients) {
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
