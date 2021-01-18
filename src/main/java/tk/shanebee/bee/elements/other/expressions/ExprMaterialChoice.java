package tk.shanebee.bee.elements.other.expressions;

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
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Material Choice")
@Description({"A material choice is a list of items or a minecraft tag, that can be used as an option in some recipes.",
        "When using the 'every' item type, this will grab all relatable items in a list, ie: 'every sword'.",
        "This allows you to have one specific slot of a recipe to accept multiple items, without having to create multiple recipes.",
        "Do note that material choices do not accept custom items (ie: items with names, lore, enchants, etc). Requires Minecraft 1.13+"})
@Examples({"set {_a} to material choice of diamond sword, diamond shovel and diamond hoe",
        "set {_a} to material choice of every sword",
        "set {_a} to material choice of minecraft tag \"doors\""})
@Since("1.10.0")
public class ExprMaterialChoice extends SimpleExpression<MaterialChoice> {

    static {
        if (Skript.isRunningMinecraft(1, 13)) {
            Skript.registerExpression(ExprMaterialChoice.class, MaterialChoice.class, ExpressionType.COMBINED,
                    "material choice of %itemtypes%",
                    "material choice of %minecrafttag%");
        }
    }

    private int pattern;
    private Expression<ItemType> itemTypes;
    private Expression<Tag> tags;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        if (pattern == 0) {
            itemTypes = (Expression<ItemType>) exprs[0];
        } else if (pattern == 1) {
            tags = (Expression<Tag>) exprs[0];
        }
        return true;
    }

    @Nullable
    @Override
    protected MaterialChoice[] get(Event event) {
        if (pattern == 0) {
            List<Material> materials = new ArrayList<>();
            for (ItemType type : itemTypes.getArray(event)) {
                type.getAll().forEach(itemStack -> {
                    Material material = itemStack.getType();
                    if (!materials.contains(material)) {
                        materials.add(material);
                    }
                });
            }
            return new MaterialChoice[]{new MaterialChoice(materials)};
        } else if (pattern == 1) {
            Tag<Material> tag = tags.getSingle(event);
            if (tag != null) {
                return new MaterialChoice[]{new MaterialChoice(tag)};
            }
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MaterialChoice> getReturnType() {
        return MaterialChoice.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return String.format("material choice of %s",
                pattern == 0 ? itemTypes.toString(e, d) : tags.toString(e, d));
    }

}
