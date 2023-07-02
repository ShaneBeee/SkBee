package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Experience;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Fishing - Experience")
@Description("Get and modify the amount of experience dropped in a fishing event.")
@Examples({"on fishing:",
        "\tadd 10xp to fishing experience",
        "\tsend fishing experience to player"})
@Since("2.14.0")
public class ExprFishingExperience extends SimpleExpression<Experience> {

    static {
        Skript.registerExpression(ExprFishingExperience.class, Experience.class, ExpressionType.SIMPLE,
                "fish[ing] [event] experience");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(PlayerFishEvent.class)) {
            Skript.error("The fishing experience expression, can only be used within fishing events.");
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Experience[] get(Event event) {
        Experience experience = new Experience(((PlayerFishEvent) event).getExpToDrop());
        return new Experience[]{experience};
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE -> CollectionUtils.array(Experience.class, Number.class);
            case DELETE -> CollectionUtils.array();
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue", "DataFlowIssue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (!(event instanceof PlayerFishEvent playerFishEvent)) return;
        if (delta == null) {
            playerFishEvent.setExpToDrop(0);
            return;
        }
        int experience = delta[0] instanceof Experience ? ((Experience) delta[0]).getXP() : ((Number) delta[0]).intValue();
        int curentExperience = playerFishEvent.getExpToDrop();
        int value = 0;
        switch (mode) {
            case SET -> value = experience;
            case ADD -> value = curentExperience + experience;
            case REMOVE -> {
                value = curentExperience - experience;
                if (value < 0) value = 0;
            }
        }
        playerFishEvent.setExpToDrop(value);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Experience> getReturnType() {
        return Experience.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "fishing experience";
    }

}
