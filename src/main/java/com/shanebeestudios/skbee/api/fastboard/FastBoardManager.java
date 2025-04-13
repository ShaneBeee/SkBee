package com.shanebeestudios.skbee.api.fastboard;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.region.TaskUtils;
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

    private static boolean HAS_ADVENTURE;
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

    public FastBoardManager(SkBee plugin, boolean isTextComponentEnabled) {
        HAS_ADVENTURE = isTextComponentEnabled;
        FastBoardBase.init(plugin);
        Bukkit.getOnlinePlayers().forEach(FastBoardManager::getBoard);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        TaskUtils.getEntityScheduler(event.getPlayer()).runTaskLater(() -> removeBoard(uuid), 1);
    }

}
