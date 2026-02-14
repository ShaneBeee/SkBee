package com.shanebeestudios.skbee.elements.damagesource.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprDamageSourceProperties extends SimplePropertyExpression<DamageSource, Object> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprDamageSourceProperties.class, Object.class,
                "(1:causing entity|2:direct entity|3:damage type|4:damage location|5:food exhaustion|6:source location)",
                "damagesources")
            .name("DamageSource - Properties")
            .description("Represents different elements you can get from a damage source.")
            .examples("on damage of player:",
                "\tif damage type of damage source = arrow:",
                "\t\tbroadcast \"OUCHIE\"",
                "\tif causing entity of damage source is a chicken:",
                "\t\tbroadcast \"YOU JERK\"")
            .since("3.3.0")
            .register();
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Object convert(DamageSource damageSource) {
        return switch (this.pattern) {
            case 2 -> damageSource.getDirectEntity();
            case 3 -> damageSource.getDamageType();
            case 4 -> damageSource.getDamageLocation();
            case 5 -> damageSource.getFoodExhaustion();
            case 6 -> damageSource.getSourceLocation();
            default -> damageSource.getCausingEntity();
        };
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return switch (this.pattern) {
            case 3 -> DamageType.class;
            case 4, 6 -> Location.class;
            case 5 -> Number.class;
            default -> Entity.class;
        };
    }

    @Override
    protected @NotNull String getPropertyName() {
        return switch (this.pattern) {
            case 2 -> "direct entity";
            case 3 -> "damage type";
            case 4 -> "damage location";
            case 5 -> "food exhaustion";
            case 6 -> "source location";
            default -> "causing entity";
        };
    }

}
