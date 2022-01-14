package com.shanebeestudios.skbee.elements.board.listener;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.elements.board.objects.BeeTeams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.shanebeestudios.skbee.elements.board.objects.Board;

public class PlayerBoardListener implements Listener {

    private final BeeTeams BEE_TEAMS;

    public PlayerBoardListener(SkBee plugin) {
        BEE_TEAMS = plugin.getBeeTeams();
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Board.createBoard(player);
        BEE_TEAMS.updateTeams();
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Board.removeBoard(player);
    }

}
