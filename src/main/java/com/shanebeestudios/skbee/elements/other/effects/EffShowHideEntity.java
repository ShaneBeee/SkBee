package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffShowHideEntity extends Effect {

    private static final SkBee PLUGIN = SkBee.getPlugin();

    public static void register(Registration reg) {
        reg.newEffect(EffShowHideEntity.class,
                "make %entities% (1:appear|disappear) (for|to|from) %players%")
            .name("Show/Hide Entities")
            .description("Show/hide entities to/from players.",
                "NOTE: This is not persistent across server restarts and/or chunk unloading!")
            .examples("make all entities disappear from player",
                "make all mobs appear to all players",
                "make target entity of player disappear from all players")
            .since("2.10.0")
            .register();
    }

    private Expression<Entity> entities;
    private Expression<Player> players;
    private boolean appear;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) exprs[0];
        this.players = (Expression<Player>) exprs[1];
        this.appear = parseResult.hasTag("1");
        return true;
    }

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
        return "make " + this.entities.toString(e, d) + appear + " for " + this.players.toString(e, d);
    }

}
