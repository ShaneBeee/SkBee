package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
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
import com.shanebeestudios.skbee.elements.recipe.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.SmokingRecipe;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
@Name("Recipe - Cooking")
@Description({"Register new cooking recipes. On 1.13+ you can register recipes for furnaces. ",
        "On 1.14+ you can also register recipes for smokers, blast furnaces and campfires. ",
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens and underscores. " +
                "Used for recipe discovery/unlocking recipes for players. " +
                "You may also include an optional group for recipes. These will group the recipes together in the recipe book.",
        "By default recipes will start with the namespace \"skrecipe:\", this can be changed in the config to whatever you want."})
@Examples({"on skript load:",
        "\tregister new furnace recipe for diamond using dirt with id \"furnace_diamond\"",
        "\tregister new blasting recipe for emerald using dirt with id \"blasting_emerald\"",
        "\tregister new smoking recipe for cooked cod named \"Hot Cod\" using puffer fish with id \"smoking_cod\""})
@RequiredPlugins("1.13+ for furnaces. 1.14+ for smokers, blast furnaces and campfires.")
@Since("1.0.0")
public class EffCookingRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        Skript.registerEffect(EffCookingRecipe.class,
                "register [new] (0¦furnace|1¦(blast furnace|blasting)|2¦smok(er|ing)|3¦campfire) recipe for %itemtype% " +
                        "(using|with ingredient) %itemtype/materialchoice% with id %string% [[and ]with exp[erience] %-number%] " +
                        "[[and ]with cook[ ]time %-timespan%] [in group %-string%]");
    }

    @SuppressWarnings("null")
    private Expression<ItemType> item;
    private Expression<Object> ingredient;
    private Expression<String> key;
    private Expression<Number> experience;
    private Expression<Timespan> cookTime;
    private Expression<String> group;
    private int recipeType;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        item = (Expression<ItemType>) exprs[0];
        ingredient = (Expression<Object>) exprs[1];
        key = (Expression<String>) exprs[2];
        experience = (Expression<Number>) exprs[3];
        cookTime = (Expression<Timespan>) exprs[4];
        group = (Expression<String>) exprs[5];
        recipeType = parseResult.mark;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemType res = this.item.getSingle(event);
        Object ing = this.ingredient.getSingle(event);
        if (res == null) {
            Skript.error("Error registering cooking recipe - result is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (ing == null) {
            Skript.error("Error registering cooking recipe - ingredient is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }

        ItemStack result = res.getRandom();
        RecipeChoice ingredient;
        if (ing instanceof ItemType) {
            ItemStack itemStack = ((ItemType) ing).getRandom();
            Material material = itemStack.getType();

            // If ingredient isn't a custom item, just register the material
            if (itemStack.isSimilar(new ItemStack(material))) {
                ingredient = new MaterialChoice(material);
            } else {
                ingredient = new ExactChoice(itemStack);
            }
        } else if (ing instanceof MaterialChoice) {
            ingredient = ((MaterialChoice) ing);
        } else {
            return;
        }
        String group = this.group != null ? this.group.getSingle(event) : "";
        NamespacedKey key = RecipeUtil.getKey(this.key.getSingle(event));
        float xp = experience != null ? experience.getSingle(event).floatValue() : 0;
        int cookTime = this.cookTime != null ? (int) this.cookTime.getSingle(event).getTicks_i() : getDefaultCookTime(recipeType);

        // Remove duplicates on script reload
        RecipeUtil.removeRecipeByKey(key);

        cookingRecipe(result, ingredient, group, key, xp, cookTime);
    }

    private void cookingRecipe(ItemStack result, RecipeChoice ingredient, String group, NamespacedKey key, float xp, int cookTime) {
        CookingRecipe<?> recipe;
        switch (recipeType) {
            case 1: // BLASTING
                recipe = new BlastingRecipe(key, result, ingredient, xp, cookTime);
                break;
            case 2: // SMOKING
                recipe = new SmokingRecipe(key, result, ingredient, xp, cookTime);
                break;
            case 3: // CAMPFIRE
                recipe = new CampfireRecipe(key, result, ingredient, xp, cookTime);
                break;
            default: // FURNACE
                recipe = new FurnaceRecipe(key, result, ingredient, xp, cookTime);
        }

        recipe.setGroup(group);
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logCookingRecipe(recipe);
        }
    }

    private int getDefaultCookTime(int t) {
        switch (t) {
            case 1: // BLASTING
            case 2: // SMOKING
                return 100;
            case 3: // CAMPFIRE
                return 600;
            default: // FURNACE
                return 200;
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        String type;
        switch (recipeType) {
            case 1:
                type = "blasting";
                break;
            case 2:
                type = "smoking";
                break;
            case 3:
                type = "campfire";
                break;
            default:
                type = "furnace";
        }
        String xp = experience != null ? " and with xp " + experience.toString(e, d) : "";
        String cook = cookTime != null ? " and with cooktime " + cookTime.toString(e, d) : "";
        return "register new " + type + " recipe for " + item.toString(e, d) + " using " + ingredient.toString(e, d) +
                " with id " + key.toString(e, d) + xp + cook;
    }

}
