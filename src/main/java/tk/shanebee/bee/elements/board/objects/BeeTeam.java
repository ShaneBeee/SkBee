package tk.shanebee.bee.elements.board.objects;

import ch.njol.skript.util.SkriptColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BeeTeam {

    private final BeeTeams beeTeams;
    final String name;
    String prefix;
    String suffix;
    SkriptColor color;
    boolean friendlyFire = true;
    boolean friendlyInvisibles = true;
    List<String> entries = new ArrayList<>();
    Map<Team.Option, Team.OptionStatus> teamOptions = new HashMap<>();

    public BeeTeam(BeeTeams beeTeams, String name) {
        this.beeTeams = beeTeams;
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
        beeTeams.updateTeams();
    }

    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix;
        beeTeams.updateTeams();
    }

    public SkriptColor getColor() {
        return color;
    }

    public void setColor(SkriptColor color) {
        this.color = color;
        beeTeams.updateTeams();
    }

    public void addEntry(Entity entity) {
        if (entity instanceof Player) {
            String name = entity.getName();
            entries.add(name);
            beeTeams.ENTRIES.put(name, this);
        } else {
            String uuid = entity.getUniqueId().toString();
            entries.add(uuid);
            beeTeams.ENTRIES.put(name, this);
        }
        beeTeams.updateTeams();
    }

    public void removeEntry(Entity entity) {
        if (entity instanceof Player) {
            String name = entity.getName();
            entries.remove(name);
            beeTeams.ENTRIES.remove(name);
        } else {
            String uuid = entity.getUniqueId().toString();
            entries.remove(uuid);
            beeTeams.ENTRIES.remove(name);
        }
        beeTeams.updateTeams();
    }

    public void setTeamOption(@NotNull Team.Option option, @Nullable Team.OptionStatus status) {
        if (status == null) {
            teamOptions.remove(option);
        } else {
            teamOptions.put(option, status);
        }
        beeTeams.updateTeams();
    }

    @Nullable
    public Team.OptionStatus getTeamOption(@NotNull Team.Option option) {
        return teamOptions.get(option);
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
        beeTeams.updateTeams();
    }

    public boolean isFriendlyInvisibles() {
        return friendlyInvisibles;
    }

    public void setFriendlyInvisibles(boolean friendlyInvisibles) {
        this.friendlyInvisibles = friendlyInvisibles;
        beeTeams.updateTeams();
    }

    public List<Entity> getEntries() {
        List<Entity> entities = new ArrayList<>();
        entries.forEach(entry -> {
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

    @Override
    public String toString() {
        return "Team{name='" + name + '\'' + '}';
    }

}
