package com.shanebeestudios.skbee.elements.advancement.expressions;

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
import org.bukkit.advancement.Advancement;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Advancement - Criteria")
@Description("Get a list of the criteria for an advancement.")
@Examples("set {_c::*} to criteria of {_advancement}")
@Since("INSERT VERSION")
public class ExprAdvancementCriteria extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprAdvancementCriteria.class, String.class, ExpressionType.SIMPLE,
                "criteria of %advancements%",
                "%advancement%'[s] criteria");
    }

    private Expression<Advancement> advancement;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.advancement = (Expression<Advancement>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable String[] get(Event event) {
        List<String> criteria = new ArrayList<>();
        for (Advancement advancement : this.advancement.getArray(event)) {
            criteria.addAll(advancement.getCriteria());
        }
        return criteria.toArray(new String[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "criteria of " + this.advancement.toString(e, d);
    }

}
