package com.shanebeestudios.skbee.api.scoreboard;

import com.shanebeestudios.skbee.SkBee;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FastBoardWrapper {

    private static final boolean REVERSE = SkBee.getPlugin().getPluginConfig().SETTINGS_SCOREBOARD_LINES;

    private final Player player;
    private FastBoard fastBoard;
    private String title = "";
    private String[] lines;
    private String[] scores;
    private boolean visible = true;

    public FastBoardWrapper(Player player) {
        this.player = player;
        this.fastBoard = new FastBoard(player);
        this.lines = new String[15];
        this.scores = new String[15];
    }

    public void setTitle(String title) {
        // Only update if title changes
        if (this.title.equals(title)) return;
        this.title = title;
        if (!visible) return;
        if (this.fastBoard == null) return;
        this.fastBoard.updateTitle(title);
    }

    public String getTitle() {
        return this.title;
    }

    public void setLine(int lineNumber, String line, @Nullable String score) {
        if (lineNumber > 15 || lineNumber < 1) return;

        String previousLine = this.lines[REVERSE ? 15 - lineNumber : lineNumber - 1];
        String previousScore = this.scores[REVERSE ? 15 - lineNumber : lineNumber - 1];
        // If neither the line nor score have changed, don't send packets
        if (previousLine != null && previousLine.equals(line)) {
            if (previousScore == null && score == null) return;
            if (previousScore != null && previousScore.equals(score)) return;
        }

        this.lines[REVERSE ? 15 - lineNumber : lineNumber - 1] = line;
        this.scores[REVERSE ? 15 - lineNumber : lineNumber - 1] = score;
        if (!visible) return;
        updateLines();
    }

    public @Nullable String getLine(int lineNumber) {
        if (lineNumber > 15 || lineNumber < 1) return null;
        return this.lines[REVERSE ? 15 - lineNumber : lineNumber - 1];
    }

    public void deleteLine(int line) {
        setLine(line, null, null);
    }

    public void clear() {
        this.title = "";
        this.lines = new String[15];
        this.scores = new String[15];
        if (this.fastBoard == null) return;
        this.fastBoard.updateTitle("");
        this.fastBoard.updateLines();
    }

    public void hide() {
        this.visible = false;
        if (this.fastBoard == null) return;
        this.fastBoard.delete();
        this.fastBoard = null;
    }

    public void show() {
        if (this.visible) return;
        this.fastBoard = new FastBoard(this.player);
        this.fastBoard.updateTitle(this.title);
        this.visible = true;
        updateLines();
    }

    public void toggle() {
        if (visible) {
            hide();
        } else {
            show();
        }
    }

    public boolean isOn() {
        return visible;
    }

    public void deleteFastboard() {
        if (this.fastBoard == null) return;
        this.fastBoard.delete();
    }

    private void updateLines() {
        List<String> lines = new ArrayList<>();
        List<String> scores = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            if (this.lines[i] != null) {
                lines.add(this.lines[i]);
                scores.add(this.scores[i] != null ? this.scores[i] : "");
            }
        }
        if (this.fastBoard == null) return;
        this.fastBoard.updateLines(lines, scores);
    }

}
