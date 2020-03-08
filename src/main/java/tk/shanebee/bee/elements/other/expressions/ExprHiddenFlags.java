package tk.shanebee.bee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import tk.shanebee.bee.api.Reflection;

import javax.annotation.Nullable;

@Name("Hidden Item Flags")
@Description("Hides the item flags on items, allowing you to make super duper custom items.")
@Examples({"set player's tool to player's tool with attribute flag hidden", "give player 1 diamond sword of sharpness 5 with hidden enchants flag",
        "set {_tool} to player's tool with all flags hidden", "give player potion of harming with hidden potion effects flag"})
@Since("1.0.0")
public class ExprHiddenFlags extends SimplePropertyExpression<ItemType, ItemType> {

    static {
        Skript.registerExpression(ExprHiddenFlags.class, ItemType.class, ExpressionType.PROPERTY,
                "%itemtype% with (0¦attribute[s]|1¦enchant[s]|2¦destroy[s]|3¦potion[ ]effect[s]|4¦unbreakable|5¦all) flag[s] hidden",
                "%itemtype% with hidden (0¦attribute[s]|1¦enchant[s]|2¦destroy[s]|3¦potion[ ]effect[s]|4¦unbreakable|5¦all) flag[s]");
    }

    @SuppressWarnings("null")
    private int parse = 5;

    @SuppressWarnings({"unchecked","null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<ItemType>) exprs[0]);
        parse = parseResult.mark;
        return true;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected String getPropertyName() {
        return "Hidden Item Flags";
    }

    @Override
    @Nullable
    public ItemType convert(ItemType item) {
        if (item == null) return null;

        ItemMeta meta = Reflection.getMeta(item);
        switch (parse) {
            case 0:
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                break;
            case 1:
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                break;
            case 2:
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                break;
            case 3:
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                break;
            case 4:
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                break;
            case 5:
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                break;
        }

        Reflection.setMeta(item, meta);
        return item;
    }

    @Override
    public String toString(Event e, boolean d) {
        String flag = null;
        switch (parse) {
            case 0:
                flag = "attribute";
                break;
            case 1:
                flag = "enchant";
                break;
            case 2:
                flag = "destroy";
                break;
            case 3:
                flag = "potion effect";
                break;
            case 4:
                flag = "unbreakable";
                break;
            case 5:
                flag = "all";
                break;
        }
        return getExpr().toString(e, d) + " with " + flag + " flags hidden";
    }

}
