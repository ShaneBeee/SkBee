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
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.recipe.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
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

    static {
        Skript.registerEffect(EffSmithingRecipe.class,
                "register [new] smithing recipe for %itemtype% using %itemtype/materialchoice% and %itemtype/materialchoice% with id %string%");
    }

    @SuppressWarnings("null")
    private Expression<ItemType> result;
    private Expression<Object> base;
    private Expression<Object> addition;
    private Expression<String> key;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parse) {
        result = (Expression<ItemType>) exprs[0];
        base = (Expression<Object>) exprs[1];
        addition = (Expression<Object>) exprs[2];
        key = (Expression<String>) exprs[3];
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void execute(@NotNull Event event) {
        ItemType result = this.result.getSingle(event);
        Object base = this.base.getSingle(event);
        Object addition = this.addition.getSingle(event);
        if (result == null) {
            RecipeUtil.error("Error registering smithing recipe - result is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (base == null) {
            RecipeUtil.error("Error registering smithing recipe - base is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (addition == null) {
            RecipeUtil.error("Error registering smithing recipe - addition is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }

        NamespacedKey key = RecipeUtil.getKey(this.key.getSingle(event));
        if (key == null) {
            RecipeUtil.error("Current Item: §6'" + toString(event, true) + "'");
            return;
        }

        //Remove duplicates on script reload
        Bukkit.removeRecipe(key);

        ItemStack resultStack = result.getRandom();
        RecipeChoice choiceBase = getChoice(base);
        RecipeChoice choiceAddition = getChoice(addition);
        if (resultStack == null || choiceBase == null || choiceAddition == null) return;

        SmithingRecipe recipe = new SmithingRecipe(
                key,
                resultStack,
                choiceBase,
                choiceAddition);
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logRecipe(recipe, recipe.getBase(), recipe.getAddition());
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "Register new smithing recipe for " + result.toString(e, d) + " using " + base.toString(e, d) + " and " +
                addition.toString(e, d) + " with id " + key.toString(e, d);
    }

    private RecipeChoice getChoice(Object object) {
        if (object instanceof ItemType itemType) {
            ItemStack itemStack = itemType.getRandom();

            Material material = itemStack.getType();
            // If ingredient isn't a custom item, just register the material
            if (itemStack.isSimilar(new ItemStack(material))) {
                return new MaterialChoice(material);
            } else {
                return new ExactChoice(itemStack);
            }
        } else if (object instanceof MaterialChoice) {
            return ((MaterialChoice) object);
        }
        return null;
    }

}
