package com.shanebeestudios.skbee.api.recipe;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.Timespan;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.potion.PotionMix;
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
     * Gets a RecipeChoice depending on the Item
     *
     * @param object Object to convert to a RecipeChoice
     * @return ExactChoice or MaterialChoice depending on Object
     */
    public static RecipeChoice getRecipeChoice(Object object) {
        if (object instanceof ItemStack || object instanceof ItemType) {
            // Skript gives ItemType priority when used in Object class
            ItemStack itemStack = object instanceof ItemStack ? (ItemStack) object : ((ItemType) object).getRandom();
            Material material = itemStack.getType();
            boolean isAir = material.isAir();
            boolean isSimilar = itemStack.isSimilar(new ItemStack(material));
            if (isAir) {
                return null;
            } else if (isSimilar) {
                return new MaterialChoice(material);
            }
            return new ExactChoice(itemStack);
        } else if (object instanceof RecipeChoice recipeChoice) {
            return recipeChoice;
        }
        return null;
    }

    /**
     * Used as a mediator between RecipeChoice type and Ingredient type
     * mainly just clean up purposes
     *
     * @param recipeChoice RecipeChoice to format
     * @return Stringified form of recipe choices, in readable format
     */
    public static String recipeChoiceToString(RecipeChoice recipeChoice) {
        List<String> itemTypes = new ArrayList<>();
        if (recipeChoice instanceof ExactChoice exactChoice) {
            exactChoice.getChoices().forEach(itemStack -> itemTypes.add(new ItemType(itemStack).toString()));
            return String.format("ExactChoice{choices=[%s]}", StringUtils.join(itemTypes, ", ").toUpperCase());
        } else if (recipeChoice instanceof MaterialChoice materialChoice) {
            materialChoice.getChoices().forEach(material -> itemTypes.add(new ItemType(material).toString()));
            return String.format("MaterialChoice{choices=[%s]}", StringUtils.join(itemTypes, ", ").toUpperCase());
        }

        return "RecipeChoice{choices=[AIR]}";
    }

    /**
     * Log a recipe to console
     *
     * @param recipe      Recipe to log
     * @param ingredients Ingredients of recipe to log
     */
    public static void logRecipe(Recipe recipe, RecipeChoice... ingredients) {
        if (!(recipe instanceof Keyed keyed)) return;
        log("&aRegistered new recipe: &7(&b%s&7)", keyed.getKey().asString());
        if (recipe instanceof StonecuttingRecipe stonecuttingRecipe && !stonecuttingRecipe.getGroup().isBlank()) {
            log(" - &7Group: &r\"&6%s&r\"", stonecuttingRecipe.getGroup());
        }
        log(" - &7Result: &e%s", recipe.getResult());
        log(" - &7Ingredients:");
        for (RecipeChoice ingredient : ingredients) {
            log("   - %s", getFancy(ingredient));
        }

    }

    /**
     * Log a potion mix to console
     * @param potionMix PotionMix to log
     */
    public static void logPotionMix(PotionMix potionMix) {
        log("&aRegistered new PotionMix: &7(&b%s&7)", potionMix.getKey().asString());
        log(" - &7Result: &e%s", potionMix.getResult());
        log(" - &7Input: &e%s", getFancy(potionMix.getInput()));
        log(" - &7Ingredient: &e%s", getFancy(potionMix.getIngredient()));
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
        recipe.getChoiceList().forEach(recipeChoice -> log("   - &6%s", getFancy(recipeChoice)));
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
        recipe.getChoiceMap().forEach((character, recipeChoice) -> log("   - &r'&d%s&r' = &6%s", character, getFancy(recipeChoice)));
    }

    private static String getFancy(RecipeChoice recipeChoice) {
        if (recipeChoice == null) return "&r[&bAIR&r]";
        return recipeChoiceToString(recipeChoice)
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
