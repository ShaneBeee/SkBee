package com.shanebeestudios.skbee.elements.text.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.text.BeeComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Text Component - Send Title")
@Description({"Send titles containing components. Supports strings as well.",
        "If you are using variables and the title won't send, make sure to add `component`."})
@Examples({"send title mini message from \"<rainbow>OOO RAINBOW TITLE\"",
        "send title component {_comp} for 10 seconds with fadein 5 ticks and fadeout 10 ticks"})
@Since("INSERT VERSION")
public class EffSendComponentTitle extends Effect {

    static {
        Skript.registerEffect(EffSendComponentTitle.class,
                "send title [component] %basecomponent/string% [with subtitle [component] %-basecomponent/string%] [to %players%] [for %-timespan%] [with fade[(-| )]in %-timespan%] [(and|with) fade[(-| )]out %-timespan%]");
    }

    private Expression<Object> title, subtitle;
    private Expression<Player> players;
    private Expression<Timespan> stay, fadeIn, fadeOut;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.title = (Expression<Object>) exprs[0];
        this.subtitle = (Expression<Object>) exprs[1];
        this.players = (Expression<Player>) exprs[2];
        this.stay = (Expression<Timespan>) exprs[3];
        this.fadeIn = (Expression<Timespan>) exprs[4];
        this.fadeOut = (Expression<Timespan>) exprs[5];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        Object title = this.title.getSingle(event);
        if (title == null) return;

        Object subtitle = this.subtitle != null ? this.subtitle.getSingle(event) : null;
        Player[] players = this.players.getArray(event);

        long stay = -1;
        long fadeIn = -1;
        long fadeOut = -1;

        if (this.stay != null) {
            Timespan staySingle = this.stay.getSingle(event);
            if (staySingle != null) {
                stay = staySingle.getTicks_i();
            }
        }
        if (this.fadeIn != null) {
            Timespan fadeInSingle = this.fadeIn.getSingle(event);
            if (fadeInSingle != null) {
                fadeIn = fadeInSingle.getTicks_i();
            }
        }
        if (this.fadeOut != null) {
            Timespan fadeOutSingle = this.fadeOut.getSingle(event);
            if (fadeOutSingle != null) {
                fadeIn = fadeOutSingle.getTicks_i();
            }
        }
        BeeComponent.sendTitle(players, title, subtitle, stay, fadeIn, fadeOut);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String title = "send title component " + this.title.toString(e, d);
        String sub = this.subtitle != null ? " with subtitle " + this.subtitle.toString(e, d) : "";
        String play = this.players != null ? " to " + this.players.toString(e, d) : "";
        String stay = this.stay != null ? " for " + this.stay.toString(e, d) : "";
        String fadeIn = this.fadeIn != null ? " with fadein " + this.fadeIn.toString(e, d) : "";
        String fadeOut = this.fadeOut != null ? " with fadeout " + this.fadeOut.toString(e, d) : "";
        return title + sub + play + stay + fadeIn + fadeOut;
    }

}
