package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
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
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Block Dropped Items")
@Description("Represents the dropped items in a block drop item event.")
@Examples({"on block drop item:",
        "\tteleport block dropped items to player"})
@Since("INSERT VERSION")
public class ExprBlockDrops extends SimpleExpression<Item> {

    static {
        Skript.registerExpression(ExprBlockDrops.class, Item.class, ExpressionType.SIMPLE,
                "block dropped items");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(BlockDropItemEvent.class)) {
            Skript.error("'block dropped items' are only available in a block drop item event.");
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Item[] get(Event event) {
        BlockDropItemEvent dropEvent = (BlockDropItemEvent) event;
        return dropEvent.getItems().toArray(new Item[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Item> getReturnType() {
        return Item.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "block dropped items";
    }

}
