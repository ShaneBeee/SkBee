package com.shanebeestudios.skbee.elements.advancement.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Advancement - Progress Criteria")
@Description("Award or revoke criteria of an advancement progress.")
@Examples("TODO") // TODO
@Since("INSERT VERSION")
public class EffAdvancementCriteriaAward extends Effect {

    static {
        Skript.registerEffect(EffAdvancementCriteriaAward.class,
                "(award|1¦revoke) criteria %string% of %advancementpros%");
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
