package com.shanebeestudios.skbee.api.fastboard;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.region.scheduler.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastBoardManager implements Listener {

    public static final boolean HAS_ADVENTURE = SkBee.getPlugin().getAddonLoader().isTextComponentEnabled();

    private static final Map<UUID, FastBoardBase<?, ?>> BOARDS = new HashMap<>();

    @Nullable
    public static FastBoardBase<?, ?> getBoard(Player player) {
        if (!player.isOnline()) return null;
        UUID uuid = player.getUniqueId();
        if (BOARDS.containsKey(uuid)) {
            return BOARDS.get(uuid);
        }
        FastBoardBase<?, ?> board = HAS_ADVENTURE ? new FastBoardAdventure(player) : new FastBoardLegacy(player);
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
        Bukkit.getOnlinePlayers().forEach(FastBoardManager::getBoard);
    }

    public FastBoardManager() {
        reload();
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        TaskUtils.getEntityScheduler(event.getPlayer()).runTaskLater(() -> removeBoard(uuid), 1);
    }

}
