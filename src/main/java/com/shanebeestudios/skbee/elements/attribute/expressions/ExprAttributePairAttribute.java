package com.shanebeestudios.skbee.elements.attribute.expressions;

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
import com.shanebeestudios.skbee.api.attribute.AttributePair;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Attribute - Attribute Pair Attribute and Modifier")
@Description("Represents the attribute and attribute modifier of an attribute pair.")
@Examples("")
@Since("INSERT VERSION")
public class ExprAttributePairAttribute extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprAttributePairAttribute.class, Object.class, ExpressionType.PROPERTY,
                "(attribute|1¦attribute modifier) of %attributepairs%");
    }

    private Expression<AttributePair> attributePair;
    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        attributePair = (Expression<AttributePair>) exprs[0];
        pattern = parseResult.mark;
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event event) {
        AttributePair[] pairs = this.attributePair.getArray(event);
        if (pattern == 0) {
            List<Attribute> attributes = new ArrayList<>();
            for (AttributePair pair : pairs) {
                attributes.add(pair.getAttribute());
            }
            return attributes.toArray(new Attribute[0]);
        } else {
            List<AttributeModifier> attributeModifiers = new ArrayList<>();
            for (AttributePair pair : pairs) {
                attributeModifiers.add(pair.getModifier());
            }
            return attributeModifiers.toArray(new AttributeModifier[0]);
        }
    }

    @Override
    public boolean isSingle() {
        return this.attributePair.isSingle();
    }

    @Override
    public Class<?> getReturnType() {
        return pattern == 0 ? Attribute.class : AttributeModifier.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        String type = pattern == 0 ? "attribute" : "attribute modifier";
        return type + " of " + this.attributePair.toString(e, d);
    }

}
