package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.localization.Noun;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.property.Property;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Object Property")
@Description({"Represents properties of objects. These are simplified expressions.",
        "See Object Property Type for available properties."})
@Examples({"set {_loc} to hive location property of last spawned bee",
        "set {_arms} to arms property of last spawned armor stand",
        "if arms property of {_armorStand} = true:",
        "set arms property of {_armorStand} to true",
        "set hive location property of all bees to player",
        "set marker property of target entity to true"})
@Since("INSERT VERSION")
public class ExprObjectProperty extends SimplePropertyExpression<Object, Object> {

    static {
        register(ExprObjectProperty.class, Object.class, "%property% property", "objects");
    }

    private Literal<Property<?, ?>> property;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!(exprs[0] instanceof Literal<?>)) {
            Skript.error("Variables cannot be used for %property%.");
            return false;
        }
        this.property = (Literal<Property<?, ?>>) exprs[0];
        setExpr(exprs[1]);

        // If the object is not a variable, make sure it can be used with the property
        if (!(exprs[1] instanceof Variable<?>)) {
            Property<?, ?> property = this.property.getSingle();
            Class<?> objectClass = getExpr().getReturnType();
            Noun objectClassName = Classes.getSuperClassInfo(objectClass).getName();
            if (!property.canBeUsedOn(objectClass)) {
                String usedOn = property.getUsedOn();
                Skript.error("Property '" + property.getPropertyName() + "' cannot be used on '" +
                        objectClassName + "', may only be used on '" + usedOn + "'");
                return false;
            }
        }
        return true;
    }

    @Override
    public @Nullable Object convert(Object object) {
        return this.property.getSingle().get(object);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return this.property.getSingle().acceptChange(mode);
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Property<?, ?> property = this.property.getSingle();
        if (delta != null) {
            for (Object object : getExpr().getArray(event)) {
                property.change(mode, object, delta[0]);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.property.getSingle().isSingle();
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return this.property.getSingle().getReturnType();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return this.property.getSingle().getPropertyName() + " property";
    }

}
