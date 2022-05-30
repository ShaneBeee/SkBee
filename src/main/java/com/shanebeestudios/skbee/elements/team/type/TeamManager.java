package com.shanebeestudios.skbee.elements.team.type;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamManager {

    private static final Scoreboard SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();
    private static final boolean ENTITY_TEAM = Skript.methodExists(Scoreboard.class, "getEntityTeam", Entity.class);

    public static Team getTeam(String name) {
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
            Player player = Bukkit.getPlayer(entry);
            if (player != null) {
                entities.add(player);
            } else {
                Entity ent = Bukkit.getServer().getEntity(UUID.fromString(entry));
                if (ent != null) {
                    entities.add(ent);
                }
            }
        });
        return entities;
    }

}
