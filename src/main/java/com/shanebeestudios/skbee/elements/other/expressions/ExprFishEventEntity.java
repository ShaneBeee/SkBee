package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Fish Event Entity")
@Description("Represents the caught entity or hook in a fish event.")
@Examples({"on fish:",
        "\tif fish state = caught fish:",
        "\t\tset item of fishing caught entity to diamond"})
@Since("1.15.2")
public class ExprFishEventEntity extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprFishEventEntity.class, Entity.class, ExpressionType.SIMPLE,
                "fish[ing] [event] caught entity",
                "fish[ing] [event] hook");
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(PlayerFishEvent.class)) {
            Skript.error("Fish event caught entity/hook can only be used in a fishing event", ErrorQuality.SEMANTIC_ERROR);
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
