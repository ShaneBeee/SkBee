package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Name("Chat Completions")
@Description({"Represents the chat completions of a player.",
        "\n`set` = Set the list of chat completion suggestions shown to the player while typing a message.",
        "If completions were set previously, this method will remove them all and replace them with the provided completions.",
        "\n`add` = Add custom chat completion suggestions shown to the player while typing a message.",
        "\n`remove` = Remove custom chat completion suggestions shown to the player while typing a message. Online player names cannot be removed with this method.",
        "This will affect only custom completions added by add/set.",
        "\n`reset` = Will remove all custom chat completions.",
        "\nNOTE: Supports all objects, will be stringified based on however Skript does it."})
@Examples({"add \"i like cheese\" to chat completions of player",
        "add all worlds to chat completions of player",
        "set chat completions of player to \"potatoes\" and \"popcorn\"",
        "reset chat completions of player"})
@Since("INSERT VERSION")
public class ExprChatCompletions extends PropertyExpression<Player, String> {

    static {
        if (Skript.methodExists(Player.class, "setCustomChatCompletions", Collection.class)) {
            register(ExprChatCompletions.class, String.class, "[custom] chat completions", "players");
        }
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<Player>) exprs[0]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected String[] get(Event event, Player[] players) {
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.REMOVE || mode == ChangeMode.ADD || mode == ChangeMode.RESET) {
            return CollectionUtils.array(Object[].class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        List<String> completions = new ArrayList<>();
        if (delta != null) {
            for (Object object : delta) {
                if (object instanceof String string) completions.add(string);
                else completions.add(Classes.toString(object));
            }
        }
        for (Player player : getExpr().getArray(event)) {
            if (mode == ChangeMode.SET || mode == ChangeMode.RESET) player.setCustomChatCompletions(completions);
            else if (mode == ChangeMode.ADD) player.addCustomChatCompletions(completions);
            else if (mode == ChangeMode.REMOVE) player.removeCustomChatCompletions(completions);
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "chat completions of " + getExpr().toString(e, d);
    }

}
