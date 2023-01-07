package com.shanebeestudios.skbee.elements.bound.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Bound - Map Marker Visibility")
@Description({"Hide/show a map marker for a bound (currently only supports BlueMaps)"})
@Examples({"hide map marker of bound with id \"le-spawn\"",
        "show map marker of bound with id \"le-spawn\"",
        "toggle map marker of bound with id \"le-spawn\""})
@Since("INSERT VERSION")
public class EffBoundMarkerHide extends Effect {

    static {
        Skript.registerEffect(EffBoundMarkerHide.class, "(hide|1:show|2:toggle) map marker of %bounds%");
    }

    private Expression<Bound> bounds;
    private int pattern;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.bounds = (Expression<Bound>) exprs[0];
        this.pattern = parseResult.mark;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        for (Bound bound : this.bounds.getArray(event)) {
            switch (pattern) {
                case 0 -> bound.setMarkerHidden(true);
                case 1 -> bound.setMarkerHidden(false);
                case 2 -> bound.setMarkerHidden(!bound.isMarkerHidden());
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String hide = this.pattern == 0 ? "hide" : this.pattern == 1 ? "show" : "toggle";
        return hide + " map marker of " + this.bounds.toString(e, d);
    }

}
