package com.shanebeestudios.skbee.elements.attribute.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.eclipse.jdt.annotation.Nullable;

import java.util.UUID;

@Name("Attribute - Modifier")
@Description({"Create a new attribute modifier. Once created, the options cannot be changed.",
        "UUID is optional, when excluded a random UUID will be used."})
@Examples("set {_mod} to new attribute modifier named \"test\" with amount 15 with operation add number for slot chest")
public class ExprAttributeModifier extends SimpleExpression<AttributeModifier> {

    static {
        Skript.registerExpression(ExprAttributeModifier.class, AttributeModifier.class, ExpressionType.COMBINED,
                "[new] attribute modifier named %string%[(,| with) uuid %-string%](,| with) amount %number%" +
                        "(,| with) operation %attributeoperation%(,| with| for) slot %equipmentslot%");
    }

    private Expression<String> name;
    private Expression<String> uuid;
    private Expression<Number> amount;
    private Expression<Operation> operation;
    private Expression<EquipmentSlot> slot;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.name = (Expression<String>) exprs[0];
        this.uuid = (Expression<String>) exprs[1];
        this.amount = (Expression<Number>) exprs[2];
        this.operation = (Expression<Operation>) exprs[3];
        this.slot = (Expression<EquipmentSlot>) exprs[4];
        return true;
    }

    @Override
    protected @Nullable AttributeModifier[] get(Event event) {
        String name = this.name.getSingle(event);
        UUID uuid;
        if (this.uuid == null || this.uuid.getSingle(event) == null) {
            uuid = UUID.randomUUID();
        } else {
            uuid = UUID.fromString(this.uuid.getSingle(event));
        }
        double amount = 0;
        Number single = this.amount.getSingle(event);
        if (single != null) {
            amount = single.doubleValue();
        }
        Operation operation = this.operation.getSingle(event);
        if (operation == null) {
            operation = Operation.ADD_NUMBER;
        }
        EquipmentSlot slot = this.slot.getSingle(event);

        AttributeModifier attributeModifier = new AttributeModifier(uuid, name, amount, operation, slot);
        return new AttributeModifier[]{attributeModifier};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends AttributeModifier> getReturnType() {
        return AttributeModifier.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        String uuid = this.uuid == null ? "" : "with uuid '" + this.uuid.toString(e, d) + "'";
        return String.format("new attribute modifier named '%s' %s with amount %s with operation %s wiht slot %s",
                this.name.toString(e, d), uuid,
                uuid,
                this.amount.toString(e, d),
                this.operation.toString(e,d),
                this.slot.toString(e, d));
    }

}
