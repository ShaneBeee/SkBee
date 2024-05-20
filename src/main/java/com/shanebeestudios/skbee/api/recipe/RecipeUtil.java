package com.shanebeestudios.skbee.api.recipe;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.slot.Slot;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.recipe.sections.SecRecipeSmithing;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Utility methods for {@link Recipe recipes}
 */
public class RecipeUtil {

    public static final boolean HAS_CATEGORY = Skript.classExists("org.bukkit.inventory.recipe.CraftingBookCategory");

    /**
     * Get a NamespacedKey from string
     * <p>If no namespace is provided, it will default to namespace in SkBee config (default = "skbee")</p>
     *
     * @param key Key for new NamespacedKey, ex: "plugin:key" or "minecraft:something"
     * @return New NamespacedKey
     */
    // TODO remove instances of `getKey` usage in other files before 3.0 release
    @Nullable
    public static NamespacedKey getKey(String key) {
        if (key == null) return null;
        return Util.getNamespacedKey(key, false);
    }

    /**
     * @param object a RecipeChoice or ItemStack/ItemType/Slot that will be converted to a RecipeChoice
     * @return null if an invalid object/item or air, otherwise a RecipeChoice
     */
    @Nullable
    public static RecipeChoice getRecipeChoice(Object object) {
        if (object instanceof ItemStack itemStack) {
            Material material = itemStack.getType();
            if (!material.isItem() || material.isAir()) return null;

            if (itemStack.isSimilar(new ItemStack(material))) {
                return new MaterialChoice(material);
            } else {
                return new ExactChoice(itemStack);
            }
        } else if (object instanceof Slot slot) {
            return getRecipeChoice(slot.getItem());
        } else if (object instanceof ItemType itemType) {
            return getRecipeChoice(itemType.getRandom());
        } else if (object instanceof RecipeChoice choice) {
            return choice;
        }
        return null;
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

    /**
     * Log a cooking recipe to console
     * Mainly used for debugging purposes
     *
     * @param recipe Recipe to log
     */
    public static void logCookingRecipe(CookingRecipe<?> recipe) {
        String type = recipe.getClass().getSimpleName().replace("Recipe", "").toLowerCase(Locale.ROOT);
        log("&aRegistered new %s recipe: &7(&b%s&7)", type, ((Keyed) recipe).getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult());
        String group = recipe.getGroup();
        if (!group.isEmpty()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        }
        if (HAS_CATEGORY) {
            log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory());
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
        if (!group.isEmpty()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        }
        if (HAS_CATEGORY) {
            log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory());
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
    public static void logShapedRecipe(ShapedRecipe recipe) {
        log("&aRegistered new shaped recipe: &7(&b%s&7)", recipe.getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult());

        String group = recipe.getGroup();
        if (!group.isEmpty()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        }
        if (HAS_CATEGORY) {
            log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory());
        }

        String[] shape = recipe.getShape();
        String grid = " - &7Shape: &r[&d%s&r]&7";
        if (shape.length > 1) grid += "&7, &r[&d%s&r]";
        if (shape.length > 2) grid += "&7, &r[&d%s&r]";
        log(grid, (Object[]) shape);
        log(" - &7Ingredients:");
        recipe.getChoiceMap().forEach((character, recipeChoice) -> {
            if (recipeChoice != null) {
                log("   - &r'&d%s&r' = &6%s", character, getFancy(recipeChoice));
            }
        });
    }

    /**
     * Log a brewing recipe to console
     *
     * @param potionMix PotionMix to log
     */
    public static void logBrewingRecipe(PotionMix potionMix) {
        log("&aRegistered new shaped recipe: &7(&b%s&7)", potionMix.getKey().toString());
        log(" - &7Result: &e%s", potionMix.getResult());
        log(" - &7Ingredient: %s", getFancy(potionMix.getIngredient()));
        log(" - &7Input: %s", getFancy(potionMix.getInput()));
    }

    /**
     * Log a smithing transform recipe to console
     *
     * @param recipe Recipe to log
     */
    public static void logSmithingRecipe(SmithingTransformRecipe recipe) {
        log("&aRegistered new smithing recipe: &7(&b%s&7)", recipe.getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult());
        log(" - &7Template: %s", getFancy(recipe.getTemplate()));
        log(" - &7Base: %s", getFancy(recipe.getBase()));
        log(" - &7Addition: %s", getFancy(recipe.getAddition()));
        if (SecRecipeSmithing.HAS_NBT_METHOD) {
            String copyNbt = recipe.willCopyNbt() ? "&atrue" : "&cfalse";
            log(" - &7CopyNbt: &e%s", copyNbt);
        }
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
     * Check if a {@link Tag} is a Material Tag
     *
     * @param object Object to check
     * @return True if material tag
     */
    public static boolean isMaterialTag(Object object) {
        if (object instanceof Tag<?> tag) {
            ParameterizedType superC = (ParameterizedType) tag.getClass().getGenericSuperclass();
            for (Type arg : superC.getActualTypeArguments()) {
                if (arg.equals(Material.class)) return true;
            }
        }
        return false;
    }

    /**
     * Send an error to console prefixed with [Recipe]
     *
     * @param error Error to log
     */
    public static void error(String error) {
        log("&c" + error);
        Util.errorForAdmins("Recipe error, see console for more details.");
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
        String prefix = "&7[&bRecipe&7] %s";
        Util.log(prefix, log);
    }

    public static void log(String format, Object... objects) {
        log(String.format(format, objects));
    }

}
