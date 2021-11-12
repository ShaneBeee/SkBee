package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.recipe.util.RecipeUtil;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
@Name("Recipe - Shaped/Shapeless")
@Description({"Register a new shaped/shapeless recipe for a specific item using custom ingredients.",
        "Recipes support items and material choices for ingredients. Material choices allow you to use Minecraft tags or lists of items.",
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens and underscores.",
        "IDs are used for recipe discovery/unlocking recipes for players.",
        "You may also include an optional group for recipes. These will group the recipes together in the recipe book.",
        "<b>NOTE:</b> Recipes with 4 or less ingredients will be craftable in the player's crafting grid.",
        "By default recipes will start with the namespace \"skrecipe:\", this can be changed in the SkBee config to whatever you want.",
        "Requires MC 1.13+"})
@Examples({"on load:",
        "\tregister new shaped recipe for elytra using air, iron chestplate, air, air, iron chestplate and air with id \"elytra\"",
        "\tset {_s} to emerald named \"&3Strong Emerald\"",
        "\tregister new shaped recipe for {_s} using emerald, emerald, air, emerald, emerald and air with id \"strong_emerald\"",
        "\tregister new shaped recipe for diamond chestplate named \"&3Strong Emerald Chestplate\" using {_s}, air, {_s}, " +
                "{_s}, {_s}, {_s}, {_s}, {_s} and {_s} with id \"strong_emerald_chestplate\"", "",
        "\tset {_m} to material choice of every plank",
        "\tregister new shaped recipe for jigsaw block using {_a}, {_a}, {_a}, {_a}, {_a}, {_a}, {_a}, {_a} and {_a} with id \"jigsaw\""})
@RequiredPlugins("1.13+")
@Since("1.0.0")
public class EffCraftingRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        Skript.registerEffect(EffCraftingRecipe.class,
                "register [new] (0¦shaped|1¦shapeless) recipe for %itemtype% (using|with ingredients) " +
                        "%itemtypes/materialchoices% with id %string% [in group %-string%]");
    }

    @SuppressWarnings("null")
    private Expression<ItemType> item;
    private Expression<Object> ingredients;
    private Expression<String> key;
    private Expression<String> group;
    private boolean shaped;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        item = (Expression<ItemType>) exprs[0];
        ingredients = (Expression<Object>) exprs[1];
        key = (Expression<String>) exprs[2];
        group = (Expression<String>) exprs[3];
        shaped = parseResult.mark == 0;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemType item = this.item.getSingle(event);
        Object[] ingredients = this.ingredients.getAll(event);

        if (item == null) {
            RecipeUtil.error("Error registering crafting recipe - result is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (ingredients == null) {
            RecipeUtil.error("Error registering crafting recipe - ingredient is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }

        String group = this.group != null ? this.group.getSingle(event) : null;
        NamespacedKey key = RecipeUtil.getKey(this.key.getSingle(event));

        // Remove duplicates on script reload
        RecipeUtil.removeRecipeByKey(key);

        if (shaped)
            registerShaped(item, ingredients, key, group);
        else
            registerShapeless(item, ingredients, key, group);
    }

    @SuppressWarnings("deprecation")
    private void registerShaped(ItemType item, Object[] ingredients, NamespacedKey key, String group) {
        boolean craftingTable = ingredients.length > 4;

        ShapedRecipe recipe = new ShapedRecipe(key, item.getRandom());
        if (group != null) recipe.setGroup(group);

        Character[] oldChar = new Character[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Character[] keyChar = new Character[9];
        for (int i = 0; i < 9; i++) {
            Object object = ingredients.length > i ? ingredients[i] : null;
            if (ingredients.length - 1 < i) {
                keyChar[i] = ' ';
            } else if (object instanceof ItemType && ((ItemType) object).getMaterial() == Material.AIR) {
                keyChar[i] = ' ';
            } else {
                keyChar[i] = oldChar[i];
            }
        }

        if (craftingTable) {
            String one = "" + keyChar[0] + keyChar[1] + keyChar[2];
            String two = "" + keyChar[3] + keyChar[4] + keyChar[5];
            String thr = "" + keyChar[6] + keyChar[7] + keyChar[8];
            recipe.shape(one, two, thr);
        } else {
            String one = "" + keyChar[0] + keyChar[1];
            String two = "" + keyChar[2] + keyChar[3];
            recipe.shape(one, two);
        }

        for (int i = 0; i < ingredients.length; i++) {
            Object object = ingredients[i];
            if (object instanceof ItemType) {
                ItemStack itemStack = ((ItemType) object).getRandom();
                Material material = itemStack.getType();

                // Make sure this item can be used in a recipe
                if (material != Material.AIR && material.isItem()) {

                    // If ingredient isn't a custom item, just register the material
                    if (itemStack.isSimilar(new ItemStack(material))) {
                        recipe.setIngredient(keyChar[i], material);
                    } else {
                        recipe.setIngredient(keyChar[i], new ExactChoice(itemStack));
                    }
                }
            } else if (object instanceof MaterialChoice) {
                recipe.setIngredient(keyChar[i], ((MaterialChoice) object));
            }
        }
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logShapedRecipe(recipe);
        }
        Bukkit.addRecipe(recipe);
    }

    @SuppressWarnings("deprecation")
    private void registerShapeless(ItemType item, Object[] ingredients, NamespacedKey key, String group) {
        ShapelessRecipe recipe = new ShapelessRecipe(key, item.getRandom());
        if (group != null) recipe.setGroup(group);

        for (Object ingredient : ingredients) {
            if (ingredient instanceof ItemType) {
                ItemStack itemStack = ((ItemType) ingredient).getRandom();
                Material material = itemStack.getType();

                // Make sure this item can be used in a recipe
                if (material != Material.AIR && material.isItem()) {

                    // If ingredient isn't a custom item, just register the material
                    if (itemStack.isSimilar(new ItemStack(material))) {
                        recipe.addIngredient(material);
                    } else {
                        recipe.addIngredient(new ExactChoice(itemStack));
                    }
                } else {
                    if (config.SETTINGS_DEBUG) {
                        RecipeUtil.warn("ERROR LOADING RECIPE: &7(&b" + key.getKey() + "&7)");
                        RecipeUtil.warn("Non item &b" + ((ItemType) ingredient).toString(0) + "&e found, this item will be removed from the recipe.");
                    }
                }
            } else if (ingredient instanceof MaterialChoice) {
                recipe.addIngredient(((MaterialChoice) ingredient));
            }
        }
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logShapelessRecipe(recipe);
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        return String.format("Register new %s recipe for %s using %s with id '%s' %s",
                shaped ? "shaped" : "shapeless",
                item.toString(e, d),
                ingredients.toString(e, d),
                key.toString(e, d),
                this.group != null ? "in group " + this.group.toString(e, d) : "");
    }

}
