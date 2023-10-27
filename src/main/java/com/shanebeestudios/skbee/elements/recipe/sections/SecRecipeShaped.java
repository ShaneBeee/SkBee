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
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.event.recipe.ShapedRecipeCreateEvent;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Name("Recipe - Register Shaped Recipe")
@Description({"This section allows you to register a shaped recipe, define the shape and set ingredients.",
        "You can optionally add a group and category.",
        "\n`id` = The ID for your recipe. This is used for recipe discovery and Minecraft's /recipe command.",
        "\n`result` = The resulting item of this recipe.",
        "\n`shape` = A list of strings (1 to 3 strings) which each have 1-3 characters (must be the same char count per string).",
        "These correspect to the ingredients set for these shapes. (See examples for details.)",
        "Blank spaces will just be empty spaces in a crafting grid.",
        "\n`group` = Define a group to group your recipes together in the recipe book",
        "(an example would be having 3 recipes with the same outcome but a variety of ingredients) (optional).",
        "\n`category` = The recipe book category your recipe will be in (optional) [Requires MC 1.19+].",
        "Options are \"building\", \"redstone\", \"equiptment\", \"misc\".",
        "\n`ingredients` = This section is where you will set the ingredients to correspend with your shape."})
@Examples({"on load:",
        "\tregister shaped recipe with:",
        "\t\tid: \"custom:fancy_stone\"",
        "\t\tresult: stone named \"&aFANCY STONE\"",
        "\t\tshape: \"aaa\", \"aba\", \"aaa\"",
        "\t\tgroup: \"bloop\"",
        "\t\tcategory: \"building\"",
        "\t\tingredients:",
        "\t\t\tset ingredient of \"a\" to stone",
        "\t\t\tset ingredient of \"b\" to diamond",
        "",
        "\tregister shaped recipe:",
        "\t\tid: \"custom:fancy_sword\"",
        "\t\tresult: diamond sword of unbreaking 5 named \"&bStrong Sword\"",
        "\t\tshape: \"a\", \"a\", \"b\"",
        "\t\tingredients:",
        "\t\t\tset ingredient of \"a\" to emerald",
        "\t\t\tset ingredient of \"b\" to stick named \"DOOM\"",
        "",
        "\tregister shaped recipe:",
        "\t\tid: \"custom:string\"",
        "\t\tresult: 4 of string",
        "\t\tshape: \"a\"",
        "\t\tingredients:",
        "\t\t\tset ingredient of \"a\" to material choice of all wool",
        "",
        "\tregister shaped recipe:",
        "\t\tid: \"custom:bee_2\"",
        "\t\tresult: (skull of \"MHF_Bee\" parsed as offline player) named \"&bMr &3Bee\"",
        "\t\tshape: \"x x\", \" z \", \"x x\"",
        "\t\tingredients:",
        "\t\t\tset ingredient of \"x\" to honeycomb",
        "\t\t\tset ingredient of \"z\" to honey bottle"})
@Since("INSERT VERSION")
public class SecRecipeShaped extends Section {

    private static final boolean DEBUG = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;
    private static final Map<String, CraftingBookCategory> CATEGORY_MAP = new HashMap<>();
    private static final EntryValidator.EntryValidatorBuilder ENTRY_VALIDATOR = EntryValidator.builder();

    static {
        Skript.registerSection(SecRecipeShaped.class, "register [a] [new] shaped recipe");
        ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("id", null, false, String.class));
        ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("result", null, false, ItemStack.class));
        ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("shape", null, false, String.class));
        ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("group", null, true, String.class));
        if (RecipeUtil.HAS_CATEGORY) {
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("category", null, true, String.class));
            for (CraftingBookCategory value : CraftingBookCategory.values()) {
                String name = value.name().toLowerCase(Locale.ROOT);
                CATEGORY_MAP.put(name, value);
            }
        }
        ENTRY_VALIDATOR.addSection("ingredients", false);
    }

    private Expression<String> id;
    private Expression<ItemStack> result;
    private Expression<String> shape;
    private Expression<String> group;
    private Expression<String> category;
    private Trigger ingredientSection;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = ENTRY_VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        this.id = (Expression<String>) container.getOptional("id", false);
        if (this.id == null) {
            Skript.error("Invalid/Empty 'id' entry");
            return false;
        }
        this.result = (Expression<ItemStack>) container.getOptional("result", false);
        if (this.result == null) {
            Skript.error("Invalid/Empty 'result' entry");
            return false;
        }
        this.shape = (Expression<String>) container.getOptional("shape", false);
        if (this.shape == null) {
            Skript.error("Invalid/Empty 'shape' entry");
            return false;
        }
        this.group = (Expression<String>) container.getOptional("group", false);
        this.category = RecipeUtil.HAS_CATEGORY ? (Expression<String>) container.getOptional("category", false) : null;

        // Parse the ingredients section
        SectionNode ingredients = container.get("ingredients", SectionNode.class, false);
        this.ingredientSection = loadCode(ingredients, "ingredients section", ShapedRecipeCreateEvent.class);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        execute(event);
        return super.walk(event, false);
    }

    private void execute(Event event) {
        Object localVars = Variables.copyLocalVariables(event);

        String id = this.id.getSingle(event);
        if (id == null) {
            RecipeUtil.error("Invalid/Missing recipe ID: &e" + this.toString(event, false));
            return;
        }
        NamespacedKey key = Util.getNamespacedKey(id, false);
        if (key == null) return;

        ItemStack result = this.result.getSingle(event);
        if (result == null || result.getType().isAir() || !result.getType().isItem()) {
            RecipeUtil.error("Invalid result: &e" + result);
            return;
        }

        String[] shape = this.shape.getArray(event);
        if (shape.length < 1 || shape.length > 3) {
            RecipeUtil.error("Invalid shape:");
            RecipeUtil.log("&7- &eShape must be between 1 and 3 strings.");
            RecipeUtil.log("&7- Found: &e" + Arrays.toString(shape));
            return;
        }
        int shapeLength = shape[0].length();
        for (String s : shape) {
            int length = s.length();
            if (length < 1 || length > 3 || length != shapeLength) {
                RecipeUtil.error("Invalid shape:");
                RecipeUtil.log("&7- &eShapes must be between 1 and 3 characters, and the same amount for each shape.");
                RecipeUtil.log("&7- Found: &e" + Arrays.toString(shape));
                return;
            }
        }

        // Start recipe registration
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
        shapedRecipe.shape(shape);
        if (this.group != null) {
            String group = this.group.getSingle(event);
            if (group != null) shapedRecipe.setGroup(group);
        }
        if (this.category != null) {
            String category = this.category.getSingle(event);
            if (category != null && CATEGORY_MAP.containsKey(category))
                shapedRecipe.setCategory(CATEGORY_MAP.get(category));
        }

        // Execute ingredients section
        // Recipe ingredients are set in there
        ShapedRecipeCreateEvent recipeEvent = new ShapedRecipeCreateEvent(shapedRecipe);
        Variables.setLocalVariables(recipeEvent, localVars);
        TriggerItem.walk(this.ingredientSection, recipeEvent);
        Variables.setLocalVariables(event, localVars);
        Variables.removeLocals(recipeEvent);

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(shapedRecipe);
        if (DEBUG) RecipeUtil.logShapedRecipe(shapedRecipe);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "register shaped recipe";
    }

}
