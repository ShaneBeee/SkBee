package com.shanebeestudios.skbee.elements.tickmanager.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ServerTickManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Server Tick - Is Frozen/Running Normally")
@Description({"Checks if the server is currently frozen/running normally.",
        "When the server is running normally it indicates that the server is not currently frozen.",
        Util.MCWIKI_TICK_COMMAND, "Requires Minecraft 1.20.4+"})
@Examples({"if server is frozen:",
        "\tteleport all players to spawn of world \"world\"",
        "if server is running normally:",
        "\tkill all sheep"})
@Since("INSERT VERSION")
public class CondServerTickFrozen extends Condition {

    static {
        Skript.registerCondition(CondServerTickFrozen.class,
                "(server|game) (is|neg:(isn't|is not)) [currently] (frozen|normal:running normally)");
    }

    private boolean normal;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setNegated(parseResult.hasTag("neg"));
        this.normal = parseResult.hasTag("normal");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        ServerTickManager tickManager = Bukkit.getServerTickManager();
        return !isNegated() == (this.normal ? tickManager.isRunningNormally() : tickManager.isFrozen());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        String is = isNegated() ? "is not" : "is";
        String normal = this.normal ? " running normally" : " frozen";
        return "server " + is + normal;
    }

}
