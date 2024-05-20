package com.shanebeestudios.skbee.elements.worldborder.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("WorldBorder - Stats")
@Description({"Get/set different stats of a world border.",
        "\ndamage amount = amount of damage a player takes when outside the border plus the border buffer.",
        "\ndamage buffer = amount of blocks a player may safely be outside the border before taking damage.",
        "\nsize = border to a square region with the specified side length in blocks.",
        "\nwarning distance = distance that causes the screen to be tinted red when the player is within the specified number of blocks from the border."})
@Examples({"set damage amount of world border of world of player to 10",
        "set size of world border of player to 100"})
@Since("1.17.0")
public class ExprWorldBorderNumbers extends SimplePropertyExpression<WorldBorder, Number> {

    static {
        register(ExprWorldBorderNumbers.class, Number.class,
                "[border] (damage amount|1:damage buffer|2:size|3:warning distance)",
                "worldborders");
    }

    private int pattern;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        setExpr((Expression<? extends WorldBorder>) exprs[0]);
        return true;
    }

    @Override
    public @Nullable Number convert(WorldBorder worldBorder) {
        return switch (pattern) {
            case 1 -> worldBorder.getDamageBuffer();
            case 2 -> worldBorder.getSize();
            case 3 -> worldBorder.getWarningDistance();
            default -> worldBorder.getDamageAmount();
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Number number = (Number) delta[0];
        if (number == null) return;

        for (WorldBorder border : getExpr().getArray(event)) {
            switch (pattern) {
                case 1 -> border.setDamageBuffer(number.doubleValue());
                case 2 -> border.setSize(number.doubleValue());
                case 3 -> border.setWarningDistance(number.intValue());
                default -> border.setDamageAmount(number.doubleValue());
            }
        }
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    private static final String[] PATTERNS = new String[]{"damage amount", "damage buffer", "size", "warning distance"};

    @Override
    protected @NotNull String getPropertyName() {
        return PATTERNS[pattern];
    }

}
