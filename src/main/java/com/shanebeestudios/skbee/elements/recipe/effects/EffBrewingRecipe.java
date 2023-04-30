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
        "\nNote: brewing recipes are fairly new within Paper, as such support for this is minimal.",
        "There is currently no way to retrieve all potion recipes or get recipe information like ingredients.",
        "Requires PaperMC 1.18+"})
@Examples({"set {_choice} to material choice using magma block, magma cream and blaze rod",
        "register a brewing recipe for lava bucket with input bucket using {_choice}"})
@Since("INSERT VERSION")
public class EffBrewingRecipe extends Effect {

    private static final boolean SUPPORTS_POTION_MIX = Skript.classExists("io.papermc.paper.potion.PotionMix");
    private final Config config = SkBee.getPlugin().getPluginConfig();
    private Expression<ItemStack> result;

    private Expression<Object> keyID;
    private Expression<RecipeChoice> input;
    private Expression<RecipeChoice> ingredient;

    static {
        if (SUPPORTS_POTION_MIX) {
            Skript.registerEffect(EffBrewingRecipe.class, "register [a] [new] brewing[[ ]stand] recipe for %itemstack% with input %recipechoice% using %recipechoice% (using|with (id|key)) %string/namespacedkey%");
        }
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        result = (Expression<ItemStack>) exprs[0];
        input = (Expression<RecipeChoice>) exprs[1];
        ingredient = (Expression<RecipeChoice>) exprs[2];
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
        } else if (result == null) {
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

