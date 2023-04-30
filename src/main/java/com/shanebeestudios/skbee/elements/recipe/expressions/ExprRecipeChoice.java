package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Recipe - RecipeChoice")
@Description({"Gets a material choice or exact choice using a set of items or singular item",
        ""})
@Examples({
        "set {_materialChoice} to material choice using every weapon",
        "set {_exactChoice} to exact choice using {_item1}, {_item2}, {_item3}",
        "",
        "set {_materialChoice} to material choice using {_materialChoice}, all armor and turtle helmet",
        "",
})
@Since("INSERT VERSION")
public class ExprRecipeChoice extends SimpleExpression<RecipeChoice> {

    private static final boolean MINECRAFT_TAGS_ENABLED = SkBee.getPlugin().getPluginConfig().ELEMENTS_MINECRAFT_TAG;

    private Expression<Object> choices;
    private boolean exactRecipe;

    static {
        Skript.registerExpression(ExprRecipeChoice.class, RecipeChoice.class, ExpressionType.SIMPLE,
                "exact [recipe[ ]]choice (of|using) %itemtypes/recipechoices%",
                "material [recipe[ ]]choice (of|using) %itemtypes/recipechoices" + (MINECRAFT_TAGS_ENABLED ? "/minecrafttags" : "") + "%");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        exactRecipe = matchedPattern == 0;
        choices = (Expression<Object>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected RecipeChoice[] get(Event event) {
        if (choices == null) return null;
        Object[] objects = this.choices.getArray(event);

        if (exactRecipe) {
            List<ItemStack> itemStacks = new ArrayList<>();
            for (Object object : objects) {
                if (object instanceof ItemStack itemStack) {
                    itemStacks.add(itemStack);
                } else if (object instanceof ItemType itemType) {
                    itemStacks.add(itemType.getRandom());
                } else if (object instanceof ExactChoice exactChoice) {
                    itemStacks.addAll(exactChoice.getChoices());
                }
            }
            if (itemStacks.size() == 0) return new ExactChoice[0];
            return new RecipeChoice[]{new ExactChoice(itemStacks)};
        } else {
            List<Material> materials = new ArrayList<>();
            for (Object object : objects) {
                if (object instanceof ItemStack itemStack) {
                    materials.add(itemStack.getType());
                } else if (object instanceof ItemType itemType) {
                    itemType.getAll().forEach(itemStack -> {
                        if (!materials.contains(itemStack.getType()))
                            materials.add(itemStack.getType());
                    });
                } else if (object instanceof Tag<?> tag) {
                    Tag<Material> materialTag = (Tag<Material>) tag;
                    materialTag.getValues().stream()
                            .filter(material -> !materials.contains(material))
                            .forEach(materials::add);
                } else if (object instanceof MaterialChoice materialChoice) {
                    materialChoice.getChoices().stream()
                            .filter(material -> !materials.contains(material))
                            .forEach(materials::add);
                }
            }
            if (materials.size() == 0) return new MaterialChoice[0];
            return new RecipeChoice[]{new MaterialChoice(materials)};
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends RecipeChoice> getReturnType() {
        return RecipeChoice.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return (exactRecipe ? "exact" : "material") + " choice using " + choices.toString(event, debug);
    }

}
