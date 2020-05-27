package tk.shanebee.bee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NullableProblems"})
@Name("Recipe - Recipes from Item")
@Description("Get a list of all recipes from a specific item. " +
        "Due to some items having more than 1 recipe this may return multiple recipes. Requires 1.13+")
@Examples("set {_recipes::*} to all recipes of iron ingot")
@Since("INSERT VERSION")
public class ExprRecipesFromItems extends SimpleExpression<String> {

    static {
        if (Skript.classExists("org.bukkit.Keyed")) {
            Skript.registerExpression(ExprRecipesFromItems.class, String.class, ExpressionType.COMBINED,
                    "[(all [[of] the]|the)] recipe[s] (for|of) %itemtypes%");
        }
    }

    private Expression<ItemType> items;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        items = (Expression<ItemType>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected String[] get(Event e) {
        List<String> recipes = new ArrayList<>();
        for (ItemType itemType : items.getAll(e)) {
            ItemStack itemStack = itemType.getRandom();
            assert itemStack != null;
            for (Recipe recipe : Bukkit.getRecipesFor(itemStack)) {
                if (recipe instanceof Keyed) {
                    recipes.add(((Keyed) recipe).getKey().toString());
                }
            }
        }
        return recipes.toArray(new String[0]);
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
        return "recipes of " + items.toString(e, d);
    }

}
