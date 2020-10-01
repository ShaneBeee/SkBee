package tk.shanebee.bee.elements.recipe.effects;

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
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.elements.recipe.util.RecipeUtil;

import javax.annotation.Nullable;

@Name("Recipe - Smithing")
@Description("Register a new smithing recipe. " +
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens and underscores." +
        " Used for recipe discovery/unlocking recipes for players. Requires MC 1.13+")
@Examples({"on load:",
        "\tregister new smithing recipe for diamond chestplate using an iron chestplate and a diamond with id \"smith_diamond_chestplate\""})
@RequiredPlugins("1.16+")
@Since("1.4.2")
public class EffSmithingRecipe extends Effect {

    static {
        if (Skript.isRunningMinecraft(1, 16)) {
            Skript.registerEffect(EffSmithingRecipe.class,
                    "register [new] smithing recipe for %itemtype% using %itemtype% and %itemtype% with id %string%");
        }
    }

    @SuppressWarnings("null")
    private Expression<ItemType> result;
    private Expression<ItemType> base;
    private Expression<ItemType> addition;
    private Expression<String> key;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parse) {
        result = (Expression<ItemType>) exprs[0];
        base = (Expression<ItemType>) exprs[1];
        addition = (Expression<ItemType>) exprs[2];
        key = (Expression<String>) exprs[3];
        return true;
    }

    @SuppressWarnings({"deprecation", "ConstantConditions"}) // RecipeChoice = draft API
    @Override
    protected void execute(@NotNull Event event) {
        ItemType result = this.result.getSingle(event);
        ItemType base = this.base.getSingle(event);
        ItemType addition = this.addition.getSingle(event);
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

        //Remove duplicates on script reload
        RecipeUtil.removeRecipeByKey(key);

        SmithingRecipe recipe = new SmithingRecipe(key,
                result.getRandom(),
                new RecipeChoice.ExactChoice(base.getRandom()),
                new RecipeChoice.ExactChoice(addition.getRandom()));
        Bukkit.addRecipe(recipe);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "Register new smithing recipe for " + result.toString(e, d) + " using " + base.toString(e, d) + " and " +
                addition.toString(e, d) + " with id " + key.toString(e, d);
    }

}
