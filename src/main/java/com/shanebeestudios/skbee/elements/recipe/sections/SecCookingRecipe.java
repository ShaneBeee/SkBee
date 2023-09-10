package com.shanebeestudios.skbee.elements.recipe.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
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
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;
import java.util.Locale;

public class SecCookingRecipe extends Section {

    private enum CookingRecipeType {
        // Other information if needed can be added over time, for now this provides an easier api interface.
        // Reviewer note:
        // if we believe this should be changed to recipe api folder, we can do that as cooking effect could use this.
        FURNACE(200),
        SMOKER(100),
        BLASTING(100),
        CAMPFIRE(600);

        private final int cookTime;

        CookingRecipeType(int cookTime) {
            this.cookTime = cookTime;
        }

        public int getCookTime() {
            return this.cookTime;
        }

    }

    private static final EntryValidator entries = EntryValidator.builder()
            .addEntryData(new ExpressionEntryData<>("input", null, false, Object.class))
            .addEntryData(new ExpressionEntryData<>("group", null, true, String.class))
            .addEntryData(new ExpressionEntryData<>("category", null, true, CookingBookCategory.class))
            .addEntryData(new ExpressionEntryData<>("cooktime", null, true, Timespan.class))
            .addEntryData(new ExpressionEntryData<>("experience", null, true, Number.class))
            .build();

    private static final boolean DEBUG = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;

    static {
        // Reviewer note:
        // using 'itemstack' over 'itemtype' is due to we can't return 'any dirt', but we can return 'rooted dirt'
        // while skript supports this, it's better to be clear and understanding
        Skript.registerSection(SecCookingRecipe.class, "register [a] [new] (:furnace|:smoker|blasting:(blasting|blast furnace)|:campfire) recipe with id %string% for %itemstack%");
    }

    private CookingRecipeType recipeType;
    private Expression<String> recipeId;
    private Expression<ItemStack> recipeResult;
    private Expression<Object> recipeInput;
    private Expression<CookingBookCategory> recipeCategory;
    private Expression<String> recipeGroup;
    private Expression<Timespan> cookTime;
    private Expression<Number> experience;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = entries.validate(sectionNode);
        if (container == null) return false;

        this.recipeType = CookingRecipeType.valueOf(parseResult.tags.get(0).toUpperCase(Locale.ROOT));
        this.recipeId = (Expression<String>) exprs[0];
        this.recipeResult = (Expression<ItemStack>) exprs[1];

        this.recipeCategory = (Expression<CookingBookCategory>) container.getOptional("category", false);
        this.recipeGroup = (Expression<String>) container.getOptional("group", false);
        this.cookTime = (Expression<Timespan>) container.getOptional("cooktime", false);
        this.experience = (Expression<Number>) container.getOptional("experience", false);
        this.recipeInput = (Expression<Object>) container.get("input", false);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        execute(event);
        return super.walk(event, false);
    }

    private void execute(Event event) {
        NamespacedKey recipeId = RecipeUtil.getKey(this.recipeId.getSingle(event));
        ItemStack recipeResult = this.recipeResult.getSingle(event);
        // Reviewer note: you wouldn't believe what I ran into again.
        // #getConvertedExpression() is used to prevent the famous 'UnparsedLiterals must be converted before use'
        RecipeChoice recipeInput = this.recipeInput != null ? RecipeUtil.getRecipeChoice(this.recipeInput.getConvertedExpression(Object.class).getSingle(event)) : null;
        CookingBookCategory recipeCategory = this.recipeCategory != null ? this.recipeCategory.getSingle(event) : null;
        String recipeGroup = this.recipeGroup != null ? this.recipeGroup.getSingle(event) : null;
        int cookTime = this.cookTime != null ? (int) this.cookTime.getSingle(event).getTicks_i() : recipeType.getCookTime();
        float experience = this.experience != null ? this.experience.getSingle(event).floatValue() : 0;

        if (recipeId == null) {
            RecipeUtil.error("Invalid/Missing recipe Id: &e" + this.toString(event, DEBUG));
            return;
        } else if (recipeResult == null || !recipeResult.getType().isItem() || recipeResult.getType().isAir()) {
            RecipeUtil.error("Invalid/Missing recipe result: &e" + this.toString(event, DEBUG));
            return;
        } else if (recipeInput == null) {
            RecipeUtil.error("Invalid/Missing recipe input: &e" + this.toString(event, DEBUG));
            return;
        }

        CookingRecipe<?> recipe = switch (recipeType) {
            case SMOKER -> new SmokingRecipe(recipeId, recipeResult, recipeInput, experience, cookTime);
            case FURNACE -> new FurnaceRecipe(recipeId, recipeResult, recipeInput, experience, cookTime);
            case BLASTING -> new BlastingRecipe(recipeId, recipeResult, recipeInput, experience, cookTime);
            case CAMPFIRE -> new CampfireRecipe(recipeId, recipeResult, recipeInput, experience, cookTime);
        };
        if (recipeCategory != null)
            recipe.setCategory(recipeCategory);
        if (recipeGroup != null && !recipeGroup.isBlank())
            recipe.setGroup(recipeGroup);
        Bukkit.removeRecipe(recipeId);
        Bukkit.addRecipe(recipe);
        if (DEBUG) RecipeUtil.logCookingRecipe(recipe);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "register a new " + recipeType.toString().toLowerCase(Locale.ROOT)
                + "recipe with id " + this.recipeId.toString(event, debug)
                + " for " + this.recipeResult.toString(event, debug);
    }

}
