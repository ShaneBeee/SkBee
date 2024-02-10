package com.shanebeestudios.skbee.elements.team.type;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(Team.class, "team")
                .user("teams?")
                .name("Team")
                .description("Represents a scoreboard team. Teams can be delete (unregistered).",
                        "Players, entities and strings can be added to and removed from teams.")
                .examples("add all players to team of player",
                        "add all players to team named \"a-team\"",
                        "remove all entities from team named \"the-mobs\"",
                        "delete team named \"z-team\"")
                .since("1.16.0, 2.11.0 (add/remove/delete)")
                .parser(new Parser<>() {
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Team team, int flags) {
                        return "Team[" + team.getName() + "]";
                    }

                    @Override
                    public @NotNull String toVariableNameString(Team team) {
                        return toString(team, 0);
                    }
                })
                .serializer(new Serializer<>() {
                    @Override
                    public @NotNull Fields serialize(Team team) {
                        Fields fields = new Fields();
                        try {
                            // If the team was unregistered, this will throw IllegalStateException
                            fields.putObject("name", team.getName());
                        } catch (IllegalStateException ignore) {
                        }
                        return fields;
                    }

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public void deserialize(Team o, Fields f) {
                    }

                    @Override
                    protected Team deserialize(@NotNull Fields fields) throws StreamCorruptedException {
                        String name = fields.getObject("name", String.class);
                        return TeamManager.getTeam(name);
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return true;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                }).changer(new Changer<>() {
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) return CollectionUtils.array();
                        else if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
                            return CollectionUtils.array(OfflinePlayer[].class, Player[].class, Entity[].class, String[].class);
                        }
                        return null;
                    }

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public void change(Team[] teams, @Nullable Object[] delta, ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) {
                            for (Team team : teams) {
                                team.unregister();
                            }
                        } else {
                            List<String> names = new ArrayList<>();
                            for (Object object : delta) {
                                if (object instanceof OfflinePlayer player) {
                                    names.add(player.getName());
                                } else if (object instanceof Entity entity) {
                                    names.add(entity.getUniqueId().toString());
                                } else if (object instanceof String string) {
                                    names.add(string);
                                }
                            }
                            if (mode == ChangeMode.ADD) {
                                for (Team team : teams) {
                                    names.forEach(team::addEntry);
                                }
                            } else if (mode == ChangeMode.REMOVE) {
                                for (Team team : teams) {
                                    names.forEach(team::removeEntry);
                                }
                            }
                        }
                    }
                }));

        if (Classes.getExactClassInfo(Team.Option.class) == null) {
            EnumWrapper<Team.Option> TEAM_OPTIONS = new EnumWrapper<>(Team.Option.class);
            Classes.registerClass(TEAM_OPTIONS.getClassInfo("teamoption")
                    .user("team ?options?")
                    .name("Team - Option")
                    .description("Represents an option for a team.")
                    .since("1.16.0"));
        }

        if (Classes.getExactClassInfo(Team.OptionStatus.class) == null) {
            EnumWrapper<Team.OptionStatus> TEAM_OPTION_STATUS = new EnumWrapper<>(Team.OptionStatus.class);
            Classes.registerClass(TEAM_OPTION_STATUS.getClassInfo("teamoptionstatus")
                    .user("team ?option ?status")
                    .name("Team - Option Status")
                    .description("Represents an option status for a team option")
                    .since("1.16.0"));
        }
    }

}
