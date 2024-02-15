package com.shanebeestudios.skbee.elements.damagesource.expressions;

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
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DamageSource - Create")
@Description({"Create a new damage source which includes a damage type and some optional values.",
        "Optional Values:",
        "`caused by %entity%` = The entity that caused the damage.",
        "`directly by %entity%` = The entity that directly inflicted the damage.",
        "`at %location%` = The source of the damage."})
@Examples({"set {_source} to damage source from arrow directly by (random element of all entities)",
        "set {_source} to damage source of dragon breath",
        "set {_source} to damage source of magic",
        "set {_source} to damage source of mob_attack_no_aggro caused by target entity of player",
        "damage player by 100 with {_source}"})
@Since("INSERT VERSION")
public class ExprDamageSourceCreate extends SimpleExpression<DamageSource> {

    static {
        Skript.registerExpression(ExprDamageSourceCreate.class, DamageSource.class, ExpressionType.COMBINED,
                "[[a] new] damage source (of|from) %damagetype% [caused by %-entity%] " +
                        "[directly (by|from) %-entity%] [at %-location%]");
    }

    private Expression<DamageType> damageType;
    private Expression<Entity> causingEntity;
    private Expression<Entity> directEntity;
    private Expression<Location> damageLocation;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.damageType = (Expression<DamageType>) exprs[0];
        this.causingEntity = (Expression<Entity>) exprs[1];
        this.directEntity = (Expression<Entity>) exprs[2];
        this.damageLocation = (Expression<Location>) exprs[3];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected DamageSource @Nullable [] get(Event event) {
        DamageType damageType = this.damageType.getSingle(event);
        if (damageType == null) return null;

        Entity causing = this.causingEntity != null ? this.causingEntity.getSingle(event) : null;
        Entity direct = this.directEntity != null ? this.directEntity.getSingle(event) : null;
        Location damageLocation = this.damageLocation != null ? this.damageLocation.getSingle(event) : null;

        DamageSource.Builder builder = DamageSource.builder(damageType);
        if (causing != null) builder.withCausingEntity(causing);
        if (direct != null) builder.withDirectEntity(direct);
        if (damageLocation != null) builder.withDamageLocation(damageLocation);

        return new DamageSource[]{builder.build()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends DamageSource> getReturnType() {
        return DamageSource.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String type = this.damageType.toString(e, d);
        String cause = this.causingEntity != null ? (" caused by " + this.causingEntity.toString(e, d)) : "";
        String direct = this.directEntity != null ? (" directly by " + this.directEntity.toString(e, d)) : "";
        String loc = this.damageLocation != null ? (" at " + this.damageLocation.toString(e, d)) : "";
        return "damage source of " + type + cause + direct + loc;
    }

}
