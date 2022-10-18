package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.text.BeeComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Text Component - Chest Inventory")
@Description("Create a chest inventory with a component name.")
@Examples({"set {_t} to mini message from \"<rainbow>THIS IS A CHEST\"",
        "set {_i} to chest inventory with component name {_t} with 2 rows",
        "set slot 1 of {_i} to diamond sword",
        "open {_i} to player"})
@Since("INSERT VERSION")
public class ExprChestInventory extends SimpleExpression<Inventory> {

    static {
        Skript.registerExpression(ExprChestInventory.class, Inventory.class, ExpressionType.COMBINED,
                "[a [new]] chest inventory with component name %textcomponent% [with %-number% row[s]]",
                "[a [new]] chest inventory with %number% row[s] with component name %textcomponent%");
    }

    private Expression<Number> rows;
    private Expression<BeeComponent> name;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        name = (Expression<BeeComponent>) exprs[matchedPattern];
        rows = (Expression<Number>) exprs[matchedPattern ^ 1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Inventory[] get(Event event) {
        int defaultRows = InventoryType.CHEST.getDefaultSize() / 9;
        Number rows = this.rows != null ? this.rows.getSingle(event) : defaultRows;
        rows = rows == null ? defaultRows : rows;
        int size = rows.intValue() * 9;
        if (size % 9 != 0) {
            size = 27;
        }
        if (size < 0) size = 0;
        if (size > 54) size = 54;

        BeeComponent name = this.name.getSingle(event);
        if (name == null) return null;
        return new Inventory[]{Bukkit.createInventory(null, size, name.getComponent())};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Inventory> getReturnType() {
        return Inventory.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String rows = this.rows != null ? " with " + this.rows.toString(e, d) + " rows" : "";
        String name = " with component name " + this.name.toString(e, d);
        return "chest inventory" + rows + name;
    }

}
