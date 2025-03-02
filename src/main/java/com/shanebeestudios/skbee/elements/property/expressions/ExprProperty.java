package com.shanebeestudios.skbee.elements.property.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.property.Property;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Property - Property of Object")
@Description({"Represents different properties of an object.",
    "See [**Property Wiki**](https://github.com/ShaneBeee/SkBee/wiki/Properties) for available properties and examples."})
@Examples("")
@Since("INSERT VERSION")
@SuppressWarnings("unchecked")
public class ExprProperty<F, T> extends SimpleExpression<T> {

    static {
        Skript.registerExpression(ExprProperty.class, Object.class, ExpressionType.COMBINED,
            "%*property% property of %objects%",
            "%objects%'[s] %*property% property");
    }

    private Literal<Property<F, T>> property;
    private Expression<F> objects;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.property = (Literal<Property<F, T>>) exprs[matchedPattern];
        this.objects = LiteralUtils.defendExpression( exprs[matchedPattern == 0 ? 1 : 0]);
        if (this.objects.getReturnType().isAssignableFrom(this.property.getSingle().getFromType())) return true;
        if (this.property.getSingle().getFromType().isAssignableFrom(this.objects.getReturnType())) return true;
        if (Converters.converterExists(this.objects.getReturnType(), this.property.getSingle().getFromType()))
            return true;

        ClassInfo<F> propertyInfo = Classes.getExactClassInfo(this.property.getSingle().getFromType());
        ClassInfo<? extends F> found = Classes.getExactClassInfo(this.objects.getReturnType());

        Skript.error("Property '" + this.property.getSingle().getName() + "' can only be used on '" + propertyInfo + "' but found '" + found + "'");
        return false;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected T @Nullable [] get(Event event) {
        List<T> objects = new ArrayList<>();
        Property<F, T> property = this.property.getSingle();
        if (property == null) return null;

        for (F from : this.objects.getArray(event)) {
            if (!property.getFromType().isAssignableFrom(from.getClass())) {
                // If the object types don't match, let's try convert
                from = Converters.convert(from, property.getFromType());
                if (from != null && !property.getFromType().isAssignableFrom(from.getClass())) continue;
            }
            T propertyReturn = property.get(from);
            if (property.isArray()) {
                objects.addAll(Arrays.asList((T[]) propertyReturn));
            } else {
                objects.add(propertyReturn);
            }
        }
        return (T[]) objects.toArray(new Object[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (this.property == null || this.property.getSingle() == null) return null;
        return this.property.getSingle().acceptChange(mode);
    }

    @SuppressWarnings({"ConstantValue", "unchecked"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Property<F, T> property = this.property.getSingle();
        Object change = delta != null ? delta[0] : null;
        if (property == null) return;

        for (F object : this.objects.getArray(event)) {
            if (!property.getFromType().isAssignableFrom(object.getClass())) {
                // If the object types don't match, let's try convert
                object = Converters.convert(object, property.getFromType());
                if (object != null && !property.getFromType().isAssignableFrom(object.getClass())) continue;
            }
            switch (mode) {
                case SET -> property.set(object, property.isArray() ? (T) delta : (T) change);
                case ADD -> property.add(object, property.isArray() ? (T) delta : (T) change);
                case REMOVE -> property.remove(object, property.isArray() ? (T) delta : (T) change);
                case DELETE -> property.delete(object);
                case RESET -> property.reset(object);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.objects.isSingle() && !this.property.getSingle().getReturnType().isArray();
    }

    @Override
    public @NotNull Class<? extends T> getReturnType() {
        return this.property.getSingle().getReturnType();
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return this.property.toString(e, d) + " property of " + this.objects.toString(e, d);
    }

}
