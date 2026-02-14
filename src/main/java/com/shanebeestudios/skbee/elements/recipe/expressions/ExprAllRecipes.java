package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprAllRecipes extends SimpleExpression<String> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprAllRecipes.class, String.class,
                "[(all [[of] the]|the)] [(1:(mc|minecraft)|2:custom)] recipe[s] [(for|of) %-itemtypes%]")
            .name("Recipe - All Recipes")
            .description("Get a list of all recipes. May be from a specific item, may be just Minecraft recipes or custom recipes.",
                "Due to some items having more than 1 recipe this may return multiple recipes.")
            .examples("set {_recipes::*} to all recipes of iron ingot")
            .since("1.4.0")
            .register();
    }

    private int pattern;
    private Expression<ItemType> items;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pattern = parseResult.mark;
        items = (Expression<ItemType>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected String[] get(Event e) {
        if (this.items != null) {
            return getRecipesForItems(this.items.getAll(e));
        }
        return getAllRecipes();
    }

    private String[] getAllRecipes() {
        List<String> recipes = new ArrayList<>();
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof Keyed) {
                NamespacedKey key = ((Keyed) recipe).getKey();
                if (pattern == 0 || isMinecraft(key) || isCustom(key)) {
                    recipes.add(key.toString());
                }
            }
        });
        return recipes.toArray(new String[0]);
    }

    private String[] getRecipesForItems(ItemType[] itemTypes) {
        List<String> recipes = new ArrayList<>();
        for (ItemType itemType : itemTypes) {
            ItemStack itemStack = itemType.getRandom();
            assert itemStack != null;
            for (Recipe recipe : Bukkit.getRecipesFor(itemStack)) {
                if (recipe instanceof Keyed keyed) {
                    NamespacedKey key = keyed.getKey();
                    if (pattern == 0 || isMinecraft(key) || isCustom(key)) {
                        recipes.add(key.toString());
                    }
                }
            }
        }
        return recipes.toArray(new String[0]);
    }

    private boolean isMinecraft(NamespacedKey key) {
        return pattern == 1 && key.getNamespace().equalsIgnoreCase("minecraft");
    }

    private boolean isCustom(NamespacedKey key) {
        return pattern == 2 && !key.getNamespace().equalsIgnoreCase("minecraft");
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        if (this.items == null) {
            return "all recipes";
        }
        return "all recipes for " + this.items.toString(e, d);
    }

}
