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
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Brewing Recipe")
@Description({"Register a custom brewing stand recipe. Requires PaperMC 1.18+",
        "",
        "input refers to the bottom 3 ingredient slots, please note when using items that can stack " +
                "this will replace all items in the slot with 1 of the result",
        "",
        "Note:\n",
        "Paper has a pretty bad API for potions as such support for this is minimal",
        "issues you have may or may not be solvable please report any of your issues to the issue tracker."})
@Examples({"register new brewing stand recipe with id \"stone_from_magma_block\" for stone using magma block with input cobblestone",
        "set {_choice} to material choice using magma block, blaze rod and lava bucket",
        "register new brewing recipe with id \"stone_from_heat\" for stone using {_choice} with input cobblestone"})
@Since("INSERT VERSION")
public class EffBrewingRecipe extends Effect {

    private static final boolean SUPPORTS_POTION_MIX = Skript.classExists("io.papermc.paper.potion.PotionMix");

    static {
        Skript.registerEffect(EffBrewingRecipe.class,
                "register [a] [new] brewing[[ ]stand] recipe with id %string% for %itemstack% (using|with ingredient) %recipechoice/itemtype% [and] with input %recipechoice/itemtype%");
    }

    private Expression<ItemStack> result;
    private Expression<String> id;
    private Expression<Object> ingredient;
    private Expression<Object> input;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!SUPPORTS_POTION_MIX) {
            Skript.error("Brewing stand recipes are not support on your server, you must be running 1.18+ PaperMC.");
            return false;
        }
        this.id = (Expression<String>) exprs[0];
        this.result = (Expression<ItemStack>) exprs[1];
        this.ingredient = (Expression<Object>) exprs[2];
        this.input = (Expression<Object>) exprs[3];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemStack result = this.result.getSingle(event);
        NamespacedKey id = RecipeUtil.getKey(this.id.getSingle(event));
        RecipeChoice ingredient = RecipeUtil.getRecipeChoice(this.ingredient.getSingle(event));
        RecipeChoice input = RecipeUtil.getRecipeChoice(this.input.getSingle(event));
        if (result == null) {
            RecipeUtil.error("Error registering brewing recipe - result is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (result.getType().isAir()) {
            RecipeUtil.error("Error registering brewing recipe - result can't be air");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (id == null) {
            RecipeUtil.error("Error registering brewing recipe - id is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (ingredient == null) {
            RecipeUtil.error("Error registering brewing recipe - ingredient is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (input == null) {
            RecipeUtil.error("Error registering brewing recipe - input is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }
        PotionMix potionMix = new PotionMix(id, result, input, ingredient);
        Bukkit.getPotionBrewer().addPotionMix(potionMix);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return String.format("register brewing recipe with id %s for %s using %s with input %s",
                id.toString(event, debug),
                result.toString(event, debug),
                ingredient.toString(event, debug),
                input.toString(event, debug));
    }

}
