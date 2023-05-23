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
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Has Discovered")
@Description({"Checks if a player has discovered a recipe. Can check recipes you craeted, another plugin has created, or vanilla Minecraft recipes.",
        "When checking recipes that are not your own, make sure to include the namespaced. ex: \"minecraft:diamond_sword\", \"someplugin:some_recipe\"",
        "This condition is only available on Minecraft 1.16+"})
@Examples({"set {_players::*} to players where [input hasn't discovered recipe with id \"minecraft:furnace\"]",
        "if player has discovered recipe with id \"someplugin:advanced_sword\":",
        "if player hasn't discovered recipe with id \"harvest:my_first_recipe\":"})
@Since("1.4.9")
public class CondHasDiscoveredRecipe extends Condition {

    static {
        Skript.registerCondition(CondHasDiscoveredRecipe.class,
                "%players% (has|have) discovered [recipe[s]] %recipes%",
                "%players% (has(n't| not)|have(n't| not)) discovered [recipe[s]] %recipes%");
    }

    private Expression<Player> players;
    private Expression<Recipe> recipes;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setNegated(matchedPattern == 1);
        players = (Expression<Player>) exprs[0];
        recipes = (Expression<Recipe>) exprs[1];
        return true;
    }

    @Override
    public boolean check(Event event) {
        return recipes.check(event, recipe -> {
            if (recipe instanceof Keyed keyed) {
                return players.check(event, player -> player.hasDiscoveredRecipe(keyed.getKey()));
            }
            return false;
        });
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return String.format("%s %s discovered recipes %s",
                players.toString(event, debug),
                isNegated() ? "has not" : "has",
                recipes.toString(event, debug));
    }

}
