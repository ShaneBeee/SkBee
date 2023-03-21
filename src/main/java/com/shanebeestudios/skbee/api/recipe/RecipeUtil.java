package com.shanebeestudios.skbee.api.recipe;

import ch.njol.skript.util.Timespan;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

public class RecipeUtil {

    private static final String NAMESPACE = SkBee.getPlugin().getPluginConfig().RECIPE_NAMESPACE;

    /**
     * Get a NamespacedKey from string
     * <p>If no namespace is provided, it will default to namespace in SkBee config (default = "skbee")</p>
     *
     * @param key Key for new NamespacedKey, ex: "plugin:key" or "minecraft:something"
     * @return New NamespacedKey
     */
    public static NamespacedKey getKey(String key) {
        try {
            NamespacedKey namespacedKey;
            if (key.contains(":")) {
                namespacedKey = NamespacedKey.fromString(key.toLowerCase(Locale.ROOT));
            } else {
                namespacedKey = new NamespacedKey(NAMESPACE, key.toLowerCase());
            }
            if (namespacedKey == null) {
                error("Invalid namespaced key. Must be [a-z0-9/._-:]: " + key);
                return null;
            }
            return namespacedKey;
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
            return null;
        }
    }

    /**
     * Remove all Minecraft recipes registered to the server
     */
    public static void removeAllMCRecipes() {
        try {
            List<Recipe> recipes = new ArrayList<>();
            Bukkit.recipeIterator().forEachRemaining(recipe -> {
                if (recipe instanceof Keyed && !((Keyed) recipe).getKey().getNamespace().equalsIgnoreCase("minecraft")) {
                    recipes.add(recipe);
                }
            });
            Bukkit.clearRecipes();
            recipes.forEach(Bukkit::addRecipe);
        } catch (NoSuchElementException ignore) {
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
        }
        else if (recipeChoice instanceof MaterialChoice materialChoice) {
            return materialChoice.getItemStack().clone();
        }
        return new ItemStack(Material.AIR);
    }

    /**
     *
     * @param itemStack ItemStack to convert to a RecipeChoice
     * @return ExactChoice or MaterialChoice depending on ItemStack
     */
    public static RecipeChoice getRecipeChoice(ItemStack itemStack) {
        if(itemStack == null)
            return null;
        Material material = itemStack.getType();
        boolean isAir = material.isAir();
        boolean isSimilar = itemStack.isSimilar(new ItemStack(material));
        if (isAir) {
            return null;
        }
        else if (isSimilar) {
            return  new MaterialChoice(material);
        }
        return new ExactChoice(itemStack);
    }

    /**
     *
     * @param itemStacks ItemStacks to convert to a RecipeChoice
     * @return ExactChoice or MatrerialChoice depending on ItemStack
     */
    public static RecipeChoice[] getRecipeChoices(ItemStack ...itemStacks) {
        List<RecipeChoice> recipeChoices = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            recipeChoices.add(getRecipeChoice(itemStack));
        }
        return recipeChoices.toArray(new RecipeChoice[0]);
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
        if (group.length() > 0) {
            log(" - &7Group: &r\"&6%s&r\"", group);
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
        if (group.length() > 0) {
            log(" - &7Group: &r\"&6%s&r\"", group);
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
        if (group.length() > 0) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        }

        String[] shape = recipe.getShape();
        String grid = " - &7Shape: &r[&d%s&r]&7";
        if (shape.length > 1) grid += "&7, &r[&d%s&r]";
        if (shape.length > 2) grid += "&7, &r[&d%s&r]";
        log(grid, shape);
        log(" - &7Ingredients:");
        recipe.getChoiceMap().forEach((character, recipeChoice) -> {
            if (recipeChoice != null) {
                log("   - &r'&d%s&r' = &6%s", character, getFancy(recipeChoice));
            }
        });
    }

    private static String getFancy(RecipeChoice matChoice) {
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
