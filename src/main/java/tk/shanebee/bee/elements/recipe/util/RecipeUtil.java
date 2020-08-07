package tk.shanebee.bee.elements.recipe.util;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import tk.shanebee.bee.SkBee;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class RecipeUtil {

    private static final String NAMESPACE = SkBee.getPlugin().getPluginConfig().RECIPE_NAMESPACE;
    private static final boolean BUKKIT_REMOVE = Skript.methodExists(Bukkit.class, "removeRecipe",
            new Class[]{NamespacedKey.class}, Boolean.class);

    /**
     * Get a NamespacedKey from string using this plugin as the namespace
     *
     * @param key Key for new NamespacedKey, ex: "thisplugin:key"
     * @return New NamespacedKey
     */
    public static NamespacedKey getKey(String key) {
        return new NamespacedKey(NAMESPACE, key.toLowerCase());
    }

    private static NamespacedKey getKeyByPlugin(String key) {
        if (key.contains(":")) {
            String[] split = key.split(":");
            return new NamespacedKey(split[0], split[1]);
        }
        return null;
    }

    /**
     * Get a NamespacedKey from a string
     *
     * @param key Key to get NamespacedKey for
     * @return NamespacedKey from string
     */
    public static NamespacedKey getKeyFromString(String key) {
        if (key.contains(":")) {
            String[] split = key.toLowerCase().split(":");
            if (split[0].equalsIgnoreCase("minecraft"))
                return NamespacedKey.minecraft(split[1]);
            return getKeyByPlugin(key);
        }
        return getKey(key);
    }

    /**
     * Remove a recipe that is currently registered to the server
     *
     * @param recipe Recipe to remove
     */
    public static void removeRecipe(String recipe) {
        recipe = recipe.toLowerCase();
        if (recipe.contains("minecraft:")) {
            removeMCRecipe(recipe);
        } else if (recipe.contains(NAMESPACE + ":")) {
            recipe = recipe.split(":")[1];
            removeRecipeByKey(getKey(recipe));
        } else if (recipe.contains(":")) {
            NamespacedKey key = getKeyByPlugin(recipe);
            if (key != null) {
                removeRecipeByKey(key);
            }
        } else {
            removeRecipeByKey(getKey(recipe));
        }
    }

    public static void removeRecipe(NamespacedKey key) {
        removeRecipeByKey(key);
    }

    /**
     * Remove a Minecraft recipe that is currently registered to the server
     *
     * @param recipe Key of recipe to remove
     */
    public static void removeMCRecipe(String recipe) {
        recipe = recipe.replace("minecraft:", "");
        removeRecipeByKey(NamespacedKey.minecraft(recipe));
    }

    /**
     * Remove a recipe from the server based on a NamespacedKey
     *
     * @param recipeKey NamespacedKey of recipe to remove
     */
    public static void removeRecipeByKey(NamespacedKey recipeKey) {
        if (BUKKIT_REMOVE) {
            Bukkit.removeRecipe(recipeKey);
        } else {
            List<Recipe> recipes = new ArrayList<>();
            Bukkit.recipeIterator().forEachRemaining(recipe -> {
                if (recipe instanceof Keyed && !((Keyed) recipe).getKey().equals(recipeKey)) {
                    recipes.add(recipe);
                }
            });
            Bukkit.clearRecipes();
            recipes.forEach(Bukkit::addRecipe);
        }
    }

    /**
     * Remove all Minecraft recipes registered to the server
     */
    public static void removeAllMCRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof Keyed && !((Keyed) recipe).getKey().getNamespace().equalsIgnoreCase("minecraft")) {
                recipes.add(recipe);
            }
        });
        Bukkit.clearRecipes();
        recipes.forEach(Bukkit::addRecipe);
    }

    /**
     * Log a recipe to console
     * Mainly used for debugging purposes
     *
     * @param recipe      Recipe to log
     * @param ingredients Ingredients of recipe to log
     */
    public static void logRecipe(Recipe recipe, String ingredients) {
        log("&aRegistered new recipe:");
        log(" - &7Result: " + recipe.getResult());
        log(" - &7Ingredients: " + ingredients);
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
     * Send an warning to console prefixed with [Recipe]
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
        SkBee.log(prefix + log);
    }

}
