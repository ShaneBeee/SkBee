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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Name("Resource Pack Remove")
@Description("Remove all resource packs from a player or remove resource packs from uuids. Requires Minecraft 1.20.4+")
@Examples({"remove all resource packs from player",
        "remove resource pack with uuid {pack::1} from player",
        "remove resource packs with uuids {pack::*} from player"})
@Since("INSERT VERSION")
public class EffResourcePackRemove extends Effect {

    static {
        if (Skript.methodExists(Player.class, "removeResourcePacks")) {
            Skript.registerEffect(EffResourcePackRemove.class,
                    "remove all resource packs from %players%",
                    "remove resource pack[s] with (uuid|id)[s] %strings% from %players%");
        }
    }

    private boolean removeAll;
    private Expression<Player> players;
    private Expression<String> uuids;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.removeAll = matchedPattern == 0;
        if (matchedPattern == 1) {
            this.uuids = (Expression<String>) exprs[0];
            this.players = (Expression<Player>) exprs[1];
        } else {
            this.players = (Expression<Player>) exprs[0];
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        List<UUID> uuids = new ArrayList<>();
        if (!this.removeAll) {
            for (String uuid : this.uuids.getArray(event)) {
                try {
                    UUID u = UUID.fromString(uuid);
                    uuids.add(u);
                } catch (IllegalArgumentException ignore) {

                }
            }
        }

        for (Player player : this.players.getArray(event)) {
            if (this.removeAll) player.removeResourcePacks();
            else uuids.forEach(player::removeResourcePack);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String players = this.players.toString(e, d);
        if (this.removeAll) return "remove all resource packs from " + players;

        String uuids = this.uuids.toString(e, d);
        return "remove resourece pack[s] with uuid[s] " + uuids + " from " + players;
    }

}
