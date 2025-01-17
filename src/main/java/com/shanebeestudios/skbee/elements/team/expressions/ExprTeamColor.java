package com.shanebeestudios.skbee.elements.team.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@SuppressWarnings("deprecation")
@Name("Team - Color")
@Description("Get/set the color of a team.")
@Examples({"set team color of {_team} to blue",
    "set team color of team of player to red"})
@Since("1.16.0")
public class ExprTeamColor extends SimplePropertyExpression<Team, SkriptColor> {

    static {
        register(ExprTeamColor.class, SkriptColor.class, "team color", "teams");
    }

    @Override
    public @Nullable SkriptColor convert(Team team) {
        return ChatUtil.getSkriptColorByBungee(team.getColor().asBungee());
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, RESET -> CollectionUtils.array(Color.class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        ChatColor chatColor;
        if (delta != null && delta[0] instanceof SkriptColor skriptColor) {
            chatColor = skriptColor.asChatColor();
        } else {
            error("Invalid color " + Arrays.toString(delta));
            return;
        }

        Team[] teams = getExpr().getArray(event);
        if (teams == null || teams.length == 0) {
            error("Invalid team: " + getExpr().toString(event, true));
            return;
        }
        for (Team team : teams) {
            team.setColor(chatColor);
        }
    }

    @Override
    public @NotNull Class<? extends SkriptColor> getReturnType() {
        return SkriptColor.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "team color";
    }

}
