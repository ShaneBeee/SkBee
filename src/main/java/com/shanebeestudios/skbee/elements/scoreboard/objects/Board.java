package com.shanebeestudios.skbee.elements.scoreboard.objects;

import com.shanebeestudios.skbee.SkBee;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    private static final SkBee PLUGIN = SkBee.getPlugin();

    private final FastBoard fastBoard;
    private String title = "";
    private String[] lines;
    private boolean visible = true;

    public Board(Player player) {
        this.fastBoard = new FastBoard(player);
        this.lines = new String[15];
    }

    public void setTitle(String title) {
        // Only update if title changes
        if (this.title.equals(title)) return;
        this.title = title;
        if (!visible) return;
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
        updateLines();
    }

    public void clear() {
        this.title = "";
        this.lines = new String[15];
        this.fastBoard.updateTitle("");
        this.fastBoard.updateLines();
    }

    public void hide() {
        this.fastBoard.updateTitle("");
        this.fastBoard.updateLines();
        this.visible = false;
    }

    public void show() {
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
        this.fastBoard.delete();
    }

    private void updateLines() {
        List<String> lines = new ArrayList<>();
        for (String line : this.lines) {
            if (line != null) {
                lines.add(line);
            }
        }
        SCHEDULER.runTaskAsynchronously(PLUGIN, () -> fastBoard.updateLines(lines));
    }

}
