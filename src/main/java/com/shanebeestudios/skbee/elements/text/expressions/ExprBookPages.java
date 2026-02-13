package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprBookPages extends SimpleExpression<ComponentWrapper> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprBookPages.class, ComponentWrapper.class,
                "page %number% of [book] %itemtype%")
            .name("Book - Pages")
            .description("Allows you to set pages in a book to text components. You can also retrieve the pages. " +
                "Based on testing, a book's author/title needs to be set AFTER setting the pages, why? I have no idea!")
            .examples("set {_i} to a written book",
                "set {_a} to text component of \"RULES\"",
                "set hover event of {_a} to hover event showing \"make sure to read our rules\"",
                "set click event of {_a} to click event to run command \"/rules\"",
                "set page 1 of book {_i} to {_a}",
                "set book author of {_i} to \"Bob\"",
                "set book title of {_i} to \"MyBook\"",
                "give player 1 of {_i}")
            .since("1.8.0")
            .register();
    }

    private Expression<ItemType> item;
    private Expression<Number> page;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.item = (Expression<ItemType>) exprs[1];
        this.page = (Expression<Number>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ComponentWrapper @Nullable [] get(@NotNull Event event) {
        ItemType item = this.item.getSingle(event);
        if (item == null) return null;

        if (item.getMaterial() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = ((BookMeta) item.getItemMeta());
            Number num = this.page.getSingle(event);
            int page = num == null ? 0 : num.intValue();

            if (bookMeta.getPageCount() >= page) {
                return new ComponentWrapper[]{ComponentWrapper.fromComponent(bookMeta.page(page))};
            }
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(ComponentWrapper[].class);
        } else {
            return null;
        }
    }

    @SuppressWarnings({"deprecation", "ConstantValue", "NullableProblems"})
    @Override
    public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        ComponentWrapper[] componentWrappers = delta == null ? null : (ComponentWrapper[]) delta;
        ItemType book = this.item.getSingle(e);
        if (book == null) return;

        Material bookMaterial = book.getMaterial();

        ComponentWrapper comp = ComponentWrapper.fromComponents(componentWrappers);

        if (book.getMaterial() == Material.WRITTEN_BOOK) {
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
        return true;
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "page " + this.page.toString(e, d) + " of book " + this.item.toString(e, d);
    }

}
