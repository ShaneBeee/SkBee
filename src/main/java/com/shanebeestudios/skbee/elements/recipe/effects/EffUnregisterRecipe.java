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
@Description({"Remove a recipe from your server. Recipes can be removed at any time",
        "but it is best to do so during a server load event. If a recipe is removed whilst a player is online",
        "it will still show up in their recipe book, but they will not be able to craft it. If need be, you can get",
        "a list of all recipes by simply typing \"/minecraft:recipe give YourName \" in game.",
        "You can remove Minecraft recipes, custom recipes and recipes from other plugins.",
        "You can remove custom potion recipes by using the potion keyword. Potions requires PaperMC 1.18+"})
@Examples({"remove mc recipe \"acacia_boat\"",
        "remove minecraft recipe \"cooked_chicken_from_campfire_cooking\"",
        "remove recipe \"minecraft:diamond_sword\"",
        "remove all minecraft recipes",
        "remove all recipes",
        "remove custom recipe \"my_recipe\"",
        "remove recipe \"another_recipe\"",
        "remove recipe \"some_plugin:some_recipe\"",
        "unregister potion recipe \"some_plugin:some_other_recipe_brewing_stand\""})
@Since("1.0.0")
public class EffUnregisterRecipe extends Effect {

    private Expression<Object> recipes;
    private boolean removePotion;
    private static final boolean SUPPORTS_POTION_MIX = Skript.classExists("io.papermc.paper.potion.PotionMix");

    static {
        Skript.registerEffect(EffUnregisterRecipe.class, "(remove|unregister) [:potion] [recipe[s]] %recipes/namespacedkeys/strings%");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        recipes = (Expression<Object>) exprs[0];
        removePotion = parseResult.hasTag("potion");
        if (removePotion && !SUPPORTS_POTION_MIX) {
            Skript.error("Potion recipes are not supported on your server version.");
            return false;
        }
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
            if (removePotion) {
                Bukkit.getPotionBrewer().removePotionMix(key);
                continue;
            }
            Bukkit.removeRecipe(key);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "unregister" + (removePotion ? " potion " : "") + "recipe(s)" + recipes.toString(event, debug);
    }

}

