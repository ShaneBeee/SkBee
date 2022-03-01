package com.shanebeestudios.skbee.elements.board.objects;

import com.shanebeestudios.skbee.api.reflection.ChatReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeeTeams {

    private final Map<String, BeeTeam> TEAMS = new HashMap<>();
    final Map<String, BeeTeam> ENTRIES = new HashMap<>();

    public void registerTeam(String name) {
        if (!TEAMS.containsKey(name)) {
            TEAMS.put(name, new BeeTeam(this, name));
            updateTeams();
        }
    }

    public void unregisterTeam(String name) {
        if (TEAMS.containsKey(name)) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                Board board = Board.getBoard(player);
                if (board != null) {
                    Team team = board.getScoreboard().getTeam(name);
                    if (team != null) {
                        team.unregister();
                    }
                }
            });
            TEAMS.remove(name);
            updateTeams();
        }
    }

    public Collection<BeeTeam> getTeams() {
        return TEAMS.values();
    }

    @Nullable
    public BeeTeam getBeeTeam(String name) {
        if (TEAMS.containsKey(name)) {
            return TEAMS.get(name);
        }
        return null;
    }

    public BeeTeam getBeeTeamByEntry(Entity entity) {
        if (entity instanceof Player) {
            String name = entity.getName();
            if (ENTRIES.containsKey(name)) {
                return ENTRIES.get(name);
            } else {
                String uuid = entity.getUniqueId().toString();
                if (ENTRIES.containsKey(uuid)) {
                    return ENTRIES.get(uuid);
                }
            }
        }
        return null;
    }

    public void updateTeams() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Board board = Board.getBoard(player);
            if (board != null) {
                Scoreboard scoreboard = board.getScoreboard();

                TEAMS.forEach((name, beeTeam) -> {
                    Team team = scoreboard.getTeam(name);
                    if (team == null) {
                        team = scoreboard.registerNewTeam(name);

                    }
                    ChatReflection.setTeamPrefix(team, beeTeam.prefix);
                    ChatReflection.setTeamSuffix(team, beeTeam.suffix);
                    beeTeam.entries.forEach(team::addEntry);
                    if (beeTeam.color != null) {
                        team.setColor(beeTeam.color.asChatColor());
                    }
                    beeTeam.teamOptions.forEach(team::setOption);
                    team.setAllowFriendlyFire(beeTeam.friendlyFire);
                    team.setCanSeeFriendlyInvisibles(beeTeam.friendlyInvisibles);
                });
            }
        });
    }
}
