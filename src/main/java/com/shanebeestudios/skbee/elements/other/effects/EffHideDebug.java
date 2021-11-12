package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Simplified Debug Screen")
@Description("This effect has been removed due to the strugle to keep it alive.")
@Examples({""})
@Since("1.3.0")
public class EffHideDebug extends Effect {

    static {
        Skript.registerEffect(EffHideDebug.class,
                "(0¦reduce|1¦expand) debug [screen] for %players%");
    }

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        Skript.error("[SkBee] This effect has been removed");
        return false;
    }

    @Override
    protected void execute(Event e) {
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "[REMOVED] hide/show debug screen";
    }

}
