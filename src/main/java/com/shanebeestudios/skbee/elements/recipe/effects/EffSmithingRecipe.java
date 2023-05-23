package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
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
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Smithing")
@Description({"Register a new smithing recipe.",
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens, a single colon and underscores,",
        "NOT SPACES!!! By default, if no namespace is provided, recipes will start with the namespace \"skbee:\",",
        "this can be changed in the config to whatever you want. IDs are used for recipe discovery/unlocking recipes for players.",
        "\nNote: While 'custom' items will work in these recipes, it appears the smithing table will not recognize them. Requires MC 1.16+"})
@Examples({"on load:",
        "\tregister new smithing recipe for diamond chestplate using an iron chestplate and a diamond with id \"smith_diamond_chestplate\""})
@Since("1.4.2")
public class EffSmithingRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();
    private static final boolean USE_EXPERIMENTAL_SYNTAX = SkBee.getPlugin().getPluginConfig().RECIPE_EXPERIMENTAL_SYNTAX;

    static {
        String STRING_PATTERN = USE_EXPERIMENTAL_SYNTAX ? "with (key|id) %namespacedkey%" : "with id %string%";
        Skript.registerEffect(EffSmithingRecipe.class,
                "register [a] [new] smithing recipe for %itemstack% using %recipechoice/itemtype% and %recipechoice/itemtype% " + STRING_PATTERN);
    }

    private Expression<ItemStack> result;
    private Expression<Object> base;
    private Expression<Object> addition;
    private Expression<Object> keyID;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parse) {
        result = (Expression<ItemStack>) exprs[0];
        base = (Expression<Object>) exprs[1];
        addition = (Expression<Object>) exprs[2];
        keyID = (Expression<Object>) exprs[3];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        ItemStack result = this.result.getSingle(event);
        RecipeChoice base = RecipeUtil.getRecipeChoice(this.base.getSingle(event));
        RecipeChoice addition = RecipeUtil.getRecipeChoice(this.addition.getSingle(event));
        NamespacedKey key = RecipeUtil.getKey(this.keyID.getSingle(event));
        if (result == null) {
            RecipeUtil.error("Error registering smithing recipe - result is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        } else if (base == null) {
            RecipeUtil.error("Error registering smithing recipe - base is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        } else if (addition == null) {
            RecipeUtil.error("Error registering smithing recipe - addition is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        } else if (key == null) {
            RecipeUtil.error("Current Item: §6'" + toString(event, true) + "'");
            return;
        }

        SmithingRecipe recipe = new SmithingRecipe(key, result, base, addition);
        //Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logRecipe(recipe, recipe.getBase(), recipe.getAddition());
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return String.format("register new smithing recipe for %s using %s and %s with id %s",
                result.toString(event, debug),
                base.toString(event, debug),
                addition.toString(event, debug),
                keyID.toString(event, debug));
    }

}
