package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Recipes - Discovered")
@Description("Get and modify the discovered recipes of a player.")
@Examples({"clear discovered recipes of player",
        "delete discovered recipes of player",
        "reset discovered recipes of player",
        "add \"harvest:harvester_hoe\" to discovered recipes of player",
        "remove \"trashtaste:dumpster_fire\" from discovered recipes of player",
        "set discovered recipes of players to recipes"})
@Since("INSERT VERSION")
public class ExprDiscoveredRecipes extends PropertyExpression<Player, Recipe> {

    static {
        register(ExprDiscoveredRecipes.class, Recipe.class, "discovered recipes", "players");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends Player>) exprs[0]);
        return true;
    }

    @Override
    protected Recipe[] get(Event event, Player[] players) {
        return getExpr().stream(event)
                .flatMap(player -> player.getDiscoveredRecipes().stream())
                .map(Bukkit::getRecipe)
                .toArray(Recipe[]::new);
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        switch (mode) {
            case ADD, REMOVE, SET, RESET, DELETE -> {
                return CollectionUtils.array(Recipe[].class);
            }
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        List<NamespacedKey> recipes = new ArrayList<>();
        if (delta != null) {
            for (Object object : delta) {
                if (!(object instanceof Keyed keyed)) continue;
                recipes.add(keyed.getKey());
            }
        }
        switch (mode) {
            case RESET, DELETE:
                getExpr().stream(event).forEach(player -> player.undiscoverRecipes(player.getDiscoveredRecipes()));
            case SET:
                getExpr().stream(event).forEach(player -> player.discoverRecipes(recipes));
                break;
            case ADD:
                getExpr().stream(event).forEach(player -> player.discoverRecipes(recipes));
                break;
            case REMOVE:
                getExpr().stream(event).forEach(player -> player.undiscoverRecipes(recipes));
                break;

        }
    }

    @Override
    public Class<? extends Recipe> getReturnType() {
        return Recipe.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "discovered recipes of " + getExpr().toString(event, debug);
    }

}
