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
@Description({"Add/remove entries to/from a team. Entries can be entities/players or strings.",
        "\nNOTE: `as strings` is useless when adding/remove. When returning, this will return the",
        "list how Minecraft stores it, player names and entity UUIDs."})
@Examples({"set {_team} to team named \"my-team\"",
        "add all players to team entries of {_team}",
        "add player to team entries of team of target entity",
        "kill team entries of team named \"mob-team\"",
        "add \"Batman\" to team entries of team named \"superheros\"",
        "set {_entities::*} to team entries of team named \"mobs\"",
        "set {_strings::*} to team entries as strings of team named \"mobs\""})
@Since("1.16.0, INSERT VERSION (strings)")
public class ExprTeamEntries extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprTeamEntries.class, Object.class, ExpressionType.PROPERTY,
                "team entries [string:as strings] of %team%",
                "%team%'[s] team entries [string:as strings]");
    }

    private Expression<Team> team;
    private boolean strings;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.team = (Expression<Team>) exprs[0];
        this.strings = parseResult.hasTag("string");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object[] get(Event event) {
        Team team = this.team.getSingle(event);
        if (team != null) {
            if (this.strings) {
                return team.getEntries().toArray(new String[0]);
            } else {
                return TeamManager.getEntries(team).toArray(new Entity[0]);
            }
        }
        return new Entity[0];
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case DELETE -> CollectionUtils.array();
            case ADD, REMOVE -> CollectionUtils.array(Entity[].class, String[].class);
            default -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Team team = this.team.getSingle(event);
        if (team != null) {
            if (mode == ChangeMode.DELETE) {
                team.getEntries().forEach(team::removeEntry);
                return;
            }
            for (Object object : delta) {
                String entry = null;
                if (object instanceof Player player) {
                    entry = player.getName();
                } else if (object instanceof Entity entity) {
                    entry = entity.getUniqueId().toString();
                } else if (object instanceof String string) {
                    entry = string;
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
    public @NotNull Class<?> getReturnType() {
        return this.strings ? String.class : Entity.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String string = this.strings ? " as strings " : " ";
        return "team entries" + string + "of " + this.team.toString(e, d);
    }

}
