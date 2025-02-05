package com.shanebeestudios.skbee.api.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Utilities for {@link Scoreboard Scoreboards}
 */
public class ScoreboardUtils {

    private static final Scoreboard MAIN = Bukkit.getScoreboardManager().getMainScoreboard();

    /**
     * Get the main server scoreboard
     *
     * @return Main scoreboard
     */
    public static Scoreboard getMainScoreboard() {
        return MAIN;
    }

    /**
     * Get a new scoreboard
     *
     * @return New scoreboard
     */
    public static Scoreboard getNewScoreboard() {
        return Bukkit.getScoreboardManager().getNewScoreboard();
    }

}
