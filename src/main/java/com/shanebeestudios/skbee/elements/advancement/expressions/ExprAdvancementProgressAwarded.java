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
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Advancement - Progress Criteria")
@Description("Get the awarded/remaining criteria of an advancement progress.")
@Examples("set {_c::*} to awarded criteria of advancement progress of {_advancement} of player")
@Since("1.17.0")
public class ExprAdvancementProgressAwarded extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprAdvancementProgressAwarded.class, String.class, ExpressionType.SIMPLE,
                "(awarded|1:remaining) criteria of %advancementpro%");
    }

    private boolean awarded;
    private Expression<AdvancementProgress> progress;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.awarded = parseResult.mark == 0;
        this.progress = (Expression<AdvancementProgress>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable String[] get(Event event) {
        AdvancementProgress progress = this.progress.getSingle(event);
        if (progress == null) return null;

        if (awarded) {
            return progress.getAwardedCriteria().toArray(new String[0]);
        } else {
            return progress.getRemainingCriteria().toArray(new String[0]);
        }
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
        String award = awarded ? "awarded" : "remaining";
        return award + " criteria of " + this.progress.toString(e, d);
    }

}
