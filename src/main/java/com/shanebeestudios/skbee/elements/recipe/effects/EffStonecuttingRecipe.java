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
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.recipe.util.RecipeUtil;

@SuppressWarnings({"ConstantConditions", "NullableProblems"})
@Name("Recipe - StoneCutting")
@Description({"Register a new stone cutting recipe. " +
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens and underscores.",
        "Used for recipe discovery/unlocking recipes for players. ",
        "You may also include an optional group for recipes. These will group the recipes together in the recipe book.",
        "By default recipes will start with the namespace \"skrecipe:\", this can be changed in the config to whatever you want.",
        "Requires MC 1.13+"})
@Examples({"on skript load:", "\tregister new stone cutting recipe for diamond using diamond ore with id \"cutting_diamond\""})
@RequiredPlugins("1.14+")
@Since("1.0.0")
public class EffStonecuttingRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        if (Skript.isRunningMinecraft(1, 14)) {
            Skript.registerEffect(EffStonecuttingRecipe.class,
                    "register [new] stone[ ]cutt(ing|er) recipe for %itemtype% (using|with ingredient) %itemtype/materialchoice% with id %string% [in group %-string%]");
        }
    }

    @SuppressWarnings("null")
    private Expression<ItemType> item;
    private Expression<Object> ingredient;
    private Expression<String> key;
    private Expression<String> group;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<ItemType>) exprs[0];
        ingredient = (Expression<Object>) exprs[1];
        key = (Expression<String>) exprs[2];
        group = (Expression<String>) exprs[3];
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void execute(Event event) {
        ItemType item = this.item.getSingle(event);
        Object ingredient = this.ingredient.getSingle(event);

        if (item == null) {
            Skript.error("Error registering stonecutting recipe - result is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (ingredient == null) {
            Skript.error("Error registering stonecutting recipe - ingredient is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }

        String group = this.group != null ? this.group.getSingle(event) : "";

        NamespacedKey key = RecipeUtil.getKey(this.key.getSingle(event));
        if (key == null) {
            RecipeUtil.error("Current Item: §6'" + toString(event, true) + "'");
            return;
        }

        RecipeChoice choice;
        if (ingredient instanceof ItemType) {
            ItemStack itemStack = ((ItemType) ingredient).getRandom();
            if (itemStack == null) return;
            Material material = itemStack.getType();

            // If ingredient isn't a custom item, just register the material
            if (itemStack.isSimilar(new ItemStack(material))) {
                choice = new MaterialChoice(material);
            } else {
                choice = new ExactChoice(itemStack);
            }
        } else {
            choice = ((MaterialChoice) ingredient);
        }
        StonecuttingRecipe recipe = new StonecuttingRecipe(key, item.getRandom(), choice);
        recipe.setGroup(group);

        // Remove duplicates on script reload
        RecipeUtil.removeRecipeByKey(key);

        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logRecipe(recipe, recipe.getInputChoice());
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        String group = this.group != null ? " in group " + this.group.toString(e, d) : "";
        return "register new stone cutting recipe for " + item.toString(e, d) + " using " + ingredient.toString(e, d) + group;
    }

}
