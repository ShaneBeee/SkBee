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

    public static void removeMCRecipe(String recipe) {
        recipe = recipe.replace("minecraft:", "");
        removeRecipeByKey(NamespacedKey.minecraft(recipe));
    }

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
