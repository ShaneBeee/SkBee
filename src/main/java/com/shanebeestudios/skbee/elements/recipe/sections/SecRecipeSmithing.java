package com.shanebeestudios.skbee.elements.recipe.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@Name("Recipe - Register Smithing Recipe")
@Description({"This section allows you to register a smithing transform recipe, define the output as well as the template, ",
        "base and addition items. Requires MC 1.13+",
        "\n`template` = Represents the first slot in the smithing inventory.",
        "\n`base` = Represents the second slot in the smithing inventory.",
        "\n`addition` = Represents the third slot in the smithing inventory (Optional)."})
@Examples({"on load:",
        "\tregister smithing transform recipe with id \"test:smithing\" with result emerald of unbreaking named \"&cFire Stone\" with all flags hidden:",
        "\t\ttemplate: paper named \"&cFire Paper\"",
        "\t\tbase: diamond",
        "\t\taddition: blaze powder"})
@Since("INSERT VERSION")
public class SecRecipeSmithing extends Section {

    private static final EntryValidator.EntryValidatorBuilder ENTRY_VALIDATOR = EntryValidator.builder();
    private static final boolean DEBUG = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;

    static {
        if (Skript.isRunningMinecraft(1, 20)) {
            Skript.registerSection(SecRecipeSmithing.class,
                    "register [a] [new] smithing [transform] recipe with id %string% (for|with result) %itemstack%");
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("template", null, false, Object.class));
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("base", null, false, Object.class));
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("addition", null, true, Object.class));
        }
    }

    private Expression<String> recipeId;
    private Expression<ItemStack> result;
    private Expression<?> template;
    private Expression<?> base;
    private Expression<?> addition;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = ENTRY_VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        this.recipeId = (Expression<String>) exprs[0];
        this.result = (Expression<ItemStack>) exprs[1];
        this.template = ((Expression<?>) container.get("template", false)).getConvertedExpression(Object.class);
        this.base = ((Expression<?>) container.get("base", false)).getConvertedExpression(Object.class);
        this.addition = ((Expression<?>) container.getOptional("addition", false));
        if (this.addition != null) this.addition = this.addition.getConvertedExpression(Object.class);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        execute(event);
        return super.walk(event, false);
    }

    private void execute(Event event) {
        String recipeId = this.recipeId.getSingle(event);
        if (recipeId == null) {
            RecipeUtil.error("Invalid/Missing recipe Id: &e" + this.toString(event, DEBUG));
            return;
        }
        NamespacedKey key = Util.getNamespacedKey(recipeId, false);
        ItemStack result = this.result.getSingle(event);
        RecipeChoice base = this.base != null ? RecipeUtil.getRecipeChoice(this.base.getSingle(event)) : null;
        RecipeChoice template = this.template != null ? RecipeUtil.getRecipeChoice(this.template.getSingle(event)) : null;
        RecipeChoice addition = this.addition != null ? RecipeUtil.getRecipeChoice(this.addition.getSingle(event)) : null;

        if (key == null) {
            RecipeUtil.error("Invalid/Missing recipe Id: &e" + this.toString(event, DEBUG));
            return;
        } else if ((result == null || !result.getType().isItem() || result.getType().isAir())) {
            RecipeUtil.error("Invalid/Missing recipe result: &e" + this.toString(event, DEBUG));
            return;
        } else if (base == null) {
            RecipeUtil.error("Invalid/Missing recipe base: &e" + this.toString(event, DEBUG));
            return;
        } else if (template == null) {
            RecipeUtil.error("Invalid/Missing recipe template: &e" + this.toString(event, DEBUG));
            return;
        } else if (addition == null) {
            addition = new RecipeChoice.MaterialChoice(Material.AIR);
        }

        SmithingTransformRecipe recipe = new SmithingTransformRecipe(key, result, template, base, addition);

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(recipe);
        if (DEBUG) RecipeUtil.logSmithingRecipe(recipe);

    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "register smithing recipe with id " + this.recipeId.toString(e, d) + " for " + this.result.toString(e, d);
    }

}
