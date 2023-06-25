package com.shanebeestudios.skbee.api.recipe;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.Timespan;
import ch.njol.util.StringUtils;
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

/**
 * Utility methods for {@link Recipe recipes}
 */
public class RecipeUtil {

    private static final boolean COOKING_CATEGORY_EXISTS = Skript.classExists("org.bukkit.inventory.recipe.CookingBookCategory");
    private static final boolean CRAFTING_CATEGORY_EXISTS = Skript.classExists("org.bukkit.inventory.recipe.CraftingBookCategory");

    /**
     * Get a NamespacedKey from string
     * <p>If no namespace is provided, it will default to namespace in SkBee config (default = "skbee")</p>
     *
     * @param key Key for new NamespacedKey, ex: "plugin:key" or "minecraft:something"
     * @return New NamespacedKey
     * @deprecated Planning to remove all string based ids for recipes in the future, please use Util#getNamespacedkey
     * more information on this in the future when it's put into action
     */
    @Deprecated()
    public static NamespacedKey getKey(Object key) {
        try {
            if (key instanceof NamespacedKey) {
                return (NamespacedKey) key;
            } else if (key instanceof String stringKey) {
                NamespacedKey namespacedKey;
                if (stringKey.contains(":")) {
                    namespacedKey = NamespacedKey.fromString(stringKey.toLowerCase(Locale.ROOT));
                } else {
                    namespacedKey = Util.getNamespacedKey(stringKey, false);
                }
                if (namespacedKey == null) {
                    error("Invalid namespaced key. Must be [a-z0-9/._-:]: " + key);
                }
                return namespacedKey;
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
     * Gets an ExactChoice using the given objects
     *
     * @param objects list of objects to convert to an ExactChoice
     * @return ExactChoice based off given objects or null if invalid
     */
    public static ExactChoice getExactChoice(Object ...objects) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof ExactChoice exactChoice) {
                for (ItemStack itemStack : exactChoice.getChoices()) {
                    if (itemStacks.contains(itemStack)) continue;
                    itemStacks.add(itemStack);
                }
            } else if (object instanceof ItemType itemType) {
                for (ItemStack itemStack : itemType.getAll()) {
                    if (itemStacks.contains(itemStack)) continue;
                    itemStacks.add(itemStack);
                }
            } else if (object instanceof ItemStack itemStack) {
                if (!itemStacks.contains(itemStack)) {
                    itemStacks.add(itemStack);
                }
            }
        }
        if (itemStacks.size() == 0) return null;
        return new ExactChoice(itemStacks);
    }

    /**
     * Gets a MaterialChoice using the given objects
     *
     * @param objects list of objects to convert to a MaterialChoice
     * @return MaterialChoice based off given objects or null if invalid
     */
    public static MaterialChoice getMaterialChoice(Object ...objects) {
        List<Material> materials = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof MaterialChoice materialChoice) {
                for (Material choice : materialChoice.getChoices()) {
                    if (materials.contains(choice)) continue;
                    materials.add(choice);
                }
            } else if (object instanceof ItemType itemType) {
                for (ItemStack itemStack : itemType.getAll()) {
                    if (materials.contains(itemStack.getType())) continue;
                    materials.add(itemStack.getType());
                }
            } else if (object instanceof ItemStack itemStack) {
                if (!materials.contains(itemStack.getType())) {
                    materials.add(itemStack.getType());
                }
            }
        }
        if (materials.size() == 0) return null;
        return new MaterialChoice(materials);
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
            exactChoice.getChoices().forEach(itemStack -> itemTypes.add(itemStack.getType().toString()));
            return String.format("ExactChoice{choices=[%s]}", StringUtils.join(itemTypes, ", "));
        } else if (recipeChoice instanceof MaterialChoice materialChoice) {
            materialChoice.getChoices().forEach(material -> itemTypes.add(material.toString()));
            return String.format("MaterialChoice{choices=[%s]}", StringUtils.join(itemTypes, ", "));
        }
        return "RecipeChoice{choices=null}";
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

    /**
     * Log a cooking recipe to console
     * Mainly used for debugging purposes
     *
     * @param recipe Recipe to log
     */
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
