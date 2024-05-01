package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Transfer - Transfer Player")
@Description({"Transfer players to another server, requires Minecraft 1.20.5+.",
    "NOTE: `accepts-transfers` needs to be enabled in `server.properties`",
    "NOTE: If port is omitted, will default to `25565`."})
@Examples({"transfer all players to \"my.server.com\" on port 25566",
    "transfer player to \"1.1.1.1\" on port 25567",
    "transfer player to \"localhost\""})
@Since("3.5.0")
public class EffTransferPlayer extends Effect {

    static {
        if (Skript.methodExists(Player.class, "isTransferred")) {
            Skript.registerEffect(EffTransferPlayer.class,
                "transfer %players% to [server] %string% [(on|with) port %-number%]");
        }
    }

    private Expression<Player> players;
    private Expression<String> host;
    private Expression<Number> port;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.players = (Expression<Player>) exprs[0];
        this.host = (Expression<String>) exprs[1];
        this.port = (Expression<Number>) exprs[2];
        return true;
    }


    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        String host = this.host.getSingle(event);
        if (host == null) return;

        int port = 25565;
        if (this.port != null) {
            Number portNum = this.port.getSingle(event);
            if (portNum != null) port = portNum.intValue();
        }
        for (Player player : this.players.getArray(event)) {
            player.transfer(host, port);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String port = this.port != null ? (" on port " + this.port.toString(e, d)) : "";
        return "transfer " + this.players.toString(e, d) + " to " + this.host.toString(e, d) + port;
    }

}
