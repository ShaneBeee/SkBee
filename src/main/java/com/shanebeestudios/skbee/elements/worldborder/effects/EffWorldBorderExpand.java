package com.shanebeestudios.skbee.elements.worldborder.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("WorldBorder - Expand/Shrink")
@Description({"Expand or shrink a world border.",
        "\nBY = adds/subtracts from current size of border.",
        "\nTO = sets to the specified size.",
        "\ntimespan = how long it will take the border to get to the new size."})
@Examples({"expand world border of player by 100 in 5 seconds",
        "shrink world border of world \"world\" to 100 in 10 seconds"})
@Since("1.17.0")
public class EffWorldBorderExpand extends Effect {

    static {
        Skript.registerEffect(EffWorldBorderExpand.class,
                "(expand|1:shrink) %worldborders% by %number% [in %-timespan%]",
                "(expand|1:shrink) %worldborders% to %number% [in %-timespan%]");
    }

    private boolean expand;
    private int pattern;
    private Expression<WorldBorder> worldBorders;
    private Expression<Number> size;
    private Expression<Timespan> timeSpan;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.expand = parseResult.mark == 0;
        this.pattern = i;
        this.worldBorders = (Expression<WorldBorder>) exprs[0];
        this.size = (Expression<Number>) exprs[1];
        this.timeSpan = (Expression<Timespan>) exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        Number sizeNum = this.size.getSingle(event);
        if (sizeNum == null) return;

        long speed = 0;
        if (this.timeSpan != null) {
            Timespan timeSpan = this.timeSpan.getSingle(event);
            if (timeSpan != null) speed = timeSpan.getTicks_i() / 20;
        }

        int size = sizeNum.intValue();
        for (WorldBorder border : this.worldBorders.getArray(event)) {
            if (pattern == 0) {
                expand(border, size, speed);
            } else {
                if (size < 0) size = 0;
                border.setSize(size, speed);
            }
        }
    }

    private void expand(WorldBorder worldBorder, double size, long seconds) {
        double oldSize = worldBorder.getSize();
        if (expand) {
            worldBorder.setSize(oldSize + size, seconds);
        } else {
            double newSize = oldSize - size;
            if (newSize < 0) newSize = 0;
            worldBorder.setSize(newSize, seconds);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String expand = this.expand ? "expand" : "shrink";
        String to = pattern == 1 ? "to" : "by";
        String size = this.size.toString(e, d);
        String time = this.timeSpan != null ? "in " + this.timeSpan.toString(e, d) : "";
        return String.format("%s %s %s %s %s",
                expand, this.worldBorders.toString(e, d), to, size, time);
    }

}
