package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("unused")
// This is temporary until Skript can fix getting the name of an inventory
@Name("Inventory Name")
@Description("This is a temp placeholder for Skript's inventory name expression, which is broken in 1.14+")
@Examples("if inventory name of current inventory of player = \"Settings\":")
@Since("1.0.0")
public class ExprInvName extends SimplePropertyExpression<Object, String> {

    static {
        register(ExprInvName.class, String.class, "(custom|inv[entory]) name", "inventories");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr(exprs[0]);
        return true;
    }

    @Override
    protected String getPropertyName() {
        return "inventory name";
    }

    @Override
    public String convert(Object o) {
        if (o instanceof Inventory) {
            if (!((Inventory) o).getViewers().isEmpty())
                return ((Inventory) o).getViewers().get(0).getOpenInventory().getTitle();
            else return null;
        } else
            return null;
    }


    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "Inventory name of " + getExpr().toString(e, d);
    }

}
