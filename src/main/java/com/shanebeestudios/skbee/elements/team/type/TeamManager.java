package com.shanebeestudios.skbee.elements.team.type;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class TeamManager {

    private static final Scoreboard SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();
    private static final boolean ENTITY_TEAM = Skript.methodExists(Scoreboard.class, "getEntityTeam", Entity.class);
    private static final Pattern UUID_PATTERN = Pattern.compile("(?i)[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");

    public static Team getTeam(String name) {
        // Team names in 1.17.x only support 16 chars
        if (!Skript.isRunningMinecraft(1, 18) && name.length() > 16) {
            name = name.substring(0, 16);
        }
        Team team = SCOREBOARD.getTeam(name);
        if (team == null) {
            team = SCOREBOARD.registerNewTeam(name);
        }
        return team;
    }

    public static Team getTeam(Entity entity) {
        if (ENTITY_TEAM) {
            return SCOREBOARD.getEntityTeam(entity);
        } else if (entity instanceof Player player) {
            return SCOREBOARD.getEntryTeam(player.getName());
        } else {
            return SCOREBOARD.getEntryTeam(entity.getUniqueId().toString());
        }
    }

    public static void unregisterTeam(String name) {
        // Team names in 1.17.x only support 16 chars
        if (!Skript.isRunningMinecraft(1, 18) && name.length() > 16) {
            name = name.substring(0, 16);
        }
        Team team = SCOREBOARD.getTeam(name);
        if (team != null) {
            team.unregister();
        }
    }

    public static List<Team> getTeams() {
        return new ArrayList<>(SCOREBOARD.getTeams());
    }

    public static List<Entity> getEntries(Team team) {
        List<Entity> entities = new ArrayList<>();
        team.getEntries().forEach(entry -> {
            if (UUID_PATTERN.matcher(entry).matches()) {
                Entity ent = Bukkit.getServer().getEntity(UUID.fromString(entry));
                if (ent != null) {
                    entities.add(ent);
                }
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry);
                if (offlinePlayer.isOnline()) {
                    entities.add(offlinePlayer.getPlayer());
                }
            }
        });
        return entities;
    }

}
