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
import org.bukkit.event.entity.EntityBreedEvent;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Breed Event Entities")
@Description("Get the entities involved in a breed event.")
@Examples({"on entity breed:", "\nif breeding mother is a sheep:",
        "\n\nkill breeding player"})
@Since("INSERT VERSION")
public class ExprBreedEntities extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprBreedEntities.class, Entity.class, ExpressionType.SIMPLE,
                "[the] breed[ing] parents",
                "[the] breed[ing] (mother|1¦father|2¦baby|3¦player)");
    }

    private int pattern;
    private int mark;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(EntityBreedEvent.class)) {
            Skript.error("Breeding parents can only be retrieved in a breed event.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.pattern = i;
        this.mark = parseResult.mark;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Entity[] get(Event event) {
        if (!(event instanceof EntityBreedEvent breedEvent)) return null;

        if (pattern == 0) {
            Entity[] parents = new Entity[2];
            parents[0] = breedEvent.getMother();
            parents[1] = breedEvent.getFather();
            return parents;
        } else if (mark == 0) {
            return new Entity[]{breedEvent.getMother()};
        } else if (mark == 1) {
            return new Entity[]{breedEvent.getFather()};
        } else if (mark == 2) {
            return new Entity[]{breedEvent.getEntity()};
        } else if (mark == 3) {
            return new Entity[]{breedEvent.getBreeder()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return pattern == 1;
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (pattern == 0) {
            return "breed event parents";
        } else {
            String ent = switch (mark) {
                case 1 -> "father";
                case 2 -> "baby";
                case 3 -> "breeder";
                default -> "mother";
            };
            return "breed event " + ent;
        }
    }

}
