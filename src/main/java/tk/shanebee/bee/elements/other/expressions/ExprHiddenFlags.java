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
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.api.reflection.SkReclection;

import javax.annotation.Nullable;

@Name("Hidden Item Flags")
@Description("Hides the item flags on items, allowing you to make super duper custom items. Dye item flag added in INSERT VERSION and only available on 1.16.2+.")
@Examples({"set player's tool to player's tool with attribute flag hidden", "give player 1 diamond sword of sharpness 5 with hidden enchants flag",
        "set {_tool} to player's tool with all flags hidden", "give player potion of harming with hidden potion effects flag",
        "set {_b} to leather boots with dye flag hidden"})
@Since("1.0.0")
public class ExprHiddenFlags extends SimplePropertyExpression<ItemType, ItemType> {

    private static final boolean HAS_DYE_FLAG = Skript.isRunningMinecraft(1, 16, 2);

    static {
        if (HAS_DYE_FLAG) {
            Skript.registerExpression(ExprHiddenFlags.class, ItemType.class, ExpressionType.PROPERTY,
                    "%itemtype% with (0¦all|1¦enchant[s]|2¦destroy[s]|3¦potion[ ]effect[s]|4¦unbreakable|5¦attribute[s]|6¦dye) flag[s] hidden",
                    "%itemtype% with hidden (0¦all|1¦enchant[s]|2¦destroy[s]|3¦potion[ ]effect[s]|4¦unbreakable|5¦attribute[s]|6¦dye) flag[s]");
        } else {
            Skript.registerExpression(ExprHiddenFlags.class, ItemType.class, ExpressionType.PROPERTY,
                    "%itemtype% with (0¦all|1¦enchant[s]|2¦destroy[s]|3¦potion[ ]effect[s]|4¦unbreakable|5¦attribute[s]) flag[s] hidden",
                    "%itemtype% with hidden (0¦all|1¦enchant[s]|2¦destroy[s]|3¦potion[ ]effect[s]|4¦unbreakable|5¦attribute[s]) flag[s]");
        }
    }

    @SuppressWarnings("null")
    private int parse = 0;

    @SuppressWarnings({"unchecked","null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<ItemType>) exprs[0]);
        parse = parseResult.mark;
        return true;
    }

    @Override
    @Nullable
    public ItemType convert(@NotNull ItemType item) {
        if (item == null) return null;

        ItemMeta meta = SkReclection.getMeta(item);
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getMaterial());
        }
        switch (parse) {
            case 0:
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                if (HAS_DYE_FLAG)
                    meta.addItemFlags(ItemFlag.HIDE_DYE);
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
                break;
            case 6:
                meta.addItemFlags(ItemFlag.HIDE_DYE);
                break;
        }

        SkReclection.setMeta(item, meta);
        return item;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "Hidden Item Flags";
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String flag = null;
        switch (parse) {
            case 0:
                flag = "all";
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
                flag = "attribute";
                break;
            case 6:
                flag = "dye";
        }
        return getExpr().toString(e, d) + " with " + flag + " flags hidden";
    }

}
