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

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
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
        "remove custom recipe \"my_recipe\"",
        "remove recipe \"another_recipe\"",
        "remove recipe \"someplugin:some_recipe\""})
@Since("1.0.0")
public class EffRemoveRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        Skript.registerEffect(EffRemoveRecipe.class,
                "remove [(0¦custom|1¦(mc|minecraft))] recipe[s] %strings%",
                "remove all (mc|minecraft) recipe[s]");
    }

    @SuppressWarnings("null")
    private Expression<String> recipes;
    private boolean all;
    private boolean MC;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        all = pattern == 1;
        MC = all || parseResult.mark == 1;
        recipes = pattern == 0 ? (Expression<String>) exprs[0] : null;
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (all) {
            RecipeUtil.removeAllMCRecipes();
            if (config.SETTINGS_DEBUG) {
                RecipeUtil.log("&aRemoving all Minecraft recipes.");
            }
            return;
        }
        String[] recipes = this.recipes.getAll(event);
        if (recipes == null) return;

        for (String recipe : recipes) {
            if (MC || recipe.startsWith("minecraft:")) {
                recipe = recipe.replace("minecraft:", "");
                if (config.SETTINGS_DEBUG) {
                    RecipeUtil.log("&aRemoving recipe: minecraft:" + recipe);
                }
                RecipeUtil.removeMCRecipe(recipe);
            } else {
                if (config.SETTINGS_DEBUG) {
                    RecipeUtil.log("&aRemoving recipe: " + recipe);
                }
                RecipeUtil.removeRecipeByKey(recipe);
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        if (all) {
            return "remove all minecraft recipes";
        } else if (MC) {
            return "remove minecraft recipes " + recipes.toString(e, d);
        } else {
            return "remove custom recipes " + recipes.toString(e, d);
        }
    }

}
