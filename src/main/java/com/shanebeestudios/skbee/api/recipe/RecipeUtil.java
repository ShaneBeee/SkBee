package com.shanebeestudios.skbee.api.recipe;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.Timespan;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.util.Util;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Utility methods for {@link Recipe recipes}
 */
public class RecipeUtil {

    /**
     * Get a NamespacedKey from string
     * <p>If no namespace is provided, it will default to namespace in SkBee config (default = "skbee")</p>
     *
     * @param key Key for new NamespacedKey, ex: "plugin:key" or "minecraft:something"
     * @return New NamespacedKey
     * @deprecated Planning to remove all string based ids for recipes in the future, please use {@link Util#getNamespacedKey(String, boolean)}
     * more information on this in the future when it's put into action
     */
    @Deprecated()
    public static NamespacedKey getKey(String key) {
        try {
            NamespacedKey namespacedKey;
            if (key.contains(":")) {
                namespacedKey = NamespacedKey.fromString(key.toLowerCase(Locale.ROOT));
            } else {
                namespacedKey = Util.getNamespacedKey(key, false);
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

    /**
     * Get a recipe choice based off given argument
     *
     * @param object object to get RecipeChoice from
     * @return RecipeChoice or null if invalid
     */
    @Nullable
    public static RecipeChoice getRecipeChoice(Object object) {
        if (object == null) return null;
        if (object instanceof RecipeChoice recipeChoice) return recipeChoice;
        if (object instanceof ItemStack itemStack) {
            if (itemStack.getType().isAir()) return null;
            if (itemStack.isSimilar(new ItemStack(itemStack.getType())))
                return new MaterialChoice(itemStack.getType());
            return new ExactChoice(itemStack);
        } else if (object instanceof ItemType itemType) {
            ItemStack item = itemType.getRandom();
            if (item.getType().isAir()) return null;
            if (item.isSimilar(new ItemStack(item.getType()))) {
                return getMaterialChoice(itemType);
            } else {
                return getExactChoice(itemType);
            }
        } else if (object instanceof Tag<?> tag) {
            // Honestly this shouldn't ever be reached unless we add syntax like %recipechoice/itemtype/minecrafttag%
            return getMaterialChoice(tag);
        }

        return null;
    }

    /**
     * Get an ExactChoice from a list of objects
     *
     * @param objects list of objects to get an exact choice from
     * @return ExactChoice or null if invalid
     */
    @Nullable
    public static ExactChoice getExactChoice(Object... objects)  {
        if (objects == null) return null;
        List<ItemStack> itemStacks = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof ExactChoice exactChoice) {
                for (ItemStack itemStack : exactChoice.getChoices()) {
                    if (itemStacks.contains(itemStack)) continue;
                    itemStacks.add(itemStack);
                }
            } else if (object instanceof ItemStack itemStack) {
                if (itemStacks.contains(itemStack)) continue;
                if (itemStack.getType().isAir()) continue;
                itemStacks.add(itemStack);
            } else if (object instanceof ItemType itemType) {
                for (ItemStack itemStack : itemType.getAll()) {
                    if (itemStacks.contains(itemStack)) continue;
                    if (itemStack.getType().isAir()) continue;
                    itemStacks.add(itemStack);
                }
            }
        }
        if (itemStacks.size() == 0) return null;
        return new ExactChoice(itemStacks);
    }

    /**
     * Get a MaterialChoice from a list of objects
     *
     * @param objects list of objects to get a material choice from
     * @return MaterialChoice or null if invalid
     */
    @Nullable
    public static MaterialChoice getMaterialChoice(Object... objects) {
        if (objects == null) return null;
        List<Material> materials = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof MaterialChoice materialChoice) {
                for (Material material : materialChoice.getChoices()) {
                    if (materials.contains(material)) continue;
                    materials.add(material);
                }
            } else if (object instanceof ItemStack itemStack) {
                Material material = itemStack.getType();
                if (materials.contains(material)) continue;
                if (material.isAir()) continue;
                materials.add(material);
            } else if (object instanceof ItemType itemType) {
                for (ItemStack itemStack : itemType.getAll()) {
                    Material material = itemStack.getType();
                    if (materials.contains(material)) continue;
                    if (material.isAir()) continue;
                    materials.add(material);
                }
            } else if (object instanceof Tag<?> tag) {
                Tag<Material> materialTag = (Tag<Material>) tag;
                if (materialTag.getValues().size() == 0) continue;
                for (Material material : (materialTag.getValues())) {
                    if (materials.contains(material)) continue;
                    if (material.isAir()) continue;
                    materials.add(material);
                }
            }
        }
        if (materials.size() == 0) return null;
        return new MaterialChoice(materials);
    }

    public static Collection<ItemType> getChoices(RecipeChoice recipeChoice) {
        List<ItemType> choices = new ArrayList<>();
        if (recipeChoice instanceof ExactChoice exactChoice) {
            for (ItemStack itemStack : exactChoice.getChoices()) {
                ItemType itemType = new ItemType(itemStack);
                if (choices.contains(itemType)) continue;
                choices.add(itemType);
            }
        } else if (recipeChoice instanceof  MaterialChoice materialChoice) {
            for (Material material : materialChoice.getChoices()) {
                ItemType itemType = new ItemType(material);
                if (choices.contains(itemType)) continue;
                choices.add(itemType);
            }
        }
        return choices;
    }

    public static String recipeChoiceToString(RecipeChoice recipeChoice) {
        List<String> itemTypes = new ArrayList<>();
        if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            materialChoice.getChoices().forEach(material -> itemTypes.add(new ItemType(material).toString()));
            return String.format("MaterialChoice{choices=[%s]}",
                    StringUtils.join(itemTypes, ", "));
        } else if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice) {
            exactChoice.getChoices().forEach(itemStack -> itemTypes.add(new ItemType(itemStack).toString()));
            return String.format("ExactChoice{choices=[%s]}",
                    StringUtils.join(itemTypes, ", "));
        }
        return String.format("RecipeChoice{choices=[]}", StringUtils.join(itemTypes, ", "));
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
