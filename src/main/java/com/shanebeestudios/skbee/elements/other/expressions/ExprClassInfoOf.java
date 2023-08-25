package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Skript Type of Object")
@Description({"Returns the Skript type (also known as 'ClassInfo') of an object.",
        "Useful for debugging. Will return as a string."})
@Examples("set {_info} to class info of player's tool")
@Since("2.5.2")
public class ExprClassInfoOf extends SimplePropertyExpression<Object, String> {

    static {
        register(ExprClassInfoOf.class, String.class, "(class info|skript type)", "objects");
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
