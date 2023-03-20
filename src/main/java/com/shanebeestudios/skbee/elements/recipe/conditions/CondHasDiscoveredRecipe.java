package com.shanebeestudios.skbee.elements.recipe.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Recipe;
import org.eclipse.jdt.annotation.Nullable;

@Name("Recipe - Has Discovered")
@Description("Check if a player has discovered a recipe. Can check recipes you created, another plugin has created, or vanilla Minecraft recipes." +
        "When checking recipes that are not your own, make sure to include the namespace, ex \"minecraft:diamond_sword\", \"someplugin:some_recipe\". " +
        "This condition is only available on 1.16+")
@Examples({"player has discovered recipe from id \"minecraft:furnace\"",
        "if player has discovered recipe from id \"my_custom_sword\":",
        "if player has discovered recipe from id \"someplugin:fancy_shovel\":",
        "if all players have not discovered recipe from id \"minecraft:golden_shovel\":",
        "if player has not discovered recipe from id \"my_fancy_hoe\":"})
@Since("1.4.9")
public class CondHasDiscoveredRecipe extends Condition {

    static {
        Skript.registerCondition(CondHasDiscoveredRecipe.class,
                "%players% (has|have) discovered %recipes%",
                "%players% (doesn't|does not|do not|don't) have discovered %recipes%");
    }

    private Expression<Player> players;
    private Expression<Recipe> recipes;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        setNegated(matchedPattern == 1);
        players = (Expression<Player>) exprs[0];
        recipes = (Expression<Recipe>) exprs[1];
        return true;
    }

    @Override
    public boolean check(Event event) {
        return players.check(event, player -> recipes.check(event, recipe -> {
            if(recipe instanceof Keyed keyed)
                return player.hasDiscoveredRecipe(keyed.getKey());
            return false;
        }));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return String.format("%s %s discovered recipes %s",
                players.toString(event, debug),
                isNegated() ? "has not" : "has",
                recipes.toString(event,debug));
    }
}
