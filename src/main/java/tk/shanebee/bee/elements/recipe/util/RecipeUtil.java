package tk.shanebee.bee.elements.recipe.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import tk.shanebee.bee.SkBee;

public class RecipeUtil {

    @SuppressWarnings("deprecation")
    public static NamespacedKey getKey(String key) {
        return new NamespacedKey(SkBee.getPlugin().getPluginConfig().RECIPE_NAMESPACE, key);
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
