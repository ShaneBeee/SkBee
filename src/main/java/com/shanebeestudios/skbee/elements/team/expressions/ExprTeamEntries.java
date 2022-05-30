package com.shanebeestudios.skbee.elements.team.expressions;

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
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.team.type.TeamManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Team - Entries")
@Description("Add/remove entries to/from a team. Entries can be entities or players.")
@Examples("add all players to team entries of {_team}")
@Since("1.16.0")
public class ExprTeamEntries extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprTeamEntries.class, Entity.class, ExpressionType.PROPERTY,
                "[[sk]bee] team entries of %team%",
                "%team%'[s] [[sk]bee] team entries");
    }

    private Expression<Team> team;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.team = (Expression<Team>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Entity[] get(Event event) {
        Team team = this.team.getSingle(event);
        if (team != null) {
            return TeamManager.getEntries(team).toArray(new Entity[0]);
        }
        return new Entity[0];
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE -> CollectionUtils.array(Entity[].class);
            default -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Team team = this.team.getSingle(event);
        if (team != null) {
            for (Object object : delta) {
                String entry = null;
                if (object instanceof Player player) {
                    entry = player.getName();
                } else if (object instanceof Entity entity) {
                    entry = entity.getUniqueId().toString();
                }
                if (entry == null) continue;

                if (mode == ChangeMode.ADD) {
                    team.addEntry(entry);
                } else {
                    team.removeEntry(entry);
                }
            }
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "team entries of " + this.team.toString(e, d);
    }

}
