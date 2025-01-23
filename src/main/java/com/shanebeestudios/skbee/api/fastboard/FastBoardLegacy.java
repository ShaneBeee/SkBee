package com.shanebeestudios.skbee.api.fastboard;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FastBoardLegacy extends FastBoardBase<String, String> {

    public FastBoardLegacy(Player player) {
        super(player, new FastBoard(player));
        this.title = "";
        this.lines = new String[15];
        this.formats = new String[15];
    }

    @Override
    public void setTitle(Object title) {
        if (!(title instanceof String stringTitle)) return;
        // Only update if title changes
        if (this.title.equals(stringTitle)) return;
        this.title = stringTitle;
        if (!visible) return;
        if (this.fastBoard == null) return;
        this.fastBoard.updateTitle(stringTitle);
    }

    @Override
    public void setLine(int lineNumber, Object line, @Nullable Object lineFormat) {
        if (lineNumber > 15 || lineNumber < 1) return;
        if (!(line instanceof String stringLine)) return;
        String stringFormat = lineFormat instanceof String s ? s : null;

        String previousLine = this.lines[REVERSE ? 15 - lineNumber : lineNumber - 1];
        String previousScore = this.formats[REVERSE ? 15 - lineNumber : lineNumber - 1];
        // If neither the line nor lineFormat have changed, don't send packets
        if (previousLine != null && previousLine.equals(stringLine)) {
            if (previousScore == null && stringFormat == null) return;
            if (previousScore != null && previousScore.equals(stringFormat)) return;
        }

        this.lines[REVERSE ? 15 - lineNumber : lineNumber - 1] = stringLine;
        this.formats[REVERSE ? 15 - lineNumber : lineNumber - 1] = stringFormat;
        if (!visible) return;
        updateLines();
    }

    @Override
    public void clear() {
        this.title = "";
        this.lines = new String[15];
        this.formats = new String[15];
        if (this.fastBoard == null) return;
        this.fastBoard.updateTitle("");
        this.fastBoard.updateLines();
    }

    @Override
    public void show() {
        if (this.visible) return;
        this.fastBoard = new FastBoard(this.player);
        this.fastBoard.updateTitle(this.title);
        this.visible = true;
        updateLines();
    }

    @Override
    protected void updateLines() {
        List<String> lines = new ArrayList<>();
        List<String> formats = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            if (this.lines[i] != null) {
                lines.add(this.lines[i]);
                formats.add(this.formats[i] != null ? this.formats[i] : "");
            }
        }
        if (this.fastBoard == null) return;
        this.fastBoard.updateLines(lines, formats);
    }

}
