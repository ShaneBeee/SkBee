package com.shanebeestudios.skbee.elements.attribute.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.eclipse.jdt.annotation.Nullable;

@Name("Attribute - Attribute Modifier Values")
@Description("Represents different values of an attribute modifier. These can not be changed.")
@Examples({"set {_name} to attribute name of {_modifier}",
        "if attribute name of {_modifier} = \"Sword Damage\":",
        "\tif attribute amount of {_modifier} > 5:",
        "\t\tkill player"})
@Since("INSERT VERSION")
public class ExprAttributeModifierOptions extends SimplePropertyExpression<AttributeModifier, Object> {

    static {
        register(ExprAttributeModifierOptions.class, Object.class,
                "attribute (uuid|1¦name|2¦amount|3¦operation|4¦[equipment] slot)", "attributemodifiers");
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends AttributeModifier>) exprs[0]);
        this.pattern = parseResult.mark;
        return true;
    }

    @Override
    public @Nullable Object convert(AttributeModifier attributeModifier) {
        switch (pattern) {
            case 1:
                return attributeModifier.getName();
            case 2:
                return attributeModifier.getAmount();
            case 3:
                return attributeModifier.getOperation();
            case 4:
                return attributeModifier.getSlot();
            default:
                return attributeModifier.getUniqueId().toString();
        }
    }

    @Override
    public Class<?> getReturnType() {
        switch (pattern) {
            case 2:
                return Number.class;
            case 3:
                return Operation.class;
            case 4:
                return EquipmentSlot.class;
            default:
                return String.class;
        }
    }

    @Override
    protected String getPropertyName() {
        switch (pattern) {
            case 1:
                return "attribute name";
            case 2:
                return "attribute amount";
            case 3:
                return "attribute operation";
            case 4:
                return "attribute hand";
            default:
                return "attribute uuid";
        }
    }

}
