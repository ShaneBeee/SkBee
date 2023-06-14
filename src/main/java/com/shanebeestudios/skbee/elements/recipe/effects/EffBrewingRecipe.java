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
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Brewing")
@Description({"Registers a new custom brewing stand recipe.",
        "Input refers to the bottom 3 slots of a brewing stand.",
        "\nNote: this is by default disabled within skbee, in order to enable you must go through SkBee's config",
        "\nNote: Stackable inputs are buggy and behave poorly with the server. ie. using a bucket and stacking it will replace all with 1 of the result. " +
        "Brewing recipes are fairly new within Paper, as such support for this is minimal. " +
        "There is currently no way to retrieve all potion recipes or get recipe information like ingredients.",
        "Requires Paper 1.19+"})
@Examples({"set {_inputs} to material choice using mundane potion, thick potion, awkward potion",
        "set {_recipe1} to material choice using magma cream, blaze rod and fire charge",
        "set {_recipe2} to material choice using magma block and lava bucket",
        "set {_key} to namespacedkey from \"potions:extended_fire_resistance\"",
        "register brewing stand recipe for fire resistance potion with input {_inputs} using {_recipe1} with id \"potions:short_fire_resistance\"",
        "register brewing stand recipe for extended fire resistance potion with input {_inputs} using {_recipe2} with key {_key}"})
@Since("INSERT VERSION")
public class EffBrewingRecipe extends Effect {

    private static final boolean SUPPORTS_POTION_MIX = Skript.classExists("io.papermc.paper.potion.PotionMix");
    private final Config config = SkBee.getPlugin().getPluginConfig();
    private static final boolean USE_EXPERIMENTAL_SYNTAX = SkBee.getPlugin().getPluginConfig().RECIPE_EXPERIMENTAL_SYNTAX;
    private static final boolean POTION_SYNTAX_ENABLED = SkBee.getPlugin().getPluginConfig().RECIPE_POTION_SYNTAX;

    private Expression<ItemStack> result;

    private Expression<Object> keyID;
    private Expression<RecipeChoice> input;
    private Expression<Object> ingredient;

    static {
        if (SUPPORTS_POTION_MIX && POTION_SYNTAX_ENABLED) {
            String STRING_PATTERN = USE_EXPERIMENTAL_SYNTAX ? "with (key|id) %namespacedkey%" : "with id %string%";
            Skript.registerEffect(EffBrewingRecipe.class,
                    "register [a] [new] brewing[[ ]stand] recipe for %itemstack% with input %itemtype/recipechoice% using %itemtype/recipechoice% " + STRING_PATTERN);
        }
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        result = (Expression<ItemStack>) exprs[0];
        input = (Expression<RecipeChoice>) exprs[1];
        ingredient = (Expression<Object>) exprs[2];
        keyID = (Expression<Object>) exprs[3];
        return true;
    }

    @Override
    protected void execute(Event event) {
        NamespacedKey key = RecipeUtil.getKey(this.keyID.getSingle(event));
        ItemStack result = this.result.getSingle(event);
        RecipeChoice input = RecipeUtil.getRecipeChoice(this.input.getSingle(event));
        RecipeChoice ingredient = RecipeUtil.getRecipeChoice(this.ingredient.getSingle(event));

        if (key == null) {
            RecipeUtil.error("Error registering brewing recipe - key is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (result == null || result.getType().isAir()) {
            RecipeUtil.error("Error registering brewing recipe - result is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (input == null) {
            RecipeUtil.error("Error registering brewing recipe - input is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (ingredient == null) {
            RecipeUtil.error("Error registering brewing recipe - ingredient is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }

        PotionMix potionMix = new PotionMix(key, result, input, ingredient);
        Bukkit.getPotionBrewer().removePotionMix(key);
        Bukkit.getPotionBrewer().addPotionMix(potionMix);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logPotionMix(potionMix);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "register new brewing stand recipe for " + result.toString(event, debug)
                + " with input " + input.toString(event, debug)
                + " using " + ingredient.toString(event, debug)
                + " with id " + keyID.toString(event, debug);
    }

}

