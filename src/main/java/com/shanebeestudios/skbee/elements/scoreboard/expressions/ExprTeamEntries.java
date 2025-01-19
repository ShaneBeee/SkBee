package com.shanebeestudios.skbee.elements.scoreboard.expressions;

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
import com.shanebeestudios.skbee.api.scoreboard.TeamUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - Entries")
@Description({"Get the entries of a team. Entries can be entities/players or strings.",
    "\nNOTE: When returning as entities, if the entity isn't currently loaded in a world it wont return.",
    "OfflinePlayers will also not return. Use strings intead.",
    "\nNOTE: When returning as strings this will return the list how Minecraft stores it, player names and entity UUIDs.",
    "\nNOTE: adding/removing to/from team entries is now deprecated. Please directly add/remove to/from the team itself.",
    "See Team type docs for more info!"})
@Examples({"set {_team} to team named \"my-team\"",
    "clear team entries of {_team}",
    "kill team entries of team named \"mob-team\"",
    "set {_entities::*} to team entries of team named \"mobs\"",
    "set {_strings::*} to team entries as strings of team named \"mobs\""})
@Since("1.16.0, 2.10.0 (strings)")
public class ExprTeamEntries extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprTeamEntries.class, Object.class, ExpressionType.PROPERTY,
            "[all] team entries [string:as strings] of %team%",
            "[all] team entries of %team% [string:as strings]",
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
                return TeamUtils.getEntries(team).toArray(new Entity[0]);
            }
        }
        return new Entity[0];
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        switch (mode) {
            case ADD, REMOVE -> {
                // TODO Deprecated 2.11.0
                Skript.warning("You can now add/remove entities/strings to/from teams directly without this expression. " +
                    "ex: 'add player to team named \"a-team\"'");
                return CollectionUtils.array(OfflinePlayer[].class, Player[].class, Entity[].class, String[].class);
            }
            case DELETE -> {
                return CollectionUtils.array();
            }
            default -> {
                return null;
            }
        }
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
                if (object instanceof OfflinePlayer player) {
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
    public @NotNull String toString(Event e, boolean d) {
        String string = this.strings ? " as strings " : " ";
        return "team entries" + string + "of " + this.team.toString(e, d);
    }

}
