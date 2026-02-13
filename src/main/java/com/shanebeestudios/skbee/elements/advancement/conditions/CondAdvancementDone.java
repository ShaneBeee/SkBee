package com.shanebeestudios.skbee.elements.advancement.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class CondAdvancementDone extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondAdvancementDone.class,
                "%advancementpro% is done",
                "%advancementpro% (isn't|is not) done")
            .name("Advancement - Done")
            .description("Check if the advancement progress is done.")
            .examples("if advancement progress of {_advancement} of player is done:")
            .since("1.17.0")
            .register();
    }

    private Expression<AdvancementProgress> progress;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        setNegated(i == 2);
        this.progress = (Expression<AdvancementProgress>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        return progress.check(event, AdvancementProgress::isDone, isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return PropertyCondition.toString(this, PropertyCondition.PropertyType.BE,
                e, d, progress, "done");
    }

}
