package com.shanebeestudios.skbee.elements.scoreboard.objects;

import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoardManager implements Listener {

    private static final Map<UUID, Board> BOARDS = new HashMap<>();
    private static final SkBee PLUGIN = SkBee.getPlugin();

    @Nullable
    public static Board getBoard(Player player) {
        if (!player.isOnline()) return null;
        UUID uuid = player.getUniqueId();
        if (BOARDS.containsKey(uuid)) {
            return BOARDS.get(uuid);
        }
        Board board = new Board(player);
        BOARDS.put(uuid, board);
        return board;
    }

    public static void removeBoard(UUID uuid) {
        if (BOARDS.containsKey(uuid)) {
            BOARDS.get(uuid).deleteFastboard();
            BOARDS.remove(uuid);
        }
    }

    public static void reload() {
        Bukkit.getOnlinePlayers().forEach(BoardManager::getBoard);
    }

    public BoardManager() {
        reload();
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskLater(PLUGIN, () -> removeBoard(uuid), 0);
    }

}
