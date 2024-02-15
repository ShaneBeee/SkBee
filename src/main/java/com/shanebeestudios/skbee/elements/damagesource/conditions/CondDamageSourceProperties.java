package com.shanebeestudios.skbee.elements.damagesource.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.damage.DamageSource;
import org.jetbrains.annotations.NotNull;

@Name("DamageSource - Property Conditions")
@Description("Represents some conditions of a damage source.")
@Examples({"if damage source is indirect:",
        "if damage source is scaled with difficulty:"})
@Since("3.3.0")
public class CondDamageSourceProperties extends PropertyCondition<DamageSource> {

    static {
        register(CondDamageSourceProperties.class, PropertyType.BE,
                "(indirect|1:scaled with difficulty)", "damagesources");
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
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
