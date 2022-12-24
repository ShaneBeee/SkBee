package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Simulation Distance")
@Description({"Get/change simulation distance of a player/world.",
        "This represents the distance (in chunks) that entities/blocks outside of this distance will not tick.",
        "\nNOTE: Must be a value between 2 and 32."})
@Examples({"set simulation distance of all players to 2",
        "add 1 to simulation distance of world \"world\"",
        "set {_sim} to simulation distance of world of player"})
@Since("INSERT VERSION")
public class ExprSimulationDistance extends SimplePropertyExpression<Object, Number> {

    static {
        register(ExprSimulationDistance.class, Number.class,
                "simulation distance", "players/worlds");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (!Skript.methodExists(Player.class, "setSimulationDistance", int.class)) {
            Skript.error("This expressions requires a PaperMC server.");
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Number convert(Object object) {
        return getSimulationDistance(object);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE, RESET -> CollectionUtils.array(Number.class);
            case REMOVE_ALL, DELETE -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Number num = delta != null ? (Number) delta[0] : 0;
        int changeValue = num.intValue();

        for (Object object : getExpr().getArray(event)) {
            int oldValue = getSimulationDistance(object);
            int newValue = switch (mode) {
                case SET -> changeValue;
                case ADD -> oldValue + changeValue;
                case REMOVE -> oldValue - changeValue;
                case RESET -> Bukkit.getSimulationDistance();
                case REMOVE_ALL, DELETE -> 0;
            };
            newValue = MathUtil.clamp(newValue, 2, 32);
            setSimulationDistance(object, newValue);
        }
    }

    private void setSimulationDistance(Object object, int changeValue) {
        if (object instanceof World world) world.setSimulationDistance(changeValue);
        else if (object instanceof Player player) player.setSimulationDistance(changeValue);
    }

    private int getSimulationDistance(Object object) {
        if (object instanceof World world) return world.getSimulationDistance();
        else if (object instanceof Player player) return player.getSimulationDistance();
        return 0;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "simulation distance";
    }

}
