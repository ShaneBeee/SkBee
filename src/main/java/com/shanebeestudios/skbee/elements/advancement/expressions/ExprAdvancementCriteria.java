package com.shanebeestudios.skbee.elements.advancement.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExprAdvancementCriteria extends SimpleExpression<String> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprAdvancementCriteria.class, String.class,
                "criteria of %advancements%",
                "%advancement%'[s] criteria")
            .name("Advancement - Criteria")
            .description("Get a list of the criteria for an advancement.")
            .examples("set {_c::*} to criteria of {_advancement}")
            .since("1.17.0")
            .register();
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
