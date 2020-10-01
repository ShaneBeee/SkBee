package tk.shanebee.bee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.config.Config;
import tk.shanebee.bee.elements.recipe.util.RecipeUtil;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
@Name("Recipe - Shaped/Shapeless")
@Description({"Register a new shaped/shapeless recipe for a specific item using custom ingredients. ",
        "The ID will be the name given to this recipe. IDs may only contain letters, numbers, periods, hyphens and underscores.",
        " Used for recipe discovery/unlocking recipes for players. ",
        "You may also include an optional group for recipes. These will group the recipes together in the recipe book. ",
        "<b>NOTE:</b> Recipes with 4 or less ingredients will be craftable in the player's crafting grid.",
        "By default recipes will start with the namespace \"skrecipe:\", this can be changed in the config to whatever you want.",
        "Requires MC 1.13+"})
@Examples({"on load:",
        "\tregister new shaped recipe for elytra using air, iron chestplate, air, air, iron chestplate and air with id \"elytra\"",
        "\tset {_strong} to emerald named \"&3Strong Emerald\"",
        "\tregister new shaped recipe for {_strong} using emerald, emerald, air, emerald, emerald and air with id \"strong_emerald\"",
        "\tregister new shaped recipe for diamond chestplate named \"&3Strong Emerald Chestplate\" using {_strong}, air, {_strong}, " +
                "{_strong}, {_strong}, {_strong}, {_strong}, {_strong} and {_strong} with id \"strong_emerald_chestplate\""})
@RequiredPlugins("1.13+")
@Since("1.0.0")
public class EffCraftingRecipe extends Effect {

    private final Config config = SkBee.getPlugin().getPluginConfig();

    static {
        Skript.registerEffect(EffCraftingRecipe.class,
                "register [new] (0¦shaped|1¦shapeless) recipe for %itemtype% (using|with ingredients) %itemtypes% with id %string% [in group %-string%]");
    }

    @SuppressWarnings("null")
    private Expression<ItemType> item;
    private Expression<ItemType> ingredients;
    private Expression<String> key;
    private Expression<String> group;
    private boolean shaped;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        item = (Expression<ItemType>) exprs[0];
        ingredients = (Expression<ItemType>) exprs[1];
        key = (Expression<String>) exprs[2];
        group = (Expression<String>) exprs[3];
        shaped = parseResult.mark == 0;
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemType item = this.item.getSingle(event);
        ItemType[] ingredients = this.ingredients.getAll(event);

        if (item == null) {
            RecipeUtil.error("Error registering crafting recipe - result is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (ingredients == null) {
            RecipeUtil.error("Error registering crafting recipe - ingredient is null");
            RecipeUtil.error("Current Item: §6" + this.toString(event, true));
            return;
        }

        String group = this.group != null ? this.group.getSingle(event) : null;
        NamespacedKey key = RecipeUtil.getKey(this.key.getSingle(event));

        // Remove duplicates on script reload
        RecipeUtil.removeRecipeByKey(key);

        if (shaped)
            registerShaped(item, ingredients, key, group);
        else
            registerShapeless(item, ingredients, key, group);
    }

    @SuppressWarnings("deprecation")
    private void registerShaped(ItemType item, ItemType[] ingredients, NamespacedKey key, String group) {
        boolean craftingTable = ingredients.length > 4;

        ShapedRecipe recipe = new ShapedRecipe(key, item.getRandom());
        if (group != null) recipe.setGroup(group);

        Character[] oldChar = new Character[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Character[] keyChar = new Character[9];
        for (int i = 0; i < 9; i++) {
            if (ingredients.length - 1 < i) {
                keyChar[i] = ' ';
            } else if (ingredients[i].getMaterial() == Material.AIR) {
                keyChar[i] = ' ';
            } else {
                keyChar[i] = oldChar[i];
            }
        }

        if (craftingTable) {
            String one = String.valueOf(keyChar[0]) + keyChar[1] + keyChar[2];
            String two = String.valueOf(keyChar[3]) + keyChar[4] + keyChar[5];
            String three = String.valueOf(keyChar[6]) + keyChar[7] + keyChar[8];
            recipe.shape(one, two, three);
        } else {
            String one = String.valueOf(keyChar[0]) + keyChar[1];
            String two = String.valueOf(keyChar[2]) + keyChar[3];
            recipe.shape(one, two);
        }

        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i].getMaterial() != Material.AIR) {
                recipe.setIngredient(keyChar[i], new RecipeChoice.ExactChoice(ingredients[i].getRandom()));
            }
        }
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logRecipe(recipe, recipe.getIngredientMap().toString());
        }
        Bukkit.addRecipe(recipe);
    }

    @SuppressWarnings("deprecation")
    private void registerShapeless(ItemType item, ItemType[] ingredients, NamespacedKey key, String group) {
        ShapelessRecipe recipe = new ShapelessRecipe(key, item.getRandom());
        if (group != null) recipe.setGroup(group);

        for (ItemType ingredient : ingredients) {
            // Exclude non-items from shapeless recipes (produced IllegalArgumentException)
            if (ingredient.getMaterial() != Material.AIR && ingredient.getMaterial().isItem()) {
                recipe.addIngredient(new RecipeChoice.ExactChoice(ingredient.getRandom()));
            } else {
                if (config.SETTINGS_DEBUG) {
                    RecipeUtil.warn("ERROR LOADING RECIPE:");
                    RecipeUtil.warn("Non item &b" + ingredient + "&e found in recipe with ID &b" + key.getKey() +
                            "&e, this item will be removed from the recipe.");
                }
            }
        }
        Bukkit.addRecipe(recipe);
        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logRecipe(recipe, recipe.getIngredientList().toString());
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        String shape = shaped ? "shaped" : "shapeless";
        String group = this.group != null ? " in group " + this.group.toString(e, d) : "";
        return "Register new " + shape + " recipe for " + item.toString(e, d) + " using " + ingredients.toString(e, d) +
                " with id " + key.toString(e, d) + group;
    }

}
