package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprFishEventEntity extends SimpleExpression<Entity> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprFishEventEntity.class, Entity.class,
                "fish[ing] [event] caught entity",
                "fish[ing] [event] hook")
            .name("Fish Event Entity")
            .description("Represents the caught entity or hook in a fish event.")
            .examples("on fish:",
                "\tif fish state = caught fish:",
                "\t\tset item of fishing caught entity to diamond")
            .since("1.15.2")
            .register();
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(PlayerFishEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in a fishing event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        pattern = matchedPattern;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Entity[] get(Event event) {
        if (!(event instanceof PlayerFishEvent fishEvent)) {
            return null;
        }
        return new Entity[]{pattern == 0 ? fishEvent.getCaught() : fishEvent.getHook()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        String entity = pattern == 0 ? "caught entity" : "fish hook";
        return "fish event " + entity;
    }

}
