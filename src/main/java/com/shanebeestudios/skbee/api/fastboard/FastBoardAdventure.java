package com.shanebeestudios.skbee.api.fastboard;

import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FastBoardAdventure extends FastBoardBase<ComponentWrapper, Component> {

    public FastBoardAdventure(Player player) {
        super(player, new FastBoard(player));
        this.title = ComponentWrapper.empty();
        this.lines = new ComponentWrapper[15];
        this.formats = new ComponentWrapper[15];
    }

    @Override
    public void setTitle(Object title) {
        ComponentWrapper titleComp;
        if (title instanceof ComponentWrapper cw) titleComp = cw;
        else if (title instanceof String s) titleComp = ComponentWrapper.fromText(s);
        else return;

        // Only update if title changes
        if (this.title.equals(titleComp)) return;
        this.title = titleComp;
        if (!visible) return;
        if (this.fastBoard == null) return;
        this.fastBoard.updateTitle(titleComp.getComponent());
    }

    @Override
    public void setLine(int lineNumber, Object line, @Nullable Object lineFormat) {
        if (lineNumber > 15 || lineNumber < 1) return;

        ComponentWrapper lineComp;
        if (line instanceof ComponentWrapper cw) lineComp = cw;
        else if (line instanceof String s) lineComp = ComponentWrapper.fromText(s);
        else return;

        ComponentWrapper formatComp = null;
        if (lineFormat instanceof ComponentWrapper cw) formatComp = cw;
        else if (lineFormat instanceof String s) formatComp = ComponentWrapper.fromText(s);

        ComponentWrapper previousLine = this.lines[REVERSE ? 15 - lineNumber : lineNumber - 1];
        ComponentWrapper previousScore = this.formats[REVERSE ? 15 - lineNumber : lineNumber - 1];
        // If neither the line nor lineFormat have changed, don't send packets
        if (previousLine != null && previousLine.equals(lineComp)) {
            if (previousScore == null && formatComp == null) return;
            if (previousScore != null && previousScore.equals(formatComp)) return;
        }

        this.lines[REVERSE ? 15 - lineNumber : lineNumber - 1] = lineComp;
        this.formats[REVERSE ? 15 - lineNumber : lineNumber - 1] = formatComp;
        if (!visible) return;
        updateLines();
    }

    @Override
    public void clear() {
        this.title = ComponentWrapper.empty();
        this.lines = new ComponentWrapper[15];
        this.formats = new ComponentWrapper[15];
        if (this.fastBoard == null) return;
        this.fastBoard.updateTitle(this.title.getComponent());
        this.fastBoard.updateLines();
    }

    @Override
    public void show() {
        if (this.visible) return;
        this.fastBoard = new FastBoard(this.player);
        this.fastBoard.updateTitle(this.title.getComponent());
        this.visible = true;
        updateLines();
    }

    @Override
    protected void updateLines() {
        List<Component> lines = new ArrayList<>();
        List<Component> formats = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            if (this.lines[i] != null) {
                lines.add(this.lines[i].getComponent());
                formats.add(this.formats[i] != null ? this.formats[i].getComponent() : Component.empty());
            }
        }
        if (this.fastBoard == null) return;
        this.fastBoard.updateLines(lines, formats);
    }

}
