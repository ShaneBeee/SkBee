package com.shanebeestudios.skbee.elements.recipe.sections;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
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
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.event.recipe.ShapelessRecipeCreateEvent;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Name("Recipe - Register Shapeless Recipe")
@Description({"This section allows you to register a shapeless recipe and add ingredients.",
        "You can optionally add a group and category.",
        "\n`group` = Define a group to group your recipes together in the recipe book",
        "(an example would be having 3 recipes with the same outcome but a variety of ingredients) (optional).",
        "\n`category` = The recipe book category your recipe will be in (optional).",
        "Options are \"building\", \"redstone\", \"equiptment\", \"misc\".",
        "\n`ingredients` = This section is where you will add the ingredients."})
@Examples({"on load:",
        "\tregister shapeless recipe with id \"custom:string\" for 4 string:",
        "\t\tingredients:",
        "\t\t\tadd material choice of every wool to ingredients",
        "",
        "\tregister shapeless recipe with id \"custom:totem_of_undying\" for totem of undying:",
        "\t\tgroup: \"custom tools\"",
        "\t\tcategory: \"redstone\"",
        "\t\tingredients:",
        "\t\t\tadd diamond block to ingredients",
        "\t\t\tadd material choice of every plank to ingredients",
        "\t\t\tadd emerald block to ingredients",
        "\t\t\tadd end rod to ingredients",
        "\t\t\tadd wither skeleton skull to ingredients",
        "",
        "\tregister shapeless recipe with id \"custom:end_rod\" for end rod:",
        "\t\tgroup: \"custom tools\"",
        "\t\tcategory: \"redstone\"",
        "\t\tingredients:",
        "\t\t\tadd diamond block to ingredients",
        "\t\t\tadd emerald block to ingredients"})
@Since("INSERT VERSION")
public class SecRecipeShapeless extends Section {

    private static final boolean DEBUG = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;
    private static final Map<String, CraftingBookCategory> CATEGORY_MAP = new HashMap<>();

    static {
        for (CraftingBookCategory value : CraftingBookCategory.values()) {
            String name = value.name().toLowerCase(Locale.ROOT);
            CATEGORY_MAP.put(name, value);
        }
        Skript.registerSection(SecRecipeShapeless.class, "register shapeless recipe with id %string% (for|with result) %itemtype%");
    }

    private Expression<String> id;
    private Expression<ItemType> result;
    private Expression<String> group;
    private Expression<String> category;
    private final EntryValidator entries = EntryValidator.builder()
            .addEntryData(new ExpressionEntryData<>("group", null, true, String.class))
            .addEntryData(new ExpressionEntryData<>("category", null, true, String.class))
            .addSection("ingredients", false)
            .build();
    private Trigger ingredientSection;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = entries.validate(sectionNode);
        if (container == null) return false;

        this.id = (Expression<String>) exprs[0];
        this.result = (Expression<ItemType>) exprs[1];
        this.group = (Expression<String>) container.getOptional("group", false);
        this.category = (Expression<String>) container.getOptional("category", false);

        // Set the event for the ingredients section
        ParserInstance.get().setCurrentEvent("ingredients section", ShapelessRecipeCreateEvent.class);
        SectionNode ingredients = container.get("ingredients", SectionNode.class, false);
        this.ingredientSection = new Trigger(ParserInstance.get().getCurrentScript(), "recipe ingredients", new SimpleEvent(), ScriptLoader.loadItems(ingredients));
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        execute(event);
        return super.walk(event, false);
    }

    private void execute(Event event) {
        String id = this.id.getSingle(event);
        if (id == null) {
            RecipeUtil.error("Invalid/Missing recipe ID: &e" + this.toString(event, false));
            return;
        }
        NamespacedKey key = RecipeUtil.getKey(id);
        if (key == null) return;

        ItemType result = this.result.getSingle(event);
        if (result == null || result.getMaterial().isAir() || !result.getMaterial().isItem()) {
            RecipeUtil.error("Invalid result: &e" + result);
            return;
        }

        // Start recipe registration
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, result.getRandom());
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
        this.ingredientSection.execute(recipeEvent);

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(shapelessRecipe);
        if (DEBUG) RecipeUtil.logShapelessRecipe(shapelessRecipe);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String id = this.id.toString(e, d);
        String result = this.result.toString(e, d);
        return "register shapeless recipe with id " + id + " with result " + result;
    }

}
