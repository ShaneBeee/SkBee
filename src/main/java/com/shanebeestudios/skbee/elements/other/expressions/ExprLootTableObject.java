package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
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
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("LootTable - Lootables")
@Description({"Get/set/delete the LootTable of a lootable object such as a block or entity.",
        "`with seed` = Provide an optional seed for loot generation otherwise will randomly generate."})
@Examples({"set {_lootTable} to loottable of target block",
        "set loottable of target block to loottable from key \"minecraft:chests/ancient_city\""})
@Since("3.4.0")
public class ExprLootTableObject extends SimpleExpression<LootTable> {

    static {
        Skript.registerExpression(ExprLootTableObject.class, LootTable.class, ExpressionType.COMBINED,
                "loot[ ]table of %blocks/entities% [with seed %-number%]");
    }

    private Expression<?> objects;
    private Expression<Number> seed;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objects = exprs[0];
        this.seed = (Expression<Number>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable LootTable[] get(Event event) {
        List<LootTable> tables = new ArrayList<>();

        for (Object object : this.objects.getArray(event)) {
            if (object instanceof Lootable lootable) tables.add(lootable.getLootTable());
            else if (object instanceof Block block && block.getState() instanceof Lootable lootable)
                tables.add(lootable.getLootTable());
        }

        return tables.toArray(new LootTable[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(LootTable.class);
        else if (mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        LootTable lootTable = (delta != null && delta[0] instanceof LootTable table) ? table : null;
        Number seed = this.seed != null ? this.seed.getSingle(event) : null;

        for (Object object : this.objects.getArray(event)) {
            if (object instanceof Lootable lootable) {
                setLootTable(lootable, lootTable, seed);
            } else if (object instanceof Block block) {
                BlockState state = block.getState();
                if (state instanceof Lootable lootable) {
                    setLootTable(lootable, lootTable, seed);
                    state.update(true);
                }
            }
        }
    }

    private static void setLootTable(@NotNull Lootable lootable, @Nullable LootTable lootTable, @Nullable Number seed) {
        if (lootTable == null) lootable.clearLootTable();
        else if (seed == null) lootable.setLootTable(lootTable);
        else lootable.setLootTable(lootTable, seed.longValue());
    }

    @Override
    public boolean isSingle() {
        return this.objects.isSingle();
    }

    @Override
    public @NotNull Class<? extends LootTable> getReturnType() {
        return LootTable.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String seed = this.seed != null ? (" with seed " + this.seed.toString(e, d)) : "";
        return "loot table of " + this.objects.toString(e, d) + seed;
    }

}
