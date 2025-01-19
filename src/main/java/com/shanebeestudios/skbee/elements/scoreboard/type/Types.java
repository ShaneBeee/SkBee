package com.shanebeestudios.skbee.elements.scoreboard.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import ch.njol.yggdrasil.YggdrasilException;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import com.shanebeestudios.skbee.api.scoreboard.ScoreboardUtils;
import com.shanebeestudios.skbee.api.scoreboard.TeamUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Types {

    private static final ScoreboardManager MANAGER = Bukkit.getScoreboardManager();
    private static final List<Criteria> CRITERIAS = new ArrayList<>();

    static {
        Class<?> craftCriteriaClass = ReflectionUtils.getOBCClass("scoreboard.CraftCriteria");
        assert craftCriteriaClass != null;
        Object defaults = ReflectionUtils.getField("DEFAULTS", craftCriteriaClass, null);
        @SuppressWarnings("unchecked") Map<String, Criteria> map = (Map<String, Criteria>) defaults;
        assert map != null;
        CRITERIAS.addAll(map.values());

        if (Classes.getExactClassInfo(Scoreboard.class) == null) {
            Classes.registerClass(new ClassInfo<>(Scoreboard.class, "scoreboard")
                .user("scoreboards?")
                .name("Scoreboard")
                .description("Represents the vanilla scoreboard of the server/players.",
                    "This can be the main server scoreboard, or a custom scoreboard.",
                    "Do note custom scoreboards are not persistent (do not save to the server).",
                    "Multiple players can share a custom scoreboard.")
                .since("INSERT VERSION")
                .defaultExpression(new SimpleLiteral<>(ScoreboardUtils.getMainScoreboard(), true))
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(Scoreboard scoreboard, int flags) {
                        String name;
                        if (scoreboard.equals(MANAGER.getMainScoreboard())) {
                            name = "Main Scoreboard";
                        } else {
                            name = "Custom Scoreboard";
                        }
                        return name;
                    }

                    @Override
                    public String toVariableNameString(Scoreboard scoreboard) {
                        return toString(scoreboard, 0);
                    }
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'scoreboard' already.");
            Util.logLoading("You may have to use their Scoreboards in SkBee's scoreboard elements.");
        }

        if (Classes.getExactClassInfo(Objective.class) == null) {
            Classes.registerClass(new ClassInfo<>(Objective.class, "objective")
                .user("objectives?")
                .name("Scoreboard - Objective")
                .description("Represents an objective in a scoreboard.",
                    "When deleting, the objective will be unregistered.")
                .since("2.6.0")
                .supplier(() -> Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().iterator())
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Objective objective, int flags) {
                        return "objective '" + objective.getName() + "' with criteria '" + objective.getTrackedCriteria().getName() + "'";
                    }

                    @Override
                    public @NotNull String toVariableNameString(Objective objective) {
                        return "objective{name=" + objective.getName() + "}";
                    }
                })
                .changer(new Changer<>() {
                    @Override
                    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) {
                            return CollectionUtils.array(Objective.class);
                        }
                        return null;
                    }

                    @Override
                    public void change(Objective[] what, @Nullable Object[] delta, ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) {
                            for (Objective objective : what) {
                                objective.unregister();
                            }
                        }
                    }
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'objective' already.");
            Util.logLoading("You may have to use their Objectives in SkBee's scoreboard elements.");
        }

        if (Classes.getExactClassInfo(Criteria.class) == null) {
            Classes.registerClass(new ClassInfo<>(Criteria.class, "criteria")
                .user("criterias?")
                .name("Scoreboard - Criteria")
                .description("Represents a criteria for a scoreboard objective.",
                    "See [**Scoreboard Criteria**](https://minecraft.wiki/w/Scoreboard#Criteria) on McWiki for more details.")
                .since("2.6.0")
                .supplier(() -> CRITERIAS.stream().sorted(Comparator.comparing(Criteria::getName)).iterator())
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Criteria criteria, int flags) {
                        return "criteria '" + criteria.getName() + "'";
                    }

                    @Override
                    public @NotNull String toVariableNameString(Criteria o) {
                        return "criteria{name=" + o.getName() + "}";
                    }
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'criteria' already.");
            Util.logLoading("You may have to use their Criterias in SkBee's scoreboard elements.");
        }

        if (Classes.getExactClassInfo(RenderType.class) == null) {
            EnumWrapper<RenderType> RENDER_ENUM = new EnumWrapper<>(RenderType.class);
            Classes.registerClass(RENDER_ENUM.getClassInfo("rendertype")
                .user("render ?types?")
                .name("Scoreboard - Objective Render Type")
                .description("Controls the way in which an Objective is rendered client side.")
                .since("2.6.0"));
        }

        if (Classes.getExactClassInfo(DisplaySlot.class) == null) {
            EnumWrapper<DisplaySlot> DISPLAY_ENUM = new EnumWrapper<>(DisplaySlot.class);
            Classes.registerClass(DISPLAY_ENUM.getClassInfo("displayslot")
                .user("display ?slots?")
                .name("Scoreboard - Objective Display Slot")
                .description("Locations for displaying objectives to the player")
                .since("2.6.0"));
        }

        if (Classes.getExactClassInfo(Team.class) == null) {
            Classes.registerClass(new ClassInfo<>(Team.class, "team")
                .user("teams?")
                .name("Team")
                .description("Represents a scoreboard team. Teams can be deleted (unregistered).",
                    "Players, entities and strings can be added to and removed from teams.",
                    "Teams off the main scoreboard cannot be serialized/saved to variables.")
                .examples("add all players to team of player",
                    "add all players to team named \"a-team\"",
                    "remove all entities from team named \"the-mobs\"",
                    "delete team named \"z-team\"")
                .since("1.16.0, 2.11.0 (add/remove/delete)")
                .parser(new Parser<>() {
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
                    @SuppressWarnings("DataFlowIssue")
                    @Override
                    public @NotNull Fields serialize(Team team) {
                        Fields fields = new Fields();
                        try {
                            if (team == null) {
                                Skript.error("Team is null!");
                            } else if (!team.getScoreboard().equals(ScoreboardUtils.getMainScoreboard())) {
                                Skript.error("Team '" + team.getName() + "' is off the main scoreboard and cannot be serialized!");
                            } else {
                                // If the team was unregistered, this will throw IllegalStateException
                                fields.putObject("name", team.getName());
                            }
                        } catch (IllegalStateException ignore) {
                            Skript.error("Team was unregistered");
                        }
                        return fields;
                    }

                    @Override
                    public void deserialize(Team o, Fields f) {
                    }

                    @Override
                    protected Team deserialize(@NotNull Fields fields) throws StreamCorruptedException {
                        if (!fields.contains("name")) {
                            throw new StreamCorruptedException("Team name field is missing");
                        }
                        String name = fields.getObject("name", String.class);
                        return TeamUtils.getTeam(name, ScoreboardUtils.getMainScoreboard());
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
                    @Override
                    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) return CollectionUtils.array();
                        else if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
                            return CollectionUtils.array(OfflinePlayer[].class, Player[].class, Entity[].class, String[].class);
                        }
                        return null;
                    }

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
        } else {
            Util.logLoading("It looks like another addon registered 'team' already.");
            Util.logLoading("You may have to use their Team in SkBee's scoreboard elements.");
        }

        if (Classes.getExactClassInfo(Team.Option.class) == null) {
            EnumWrapper<Team.Option> TEAM_OPTIONS = new EnumWrapper<>(Team.Option.class);
            Classes.registerClass(TEAM_OPTIONS.getClassInfo("teamoption")
                .user("team ?options?")
                .name("Team - Option")
                .description("Represents an option for a team.")
                .since("1.16.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'teamoption' already.");
            Util.logLoading("You may have to use their Team Option in SkBee's scoreboard elements.");
        }

        if (Classes.getExactClassInfo(Team.OptionStatus.class) == null) {
            EnumWrapper<Team.OptionStatus> TEAM_OPTION_STATUS = new EnumWrapper<>(Team.OptionStatus.class);
            Classes.registerClass(TEAM_OPTION_STATUS.getClassInfo("teamoptionstatus")
                .user("team ?option ?status")
                .name("Team - Option Status")
                .description("Represents an option status for a team option")
                .since("1.16.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'teamoptionstatus' already.");
            Util.logLoading("You may have to use their Team Option Status in SkBee's scoreboard elements.");
        }
    }

}
