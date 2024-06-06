package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Async Chat Viewers")
@Description({"Represents the viewers that this chat message will be displayed to.",
    "NOTE: Can only be used in an `async chat event`. Requires PaperMC"})
@Examples({"on async chat:",
    "\tclear chat viewers",
    "\tadd player to chat viewers",
    "",
    "on async chat:",
    "\tset chat viewers to players in world of player",
    "",
    "on async chat:",
    "\tremove (all players where [input doesn't have permission \"staff.chat\"]) from chat viewers"})
@Since("3.5.3")
public class ExprAsyncChatViewers extends SimpleExpression<CommandSender> {

    static {
        Skript.registerExpression(ExprAsyncChatViewers.class, CommandSender.class, ExpressionType.SIMPLE,
            "[async] chat viewers");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!(ParserInstance.get().isCurrentEvent(AsyncChatEvent.class))) {
            Skript.error("'" + parseResult.expr + "' can only be used in the Async Chat Event.");
            return false;
        }
        return true;
    }

    @SuppressWarnings({"NullableProblems", "SuspiciousToArrayCall"})
    @Override
    protected CommandSender @Nullable [] get(Event event) {
        if (event instanceof AsyncChatEvent chatEvent) {
            return chatEvent.viewers().toArray(new CommandSender[0]);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, SET -> CollectionUtils.array(CommandSender[].class);
            case DELETE -> CollectionUtils.array();
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (!(event instanceof AsyncChatEvent chatEvent)) return;
        if (mode == ChangeMode.DELETE) {
            chatEvent.viewers().clear();
            return;
        }
        // Failsafe just incase
        if (delta == null) return;

        List<Audience> viewers = new ArrayList<>();
        for (Object object : delta) {
            if (object instanceof CommandSender sender) viewers.add(sender);
        }
        switch (mode) {
            case ADD -> chatEvent.viewers().addAll(viewers);
            case REMOVE -> viewers.forEach(chatEvent.viewers()::remove);
            case SET -> {
                chatEvent.viewers().clear();
                chatEvent.viewers().addAll(viewers);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends CommandSender> getReturnType() {
        return CommandSender.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "async chat viewers";
    }

}
