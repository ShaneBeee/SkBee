package com.shanebeestudios.skbee.elements.fastboard.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.fastboard.FastBoardBase;
import com.shanebeestudios.skbee.api.fastboard.FastBoardManager;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CondFastBoardOn extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondFastBoardOn.class,
                "[:score|fast]board of %player% is (on|true)",
                "[:score|fast]board of %player% is(n't| not) on",
                "[:score|fast]board of %player% is (off|false)")
            .name("FastBoard - Is On")
            .description("Check if a player's fastboard is currently toggled on/off.")
            .examples("if fastboard of player is on:",
                "\tsend \"Your scoreboard is on!\"")
            .since("1.16.0")
            .register();
    }

    private Expression<Player> player;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        setNegated(matchedPattern >= 1);
        if (parseResult.hasTag("score")) {
            Skript.warning("'scoreboard' is deprecated, please use 'fastboard' instead.");
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        Player player = this.player.getSingle(event);
        if (player != null) {
            FastBoardBase<?, ?> board = FastBoardManager.getBoard(player);
            if (board == null) return false;
            return board.isOn() ^ isNegated();
        }

        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "fastboard of " + this.player.toString(e, d) + " is " + (isNegated() ? "off" : "on");
    }

}
