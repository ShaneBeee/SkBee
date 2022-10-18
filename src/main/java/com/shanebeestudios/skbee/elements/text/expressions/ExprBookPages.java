package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.Aliases;
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
import com.shanebeestudios.skbee.api.text.BeeComponent;
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
        "set click event of {_a} to click event to run command \"/rules\"",
        "set page 1 of {_i} to {_a}",
        "set book author of {_i} to \"Bob\"",
        "set book title of {_i} to \"MyBook\"",
        "give player 1 of {_i}"})
@Since("1.8.0")
public class ExprBookPages extends SimpleExpression<BeeComponent> {

    static {
        Skript.registerExpression(ExprBookPages.class, BeeComponent.class, ExpressionType.PROPERTY,
                "page %number% of [book] %itemtype%");
    }

    private final ItemType BOOK = Aliases.javaItemType("book with text");

    private Expression<ItemType> item;
    private Expression<Number> page;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        item = (Expression<ItemType>) exprs[1];
        page = (Expression<Number>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected BeeComponent[] get(@NotNull Event e) {
        ItemType item = this.item.getSingle(e);
        if (item == null) return null;
        Material material = item.getMaterial();
        if (BOOK.isOfType(material)) {
            BookMeta bookMeta = ((BookMeta) item.getItemMeta());
            Number num = this.page.getSingle(e);
            int page = num == null ? 0 : num.intValue();

            if (bookMeta.getPageCount() >= page) {
                return new BeeComponent[]{BeeComponent.fromComponent(bookMeta.page(page))};
            }
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(BeeComponent[].class);
        } else {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        BeeComponent[] beeComponents = delta == null ? null : (BeeComponent[]) delta;
        ItemType book = this.item.getSingle(e);
        if (book == null) return;

        Material bookMaterial = book.getMaterial();

        BeeComponent comp = BeeComponent.fromComponents(beeComponents);

        if (BOOK.isOfType(bookMaterial)) {
            BookMeta bookMeta = ((BookMeta) book.getItemMeta());

            Number pageNumber = this.page.getSingle(e);
            int page = pageNumber != null ? pageNumber.intValue() : 0;
            int pageCount = bookMeta.getPageCount();
            if (pageCount < page) {
                // If no pages exist for this page, we create some
                for (int i = 0; i < page - pageCount; i++) {
                    bookMeta.addPage(" ");
                }
            }
            bookMeta.page(page, comp.getComponent());
            book.setItemMeta(bookMeta);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends BeeComponent> getReturnType() {
        return BeeComponent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "page " + this.page.toString(e, d) + " of book " + this.item.toString(e, d);
    }

}
