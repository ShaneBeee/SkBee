package com.shanebeestudios.skbee.elements.recipe.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.event.recipe.ShapelessRecipeCreateEvent;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SecRecipeShapeless extends Section {

    private static final Map<String, CraftingBookCategory> CATEGORY_MAP = new HashMap<>(); // TODO this will cause errors on lower versions, will fix later
    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        builder.addRequiredEntry("id", String.class);
        builder.addRequiredEntry("result", ItemStack.class);
        builder.addOptionalEntry("group", String.class);
        builder.addOptionalEntry("category", String.class);
        for (CraftingBookCategory value : CraftingBookCategory.values()) {
            String name = value.name().toLowerCase(Locale.ROOT);
            CATEGORY_MAP.put(name, value);
        }
        builder.addRequiredSection("ingredients");
        VALIDATOR = builder.build();

        reg.newSection(SecRecipeShapeless.class, VALIDATOR, "register [a] [new] shapeless recipe")
            .name("Recipe - Register Shapeless Recipe")
            .description("This section allows you to register a shapeless recipe and add ingredients.",
                "**Entries**:",
                " - `id` = The ID for your recipe. This is used for recipe discovery and Minecraft's /recipe command.",
                " - `result` = The resulting item of this recipe.",
                " - `group` = Define a group to group your recipes together in the recipe book " +
                    "(an example would be having 3 recipes with the same outcome but a variety of ingredients) (optional).",
                " - `category` = The recipe book category your recipe will be in (optional) " +
                    "Options are \"building\", \"redstone\", \"equiptment\", \"misc\".",
                " - `ingredients` = This section is where you will add the ingredients.")
            .examples("on load:",
                "\tregister shapeless recipe:",
                "\t\tid: \"custom:string\"",
                "\t\tresult: 4 string",
                "\t\tingredients:",
                "\t\t\tadd material choice of every wool to ingredients",
                "",
                "\tregister shapeless recipe:",
                "\t\tid: \"custom:totem_of_undying\"",
                "\t\tresult: totem of undying",
                "\t\tgroup: \"custom tools\"",
                "\t\tcategory: \"redstone\"",
                "\t\tingredients:",
                "\t\t\tadd diamond block to ingredients",
                "\t\t\tadd material choice of minecraft item tag \"minecraft:planks\" to ingredients",
                "\t\t\tadd emerald block to ingredients",
                "\t\t\tadd end rod to ingredients",
                "\t\t\tadd wither skeleton skull to ingredients",
                "",
                "\tregister shapeless recipe:",
                "\t\tid: \"custom:end_rod\"",
                "\t\tresult: end rod",
                "\t\tgroup: \"custom tools\"",
                "\t\tcategory: \"redstone\"",
                "\t\tingredients:",
                "\t\t\tadd diamond block to ingredients",
                "\t\t\tadd emerald block to ingredients")
            .since("3.0.0")
            .register();
    }

    private Expression<String> id;
    private Expression<ItemStack> result;
    private Expression<String> group;
    private Expression<String> category;
    private Trigger ingredientSection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.id = (Expression<String>) container.getOptional("id", false);
        if (this.id == null) return false;
        this.result = (Expression<ItemStack>) container.getOptional("result", false);
        if (this.result == null) return false;
        this.group = (Expression<String>) container.getOptional("group", false);
        this.category = (Expression<String>) container.getOptional("category", false);

        // Parse the ingredients section
        SectionNode ingredients = container.get("ingredients", SectionNode.class, false);
        this.ingredientSection = loadCode(ingredients, "ingredients section", ShapelessRecipeCreateEvent.class);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        execute(event);
        return super.walk(event, false);
    }

    @SuppressWarnings("deprecation")
    private void execute(Event event) {
        Object localVars = Variables.copyLocalVariables(event);

        String id = this.id.getSingle(event);
        if (id == null) {
            error("Missing id");
            return;
        }
        NamespacedKey key = Util.getNamespacedKey(id, false);
        if (key == null) {
            error("Invalid id: " + id);
            return;
        }

        ItemStack result = this.result.getSingle(event);
        if (result == null || result.getType().isAir() || !result.getType().isItem()) {
            error("Invalid result: " + result);
            return;
        }

        // Start recipe registration
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, result);
        if (this.group != null) {
            String group = this.group.getSingle(event);
            if (group != null) shapelessRecipe.setGroup(group);
        }
        if (this.category != null) {
            String category = this.category.getSingle(event);
            if (category != null && CATEGORY_MAP.containsKey(category))
                shapelessRecipe.setCategory(CATEGORY_MAP.get(category));
        }

        // Execute ingredients section
        // Recipe ingredients are set in there
        ShapelessRecipeCreateEvent recipeEvent = new ShapelessRecipeCreateEvent(shapelessRecipe);
        Variables.setLocalVariables(recipeEvent, localVars);
        TriggerItem.walk(this.ingredientSection, recipeEvent);
        Variables.setLocalVariables(event, localVars);
        Variables.removeLocals(recipeEvent);

        if (shapelessRecipe.getIngredientList().isEmpty()) {
            error("Missing ingredients");
            return;
        }

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(shapelessRecipe);
        RecipeUtil.logShapelessRecipe(shapelessRecipe);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "register shapeless recipe";
    }

}
