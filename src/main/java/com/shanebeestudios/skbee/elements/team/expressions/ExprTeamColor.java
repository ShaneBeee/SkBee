package com.shanebeestudios.skbee.elements.team.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
@Name("Team - Name")
@Description("Get/set the color of a team.")
@Examples("set team color of {_team} to blue")
@Since("1.16.0")
public class ExprTeamColor extends SimplePropertyExpression<Team, SkriptColor> {

    static {
        register(ExprTeamColor.class, SkriptColor.class, "[[sk]bee] team color", "teams");
    }

    @Override
    public @Nullable SkriptColor convert(Team team) {
        return ChatUtil.getSkriptColorByBungee(team.getColor().asBungee());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, RESET -> CollectionUtils.array(Color.class);
            default -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Object object = delta[0];
        SkriptColor color = object instanceof SkriptColor ? (SkriptColor) object : null;

        for (Team team : getExpr().getArray(event)) {
            if (mode == ChangeMode.SET && color != null) {
                team.setColor(color.asChatColor());
            } else {
                team.setColor(ChatColor.WHITE);
            }
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
