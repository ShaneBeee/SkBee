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
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Recipe Choice - Material Choice")
@Description({"A material choice is a list of items or a minecraft tag, that can be used as an option in some recipes.",
        "Will return as a RecipeChoice.",
        "When using the 'every' item type, this will grab all relatable items in a list, ie: 'every sword'.",
        "This allows you to have one specific slot of a recipe to accept multiple items, without having to create multiple recipes.",
        "Do note that material choices do not accept custom items (ie: items with names, lore, enchants, etc). Requires Minecraft 1.13+"})
@Examples({"set {_a} to material choice of diamond sword, diamond shovel and diamond hoe",
        "set {_choice} to material choice of every sword",
        "set {_choice} to material choice of every sword and every axe",
        "set {_choice} to material choice of minecraft tag \"minecraft:planks\""})
@Since("1.10.0")
public class ExprMaterialChoice extends SimpleExpression<MaterialChoice> {

    private static final boolean HAS_TAGS = SkBee.getPlugin().getPluginConfig().ELEMENTS_MINECRAFT_TAG;

    static {
        Skript.registerExpression(ExprMaterialChoice.class, MaterialChoice.class, ExpressionType.COMBINED,
                HAS_TAGS ? "material choice of %itemtypes/minecrafttags%" : "material choice of %itemtypes%");
    }

    private Expression<?> objects;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objects = exprs[0];
        return true;
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    protected MaterialChoice @Nullable [] get(Event event) {
        List<Material> materials = new ArrayList<>();
        for (Object object : this.objects.getArray(event)) {
            if (object instanceof ItemType itemType) {
                itemType.getAll().forEach(itemStack -> {
                    Material material = itemStack.getType();
                    if (!materials.contains(material)) materials.add(material);
                });
            } else if (HAS_TAGS && RecipeUtil.isMaterialTag(object)) {
                MaterialChoice materialChoice = new MaterialChoice((Tag<Material>) object);
                materialChoice.getChoices().forEach(material -> {
                    if (!materials.contains(material)) materials.add(material);
                });
            }
        }
        if (!materials.isEmpty()) {
            return new MaterialChoice[]{new MaterialChoice(materials)};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<? extends MaterialChoice> getReturnType() {
        return MaterialChoice.class;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "material choice of " + this.objects.toString(e, d);
    }

}
