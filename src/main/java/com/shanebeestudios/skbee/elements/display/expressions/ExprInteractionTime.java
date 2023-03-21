package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Interaction - Last Attack/Interaction Timestamp")
@Description({"Represents the timestamp of the last attack/interaction of an Interaction Entity.",
        "As per Minecraft \"The timestamp of the game tick when the event happened\".",
        "Appears to be how many ticks since the world was created.", Types.McWiki_INTERACTION})
@Examples("set {_time} to last attack timestamp of target entity")
@Since("2.8.1")
public class ExprInteractionTime extends SimplePropertyExpression<Entity, Long> {

    static {
        register(ExprInteractionTime.class, Long.class,
                "last (attack|i:interaction) timestamp[s]", "entities");
    }

    private boolean interact;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.interact = parseResult.hasTag("i");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Long convert(Entity entity) {
        if (entity instanceof Interaction interaction) {
            if (this.interact) {
                Interaction.PreviousInteraction lastInteraction = interaction.getLastInteraction();
                if (lastInteraction != null) return lastInteraction.getTimestamp();
            } else {
                Interaction.PreviousInteraction lastAttack = interaction.getLastAttack();
                if (lastAttack != null) return lastAttack.getTimestamp();
            }
        }
        return null;
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "last " + (this.interact ? "interaction" : "attack") + " timestamp";
    }

}
