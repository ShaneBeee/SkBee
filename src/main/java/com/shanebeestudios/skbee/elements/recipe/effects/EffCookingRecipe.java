package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;

@Name("Recipe - Cooking")
@Description({"Register new cooking recipes. On 1.13+ you can register recipes for furnaces.",
        "On 1.14+ you can also register recipes for smokers, blast furnaces and campfires.",
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens, a single colon and underscores,",
        "NOT SPACES!!! By default, if no namespace is provided, recipes will start with the namespace \"skbee:\",",
        "this can be changed in the config to whatever you want. IDs are used for recipe discovery/unlocking recipes for players.",
        "You may also include an optional group for recipes. These will group the recipes together in the recipe book.",})
@Examples({"on skript load:",
        "\tregister new furnace recipe for diamond using dirt with id \"furnace_diamond\"",
        "\tregister new blasting recipe for emerald using dirt with id \"my_recipes:blasting_emerald\"",
        "\tregister new smoking recipe for cooked cod named \"Hot Cod\" using puffer fish with id \"smoking_cod\""})
@RequiredPlugins("1.13+ for furnaces. 1.14+ for smokers, blast furnaces and campfires. 1.19+ for Categories")
@Since("1.0.0, INSERT VERSION (Category)")
public class EffCookingRecipe extends Effect {

    private static final boolean COOKING_CATEGORY_EXISTS = Skript.classExists("org.bukkit.inventory.recipe.CookingBookCategory");

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        String register = "register [a] [new] ";
        String recipeForUsingID = " recipe for %itemstack% (using|with ingredient) %itemstack/materialchoice% (using|with (id|key)) %string/namespacedkey%";
        String withExperience = " [[and ]with exp[erience] %-number%]";
        String withCookTime = " [[and ]with cook[ ]time %-timespan%]";
        String inGroup = " [[and ](in|with) group %-string%]";
        String inCategory = COOKING_CATEGORY_EXISTS ? " [[and ](in|with) category %-cookingcategory%]" : "";
        Skript.registerEffect(EffCookingRecipe.class,
                register + "furnace" + recipeForUsingID + withExperience + withCookTime + inGroup + inCategory,
                register + "(blast furnace|blasting)" + recipeForUsingID + withExperience + withCookTime + inGroup + inCategory,
                register + "smok(er|ing)" + recipeForUsingID + withExperience + withCookTime + inGroup + inCategory,
                register + "campfire" + recipeForUsingID + withExperience + withCookTime + inGroup + inCategory);
    }

    private Expression<ItemStack> result;
    private Expression<Object> ingredient;
    private Expression<Object> keyID;
    private Expression<Number> experience;
    private Expression<Timespan> cookTime;
    private Expression<String> group;
    private Expression<CookingBookCategory> category;
    private int recipeType;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        result = (Expression<ItemStack>) exprs[0];
        ingredient = (Expression<Object>) exprs[1];
        keyID = (Expression<Object>) exprs[2];

        experience = (Expression<Number>) exprs[3];

        cookTime = (Expression<Timespan>) exprs[4];

        group = (Expression<String>) exprs[5];

        category = (Expression<CookingBookCategory>) exprs[6];
        recipeType = matchedPattern;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemStack result = this.result.getSingle(event);
        RecipeChoice ingredient = RecipeUtil.getRecipeChoice(this.ingredient.getSingle(event));
        NamespacedKey key = RecipeUtil.getKey(this.keyID.getSingle(event));
        if (result == null) {
            RecipeUtil.error("Error registering cooking recipe - result is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }  else if (ingredient == null) {
            RecipeUtil.error("Error registering cooking recipe - ingredient is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        } else if (key == null) {
            RecipeUtil.error("Error registering cooking recipe - key is null");
            RecipeUtil.error("Current Item: §6'" + toString(event, true) + "'");
            return;
        }

        float experience = this.experience != null ? this.experience.getSingle(event).floatValue() : 0;
        int cookTime = this.cookTime != null ? (int) this.cookTime.getSingle(event).getTicks_i() : getDefaultCookTime(recipeType);
        String group = this.group != null ? this.group.getSingle(event) : null;
        CookingBookCategory category = this.category != null && COOKING_CATEGORY_EXISTS ? this.category.getSingle(event) : null;

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        cookingRecipe(key, result, ingredient, experience, cookTime, group, category);
    }

    private void cookingRecipe(NamespacedKey keyID, ItemStack result, RecipeChoice ingredient, float experience, int cookTime, String group, CookingBookCategory category) {
        CookingRecipe<?> recipe = switch (recipeType) {
            case 1 -> // BLASTING
                    new BlastingRecipe(keyID, result, ingredient, experience, cookTime);
            case 2 -> // SMOKING
                    new SmokingRecipe(keyID, result, ingredient, experience, cookTime);
            case 3 -> // CAMPFIRE
                    new CampfireRecipe(keyID, result, ingredient, experience, cookTime);
            default -> // FURNACE
                    new FurnaceRecipe(keyID, result, ingredient, experience, cookTime);
        };
        if(group != null && !group.isBlank()) recipe.setGroup(group);
        if (category != null) recipe.setCategory(category);
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logCookingRecipe(recipe);
        }
    }

    private int getDefaultCookTime(int type) {
        return switch (type) {
            case 1, 2 -> // BLASTING & SMOKING
                    100;
            case 3 -> // CAMPFIRE
                    600;
            default -> // FURNACE
                    200;
        };
    }

    @Override
    public String toString(Event event, boolean debug) {
        String type = switch (recipeType) {
            case 1 -> "blasting";
            case 2 -> "smoking";
            case 3 -> "campfire";
            default -> "furnace";
        };
        String result = this.result.toString(event, debug);
        String ingredient = this.ingredient.toString(event, debug);
        String key = this.keyID.toString(event, debug);
        String exp = experience != null ? " and with exp " + experience.toString(event, debug) : "";
        String cooktime = cookTime != null ? " and with cooktime " + cookTime.toString(event, debug) : "";
        String group = this.group != null ? " with group " + this.group.toString(event, debug) : "";
        String category = this.category != null && COOKING_CATEGORY_EXISTS ? " and in category " + this.category.toString(event, debug) : "";
        return String.format("register new %s recipe for %s using %s with id %s%s%s%s%s",
                type, result, ingredient, key, exp, cooktime, group, category);
    }

}
