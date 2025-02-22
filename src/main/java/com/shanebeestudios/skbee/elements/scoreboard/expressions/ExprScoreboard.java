package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.scoreboard.ScoreboardUtils;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

@Name("Scoreboard - Scoreboard Get")
@Description("Get the vanilla scoreboard, or create a new scoreboard (custom scoreboard are not persistent).")
@Examples({"set {_scoreboard} to scoreboard of player",
    "set scoreboard of player to a new scoreboard",
    "set scoreboard of player to the vanilla scoreboard",
    "reset scoreboard of player"})
@Since("3.9.0")
public class ExprScoreboard extends SimpleExpression<Scoreboard> {

    static {
        Skript.registerExpression(ExprScoreboard.class, Scoreboard.class, ExpressionType.SIMPLE,
            "[a] (new|custom) scoreboard",
            "[the] (vanilla|main|server|default) scoreboard");
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.pattern = matchedPattern;
        return true;
    }

    @Override
    protected Scoreboard @Nullable [] get(Event event) {
        return new Scoreboard[]{this.pattern == 1 ? ScoreboardUtils.getMainScoreboard() : ScoreboardUtils.getNewScoreboard()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Scoreboard> getReturnType() {
        return Scoreboard.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return this.pattern == 1 ? "vanilla scoreboard" : "new scoreboard";
    }

}
