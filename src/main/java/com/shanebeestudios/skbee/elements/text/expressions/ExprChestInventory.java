package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprChestInventory extends SimpleExpression<Inventory> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprChestInventory.class, Inventory.class,
                "[a [new]] chest inventory with component name %textcomponent% [with %-number% row[s]]",
                "[a [new]] chest inventory with %number% row[s] with component name %textcomponent%")
            .name("TextComponent - Chest Inventory")
            .description("Create a chest inventory with a component name.")
            .examples("set {_t} to mini message from \"<rainbow>THIS IS A CHEST\"",
                "set {_i} to chest inventory with component name {_t} with 2 rows",
                "set slot 1 of {_i} to diamond sword",
                "open {_i} to player")
            .since("2.4.0")
            .register();
    }

    private Expression<Number> rows;
    private Expression<ComponentWrapper> name;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        name = (Expression<ComponentWrapper>) exprs[matchedPattern];
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

        ComponentWrapper name = this.name.getSingle(event);
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
