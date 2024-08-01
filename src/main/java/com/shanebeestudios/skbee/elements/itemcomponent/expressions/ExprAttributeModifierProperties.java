package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Attribute Modifier Properties")
@Description({"Represents the different components of an attribute modifier.",
    "NOTE: These cannot be changed.",
    "`id` = The unique NamespacedKey of a modifier.",
    "`amount` = Amount of change from the modifier.",
    "`slot` = Equipment Slot Group the item must be in for the modifier to take effect.",
    "`operation` = The operation of a modifier. See [**McWiki**](https://minecraft.wiki/w/Attribute#Operations) for more details."})
@Examples({"set {_id} to modifier id of {_modifier}",
    "set {_slot} to modifier slot of {_modifier}",
    "if modifier amount of {_mod} > 1:"})
@Since("INSERT VERSION")
public class ExprAttributeModifierProperties extends SimplePropertyExpression<AttributeModifier, Object> {

    static {
        register(ExprAttributeModifierProperties.class, Object.class,
            "[attribute] modifier (0:id|1:amount|2:slot|3:operation)", "attributemodifiers");
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Object convert(AttributeModifier modifier) {
        return switch (this.pattern) {
            case 1 -> modifier.getAmount();
            case 2 -> modifier.getSlotGroup();
            case 3 -> modifier.getOperation();
            default -> modifier.getKey();
        };
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "modifier" + switch (this.pattern) {
            case 1 -> "amount";
            case 2 -> "slot";
            case 3 -> "operation";
            default -> "id";
        };
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return switch (this.pattern) {
            case 1 -> Number.class;
            case 2 -> EquipmentSlotGroup.class;
            case 3 -> Operation.class;
            default -> NamespacedKey.class;
        };
    }

}
