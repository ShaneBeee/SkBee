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
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Unregister")
@Description({"Remove a recipe from your server. Recipes can be removed at any time ",
        "but it is best to do so during a server load event. If a recipe is removed whilst a player is online ",
        "it will still show up in their recipe book, but they will not be able to craft it. If need be, you can get ",
        "a list of all recipes by simply typing \"/minecraft:recipe give YourName \" in game.",
        "You can remove Minecraft recipes, custom recipes and recipes from other plugins. Requires MC 1.13+"})
@Examples({"remove mc recipe \"acacia_boat\"",
        "remove minecraft recipe \"cooked_chicken_from_campfire_cooking\"",
        "remove recipe \"minecraft:diamond_sword\"",
        "remove all minecraft recipes",
        "remove all recipes",
        "remove custom recipe \"my_recipe\"",
        "remove recipe \"another_recipe\"",
        "remove recipe \"some_plugin:some_recipe\""})
@Since("1.0.0")
public class EffUnregisterRecipe extends Effect {

    private Expression<Object> recipes;

    static {
        Skript.registerEffect(EffUnregisterRecipe.class, "(remove|unregister) [recipe[s]] %recipes/namespacedkeys/strings%");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        recipes = (Expression<Object>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Object object : this.recipes.getArray(event)) {
            NamespacedKey key;
            if (object instanceof Keyed keyed) {
                key = keyed.getKey();
            } else {
                key = RecipeUtil.getKey(object);
            }

            if (key == null) continue;
            Bukkit.removeRecipe(key);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "unregister recipe(s)" + recipes.toString(event, debug);
    }

}
