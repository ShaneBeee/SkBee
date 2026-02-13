package com.shanebeestudios.skbee.elements.advancement.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffAdvancementCriteriaAward extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffAdvancementCriteriaAward.class,
                "(award|1:revoke) criteria %string% of %advancementpros%")
            .name("Advancement - Progress Criteria")
            .description("Award or revoke criteria of an advancement progress.")
            .examples("TODO") // TODO
            .since("1.17.0")
            .register();
    }

    private boolean award;
    private Expression<String> criteria;
    private Expression<AdvancementProgress> progress;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.award = parseResult.mark == 0;
        this.criteria = (Expression<String>) exprs[0];
        this.progress = (Expression<AdvancementProgress>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        String criteria = this.criteria.getSingle(event);
        if (criteria == null) return;

        for (AdvancementProgress progress : this.progress.getArray(event)) {
            if (award) {
                progress.awardCriteria(criteria);
            } else {
                progress.revokeCriteria(criteria);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String award = this.award ? "award" : "revoke";
        return award + " criteria " + this.criteria.toString(e, d) + " of " + this.progress.toString(e, d);
    }

}
