package com.shanebeestudios.skbee.elements.advancement.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Advancement - Progress")
@Description("Returns the advancement progress of a player.")
public class ExprAdvancementProgress extends SimpleExpression<AdvancementProgress> {

    static {
        Skript.registerExpression(ExprAdvancementProgress.class, AdvancementProgress.class, ExpressionType.COMBINED,
                "advancement progress of %advancement% (for|of) %players%");
    }

    private Expression<Advancement> advancement;
    private Expression<Player> player;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.advancement = (Expression<Advancement>) exprs[0];
        this.player = (Expression<Player>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable AdvancementProgress[] get(Event event) {
        Advancement advancement = this.advancement.getSingle(event);
        if (advancement == null) return null;

        List<AdvancementProgress> progresses = new ArrayList<>();
        for (Player player : this.player.getArray(event)) {
            progresses.add(player.getAdvancementProgress(advancement));
        }
        return progresses.toArray(new AdvancementProgress[0]);
    }

    @Override
    public boolean isSingle() {
        return this.player.isSingle();
    }

    @Override
    public @NotNull Class<? extends AdvancementProgress> getReturnType() {
        return AdvancementProgress.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "advancement progress of advancement " + this.advancement.toString(e, d) + " of " +
                this.player.toString(e, d);
    }

}
