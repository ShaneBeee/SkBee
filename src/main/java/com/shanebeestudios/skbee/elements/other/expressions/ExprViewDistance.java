package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("View Distance")
@Description({"Get/change view distance of a player/world.",
        "This represents the distance (in chunks) that will be sent to the player.",
        "\nNOTE: Must be a value between 2 and 32.",
        "\nWhile I understand Skript has this, it's been disabled in Skript due to previous PaperMC issues."})
@Examples({"set view distance of all players to 2",
        "add 1 to view distance of world \"world\"",
        "set {_sim} to view distance of world of player"})
@Since("INSERT VERSION")
public class ExprViewDistance extends SimplePropertyExpression<Object, Number> {

    static {
        register(ExprViewDistance.class, Number.class,
                "view distance", "players/worlds");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.methodExists(Player.class, "setViewDistance", int.class)) {
            Skript.error("This expressions requires a PaperMC server.");
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Number convert(Object object) {
        return getViewDistance(object);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE, RESET -> CollectionUtils.array(Number.class);
            case REMOVE_ALL, DELETE -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) {
        Number num = delta != null ? (Number) delta[0] : 0;
        int changeValue = num.intValue();

        for (Object object : getExpr().getArray(event)) {
            int oldValue = getViewDistance(object);
            int newValue = switch (mode) {
                case SET -> changeValue;
                case ADD -> oldValue + changeValue;
                case REMOVE -> oldValue - changeValue;
                case RESET -> Bukkit.getViewDistance();
                case REMOVE_ALL, DELETE -> 0;
            };
            newValue = MathUtil.clamp(newValue, 2, 32);
            setViewDistance(object, newValue);
        }
    }

    private void setViewDistance(Object object, int changeValue) {
        if (object instanceof World world) world.setViewDistance(changeValue);
        else if (object instanceof Player player) player.setViewDistance(changeValue);
    }

    private int getViewDistance(Object object) {
        if (object instanceof World world) return world.getViewDistance();
        else if (object instanceof Player player) return player.getViewDistance();
        return 0;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "view distance";
    }

}
