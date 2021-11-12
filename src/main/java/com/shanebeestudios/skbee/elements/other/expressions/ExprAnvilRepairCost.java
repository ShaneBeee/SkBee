package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

@Name("Anvil Repair Cost")
@Description({"Represents the anvil inventory's repair cost and maximum repair cost.",
        "Repair cost = the experience cost (in levels) to complete the current repair.",
        "Maximum repair cost = the maximum experience cost (in levels) to be allowed by the current repair.",
        "If the result of 'repair cost' exceeds the returned value, the repair result will be air to due being \"too expensive\".",
        "By default, this level is set to 40. Players in creative mode ignore the maximum repair cost.",
        "NOTE: the 'max repair cost' seems to be a little finicky, it may or may not work as intended (this is out of my control)."})
@Examples({"on anvil prepare:",
        "\tif slot 0 of event-inventory is a diamond sword:",
        "\t\tif slot 1 of event-inventory is an enchanted book:",
        "\t\t\tif stored enchants of slot 1 of event-inventory contains sharpness 5:",
        "\t\t\t\tset {_i} to slot 0 of event-inventory",
        "\t\t\t\tadd \"&aOOOOOOO\" and \"&bAHHHHHH\" to lore of {_i}",
        "\t\t\t\tenchant {_i} with sharpness 6",
        "\t\t\t\tset event-slot to {_i}",
        "\t\t\t\tset repair cost of event-inventory to 30"})
@Since("1.11.0")
public class ExprAnvilRepairCost extends SimplePropertyExpression<Inventory, Number> {

    static {
        register(ExprAnvilRepairCost.class, Number.class, "[anvil] (0¦repair cost|1¦max[imum] repair cost)", "inventories");
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        setExpr((Expression<? extends Inventory>) exprs[0]);
        return true;
    }

    @Nullable
    @Override
    public Number convert(Inventory inv) {
        if (!(inv instanceof AnvilInventory)) return null;
        return pattern == 0 ? ((AnvilInventory) inv).getRepairCost() : ((AnvilInventory) inv).getMaximumRepairCost();
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        switch (mode) {
            case SET:
            case ADD:
            case REMOVE:
                return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
        Number number = (Number) delta[0];
        if (number == null) return;

        int cost = number.intValue();
        Inventory inv = getExpr().getSingle(e);
        if (!(inv instanceof AnvilInventory)) return;

        AnvilInventory anvilInv = (AnvilInventory) inv;

        if (pattern == 0) {
            switch (mode) {
                case SET:
                    anvilInv.setRepairCost(cost);
                    break;
                case ADD:
                    anvilInv.setRepairCost(anvilInv.getRepairCost() + cost);
                    break;
                case REMOVE:
                    anvilInv.setRepairCost(Math.max(anvilInv.getRepairCost() - cost, 0));
            }
        } else {
            switch (mode) {
                case SET:
                    anvilInv.setMaximumRepairCost(cost);
                    break;
                case ADD:
                    anvilInv.setMaximumRepairCost(anvilInv.getMaximumRepairCost() + cost);
                    break;
                case REMOVE:
                    anvilInv.setMaximumRepairCost(Math.max(anvilInv.getMaximumRepairCost() - cost, 0));
            }
        }
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        String cost = pattern == 0 ? "repair cost" : "max repair cost";
        return String.format("anvil inventory %s", cost);
    }

}
