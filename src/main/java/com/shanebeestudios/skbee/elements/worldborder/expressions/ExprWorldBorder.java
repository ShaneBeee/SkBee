package com.shanebeestudios.skbee.elements.worldborder.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("WorldBorder")
@Description({"Represents the world border of a player/world, or create a virtual world border.",
        "World border of a world can not be changed.",
        "World border of a player can be set to a virtual world border.",
        "Resetting border of player will set back to the world border of the world the player is in.",
        "If you would like to reset default values of a border, you will have to do it in a var (see examples).",
        "Multiple players can share a virtual world border.",
        "NOTE: Virtual world borders do not seem to be persistent (ie: restarts, quitting, death, world change).",
        "NOTE: Virtual world borders were added in MC 1.18.x+"})
@Examples({"set world border of player to virtual world border",
        "set center of world border of player to location of player",
        "set size of world border of player to 100",
        "reset world border of player #will remove the player's virtual border",
        "set {_w} to world border of player",
        "reset {_w} #will reset default values of the border"})
@Since("1.17.0")
public class ExprWorldBorder extends SimpleExpression<WorldBorder> {

    private static final boolean SUPPORTS_VIRTUAL_BORDER = Skript.methodExists(Player.class, "getWorldBorder");

    static {
        Skript.registerExpression(ExprWorldBorder.class, WorldBorder.class, ExpressionType.SIMPLE,
                "world border of %players/worlds%",
                "[new] virtual world border");
    }

    private int pattern;
    private Expression<Object> object;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        if (i == 1 && !SUPPORTS_VIRTUAL_BORDER) {
            Skript.error("Virtual world borders are not supported on your version.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.pattern = i;
        this.object = i == 0 ? (Expression<Object>) exprs[0] : null;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable WorldBorder[] get(Event event) {
        List<WorldBorder> borders = new ArrayList<>();

        if (pattern == 1 && SUPPORTS_VIRTUAL_BORDER) {
            return new WorldBorder[]{Bukkit.createWorldBorder()};
        }
        for (Object object : this.object.getArray(event)) {
            if (object instanceof Player player && SUPPORTS_VIRTUAL_BORDER) {
                borders.add(player.getWorldBorder());
            } else if (object instanceof World world) {
                borders.add(world.getWorldBorder());
            }
        }
        return borders.toArray(new WorldBorder[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (pattern == 0 && !SUPPORTS_VIRTUAL_BORDER) {
            Skript.error("Virtual world borders are not supported on your version therefor a border cannot be changed", ErrorQuality.SEMANTIC_ERROR);
            return null;
        }
        if (pattern == 0 && (mode == ChangeMode.SET || mode == ChangeMode.RESET)) {
            return CollectionUtils.array(WorldBorder.class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        WorldBorder border = delta != null ? ((WorldBorder) delta[0]) : null;
        for (Object object : this.object.getArray(event)) {
            if (object instanceof Player player) {
                if (mode == ChangeMode.SET) {
                    if (border != null && border.getWorld() != null && border.getWorld() != player.getWorld()) {
                        // This means the border belongs to a world the player is not in
                        // Virtual borders will not have an attached world
                        continue;
                    }
                    player.setWorldBorder(border);
                } else {
                    // Assigns the border of the world the player is in
                    player.setWorldBorder(null);
                }
            } else if (object instanceof World world && mode == ChangeMode.RESET) {
                world.getWorldBorder().reset();
            }
        }
    }

    @Override
    public boolean isSingle() {
        if (pattern == 0) {
            return this.object.isSingle();
        }
        return true;
    }

    @Override
    public @NotNull Class<? extends WorldBorder> getReturnType() {
        return WorldBorder.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (pattern == 0) {
            return "world border of " + this.object.toString(e, d);
        }
        return "virtual world border";
    }

}
