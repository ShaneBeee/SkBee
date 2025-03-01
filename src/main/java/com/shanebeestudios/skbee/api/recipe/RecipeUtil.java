package com.shanebeestudios.skbee.api.recipe;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.test.runner.TestMode;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.slot.Slot;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.recipe.sections.SecRecipeSmithing;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
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
import org.bukkit.inventory.TransmuteRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

/**
 * Utility methods for {@link Recipe recipes}
 */
public class RecipeUtil {

    public static final boolean HAS_CATEGORY = Skript.classExists("org.bukkit.inventory.recipe.CraftingBookCategory");
    private static final Map<String, CraftingBookCategory> CATEGORY_MAP = new HashMap<>();

    static {
        for (CraftingBookCategory value : CraftingBookCategory.values()) {
            String name = value.name().toLowerCase(Locale.ROOT);
            CATEGORY_MAP.put(name, value);
        }
    }

    /**
     * @param object a RecipeChoice or ItemStack/ItemType/Slot/Tag that will be converted to a RecipeChoice
     * @return null if an invalid object/item or air, otherwise a RecipeChoice
     */
    @SuppressWarnings("unchecked")
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
        } else if (object instanceof Tag<?> tag && Util.isMaterialTag(tag)) {
            return new MaterialChoice((Tag<Material>) tag);
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

    public static CraftingBookCategory getCraftingBookCategory(String name) {
        return CATEGORY_MAP.get(name);
    }

    /**
     * Log a recipe to console
     * Mainly used for debugging purposes
     *
     * @param recipe      Recipe to log
     * @param ingredients Ingredients of recipe to log
     */
    public static void logRecipe(Recipe recipe, RecipeChoice... ingredients) {
        if (!SkBee.isDebug() || TestMode.DEV_MODE) return;
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
        if (!SkBee.isDebug() || TestMode.DEV_MODE) return;
        String type = recipe.getClass().getSimpleName().replace("Recipe", "").toLowerCase(Locale.ROOT);
        log("&aRegistered new %s recipe: &7(&b%s&7)", type, ((Keyed) recipe).getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult());
        String group = recipe.getGroup();
        if (!group.isEmpty()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        }
        if (HAS_CATEGORY) {
            log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory().name().toLowerCase(Locale.ROOT));
        }
        log(" - &7CookTime: &b%s", new Timespan(Timespan.TimePeriod.TICK, recipe.getCookingTime()));
        log(" - &7Experience: &b%s", recipe.getExperience());
        log(" - &7Ingredients: %s", getFancy(recipe.getInputChoice()));
    }

    /**
     * Log a shapeless recipe to console
     *
     * @param recipe Recipe to log
     */
    public static void logShapelessRecipe(ShapelessRecipe recipe) {
        if (!SkBee.isDebug() || TestMode.DEV_MODE) return;
        log("&aRegistered new shapeless recipe: &7(&b%s&7)", recipe.getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult());
        String group = recipe.getGroup();
        if (!group.isEmpty()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        }
        if (HAS_CATEGORY) {
            log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory().name().toLowerCase(Locale.ROOT));
        }
        log(" - &7Ingredients:");
        recipe.getChoiceList().forEach(recipeChoice ->
            log("   - %s", getFancy(recipeChoice)));
    }

    /**
     * Log a shaped recipe to console
     *
     * @param recipe Recipe to log
     */
    public static void logShapedRecipe(ShapedRecipe recipe) {
        if (!SkBee.isDebug() || TestMode.DEV_MODE) return;
        log("&aRegistered new shaped recipe: &7(&b%s&7)", recipe.getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult());

        String group = recipe.getGroup();
        if (!group.isEmpty()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        }
        if (HAS_CATEGORY) {
            log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory().name().toLowerCase(Locale.ROOT));
        }

        String[] shape = recipe.getShape();
        StringJoiner joiner = new StringJoiner("&r], &r[&d", "&r[&d", "&r]");
        for (String s : shape) {
            joiner.add(s);
        }
        log(" - &7Shape: &r%s&7", joiner.toString());
        log(" - &7Ingredients:");
        recipe.getChoiceMap().forEach((character, recipeChoice) -> {
            if (recipeChoice != null) {
                log("   - &r'&d%s&r' = %s", character, getFancy(recipeChoice));
            }
        });
    }

    /**
     * Log a brewing recipe to console
     *
     * @param potionMix PotionMix to log
     */
    public static void logBrewingRecipe(PotionMix potionMix) {
        if (!SkBee.isDebug() || TestMode.DEV_MODE) return;
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
        if (!SkBee.isDebug() || TestMode.DEV_MODE) return;
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

    public static void logTransmuteRecipe(TransmuteRecipe recipe) {
        if (!SkBee.isDebug() || TestMode.DEV_MODE) return;
        log("&aRegistering new transmute recipe: &7(&b%s&7)", recipe.getKey().toString());
        log(" - &7Result: &e%s", recipe.getResult().getType().getKey());
        log(" - &7Input: &e%s", getFancy(recipe.getInput()));
        log(" - &7Material: &e%s", getFancy(recipe.getMaterial()));
        log(" - &7Category: &r\"&6%s&r\"", recipe.getCategory().name().toLowerCase(Locale.ROOT));
        String group = recipe.getGroup();
        if (!group.isEmpty()) {
            log(" - &7Group: &r\"&6%s&r\"", group);
        }
    }

    private static String getFancy(RecipeChoice recipeChoice) {
        StringJoiner joiner = new StringJoiner("&r, &e", "&r[&e", "&r]");
        if (recipeChoice instanceof MaterialChoice materialChoice) {
            materialChoice.getChoices().forEach(material ->
                joiner.add(material.getKey().toString()));
        } else if (recipeChoice instanceof ExactChoice exactChoice) {
            joiner.add(exactChoice.toString());
        }
        return joiner.toString();
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
