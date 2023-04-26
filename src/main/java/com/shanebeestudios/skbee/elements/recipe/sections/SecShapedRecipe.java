package com.shanebeestudios.skbee.elements.recipe.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.recipe.Ingredient;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Name("Recipes - Advanced Shaped Recipe")
@Description({"Ceates a new shaped recipe using sections and entries. Minecraft 1.19+ required for categories.",
        "\nShape: When using the shape entry, it is required to follow a rectangular shape in example (\"ab\", \"abc\") will not work",
        "while (\"ab\", \"ab\"), (\"abc\") and (\"a\", \"a\", \"a\") will work.",
        "The character here will correspond with the character used in ingredients.",
        "Empty spaces (ex: \"a b\") will represent an empty spot in a recipe, no need to register air.",
        "\nIngredients: Uses the ingredients expression, with a key/value system (see expression for more info)."})
@Examples({"register shaped recipe for diamond named \"&bDiamond\" with id \"my_recipes:blue_diamond\"",
        "\tshape: \"a\", \"e\", \"a\"",
        "\tingredients: a:diamond, e:emerald",
        "\tgroup: \"coloured_diamonds\"",
        "\tcategory: misc",
        "\t# Requires MC 1.19 for category",
        "",
        "set {_MUD_BALL} to clay ball with custom model data 1",
        "register shaped recipe for mud with id \"mud\"",
        "\tshape: \"11\", \"11\"",
        "\tingredients: 1:{_MUD_BALL}"})
@Since("INSERT VERSION")
public class SecShapedRecipe extends Section {

    private final Config config = SkBee.getPlugin().getPluginConfig();
    private static final boolean CRAFTING_CATEGORY_EXISTS = Skript.classExists("org.bukkit.inventory.recipe.CraftingBookCategory");

    static {
        Skript.registerSection(SecShapedRecipe.class, "register [a] [new] shaped recipe for %itemstack% (using|with (id|key)) %string/namespacedkey%");
    }

    private static final EntryValidator validator = EntryValidator.builder()
            .addEntryData(new ExpressionEntryData<>("ingredients", null, false, Ingredient.class))
            .addEntryData(new ExpressionEntryData<>("shape", null, false, String.class))
            .addEntryData(new ExpressionEntryData<>("category", null, true, CraftingBookCategory.class))
            .addEntryData(new ExpressionEntryData<>("group", null, true, String.class))
            .build();

    private Expression<Object> keyID;
    private Expression<ItemStack> result;
    private Expression<? extends String> shape;
    private Expression<Ingredient> ingredients;

    @Nullable
    private Expression<? extends String> group;
    @Nullable
    private Expression<? extends CraftingBookCategory> category;


    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int MatchedPattern, Kleenean kleenean, ParseResult isDelayed, SectionNode sectionNode, List<TriggerItem> list) {
        EntryContainer entryContainer = validator.validate(sectionNode);
        if (entryContainer == null)
            return false;
        keyID = (Expression<Object>) exprs[1];
        result = (Expression<ItemStack>) exprs[0];

        shape = (Expression<? extends String>) entryContainer.get("shape", false);
        ingredients = (Expression<Ingredient>) entryContainer.get("ingredients", false);
        group = (Expression<? extends String>) entryContainer.getOptional("group", true);
        if (CRAFTING_CATEGORY_EXISTS)
            category = (Expression<? extends CraftingBookCategory>) entryContainer.getOptional("category", true);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        execute(event);
        return super.walk(event, false);
    }

    public void execute(Event event) {
        NamespacedKey key = RecipeUtil.getKey(this.keyID.getSingle(event));
        if (key == null) {
            RecipeUtil.error("Error registering crafting recipe - key is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (this.result == null) {
            RecipeUtil.error("Error registering crafting recipe - result is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (this.shape == null) {
            RecipeUtil.error("Error registering crafting recipe - invalid shape");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (this.ingredients == null) {
            RecipeUtil.error("Error registering crafting recipe - invalid ingredients");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }

        String[] shape = this.shape.getArray(event);
        Ingredient[] ingredients = this.ingredients.getArray(event);
        ItemStack result = this.result.getSingle(event);
        if (ingredients.length < 1 || ingredients.length > 9) {
            RecipeUtil.error("Error registering crafting recipe - invalid ingredients");
            RecipeUtil.error("Error: array size was too large or too small");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        } else if (!isValidShape(shape)) {
            RecipeUtil.error("Error registering crafting recipe - invalid shape");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }
//        else if (String.join("", shape).length() > ingredients.length) { // FIXES out-of-bounds if shape is too small
//            RecipeUtil.error("Error registering crafting recipe - invalid shape/ingredients");
//            RecipeUtil.error("Shape is either missing an entry or too many ingredients are provided"); // Being more specific as this is hard to debug
//            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
//            return;
//        }

        String group = this.group != null ? this.group.getSingle(event) : null;
        CraftingBookCategory category = this.category != null && CRAFTING_CATEGORY_EXISTS ? this.category.getSingle(event) : null;

        if (result == null) {
            RecipeUtil.error("Error registering crafting recipe - result is null");
            RecipeUtil.error("Current Item: &6" + this.toString(event, true));
            return;
        }
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        if (group != null) recipe.setGroup(group);
        if (category != null) recipe.setCategory(category);

        recipe.shape(shape);

        Map<Character, Object> ingredientMap = new HashMap<>();
        for (Ingredient ingredient : this.ingredients.getArray(event)) {
            ingredientMap.put(ingredient.key(), ingredient.item());
        }

        char[] chars = String.join("", shape).toCharArray();
        for (char aChar : chars) {
            if (aChar == ' ') continue;

            Object ingredient = ingredientMap.get(aChar);
            if (ingredient == null) continue;
            RecipeChoice recipeChoice = RecipeUtil.getRecipeChoice(ingredient);
            if (recipeChoice == null) continue;
            recipe.setIngredient(aChar, recipeChoice);
        }

        if (config.SETTINGS_DEBUG) {
            RecipeUtil.logShapedRecipe(recipe);
        }
        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(recipe);
    }

    private boolean isValidShape(String... shapes) {
        if (shapes == null) return false;
        else if (shapes.length < 1 || shapes.length > 3) return false;

        int lastLength = -1;
        for (String row : shapes) {
            if (row == null) return false;
            else if (row.length() > 3 || row.length() < 1 || lastLength > row.length()) return false;
            lastLength = row.length();
        }

        return true;

    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "register shaped recipe for " + result.toString(event, debug) + " with id " + keyID.toString(event, debug);
    }

}

