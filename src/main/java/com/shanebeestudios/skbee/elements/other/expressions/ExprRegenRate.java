package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Regeneration Rate")
@Description({"Represents the regeneration rate (1 health per x ticks) of the Player.",
        "\nSaturated = When they have saturation and their food level >= 20. Default is 10 ticks.",
        "\nUnsaturated = When they have no saturation and their food level >= 18. Default is 80 ticks."})
@Examples({"set {_regen} to saturated regen rate of player",
        "set unsaturated regen rate of player to 10 ticks",
        "add 1 second to unsaturated regen rate of player"})
@Since("INSERT VERSION")
public class ExprRegenRate extends SimplePropertyExpression<Player, Timespan> {

    static {
        if (Skript.methodExists(HumanEntity.class, "getSaturatedRegenRate")) {
            register(ExprRegenRate.class, Timespan.class,
                    "[:un]saturated regen[eration] rate", "players");
        }
    }

    private boolean saturated;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.saturated = !parseResult.hasTag("un");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Timespan convert(Player player) {
        int ticks = this.saturated ? player.getSaturatedRegenRate() : player.getUnsaturatedRegenRate();
        return Timespan.fromTicks_i(ticks);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE -> CollectionUtils.array(Timespan.class);
            case RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int changeValue;
        if (mode == ChangeMode.RESET) changeValue = this.saturated ? 10 : 80;
        else if (delta != null && delta[0] instanceof Timespan timespan) {
            changeValue = (int) timespan.getTicks_i();
        } else return;


        for (Player player : getExpr().getArray(event)) {
            int previous = this.saturated ? player.getSaturatedRegenRate() : player.getUnsaturatedRegenRate();
            if (mode == ChangeMode.ADD) changeValue += previous;
            else if (mode == ChangeMode.REMOVE) changeValue = previous - changeValue;

            if (saturated) player.setSaturatedRegenRate(changeValue);
            else player.setUnsaturatedRegenRate(changeValue);
        }
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        String saturated = this.saturated ? "saturated" : "unsaturated";
        return saturated + " regen rate";
    }

}
