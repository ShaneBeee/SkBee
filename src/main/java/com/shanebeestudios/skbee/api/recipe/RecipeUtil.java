package com.shanebeestudios.skbee.api.recipe;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.Timespan;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeUtil {

    private static final String NAMESPACE = SkBee.getPlugin().getPluginConfig().RECIPE_NAMESPACE;
    private static final boolean COOKING_CATEGORY_EXISTS = Skript.classExists("org.bukkit.inventory.recipe.CookingBookCategory");
    private static final boolean CRAFTING_CATEGORY_EXISTS = Skript.classExists("org.bukkit.inventory.recipe.CraftingBookCategory");

    /**
     * Get a NamespacedKey from string
     * <p>If no namespace is provided, it will default to namespace in SkBee config (default = "skbee")</p>
     *
     * @param key Key for new NamespacedKey, ex: "plugin:key" or "minecraft:something"
     * @return New NamespacedKey
     */
    public static NamespacedKey getKey(Object key) {
        try {
            if (key instanceof NamespacedKey) {
                return (NamespacedKey) key;
            } else if (key instanceof String stringKey) {
                NamespacedKey namespacedKey;
                if (stringKey.contains(":")) {
                    namespacedKey = NamespacedKey.fromString(stringKey.toLowerCase(Locale.ROOT));
                } else {
                    namespacedKey = new NamespacedKey(NAMESPACE, stringKey.toLowerCase());
                }
                if (namespacedKey != null) {
                    return namespacedKey;
                }
            }
            error("Invalid namespaced key. Must be [a-z0-9/._-:]: " + key);
            return null;
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
            return null;
        }
    }

    /**
     * Gets the ItemStack used for a RecipeChoice
     *
     * @param recipeChoice RecipeChoice to get ItemStack from
     * @return Itemstack from given RecipeChoice
     */
    public static ItemStack getItemStack(RecipeChoice recipeChoice) {
        if (recipeChoice instanceof ExactChoice exactChoice) {
            return exactChoice.getItemStack().clone();
        } else if (recipeChoice instanceof MaterialChoice materialChoice) {
            return materialChoice.getItemStack().clone();
        }
        return new ItemStack(Material.AIR);
    }

    /**
     * Gets a RecipeChoice depending on the Item
     *
     * @param item Object to convert to a RecipeChoice
     * @return ExactChoice or MaterialChoice depending on Object
     */
    public static RecipeChoice getRecipeChoice(Object item) {
        if (item instanceof ItemStack || item instanceof ItemType) {
            // Skript gives ItemType priority when used in Object class
            ItemStack itemStack = item instanceof ItemStack ? (ItemStack) item : ((ItemType) item).getRandom();
            Material material = itemStack.getType();
            boolean isAir = material.isAir();
            boolean isSimilar = itemStack.isSimilar(new ItemStack(material));
            if (isAir) {
                return null;
            } else if (isSimilar) {
                return new MaterialChoice(material);
            }
            return new ExactChoice(itemStack);
        } else if (item instanceof MaterialChoice materialChoice) {
            return materialChoice;
        }
        return null;
    }

    /**
     * @param items Object to convert to a RecipeChoice
     * @return ExactChoice or MatrerialChoice depending on Object
     */
    public static RecipeChoice[] getRecipeChoices(Object... items) {
        List<RecipeChoice> recipeChoices = new ArrayList<>();
        for (Object item : items) {
            recipeChoices.add(getRecipeChoice(item));
        }
        return recipeChoices.toArray(new RecipeChoice[0]);
    }

    /**
     * Gets the ingredients of a crafting recipe
     *
     * @param recipe recipe to get the ingredients of
     * @return An array of ingredients for a recipe
     */
    public static ItemStack[] getCraftingIngredients(Recipe recipe) {
        List<ItemStack> ingredients = new ArrayList<>();
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            for (Map.Entry<Character, RecipeChoice> entry : shapedRecipe.getChoiceMap().entrySet()) {
                ingredients.add(getItemStack(entry.getValue()));
            }
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            for (RecipeChoice recipeChoice : shapelessRecipe.getChoiceList()) {
                ingredients.add(getItemStack(recipeChoice));
            }
        }
        return ingredients.toArray(new ItemStack[0]);
    }


    /**
     * Log a recipe to console
     * Mainly used for debugging purposes
     *
     * @param recipe      Recipe to log
     * @param ingredients Ingredients of recipe to log
     */
    public static void logRecipe(Recipe recipe, RecipeChoice... ingredients) {
        if (!(recipe instanceof Keyed)) return;
        log("&aRegistered new recipe: &7(&b%s&7)", ((Keyed) recipe).getKey().toString());
        if (recipe instanceof StonecuttingRecipe stonecuttingRecipe && !stonecuttingRecipe.getGroup().isBlank()) {
            log(" - &7Group: &r\"&6%s&r\"", stonecuttingRecipe.getGroup());
        }
        log(" - &7Result: &e%s", recipe.getResult());
        log(" - &7Ingredients:");
        for (RecipeChoice ingredient : ingredients) {
            log("   - %s", getFancy(ingredient));
        }

    }

    public static void logCookingRecipe(CookingRecipe<?> recipe) {
        log("&aRegistered new cooking recipe: &7(&b%s&7)", ((Keyed) recipe).getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult());
        String group = recipe.getGroup();
        if (!group.isBlank()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        } else {
            log(" - &7Group: &6Undefined");
        }
        if (COOKING_CATEGORY_EXISTS) {
            log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory());
        } else {
            log(" - &7Category: &r\"&6Misc&r\"");
        }
        log(" - &7CookTime: &b%s", Timespan.fromTicks_i(recipe.getCookingTime()));
        log(" - &7Experience: &b%s", recipe.getExperience());
        log(" - &7Ingredients: %s", getFancy(recipe.getInputChoice()));
    }

    /**
     * Log a shapeless recipe to console
     *
     * @param recipe Recipe to log
     */
    public static void logShapelessRecipe(ShapelessRecipe recipe) {
        log("&aRegistered new shapeless recipe: &7(&b%s&7)", recipe.getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult());
        String group = recipe.getGroup();
        if (!group.isBlank()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        } else {
            log(" - &7Group: &6Undefined");
        }
        if (CRAFTING_CATEGORY_EXISTS) {
            log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory());
        } else {
            log(" - &7Category: &r\"&6Misc&r\"");
        }
        log(" - &7Ingredients:");
        recipe.getChoiceList().forEach(recipeChoice ->
                log("   - &6%s", getFancy(recipeChoice)));
    }

    /**
     * Log a shaped recipe to console
     *
     * @param recipe Recipe to log
     */
    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    public static void logShapedRecipe(ShapedRecipe recipe) {
        log("&aRegistered new shaped recipe: &7(&b%s&7)", recipe.getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult());

        String group = recipe.getGroup();
        if (!group.isBlank()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        } else {
            log(" - &7Group: &6Undefined");
        }
        if (CRAFTING_CATEGORY_EXISTS) {
            log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory());
        } else {
            log(" - &7Category: &r\"&6Misc&r\"");
        }

        String[] shape = recipe.getShape();
        String grid = " - &7Shape: &r[&d%s&r]";
        if (shape.length > 1) grid += "&7, &r[&d%s&r]";
        if (shape.length > 2) grid += "&7, &r[&d%s&r]";
        log(grid, shape);
        log(" - &7Ingredients:");
        recipe.getChoiceMap().forEach((character, recipeChoice) ->
                log("   - &r'&d%s&r' = &6%s", character, getFancy(recipeChoice)));
    }

    private static String getFancy(RecipeChoice matChoice) {
        if (matChoice == null) return "&r[&bAIR&r]";
        return matChoice.toString()
                .replace("MaterialChoice{choices=", "")
                .replace("ExactChoice{choices=", "")
                .replace("[", "&r[&b")
                .replace(",", "&r,&b")
                .replace("]}", "&r]");
    }

    /**
     * Send an error to console prefixed with [Recipe]
     *
     * @param error Error to log
     */
    public static void error(String error) {
        log("&c" + error);
    }

    /**
     * Send a warning to console prefixed with [Recipe]
     *
     * @param warning Warning to log
     */
    public static void warn(String warning) {
        log("&e" + warning);
    }

    /**
     * Log to console prefixed with [Recipe]
     *
     * @param log Message to log
     */
    public static void log(String log) {
        String prefix = "&7[&bRecipe&7] ";
        Util.log(prefix + log);
    }

    public static void log(String format, Object... objects) {
        log(String.format(format, objects));
    }

}
