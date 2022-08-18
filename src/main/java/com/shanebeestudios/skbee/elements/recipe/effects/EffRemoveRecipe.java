package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.config.Config;
import com.shanebeestudios.skbee.elements.recipe.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;

@SuppressWarnings({"NullableProblems"})
@Name("Recipe - Remove")
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
public class EffRemoveRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        Skript.registerEffect(EffRemoveRecipe.class,
                "remove [(custom|1¦(mc|minecraft))] recipe[s] %strings%",
                "remove all [(1¦(mc|minecraft))] recipe[s]");
    }

    @SuppressWarnings("null")
    private Expression<String> recipes;
    private boolean all;
    private boolean minecraft;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        all = pattern == 1;
        minecraft = parseResult.mark == 1;
        recipes = pattern == 0 ? (Expression<String>) exprs[0] : null;
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (all) {
            if (minecraft) {
                RecipeUtil.removeAllMCRecipes();
                if (config.SETTINGS_DEBUG) {
                    RecipeUtil.log("&aRemoving all Minecraft recipes.");
                }
            } else {
                Bukkit.clearRecipes();
                if (config.SETTINGS_DEBUG) {
                    RecipeUtil.log("&aRemoving all recipes.");
                }
            }
            return;
        }

        for (String recipe : this.recipes.getAll(event)) {
            NamespacedKey key;
            if (minecraft) {
                recipe = recipe.replace("minecraft:", "");
                key = NamespacedKey.minecraft(recipe);
            } else {
                key = RecipeUtil.getKey(recipe);
            }
            if (key != null) {
                Bukkit.removeRecipe(key);
                if (config.SETTINGS_DEBUG) {
                    RecipeUtil.log("&aRemoving recipe: " + recipe);
                }
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        if (all) {
            return "remove all minecraft recipes";
        } else if (minecraft) {
            return "remove minecraft recipes " + recipes.toString(e, d);
        } else {
            return "remove custom recipes " + recipes.toString(e, d);
        }
    }

}
