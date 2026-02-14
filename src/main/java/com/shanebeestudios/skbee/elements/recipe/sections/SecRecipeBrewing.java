package com.shanebeestudios.skbee.elements.recipe.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.potion.PotionBrewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

public class SecRecipeBrewing extends Section {

    private static EntryValidator VALIDATOR;
    private static final PotionBrewer POTION_BREWER = Bukkit.getPotionBrewer();

    public static void register(Registration reg) {
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        builder.addRequiredEntry("id", String.class);
        builder.addRequiredEntry("result", ItemStack.class);
        builder.addRequiredEntry("ingredient", RecipeChoice.class);
        builder.addRequiredEntry("input", RecipeChoice.class);
        VALIDATOR = builder.build();

        reg.newSection(SecRecipeBrewing.class, "register [a] [new] (brewing recipe|potion mix)")
            .name("Recipe - Register Brewing Recipe")
            .description("This section allows you to register a brewing recipe, define the ingredient and input. Requires a PaperMC server.",
                "**Entries**:",
                " - `id` = The ID of this recipe.",
                " - `result` = The resulting output ItemStack of this recipe (What the 3 bottle slots turn into).",
                " - `ingredient` = Represents the ItemStack put in the top of the brewer (Accepts an ItemStack or RecipeChoice).",
                " - `input` = Represents the ItemStack put in the 3 bottle slots (Accepts an ItemStack or RecipeChoice).")
            .examples("on load:",
                "\tregister brewing recipe:",
                "\t\tid: \"custom:brew_glow_diamond\"",
                "\t\tresult: diamond of unbreaking with all item flags",
                "\t\tingredient: glowstone dust",
                "\t\tinput: potato",
                "\t\t",
                "\tregister brewing recipe:",
                "\t\tid: \"custom:yummy_soup\"",
                "\t\tresult: mushroom stew named \"&bYummy Soup\"",
                "\t\tingredient: glowstone dust",
                "\t\tinput: water bottle")
            .since("3.0.0")
            .register();
    }

    private Expression<String> id;
    private Expression<ItemStack> result;
    private Expression<RecipeChoice> ingredient;
    private Expression<RecipeChoice> input;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.id = (Expression<String>) container.getOptional("id", false);
        if (this.id == null) return false;
        this.result = (Expression<ItemStack>) container.getOptional("result", false);
        if (this.result == null) return false;
        this.ingredient = (Expression<RecipeChoice>) container.getOptional("ingredient", false);
        if (this.ingredient == null) return false;
        this.input = (Expression<RecipeChoice>) container.getOptional("input", false);
        if (this.ingredient == null) return false;
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
        RecipeChoice input = this.input.getSingle(event);
        RecipeChoice ingredient = this.ingredient.getSingle(event);

        if (namespacedKey == null) {
            error("Invalid id: " + recipeId);
            return;
        } else if (result == null || !result.getType().isItem() || result.getType().isAir()) {
            error("Inavlid result: " + result);
            return;
        } else if (input == null) {
            error("Invalid input: " + input);
            return;
        } else if (ingredient == null) {
            error("Invalid ingredient: " + ingredient);
            return;
        }

        // Remove duplicates on script reload
        POTION_BREWER.removePotionMix(namespacedKey);
        PotionMix potionMix = new PotionMix(namespacedKey, result, input, ingredient);
        POTION_BREWER.addPotionMix(potionMix);
        RecipeUtil.logBrewingRecipe(potionMix);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "register brewing recipe";
    }

}
