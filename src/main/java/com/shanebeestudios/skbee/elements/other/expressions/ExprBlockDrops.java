package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Block Dropped Items")
@Description({"Represents the dropped items in a block drop item event.",
        "`block dropped items` = The dropped item entities.",
        "`block dropped itemtypes` = The dropped item."})
@Examples({"on block drop item:",
        "\tteleport block dropped items to player"})
@Since("2.6.0")
public class ExprBlockDrops extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBlockDrops.class, Object.class, ExpressionType.SIMPLE,
                "block dropped (items|item entities)",
                "block dropped itemtypes");
    }

    private boolean itemType;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(BlockDropItemEvent.class)) {
            Skript.error("'block dropped items' are only available in a block drop item event.");
            return false;
        }
        this.itemType = matchedPattern == 1;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Object[] get(Event event) {
        BlockDropItemEvent dropEvent = (BlockDropItemEvent) event;
        List<Item> items = dropEvent.getItems();
        if (this.itemType) {
            List<ItemType> itemTypes = new ArrayList<>();
            items.forEach(item -> itemTypes.add(new ItemType(item.getItemStack())));
            return itemTypes.toArray(new ItemType[0]);
        }
        return items.toArray(new Item[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return this.itemType ? ItemType.class : Item.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "block dropped items";
    }

}
