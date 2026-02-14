package com.shanebeestudios.skbee.elements.fastboard.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
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

public class EffFastBoardToggle extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffFastBoardToggle.class,
                "toggle [:score|fast]board[s] of %players% [[to ](1:(on|true)|2:(off|false))]",
                "toggle %players%'[s] [:score|fast]board[s] [[to ](1:(on|true)|2:(off|false))]")
            .name("FastBoard - Toggle")
            .description("Toggle a fastboard on or off.")
            .examples("toggle fastboards of all players off",
                "toggle player's fastboard")
            .since("1.16.0")
            .register();
    }

    private Expression<Player> player;
    private int pattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        this.pattern = parseResult.mark;
        if (parseResult.hasTag("score")) {
            Skript.warning("'scoreboard' is deprecated, please use 'fastboard' instead.");
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Player player : this.player.getArray(event)) {
            FastBoardBase<?, ?> board = FastBoardManager.getBoard(player);
            if (board != null) {
                switch (pattern) {
                    case 0 -> board.toggle();
                    case 1 -> board.show();
                    case 2 -> board.hide();
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String toggle = switch (pattern) {
            case 1 -> " to on";
            case 2 -> " to off";
            default -> "";
        };

        return "toggle scoreboard[s] of " + player.toString(e, d) + toggle;
    }

}
