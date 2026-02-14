package com.shanebeestudios.skbee.elements.damagesource.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.damage.DamageSource;
import org.jetbrains.annotations.NotNull;

public class CondDamageSourceProperties extends PropertyCondition<DamageSource> {

    public static void register(Registration reg) {
        reg.newCondition(CondDamageSourceProperties.class,
                "%damagesources% (is|are) (indirect|1:scaled with difficulty)",
                "%damagesources% (isn't|is not|aren't|are not) (indirect|1:scaled with difficulty)")
            .name("DamageSource - Property Conditions")
            .description("Represents some conditions of a damage source.")
            .examples("if damage source is indirect:",
                "if damage source is scaled with difficulty:")
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
    public boolean check(DamageSource damageSource) {
        if (this.pattern == 1) return damageSource.scalesWithDifficulty();
        return damageSource.isIndirect();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return this.pattern == 1 ? "scaled with difficulty" : "indirect";
    }

}
