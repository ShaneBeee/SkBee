package com.shanebeestudios.skbee.elements.tickmanager.conditions;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ServerTickManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CondServerTickSprintStep extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondServerTickSprintStep.class,
                "(server|game) (is|neg:(isn't|is not)) [currently] (:sprinting|stepping)")
            .name("Server Tick - Is Sprinting/Stepping")
            .description("Checks if the server is currently sprinting/stepping.",
                Util.MCWIKI_TICK_COMMAND)
            .examples("if server is sprinting:",
                "\tkill all players")
            .since("3.1.0")
            .register();

    }

    private boolean sprint;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.sprint = parseResult.hasTag("sprinting");
        setNegated(parseResult.hasTag("neg"));
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        ServerTickManager tickManager = Bukkit.getServerTickManager();
        return !isNegated() == (this.sprint ? tickManager.isSprinting() : tickManager.isStepping());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String is = isNegated() ? "is not " : "is ";
        return "server " + is + (this.sprint ? "sprinting" : "stepping");
    }

}
