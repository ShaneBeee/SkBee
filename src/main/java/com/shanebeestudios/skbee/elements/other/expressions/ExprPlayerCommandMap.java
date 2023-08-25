package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Name("Player Command Map")
@Description({"Represents a list of available commands sent to the client during login.",
        "\nCLEAR = will wipe all commands from the list",
        "\nREMOVE = will remove a command from the list",
        "\nSET = will clear the list and add back the ones you're setting (NOTE: this will NOT add NEW commands)"})
@Examples({"on player command send:",
        "\tremove \"ver\" and \"version\" from player command map",
        "on player command send:",
        "\tloop command map:",
        "\t\tif loop-value contains \":\":",
        "\t\t\tremove loop-value from command map"})
@Since("2.5.3")
public class ExprPlayerCommandMap extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprPlayerCommandMap.class, String.class, ExpressionType.SIMPLE,
                "[the] [player] command (map|list)");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(PlayerCommandSendEvent.class)) {
            Skript.error("The player command map can only be used in the Player Command Send Event.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable String[] get(Event event) {
        if (event instanceof PlayerCommandSendEvent commandSendEvent) {
            return commandSendEvent.getCommands().toArray(new String[0]);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        switch (mode) {
            case SET, DELETE, REMOVE -> {
                return CollectionUtils.array(String[].class);
            }
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (!(event instanceof PlayerCommandSendEvent commandSendEvent)) {
            return;
        }
        Collection<String> commands = commandSendEvent.getCommands();
        if (mode == ChangeMode.DELETE) {
            commands.clear();
            return;
        }
        List<String> changeCommands = List.of(Arrays.copyOf(delta, delta.length, String[].class));
        switch (mode) {
            case SET -> {
                commands.clear();
                commands.addAll(changeCommands);
            }
            case REMOVE -> commands.removeAll(changeCommands);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "player command map";
    }

}
