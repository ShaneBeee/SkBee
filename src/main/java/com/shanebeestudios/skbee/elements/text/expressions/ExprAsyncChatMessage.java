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
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Async Chat Format/Message")
@Description({"Get/set the chat message from an async chat event as a text component.",
        "Set the chat format of an async chat event (no getter for this).",
        "Requires PaperMC!"})
@Examples({"on async chat:",
        "\tset {_m::1} to mini message from \"<gradient:##33FFE6:##33FF68>%player% &7» \"",
        "\tset {_m::2} to async chat message",
        "\tset {_m} to merge components {_m::*}",
        "\tset async chat format to {_m}"})
@Since("2.18.0")
public class ExprAsyncChatMessage extends SimpleExpression<ComponentWrapper> {

    static {
        Skript.registerExpression(ExprAsyncChatMessage.class, ComponentWrapper.class, ExpressionType.SIMPLE,
                "async chat (format|:message)");
    }

    private boolean message;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(AsyncChatEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in the Async Chat Event.");
            return false;
        }
        this.message = parseResult.hasTag("message");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ComponentWrapper[] get(Event event) {
        if (!(event instanceof AsyncChatEvent chatEvent)) return null;
        if (this.message) {
            return new ComponentWrapper[]{ComponentWrapper.fromComponent(chatEvent.message())};
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ComponentWrapper.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof ComponentWrapper wrapper && event instanceof AsyncChatEvent chatEvent) {
            if (this.message) {
                chatEvent.message(wrapper.getComponent());
            } else {
                chatEvent.renderer((source, sourceDisplayName, message, viewer) -> wrapper.getComponent());
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String message = this.message ? "message" : "format";
        return "async chat " + message;
    }

}
