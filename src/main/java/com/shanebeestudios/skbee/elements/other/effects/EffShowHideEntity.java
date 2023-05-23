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
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Show/Hide Entities")
@Description({"Show/hide entities to/from players. Requires Minecraft 1.18+",
        "\nNOTE: This is not persistent across server restarts and/or chunk unloading!"})
@Examples({"make all entities disappear from player",
        "make all mobs appear to all players",
        "make target entity of player disappear from all players"})
@Since("INSERT VERSION")
public class EffShowHideEntity extends Effect {

    private static final SkBee PLUGIN = SkBee.getPlugin();

    static {
        if (Skript.methodExists(Player.class, "showEntity", Plugin.class, Entity.class)) {
            Skript.registerEffect(EffShowHideEntity.class,
                    "make %entities% (1:appear|disappear) (for|to|from) %players%");
        }
    }

    private Expression<Entity> entities;
    private Expression<Player> players;
    private boolean appear;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) exprs[0];
        this.players = (Expression<Player>) exprs[1];
        this.appear = parseResult.hasTag("1");
        return true;
    }

    @SuppressWarnings({"NullableProblems", "UnstableApiUsage"})
    @Override
    protected void execute(Event event) {
        for (Player player : this.players.getArray(event)) {
            for (Entity entity : this.entities.getArray(event)) {
                if (this.appear) {
                    player.showEntity(PLUGIN, entity);
                } else {
                    player.hideEntity(PLUGIN, entity);
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String appear = this.appear ? " appear" : " disappear";
        return "make " + this.entities.toString(e,d) + appear + " for " + this.players.toString(e,d);
    }

}
