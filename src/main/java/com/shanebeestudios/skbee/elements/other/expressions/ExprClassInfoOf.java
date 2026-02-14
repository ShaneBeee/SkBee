package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprClassInfoOf extends SimplePropertyExpression<Object, String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprClassInfoOf.class, String.class, "(class[ ]info|skript[ ]type)", "objects")
            .name("Skript Type of Object")
            .description("Returns the Skript type (also known as 'ClassInfo') of an object.",
                "Useful for debugging. Will return as a string.")
            .examples("set {_info} to class info of player's tool")
            .since("2.5.2")
            .register();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        Expression<Object> objectExpression = LiteralUtils.defendExpression(exprs[0]);
        setExpr(objectExpression);
        return LiteralUtils.canInitSafely(objectExpression);
    }

    @Override
    public @Nullable String convert(Object o) {
        ClassInfo<?> superClassInfo = Classes.getSuperClassInfo(o.getClass());
        return superClassInfo.toString();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "class info";
    }

}
