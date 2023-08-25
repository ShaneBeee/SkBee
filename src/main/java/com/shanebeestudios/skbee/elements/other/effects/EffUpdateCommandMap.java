package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.CommandReloader;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Update Command Map")
@Description("Attempts to update the command map sent to players.")
@Examples({"on load:",
        "\tupdate command map"})
@Since("2.5.3")
public class EffUpdateCommandMap extends Effect {

    static {
        Skript.registerEffect(EffUpdateCommandMap.class, "(update|sync) command (map|list)");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        CommandReloader.syncCommands(Bukkit.getServer());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "update command map";
    }

}
