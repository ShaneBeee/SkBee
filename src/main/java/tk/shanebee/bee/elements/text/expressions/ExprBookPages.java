package tk.shanebee.bee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("Book Pages")
@Description("Allows you to set pages in a book to text components. You can also retrieve the pages. " +
        "Based on testing, a book's author/title needs to be set AFTER setting the pages, why? I have no idea!")
@Examples({"set {_i} to a written book",
        "set {_a} to text component of \"RULES\"",
        "set hover event of {_a} to hover event showing \"make sure to read our rules\"",
        "set click event of {_a} to click event run command \"/rules\"",
        "set page 1 of {_i} to {_a}",
        "set book author of {_i} to \"Bob\"",
        "set book title of {_i} to \"MyBook\"",
        "give player 1 of {_i}"})
@Since("INSERT VERSION")
public class ExprBookPages extends SimpleExpression<BaseComponent> {

    static {
        Skript.registerExpression(ExprBookPages.class, BaseComponent.class, ExpressionType.PROPERTY,
                "page %number% of [book] %itemtype%");
    }

    private Expression<ItemType> item;
    private Expression<Number> page;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        item = (Expression<ItemType>) exprs[1];
        page = (Expression<Number>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected BaseComponent[] get(@NotNull Event e) {
        ItemType item = this.item.getSingle(e);
        if (item == null) return null;
        Material material = item.getMaterial();
        if (material == Material.WRITABLE_BOOK || material == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = ((BookMeta) item.getItemMeta());
            Number num = this.page.getSingle(e);
            int page = num == null ? 0 : num.intValue();

            if (bookMeta.getPages().size() >= page) {
                return bookMeta.spigot().getPage(page);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(BaseComponent[].class);
        } else {
            return null;
        }
    }

    @Override
    public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        BaseComponent[] baseComponents = delta == null ? null : (BaseComponent[]) delta;
        ItemType book = this.item.getSingle(e);
        if (book == null) return;

        Material bookMaterial = book.getMaterial();
        if (bookMaterial == Material.WRITABLE_BOOK || bookMaterial == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = ((BookMeta) book.getItemMeta());

            Number page = this.page.getSingle(e);
            int p = page != null ? page.intValue() : 0;
            if (bookMeta.spigot().getPages().size() < p) {
                bookMeta.spigot().addPage(baseComponents);
            } else {
                bookMeta.spigot().setPage(p, baseComponents);
            }
            book.setItemMeta(bookMeta);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends BaseComponent> getReturnType() {
        return BaseComponent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "page " + this.page.toString(e, d) + " of book " + this.item.toString(e, d);
    }

}
