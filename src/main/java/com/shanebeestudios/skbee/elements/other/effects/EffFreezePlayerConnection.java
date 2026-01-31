package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
@Name("Freeze Player Connection")
@Description({"Freeze/unfreeze a player connect.",
    "**FREEZING**:",
    "You can freeze a player connection in the async player connection configure event.",
    "There is a built in 2 minute timeout just incase you don't handle unfreezing the connection.",
    "There is an optional timeout message (Will default to \"Timeout\").",
    "This will freeze the thread, so make sure to run it last.",
    "",
    "**UNFREEZING**:",
    "You can unfreeze the connection with an option to disconnect.",
    "If not disconnecting, the player will join the server after the connection is unfrozen.",
    "This can be done in the async player connection configure event and the player custom click event."})
@Examples({"on async player connection configure:",
    "\t# open your dialog",
    "\topenDialog(event-audience)",
    "",
    "\t# Freeze the connection and wait for the player to respond",
    "\tfreeze connection event-playerconnection",
    "",
    "on player custom click:",
    "\tif event-namespacedkey contains \"custom:accept_rules\":",
    "\t\t# Unfreeze the connection and join the player to the server if they accept rules",
    "\t\tunfreeze connection event-playerconnection",
    "\tif event-namespacedkey contains \"custom:decline_rules\":",
    "\t\t# Unfreeze the connection and kick the player if they decline the rules",
    "\t\tunfreeze connection event-playerconnection and disconnect due to mini message from \"<red>You decided to leave!\""})
@Since("3.16.0")
public class EffFreezePlayerConnection extends Effect {

    private static final Map<UUID, CompletableFuture<Component>> AWAITING_RESPONSE = new ConcurrentHashMap<>();

    static {
        Skript.registerEffect(EffFreezePlayerConnection.class,
            "freeze connection %playerconnection/audience% [with timeout message %-textcomponent%]",
            "unfreeze connection %playerconnection/audience% [and disconnect due to %-textcomponent%]");
    }

    private boolean freeze;
    private Expression<?> playerConnection;
    private Expression<ComponentWrapper> message;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.freeze = matchedPattern == 0;
        if (this.freeze && !ParserInstance.get().isCurrentEvent(AsyncPlayerConnectionConfigureEvent.class)) {
            Skript.error("You can only freeze a player connection in an async player connection config event.");
            return false;
        }
        this.playerConnection = exprs[0];
        this.message = (Expression<ComponentWrapper>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (this.freeze && event instanceof AsyncPlayerConnectionConfigureEvent) {
            Object connectObject = this.playerConnection.getSingle(event);
            if (!(connectObject instanceof PlayerConfigurationConnection connection)) return;


            UUID uniqueId = connection.getProfile().getId();
            if (uniqueId == null) {
                return;
            }
            CompletableFuture<Component> response = new CompletableFuture<>();

            Component timeout = Component.text("Timeout");
            if (this.message != null) {
                ComponentWrapper message = this.message.getSingle(event);
                if (message != null) timeout = message.getComponent();
            }
            // Complete the future if nothing has been done after two minutes.
            response.completeOnTimeout(timeout, 2, TimeUnit.MINUTES);

            AWAITING_RESPONSE.put(uniqueId, response);

            Component component = response.join();
            if (component != null) {
                // Incase the player is still in a dialog screen
                connection.getAudience().closeDialog();
                connection.disconnect(component);
            }

            AWAITING_RESPONSE.remove(uniqueId);
        } else {
            Object connectionObject = this.playerConnection.getSingle(event);
            if (!(connectionObject instanceof PlayerConfigurationConnection connection)) return;

            UUID uuid = connection.getProfile().getId();
            CompletableFuture<Component> future = AWAITING_RESPONSE.get(uuid);
            if (future != null) {
                if (this.message != null) {
                    // If we have a disconnection message, complete the future with it
                    // This will disconnect the player
                    ComponentWrapper disconnectMessage = this.message.getSingle(event);
                    if (disconnectMessage != null) {
                        future.complete(disconnectMessage.getComponent());
                        return;
                    }
                }
                // Otherwise we complete it with null and the player will connect to the server
                future.complete(null);
            }
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d);
        if (this.freeze) {
            builder.append("freeze connection", this.playerConnection);
            if (this.message != null) {
                builder.append("with timeout message", this.message);
            }
        } else {
            builder.append("unfreeze connection", this.playerConnection);
            if (this.message != null) {
                builder.append("and disconnect due to", this.message);
            }
        }
        return builder.toString();
    }

}
