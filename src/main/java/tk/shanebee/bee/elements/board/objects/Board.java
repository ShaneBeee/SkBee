package tk.shanebee.bee.elements.board.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tk.shanebee.bee.SkBee;

import java.util.HashMap;
import java.util.Map;

public class Board {

    // STATIC STUFF
    private static final Map<Player, Board> BOARD_MAP = new HashMap<>();

    public static Board getBoard(Player player) {
        return BOARD_MAP.get(player);
    }

    public static void createBoard(Player player) {
        Board board = new Board(player, false);
        BOARD_MAP.put(player, board);
    }

    public static void removeBoard(Player player) {
        BOARD_MAP.remove(player);
    }

    public static void clearBoards() {
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

            for (int i = 0; i <15; i++) {
                lines[i] = scoreboard.registerNewTeam("line" + (i + 1));
            }

            for (int i = 0; i <15; i++) {
                lines[i].addEntry(getColString(entries[i]));
            }
        } else {
            scoreboard = player.getScoreboard();
            board = scoreboard.getObjective("Board");

            for (int i = 0; i <15; i++) {
                lines[i] = scoreboard.getTeam("line" + (i + 1));
            }
        }
    }

    public void setTitle(String title) {
        board.setDisplayName(getColString(title));
    }

    public void setLine(int line, String text) {
        Team t = lines[line - 1];
        t.setPrefix(getColString(text));
        board.getScore(getColString(entries[line - 1])).setScore(line);
    }

    public void deleteLine(int line) {
        scoreboard.resetScores(getColString(entries[line - 1]));
    }

    public void clearBoard() {
        for (int i = 1; i < 16; i++) {
            deleteLine(i);
        }
    }

    public void toggle(boolean on) {
        if (on) {
            player.setScoreboard(this.scoreboard);
            this.on = true;
        } else {
            player.setScoreboard(this.plugin.getServer().getScoreboardManager().getNewScoreboard());
            this.on = false;
        }
    }

    public boolean isOn() {
        return this.on;
    }

    private String getColString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
