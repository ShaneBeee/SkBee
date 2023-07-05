package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

public class ExprTest extends SimpleExpression<ItemStack> {

    static {
        Skript.registerExpression(ExprTest.class, ItemStack.class, ExpressionType.SIMPLE,
                "bloop [of|from] %entity%");
    }

    private Expression<Entity> entity;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.entity = (Expression<Entity>) exprs[0];
        return true;
    }

    @Override
    protected @Nullable ItemStack[] get(Event event) {
        Entity single = this.entity.getSingle(event);
        if (single == null) Util.log("Single is null: " + this.entity);
        if (single instanceof Item item) {
            return new ItemStack[]{item.getItemStack()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "null";
    }


}
