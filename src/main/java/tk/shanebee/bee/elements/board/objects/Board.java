package tk.shanebee.bee.elements.board.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.util.Validate;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a team based scoreboard for a player
 * <p>This class also has a map that holds all player scoreboards</p>
 */
public class Board {

    // STATIC STUFF
    private static final Map<Player, Board> BOARD_MAP = new HashMap<>();

    /**
     * Get the scoreboard for a specific player
     *
     * @param player Player to grab scoreboard for
     * @return Scoreboard of player
     */
    public static Board getBoard(Player player) {
        return BOARD_MAP.get(player);
    }

    /**
     * Create a scoreboard for a player
     * <p>Useful in a join event</p>
     *
     * @param player Player to create scoreboard for
     */
    public static void createBoard(Player player) {
        Board board = new Board(player, false);
        BOARD_MAP.put(player, board);
    }

    /**
     * Remove a scoreboard for a player
     * <p>Useful when the player leaves the server</p>
     *
     * @param player Player to remove scoreboard for
     */
    public static void removeBoard(Player player) {
        if (BOARD_MAP.containsKey(player)) {
            BOARD_MAP.get(player).clearBoard();
        }
        BOARD_MAP.remove(player);
    }

    /**
     * Clear and remove all scoreboards
     */
    public static void clearBoards() {
        for (Board board : BOARD_MAP.values()) {
            board.clearBoard();
        }
        BOARD_MAP.clear();
    }

    // OBJECT STUFF
    private final SkBee plugin = SkBee.getPlugin();
    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective board;
    private final Team[] lines = new Team[15];
    private final String[] entries = new String[]{"&1", "&2", "&3", "&4", "&5", "&6", "&7", "&9", "&9", "&0", "&a", "&b", "&c", "&d", "&e"};
    private boolean on;

    public Board(Player player, boolean load) {
        this.player = player;
        this.on = true;
        if (!load) {
            scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
            this.player.setScoreboard(scoreboard);
            board = scoreboard.registerNewObjective("Board", "dummy", "Board");
            board.setDisplaySlot(DisplaySlot.SIDEBAR);
            board.setDisplayName(" ");

            for (int i = 0; i < 15; i++) {
                lines[i] = scoreboard.registerNewTeam("line" + (i + 1));
            }

            for (int i = 0; i < 15; i++) {
                lines[i].addEntry(getColString(entries[i]));
            }
        } else {
            scoreboard = player.getScoreboard();
            board = scoreboard.getObjective("Board");

            for (int i = 0; i < 15; i++) {
                lines[i] = scoreboard.getTeam("line" + (i + 1));
            }
        }
    }

    /**
     * Set the title of this scoreboard
     *
     * @param title Title to set
     */
    public void setTitle(String title) {
        board.setDisplayName(getColString(title));
    }

    /**
     * Set a specific line for this scoreboard
     * <p>Lines 1 - 15</p>
     *
     * @param line Line to set (1 - 15)
     * @param text Text to put in line
     */
    public void setLine(int line, String text) {
        Validate.isBetween(line, 1, 15);
        Team t = lines[line - 1];
        t.setPrefix(getColString(text));
        board.getScore(getColString(entries[line - 1])).setScore(line);
    }

    /**
     * Delete a line in this scoreboard
     * <p>Lines 1 - 15</p>
     *
     * @param line Line to delete (1 - 15)
     */
    public void deleteLine(int line) {
        Validate.isBetween(line, 1, 15);
        scoreboard.resetScores(getColString(entries[line - 1]));
    }

    /**
     * Clear all lines of this scoreboard
     */
    public void clearBoard() {
        for (int i = 1; i < 16; i++) {
            deleteLine(i);
        }
    }

    /**
     * Toggle this scoreboard on or off
     * <p>When off, will not be visible to player, but can still update</p>
     *
     * @param on Whether on or off
     */
    public void toggle(boolean on) {
        if (on) {
            player.setScoreboard(this.scoreboard);
            this.on = true;
        } else {
            player.setScoreboard(this.plugin.getServer().getScoreboardManager().getNewScoreboard());
            this.on = false;
        }
    }

    /**
     * Check if this scoreboard is on or off
     *
     * @return True if on else off
     */
    public boolean isOn() {
        return this.on;
    }

    private String getColString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
