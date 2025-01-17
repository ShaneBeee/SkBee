package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Name("Resource Pack Send")
@Description({"Send a resource pack to a player with a UUID, optional hash, optional prompt message, and the option to force it.",
    "The uuid is used to later remove the pack from the player if need be. Requires Minecraft 1.20.4+"})
@Examples({"set {pack::1} to random uuid",
    "send resource pack from url \"some.url\" with uuid {pack::1} to all players",
    "send resource pack from url \"some.url\" with uuid {pack::1} to all players with force",
    "send resource pack from url \"some.url\" with uuid {pack::1} with prompt \"This Adds Cool Stuff!!!\" to player with force"})
@Since("3.4.0")
public class EffResourcePackSend extends Effect {

    static {
        if (Skript.methodExists(Player.class, "removeResourcePacks")) {
            Skript.registerEffect(EffResourcePackSend.class,
                "send [the] resource pack [from [[the] URL]] %string% with (uuid|id) %string% " +
                    "[with hash %-string%] [with prompt %-string%] to %players% [force:with force]");
        }
    }

    private Expression<String> url;
    private Expression<String> uuid;
    private Expression<String> hash;
    private Expression<String> prompt;
    private Expression<Player> players;
    private boolean force;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.url = (Expression<String>) exprs[0];
        this.uuid = (Expression<String>) exprs[1];
        this.hash = (Expression<String>) exprs[2];
        this.prompt = (Expression<String>) exprs[3];
        this.players = (Expression<Player>) exprs[4];
        this.force = parseResult.hasTag("force");
        return true;
    }

    @Override
    protected void execute(Event event) {
        String url = this.url.getSingle(event);
        String hash = this.hash != null ? this.hash.getSingle(event) : null;
        String uuidString = this.uuid.getSingle(event);
        String prompt = this.prompt != null ? this.prompt.getSingle(event) : null;

        if (url == null) {
            error("Missing URL: " + this.url.toString(event, true));
            return;
        }
        if (uuidString == null) {
            error("Missing UUID: " + this.uuid.toString(event, true));
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException ex) {
            error("Invalid UUID '" + uuidString + "' // " + ex.getMessage());
            return;
        }

        byte[] hashBytes;
        if (hash != null) {
            hashBytes = StringUtils.hexStringToByteArray(hash);
        } else {
            hashBytes = null;
        }
        for (Player player : this.players.getArray(event)) {
            player.addResourcePack(uuid, url, hashBytes, prompt, this.force);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String url = this.url.toString(e, d);
        String hash = this.hash != null ? (" with hash " + this.hash.toString(e, d)) : "";
        String players = this.players.toString(e, d);
        String uuid = this.uuid.toString(e, d);
        String prompt = this.prompt != null ? (" with prompt " + this.prompt.toString(e, d)) : "";
        String force = this.force ? " with force" : "";
        return String.format("send resource pack from url %s with uuid %s %s %s to %s %s",
            url, uuid, hash, prompt, players, force);
    }

}
