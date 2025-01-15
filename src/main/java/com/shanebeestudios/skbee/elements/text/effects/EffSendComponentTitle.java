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
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.audience.Audience;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Send Title")
@Description({"Send titles containing components. Supports strings as well.",
    "If you are using variables and the title won't send, make sure to add `component`.",
    "`to %audiences%` = An audience is anything that can receieve a component (players, entities, console, worlds, server, etc)."})
@Examples({"send title mini message from \"<rainbow>OOO RAINBOW TITLE\"",
    "send title component {_comp} for 10 seconds with fadein 5 ticks and fadeout 10 ticks"})
@Since("2.4.0")
public class EffSendComponentTitle extends Effect {

    static {
        Skript.registerEffect(EffSendComponentTitle.class,
            "send title [component] %textcomponent/string% [with subtitle [component] %-textcomponent/string%] [to %audiences%] [for %-timespan%] [with fade[(-| )]in %-timespan%] [(and|with) fade[(-| )]out %-timespan%]");
    }

    private Expression<Object> title, subtitle;
    private Expression<Audience> audiences;
    private Expression<Timespan> stay, fadeIn, fadeOut;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.title = (Expression<Object>) exprs[0];
        this.subtitle = (Expression<Object>) exprs[1];
        this.audiences = (Expression<Audience>) exprs[2];
        this.stay = (Expression<Timespan>) exprs[3];
        this.fadeIn = (Expression<Timespan>) exprs[4];
        this.fadeOut = (Expression<Timespan>) exprs[5];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object title = this.title.getSingle(event);
        if (title == null) return;

        Object subtitle = this.subtitle != null ? this.subtitle.getSingle(event) : null;
        Audience[] audiences = this.audiences.getArray(event);

        long stay = -1;
        long fadeIn = -1;
        long fadeOut = -1;

        if (this.stay != null) {
            Timespan staySingle = this.stay.getSingle(event);
            if (staySingle != null) {
                stay = staySingle.getAs(Timespan.TimePeriod.TICK);
            }
        }
        if (this.fadeIn != null) {
            Timespan fadeInSingle = this.fadeIn.getSingle(event);
            if (fadeInSingle != null) {
                fadeIn = fadeInSingle.getAs(Timespan.TimePeriod.TICK);
            }
        }
        if (this.fadeOut != null) {
            Timespan fadeOutSingle = this.fadeOut.getSingle(event);
            if (fadeOutSingle != null) {
                fadeOut = fadeOutSingle.getAs(Timespan.TimePeriod.TICK);
            }
        }
        ComponentWrapper.sendTitle(audiences, title, subtitle, stay, fadeIn, fadeOut);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String title = "send title component " + this.title.toString(e, d);
        String sub = this.subtitle != null ? " with subtitle " + this.subtitle.toString(e, d) : "";
        String play = this.audiences != null ? " to " + this.audiences.toString(e, d) : "";
        String stay = this.stay != null ? " for " + this.stay.toString(e, d) : "";
        String fadeIn = this.fadeIn != null ? " with fadein " + this.fadeIn.toString(e, d) : "";
        String fadeOut = this.fadeOut != null ? " with fadeout " + this.fadeOut.toString(e, d) : "";
        return title + sub + play + stay + fadeIn + fadeOut;
    }

}
