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
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.config.Config;
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

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
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
@RequiredPlugins("1.13+ for furnaces. 1.14+ for smokers, blast furnaces and campfires.")
@Since("1.0.0")
public class EffCookingRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        String extra = " [[and] with exp[erience] %-number%] [[and] with cook[ ]time %-timespan%] [[and] in group %-string%]";
        Skript.registerEffect(EffCookingRecipe.class,
                "register [a] [new] furnace recipe for %itemstack% (using|with ingredient) %recipechoice/itemtype% with id %string%" + extra,
                "register [a] [new] (blast furnace|blasting) recipe for %itemstack% (using|with ingredient) %recipechoice/itemtype% with id %string%" + extra,
                "register [a] [new] smok(er|ing) recipe for %itemstack% (using|with ingredient) %recipechoice/itemtype% with id %string%" + extra,
                "register [a] [new] campfire recipe for %itemstack% (using|with ingredient) %recipechoice/itemtype% with id %string%" + extra);
    }

    @SuppressWarnings("null")
    private Expression<ItemStack> result;
    private Expression<Object> ingredient;
    private Expression<String> key;
    private Expression<Number> experience;
    private Expression<Timespan> cookTime;
    private Expression<String> group;
    private int recipeType;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        result = (Expression<ItemStack>) exprs[0];
        ingredient = (Expression<Object>) exprs[1];
        key = (Expression<String>) exprs[2];
        experience = (Expression<Number>) exprs[3];
        cookTime = (Expression<Timespan>) exprs[4];
        group = (Expression<String>) exprs[5];
        recipeType = matchedPattern;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemStack result = this.result.getSingle(event);
        RecipeChoice ingredient = RecipeUtil.getRecipeChoice(this.ingredient.getSingle(event));
        if (result == null) {
            RecipeUtil.error("Error registering cooking recipe - result is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }
        if (ingredient == null) {
            RecipeUtil.error("Error registering cooking recipe - ingredient is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }

        String group = this.group != null ? this.group.getSingle(event) : null;
        NamespacedKey key = RecipeUtil.getKey(this.key.getSingle(event));
        if (key == null) {
            RecipeUtil.error("Current Item: §6'" + toString(event, true) + "'");
            return;
        }

        float xp = experience != null ? experience.getSingle(event).floatValue() : 0;
        int cookTime = this.cookTime != null ? (int) this.cookTime.getSingle(event).getTicks_i() : getDefaultCookTime(recipeType);

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);

        cookingRecipe(result, ingredient, group, key, xp, cookTime);
    }

    private void cookingRecipe(ItemStack result, RecipeChoice ingredient, String group, NamespacedKey key, float xp, int cookTime) {
        CookingRecipe<?> recipe = switch (recipeType) {
            case 1 -> // BLASTING
                    new BlastingRecipe(key, result, ingredient, xp, cookTime);
            case 2 -> // SMOKING
                    new SmokingRecipe(key, result, ingredient, xp, cookTime);
            case 3 -> // CAMPFIRE
                    new CampfireRecipe(key, result, ingredient, xp, cookTime);
            default -> // FURNACE
                    new FurnaceRecipe(key, result, ingredient, xp, cookTime);
        };

        if (group != null && !group.isBlank()) recipe.setGroup(group);
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logCookingRecipe(recipe);
        }
    }

    private int getDefaultCookTime(int t) {
        return switch (t) { // BLASTING
            case 1, 2 -> // SMOKING
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
        String xp = experience != null ? " and with xp " + experience.toString(event, debug) : "";
        String cook = cookTime != null ? " and with cooktime " + cookTime.toString(event, debug) : "";
        return "register new " + type + " recipe for " + result.toString(event, debug) + " using " + ingredient.toString(event, debug) +
                " with id " + key.toString(event, debug) + xp + cook;
    }

}
