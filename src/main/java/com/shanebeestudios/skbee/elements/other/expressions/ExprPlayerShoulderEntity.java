package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprPlayerShoulderEntity extends SimplePropertyExpression<Player, Entity> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprPlayerShoulderEntity.class, Entity.class,
                "(left|:right) shoulder entity", "players")
            .name("Player - Shoulder Entity")
            .description("Gets the entity currently perched on the shoulder of a player or null if no entity.",
                "The returned entity will not be spawned within the world, " +
                    "so most operations are invalid unless the entity is first spawned in.",
                "When setting, this will remove the entity from the world.",
                "Note that only a copy of the entity will be set to display on the shoulder.",
                "Also note that the client will currently only render Parrot entities.",
                "Deleting will remove the entity from the player's shoulder.",
                "Resetting will release the entity from the player's shoulder.")
            .examples("if right shoulder entity of player is set:",
                "set left should entity of player to {_parrot}",
                "delete right shoulder entity of player")
            .since("INSERT VERSION")
            .register();
    }

    public boolean right;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.right = parseResult.hasTag("right");
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable Entity convert(Player player) {
        return this.right ? player.getShoulderEntityRight() : player.getShoulderEntityLeft();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
            return CollectionUtils.array(Entity.class);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        Entity entity = delta != null && delta[0] instanceof Entity e ? e : null;

        for (Player player : getExpr().getArray(event)) {
            if (mode == ChangeMode.RESET) {
                if (this.right) player.releaseRightShoulderEntity();
                else player.releaseLeftShoulderEntity();
            } else {
                if (this.right) player.setShoulderEntityRight(entity);
                else player.setShoulderEntityLeft(entity);
            }
        }
    }

    @Override
    protected String getPropertyName() {
        String shoulder = this.right ? "right" : "left";
        return shoulder + " shoulder entity";
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

}
