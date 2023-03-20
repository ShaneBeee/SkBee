package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PufferFish;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("PufferFish - Puff State")
@Description("Represents the puff state of a puffer fish. An integer between 0 and 2")
@Examples({"set puff state of target entity to 1",
        "add 1 to puff state of target entity",
        "reset puff state of target entity"})
@Since("2.8.0")
public class ExprPufferFishState extends SimplePropertyExpression<Entity,Integer> {

    static {
        register(ExprPufferFishState.class, Integer.class, "puff state", "entities");
    }

    @Override
    public @Nullable Integer convert(Entity entity) {
        if (entity instanceof PufferFish pufferFish) {
            return pufferFish.getPuffState();
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD,SET, RESET, REMOVE -> CollectionUtils.array(Integer.class);
            case REMOVE_ALL, DELETE -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int changer = delta != null && delta[0] != null ? ((Integer) delta[0]) : 0;
        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof PufferFish pufferFish) {
                int puffState = pufferFish.getPuffState();
                switch (mode) {
                    case SET -> puffState = changer;
                    case ADD -> puffState += changer;
                    case REMOVE -> puffState -= changer;
                    case RESET -> puffState = 0;
                }
                if (puffState > 2) puffState = 2;
                if (puffState < 0) puffState = 0;
                pufferFish.setPuffState(puffState);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "puff state";
    }

}
