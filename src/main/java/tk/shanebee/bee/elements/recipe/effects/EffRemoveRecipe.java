package tk.shanebee.bee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.config.Config;
import tk.shanebee.bee.elements.recipe.util.RecipeUtil;
import tk.shanebee.bee.elements.recipe.util.Remover;

@Name("Recipe - Remove")
@Description({"Remove a vanilla Minecraft recipe from your server. Recipes can be removed at any time ",
        "but it is best to do so during a server load event. If a recipe is removed whilst a player is online ",
        "it will still show up in their recipe book, but they will not be able to craft it. If need be, you can get ",
        "a list of all recipes by simply typing \"/minecraft:recipe give YourName \" in game.",
        "Note: Remove all minecraft recipes pattern was added in 1.3.4"})
@Examples({"remove mc recipe \"acacia_boat\"", "remove minecraft recipe \"cooked_chicken_from_campfire_cooking\"",
        "remove all minecraft recipes"})
@Since("1.0.0")
public class EffRemoveRecipe extends Effect {

    private static Remover REMOVER;
    private Config config = SkBee.getPlugin().getPluginConfig();

    static {
        try {
            REMOVER = new Remover();
            Skript.registerEffect(EffRemoveRecipe.class,
                    "remove (mc|minecraft) recipe[s] %strings%",
                    "remove all (mc|minecraft) recipe[s]");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Skript.warning("[SkRecipe] - Recipe remover failed to load!");
        }
    }

    @SuppressWarnings("null")
    private Expression<String> recipes;
    private boolean all;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        all = pattern == 1;
        recipes = pattern == 0 ? (Expression<String>) exprs[0] : null;
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (all) {
            REMOVER.removeAll();
            if (config.SETTINGS_DEBUG) {
                RecipeUtil.log("&aRemoving all Minecraft recipes.");
            }
            return;
        }
        String[] recipes = this.recipes.getAll(event);
        if (recipes == null) return;

        for (String recipe : recipes) {
            if (recipe.startsWith("minecraft:")) {
                recipe = recipe.replace("minecraft:", "");
            }
            REMOVER.removeRecipeByKey(recipe);
            if (config.SETTINGS_DEBUG) {
                RecipeUtil.log("&aRemoving recipe: minecraft:" + recipe);
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        if (all) {
            return "remove all minecraft recipes";
        } else {
            return "remove minecraft recipes " + recipes.toString(e, d);
        }
    }

}
