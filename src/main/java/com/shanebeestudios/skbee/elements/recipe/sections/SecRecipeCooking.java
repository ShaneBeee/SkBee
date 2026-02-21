package com.shanebeestudios.skbee.elements.recipe.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.CookingRecipeType;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SecRecipeCooking extends Section {

    private static EntryValidator VALIDATOR;
    private static final Map<String, CookingBookCategory> CATEGORY_MAP = new HashMap<>();

    public static void register(Registration reg) {
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        builder.addRequiredEntry("id", String.class);
        builder.addRequiredEntry("result", ItemStack.class);
        builder.addRequiredEntry("input", RecipeChoice.class);

        builder.addOptionalEntry("group", String.class);
        builder.addOptionalEntry("cooktime", Timespan.class);
        builder.addOptionalEntry("experience", Number.class);
        builder.addOptionalEntry("category", String.class);
        for (CookingBookCategory category : CookingBookCategory.values()) {
            CATEGORY_MAP.put(category.toString().toLowerCase(Locale.ROOT), category);
        }
        VALIDATOR = builder.build();

        reg.newSection(SecRecipeCooking.class, VALIDATOR,
                "register [a] [new] (furnace|1:smoking|2:blasting|3:campfire) recipe")
            .name("Recipe - Register Cooking Recipe")
            .description("This section allows you to register any cooking recipe and define special properties.",
                "**Entries**:",
                " - `id` = The ID for your recipe. This is used for recipe discovery and Minecraft's /recipe command.",
                " - `result` = The resulting ItemStack of this recipe.",
                " - `input` = The item the recipe requires as an input to output the result (Accepts an ItemStack or RecipeChoice) (Required).",
                " - `cooktime` = How long the recipe will take to finish cooking before result is given (Optional).",
                " - `experience` = The amount of experience gained when the recipe is finished cooking (Optional) " +
                    "Default cook times are, furnace = 10 seconds, smoking/blasting = 5 seconds and campfire = 30 seconds.",
                " - `group` = You can define a group in which all recipes under this are sorted together in the recipe book (Optional). " +
                    "Examples of this in game are beds and wood types.",
                " - `category` = Which category in the recipe book this recipe should appear within (Optional 1.19.4+). " +
                    "Valid category types are \"food\", \"blocks\", \"misc\", if no category is defined it defaults to \"misc\".")
            .examples("register new furnace recipe:",
                "\tid: \"sieve:gravel_to_sand\"",
                "\tresult: sand",
                "\tinput: gravel",
                "\tgroup: \"sieve\"",
                "\tcooktime: 1 minecraft day # 20 minutes",
                "\texperience: 6",
                "\tcategory: \"blocks\"",
                "",
                "register new campfire recipe:",
                "\tid: \"sieve:cobblestone_to_gravel\"",
                "\tresult: gravel",
                "\tinput: cobblestone",
                "\tgroup: \"sieve\"",
                "\tcategory: \"blocks\"",
                "",
                "register new smoking recipe:",
                "\tid: \"chef:beef_jerky\"",
                "\tresult: cooked mutton named \"&oBeef&r Jerky\"",
                "\tinput: rotten flesh",
                "\tcategory: \"food\"",
                "",
                "register a new blasting recipe:",
                "\tid: \"firery_sword\"",
                "\tresult: diamond sword of fire aspect named \"Flaming Sword\"",
                "\tinput: diamond sword")
            .since("3.0.0")
            .register();
    }

    private CookingRecipeType recipeType;
    private Expression<String> id;
    private Expression<ItemStack> result;
    private Expression<RecipeChoice> input;
    private Expression<String> category;
    private Expression<String> group;
    private Expression<Timespan> cookTime;
    private Expression<Number> experience;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.recipeType = CookingRecipeType.values()[parseResult.mark];
        this.id = (Expression<String>) container.getOptional("id", false);
        if (this.id == null) return false;
        this.result = (Expression<ItemStack>) container.getOptional("result", false);
        if (this.result == null) return false;
        this.input = (Expression<RecipeChoice>) container.getOptional("input", false);
        if (this.input == null) return false;
        this.category = (Expression<String>) container.getOptional("category", false);
        this.group = (Expression<String>) container.getOptional("group", false);
        this.cookTime = (Expression<Timespan>) container.getOptional("cooktime", false);
        this.experience = (Expression<Number>) container.getOptional("experience", false);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        execute(event);
        return super.walk(event, false);
    }

    private void execute(Event event) {
        String recipeId = this.id.getSingle(event);
        if (recipeId == null) {
            error("Missing id");
            return;
        }
        NamespacedKey namespacedKey = Util.getNamespacedKey(recipeId, false);
        ItemStack result = this.result.getSingle(event);
        // #getConvertedExpression() is used to prevent the famous 'UnparsedLiterals must be converted before use'
        RecipeChoice input = this.input.getSingle(event);

        int cookTime = this.recipeType.getCookTime();
        if (this.cookTime != null) {
            Timespan timespan = this.cookTime.getSingle(event);
            if (timespan != null) {
                cookTime = (int) timespan.getAs(Timespan.TimePeriod.TICK);
            } else {
                warning("Invalid cooktime, defaulting to recipe default: " + new Timespan(Timespan.TimePeriod.TICK, cookTime));
            }
        }
        float experience = 0;
        if (this.experience != null) {
            Number num = this.experience.getSingle(event);
            if (num != null) {
                experience = num.floatValue();
            } else {
                warning("Invalid experience, defaulting to 0");
            }
        }

        if (namespacedKey == null) {
            error("Invalid id: " + recipeId);
            return;
        } else if (result == null || !result.getType().isItem() || result.getType().isAir()) {
            error("Invalid result: " + result);
            return;
        } else if (input == null) {
            error("Invalid input: " + this.input.toString(event, true));
            return;
        }

        CookingRecipe<?> recipe = switch (this.recipeType) {
            case FURNACE -> new FurnaceRecipe(namespacedKey, result, input, experience, cookTime);
            case SMOKING -> new SmokingRecipe(namespacedKey, result, input, experience, cookTime);
            case BLASTING -> new BlastingRecipe(namespacedKey, result, input, experience, cookTime);
            case CAMPFIRE -> new CampfireRecipe(namespacedKey, result, input, experience, cookTime);
        };
        if (this.category != null) {
            String category = this.category.getSingle(event);
            if (category != null && CATEGORY_MAP.containsKey(category.toLowerCase(Locale.ROOT)))
                recipe.setCategory(CATEGORY_MAP.get(category.toLowerCase(Locale.ROOT)));
        }

        String recipeGroup = this.group != null ? this.group.getSingle(event) : null;
        if (recipeGroup != null && !recipeGroup.isBlank())
            recipe.setGroup(recipeGroup);
        Bukkit.removeRecipe(namespacedKey);
        Bukkit.addRecipe(recipe);
        RecipeUtil.logCookingRecipe(recipe);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "register a new " + this.recipeType.toString().toLowerCase(Locale.ROOT) + " recipe";
    }

}
