package com.shanebeestudios.skbee.elements.damagesource.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.damage.DamageSource;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DamageSource - Event Value")
@Description("Get the damage source of a damage event.")
@Examples({"on damage of player:",
        "\tif damage type of damage source = arrow:",
        "\t\tbroadcast \"OUCHIE\"",
        "\tif causing entity of damage source is a chicken:",
        "\t\tbroadcast \"YOU JERK\""})
@Since("3.3.0")
public class ExprDamageSourceEvent extends SimpleExpression<DamageSource> {

    static {
        Skript.registerExpression(ExprDamageSourceEvent.class, DamageSource.class, ExpressionType.SIMPLE,
                "damage source");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return ParserInstance.get().isCurrentEvent(EntityDamageEvent.class);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected DamageSource @Nullable [] get(Event event) {
        if (event instanceof EntityDamageEvent entityDamageEvent) {
            return new DamageSource[]{entityDamageEvent.getDamageSource()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends DamageSource> getReturnType() {
        return DamageSource.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "damage source";
    }

}
