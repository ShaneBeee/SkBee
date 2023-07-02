package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("Recipe - Smithing")
@Description({"Register a new smithing recipe.",
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens, a single colon and underscores,",
        "NOT SPACES!!! By default, if no namespace is provided, recipes will start with the namespace \"skbee:\",",
        "this can be changed in the config to whatever you want. IDs are used for recipe discovery/unlocking recipes for players.",
        "Note: While 'custom' items will work in these recipes, it appears the smithing table will not recognize them. Requires MC 1.16+"})
@Examples({"on load:",
        "\tregister new smithing recipe for diamond chestplate using an iron chestplate and a diamond with id \"smith_diamond_chestplate\""})
@RequiredPlugins("1.16+")
@Since("1.4.2")
public class EffSmithingRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();
    private static final boolean IS_RUNNING_1_20 = Skript.isRunningMinecraft(1, 20);

    static {
        Skript.registerEffect(EffSmithingRecipe.class,
                "register [new] smithing recipe for %itemstack% using %recipechoice/itemtype% and %recipechoice/itemtype% with id %string%");
    }

    @SuppressWarnings("null")
    private Expression<ItemStack> result;
    private Expression<Object> base;
    private Expression<Object> addition;
    private Expression<String> key;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parse) {
        if (IS_RUNNING_1_20) {
            // TODO add back support for smithing recipes using new 1.20 classes
            Skript.error("Smithing recipes no longer work when running minecraft 1.20 and above, support for this will be added back in the future.");
            return false;
        }
        result = (Expression<ItemStack>) exprs[0];
        base = (Expression<Object>) exprs[1];
        addition = (Expression<Object>) exprs[2];
        key = (Expression<String>) exprs[3];
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void execute(@NotNull Event event) {
        ItemStack result = this.result.getSingle(event);
        RecipeChoice base = RecipeUtil.getRecipeChoice(this.base.getSingle(event));
        RecipeChoice addition = RecipeUtil.getRecipeChoice(this.addition.getSingle(event));
        if (result == null) {
            RecipeUtil.error("Error registering smithing recipe - result is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }
        if (base == null) {
            RecipeUtil.error("Error registering smithing recipe - base is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }
        if (addition == null) {
            RecipeUtil.error("Error registering smithing recipe - addition is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }

        NamespacedKey key = RecipeUtil.getKey(this.key.getSingle(event));
        if (key == null) {
            RecipeUtil.error("Current Item: &6'" + toString(event, true) + "'");
            return;
        }

        //Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        SmithingRecipe recipe = new SmithingRecipe(key, result, base, addition);
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logRecipe(recipe, recipe.getBase(), recipe.getAddition());
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "Register new smithing recipe for " + result.toString(event, debug) + " using " + base.toString(event, debug) + " and " +
                addition.toString(event, debug) + " with id " + key.toString(event, debug);
    }

}
