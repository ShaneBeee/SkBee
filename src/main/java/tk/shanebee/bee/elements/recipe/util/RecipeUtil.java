package tk.shanebee.bee.elements.recipe.util;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Timespan;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@SuppressWarnings("deprecation")
public class RecipeUtil {

    private static final String NAMESPACE = SkBee.getPlugin().getPluginConfig().RECIPE_NAMESPACE;
    private static final boolean BUKKIT_REMOVE = Skript.methodExists(Bukkit.class, "removeRecipe", NamespacedKey.class);

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
    public static void removeRecipeByKey(String recipe) {
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
            try {
                List<Recipe> recipes = new ArrayList<>();
                Bukkit.recipeIterator().forEachRemaining(recipe -> {
                    if (recipe instanceof Keyed && !((Keyed) recipe).getKey().equals(recipeKey)) {
                        recipes.add(recipe);
                    }
                });
                Bukkit.clearRecipes();
                recipes.forEach(Bukkit::addRecipe);
            } catch (NoSuchElementException ignore) {
            }
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
        String grid = " - &7Shape: &r[&d%s&r]&7, &r[&d%s&r]";
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
        Util.log(prefix + log);
    }

    public static void log(String format, Object... objects) {
        log(String.format(format, objects));
    }

}
