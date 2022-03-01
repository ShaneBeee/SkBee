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

@Name("Attribute - Pair")
@Description({"This represents an object that holds both an attribute type and attribute modifier.",
        "This can be used to add modifiers to items. While the modifier is an option, this needs to be present when adding to an item.",
        "The option to not have the modifier is used to remove attributes from an item. Note: Default attributes cant be removed."})
@Examples({"set {_pair} to new attribute using generic armor and {_mod}",
        "add {_pair} to attributes of player's tool"})
@Since("INSERT VERSION")
public class ExprAttributePair extends SimpleExpression<AttributePair> {

    static {
        Skript.registerExpression(ExprAttributePair.class, AttributePair.class, ExpressionType.COMBINED,
                "[new] attribute pair using %attributetype% [and %-attributemodifier%]");
    }

    private Expression<Attribute> attribute;
    private Expression<AttributeModifier> attributeModifier;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.attribute = (Expression<Attribute>) exprs[0];
        this.attributeModifier = (Expression<AttributeModifier>) exprs[1];
        return true;
    }

    @Override
    protected @Nullable AttributePair[] get(Event event) {
        Attribute attribute = this.attribute.getSingle(event);
        AttributeModifier attributeModifier = this.attributeModifier != null ? this.attributeModifier.getSingle(event) : null;
        if (attribute == null) return null;

        return new AttributePair[]{new AttributePair(attribute, attributeModifier)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends AttributePair> getReturnType() {
        return AttributePair.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        String mod = this.attributeModifier != null ? " and modifier " + this.attributeModifier.toString(e, d) : "";
        return "new attribute pair using " + this.attribute.toString(e, d) + mod;
    }

}
