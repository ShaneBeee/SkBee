package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class EffRecipeDiscovery extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffRecipeDiscovery.class,
                "(discover|unlock) [(custom|mc|minecraft)] recipe[s] [with id[s]] %strings% for %players%",
                "(undiscover|lock) [(custom|mc|minecraft)] recipe[s] [with id[s]] %strings% for %players%")
            .name("Recipe - Discovery")
            .description("Lock/Unlock recipes for players. This uses the IDs we created earlier when registering recipes, " +
                "you can also lock/unlock minecraft recipes.")
            .examples("unlock recipe \"smoking_cod\" for all players",
                "unlock recipe \"minecraft:baked_potato_from_smoking\" for all players",
                "unlock minecraft recipe \"baked_potato_from_smoking\" for all players",
                "unlock recipe \"some_plugin:some_recipe\" for all players",
                "on pickup of diamonds:",
                "\tdiscover recipe \"fancy_diamonds\" for player")
            .since("1.0.0")
            .register();
    }

    @SuppressWarnings("null")
    private Expression<String> recipes;
    private Expression<Player> players;
    private boolean discover;

    @SuppressWarnings({"unchecked", "null", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, ParseResult parseResult) {
        recipes = (Expression<String>) exprs[0];
        players = (Expression<Player>) exprs[1];
        discover = pattern == 0;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        Player[] players = this.players.getAll(event);
        String[] recipes = this.recipes.getAll(event);
        for (String recipe : recipes) {
            NamespacedKey key = Util.getNamespacedKey(recipe, false);
            if (key == null) continue;
            for (Player player : players) {
                if (discover)
                    player.discoverRecipe(key);
                else
                    player.undiscoverRecipe(key);
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(Event e, boolean d) {
        String disc = discover ? "discover" : "undiscover";
        return disc + " recipe[s] " + recipes.toString(e, d) + " for " + players.toString(e, d);
    }

}
