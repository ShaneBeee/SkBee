package com.shanebeestudios.skbee.elements.scoreboard.objects;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final Player player;
    private FastBoard fastBoard;
    private String title = "";
    private String[] lines;
    private boolean visible = true;

    public Board(Player player) {
        this.player = player;
        this.fastBoard = new FastBoard(player);
        this.lines = new String[15];
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

    public void setLine(int line, String value) {
        if (line > 15 || line < 1) return;

        // Only update if line changes
        String l = this.lines[15 - line];
        if (l != null && l.equals(value)) return;

        this.lines[15 - line] = value;
        if (!visible) return;
        updateLines();
    }

    public String getLine(int line) {
        return this.lines[15 - line];
    }

    public void deleteLine(int line) {
        setLine(line, null);
    }

    public void clear() {
        this.title = "";
        this.lines = new String[15];
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
        for (String line : this.lines) {
            if (line != null) {
                lines.add(line);
            }
        }
        if (this.fastBoard == null) return;
        fastBoard.updateLines(lines);
    }

}
