package tk.shanebee.bee.elements.recipe.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import tk.shanebee.bee.SkBee;

@SuppressWarnings("deprecation")
public class RecipeUtil {

    private static final Remover REMOVER = new Remover();
    private static final String NAMESPACE = SkBee.getPlugin().getPluginConfig().RECIPE_NAMESPACE;

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

    public static void removeRecipe(String recipe) {
        recipe = recipe.toLowerCase();
        if (recipe.contains("minecraft:")) {
            removeMCRecipe(recipe);
        } else if (recipe.contains(NAMESPACE + ":")) {
            recipe = recipe.split(":")[1];
            REMOVER.removeRecipeByKey(getKey(recipe));
        } else if (recipe.contains(":")) {
            NamespacedKey key = getKeyByPlugin(recipe);
            if (key != null) {
                REMOVER.removeRecipeByKey(key);
            }
        } else {
            REMOVER.removeRecipeByKey(getKey(recipe));
        }
    }

    public static void removeRecipe(NamespacedKey key) {
        REMOVER.removeRecipeByKey(key);
    }

    public static void removeMCRecipe(String recipe) {
        recipe = recipe.replace("minecraft:", "");
        REMOVER.removeRecipeByKey(NamespacedKey.minecraft(recipe));
    }

    public static void removeAllMCRecipes() {
        REMOVER.removeAll();
    }

    public static void logRecipe(Recipe recipe, String ingredients) {
        log("&aRegistered new recipe:");
        log(" - &7Result: " + recipe.getResult());
        log(" - &7Ingredients: " + ingredients);
    }

    public static void error(String error) {
        log("&c" + error);
    }

    public static void warn(String warning) {
        log("&e" + warning);
    }

    public static void log(String log) {
        String prefix = "&7[&bRecipe&7] ";
        SkBee.log(prefix + log);
    }

}
