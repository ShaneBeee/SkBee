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
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@Name("Recipe - Register Smithing Recipe")
@Description({"This section allows you to register a smithing transform recipe, define the output as well as the template, ",
        "base and addition items. Requires MC 1.20+",
        "\n`id` = The ID for your recipe.",
        "\n`result` = The resulting ItemStack of this recipe.",
        "\n`template` = Represents the first slot in the smithing inventory (Accepts an ItemStack or RecipeChoice).",
        "\n`base` = Represents the second slot in the smithing inventory (Accepts an ItemStack or RecipeChoice).",
        "\n`addition` = Represents the third slot in the smithing inventory (Optional).",
        "\n`copynbt` = Represents whether to copy the nbt from the input base item to the output, default = true (Requires PaperMC) (Optional)."})
@Examples({"on load:",
        "\tregister smithing transform recipe:",
        "\t\tid: \"test:smithing\"",
        "\t\tresult: emerald of unbreaking named \"&cFire Stone\" with all item flags",
        "\t\ttemplate: paper named \"&cFire Paper\"",
        "\t\tbase: diamond",
        "\t\taddition: blaze powder"})
@Since("3.0.0")
public class SecRecipeSmithing extends Section {

    public static final boolean HAS_NBT_METHOD = Skript.methodExists(SmithingRecipe.class, "willCopyNbt");
    private static final EntryValidator.EntryValidatorBuilder ENTRY_VALIDATOR = EntryValidator.builder();
    private static final boolean DEBUG = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;

    static {
        if (Skript.isRunningMinecraft(1, 20)) {
            Skript.registerSection(SecRecipeSmithing.class,
                    "register [a] [new] smithing [transform] recipe");
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("id", null, false, String.class));
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("result", null, false, ItemStack.class));
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("template", null, false, RecipeChoice.class));
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("base", null, false, RecipeChoice.class));
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("addition", null, true, RecipeChoice.class));
            ENTRY_VALIDATOR.addEntryData(new ExpressionEntryData<>("copynbt", null, true, Boolean.class));
        }
    }

    private Expression<String> id;
    private Expression<ItemStack> result;
    private Expression<RecipeChoice> template;
    private Expression<RecipeChoice> base;
    private Expression<RecipeChoice> addition;
    private Expression<Boolean> copyNbt;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = ENTRY_VALIDATOR.build().validate(sectionNode);
        if (container == null) return false;

        this.id = (Expression<String>) container.getOptional("id", false);
        if (this.id == null) return false;
        this.result = (Expression<ItemStack>) container.getOptional("result", false);
        if (this.result == null) return false;
        this.template = (Expression<RecipeChoice>) container.getOptional("template", false);
        if (this.template == null) return false;
        this.base = (Expression<RecipeChoice>) container.getOptional("base", false);
        if (this.base == null) return false;
        this.addition = (Expression<RecipeChoice>) container.getOptional("addition", false);
        if (this.addition == null) return false;
        this.copyNbt = (Expression<Boolean>) container.getOptional("copynbt", false);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        execute(event);
        return super.walk(event, false);
    }

    private void execute(Event event) {
        String recipeId = this.id.getSingle(event);
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

        SmithingTransformRecipe recipe;
        if (HAS_NBT_METHOD) {
            boolean copynbt = true;
            if (this.copyNbt != null) copynbt = Boolean.TRUE.equals(this.copyNbt.getSingle(event));
            recipe = new SmithingTransformRecipe(key, result, template, base, addition, copynbt);
        } else {
            recipe = new SmithingTransformRecipe(key, result, template, base, addition);
        }

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(recipe);
        if (DEBUG) RecipeUtil.logSmithingRecipe(recipe);

    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "register smithing recipe";
    }

}
