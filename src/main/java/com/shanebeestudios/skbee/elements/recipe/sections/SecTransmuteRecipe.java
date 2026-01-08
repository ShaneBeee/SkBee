package com.shanebeestudios.skbee.elements.recipe.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.TransmuteRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("Recipe - Register Transmute Recipe")
@Description({"Represents a recipe which will change the type of the input material when combined with an additional material, " +
    "but preserve all custom data. ",
    "Only the material of the result stack will be used.",
    "Used for dyeing shulker boxes in Vanilla.",
    "See [**crafting_transmute**](https://minecraft.wiki/w/Recipe#crafting_transmute) on McWiki for more info.",
    "Requires Minecraft 1.21.2+",
    "",
    "**Entries**:",
    "- `id` = The ID for your recipe. This is used for recipe discovery and Minecraft's /recipe command.",
    "- `result` = The material that will be transmuted in the result slot.",
    "- `input` = The input ingredient (The item which will have it's data copied to the result).",
    "- `material` = The item to be applied to the first (like a dye).",
    "- `group` = Define a group to group your recipes together in the recipe book " +
        "(an example would be having 3 recipes with the same outcome but a variety of ingredients) [optional].",
    "- `category` = The recipe book category your recipe will be in [optional]. " +
        "Options are \"building\", \"redstone\", \"equipment\", \"misc\"."})
@Examples({"register transmute recipe:",
    "\tid: \"custom:better_swords\"",
    "\tresult: netherite sword",
    "\tinput: minecraft item tag \"minecraft:swords\"",
    "\tmaterial: netherite ingot"})
@Since("3.8.0")
public class SecTransmuteRecipe extends Section {

    private static final EntryValidator VALIDATOR;

    static {
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        builder.addRequiredEntry("id", String.class);
        builder.addRequiredEntry("result", ItemType.class);
        builder.addRequiredEntry("input", RecipeChoice.class);
        builder.addRequiredEntry("material", RecipeChoice.class);
        builder.addOptionalEntry("group", String.class);
        builder.addOptionalEntry("category", String.class);

        VALIDATOR = builder.build();
        Skript.registerSection(SecTransmuteRecipe.class, "register transmute recipe");
    }

    private Expression<String> id;
    private Expression<ItemType> result;
    private Expression<RecipeChoice> input;
    private Expression<RecipeChoice> material;
    private Expression<String> group;
    private Expression<String> category;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.id = (Expression<String>) container.getOptional("id", false);
        this.result = (Expression<ItemType>) container.getOptional("result", false);
        this.input = (Expression<RecipeChoice>) container.getOptional("input", false);
        this.material = (Expression<RecipeChoice>) container.getOptional("material", false);
        this.group = (Expression<String>) container.getOptional("group", false);
        this.category = (Expression<String>) container.getOptional("category", false);
        if (id == null || result == null || input == null || material == null) {
            return false;
        }
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
        NamespacedKey key = Util.getNamespacedKey(recipeId, false);
        ItemType result = this.result.getSingle(event);
        RecipeChoice input = this.input.getSingle(event);
        RecipeChoice material = this.material.getSingle(event);

        if (key == null) {
            error("Invalid id: " + recipeId);
            return;
        } else if (result == null || !result.getMaterial().isItem() || result.getMaterial().isAir()) {
            error("Inavlid result: " + result);
            return;
        } else if (input == null) {
            error("Invalid input: " + this.input.toString(event, false));
            return;
        } else if (material == null) {
            error("Invalid material: " + this.material.toString(event, false));
            return;
        }

        TransmuteRecipe recipe = new TransmuteRecipe(key, result.getMaterial(), input, material);

        if (this.group != null) {
            String group = this.group.getSingle(event);
            if (group != null) {
                recipe.setGroup(group);
            }
        }
        if (this.category != null) {
            String catName_not_pet_dont_worry_fuse_winkey_face = this.category.getSingle(event);
            CraftingBookCategory category = RecipeUtil.getCraftingBookCategory(catName_not_pet_dont_worry_fuse_winkey_face);
            if (category != null) {
                recipe.setCategory(category);
            } else {
                error("Invalid category: " + catName_not_pet_dont_worry_fuse_winkey_face);
            }
        }

        // Remove duplicates on script reload
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(recipe);
        RecipeUtil.logTransmuteRecipe(recipe);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "register transmute recipe";
    }

}
