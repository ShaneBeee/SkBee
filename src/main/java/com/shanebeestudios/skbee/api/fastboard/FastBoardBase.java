package com.shanebeestudios.skbee.api.fastboard;

import com.shanebeestudios.skbee.SkBee;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FastBoardBase<T, B> {

    protected static final boolean REVERSE = SkBee.getPlugin().getPluginConfig().SETTINGS_FASTBOARD_LINES;

    protected final Player player;
    protected fr.mrmicky.fastboard.FastBoardBase<B> fastBoard;
    protected boolean visible = true;
    protected T title;
    protected T[] lines;
    protected T[] formats;

    public FastBoardBase(Player player, fr.mrmicky.fastboard.FastBoardBase<B> fastBoard) {
        this.player = player;
        this.fastBoard = fastBoard;
    }

    public abstract void setTitle(Object title);

    @NotNull
    public T getTitle() {
        return this.title;
    }

    public abstract void setLine(int lineNumber, Object line, @Nullable Object lineFormat);

    public @Nullable T getLine(int lineNumber) {
        if (lineNumber > 15 || lineNumber < 1) return null;
        return this.lines[REVERSE ? 15 - lineNumber : lineNumber - 1];
    }

    public void deleteLine(int line) {
        setLine(line, null, null);
    }

    public abstract void clear();

    public void hide() {
        this.visible = false;
        if (this.fastBoard == null) return;
        this.fastBoard.delete();
        this.fastBoard = null;
    }

    public abstract void show();

    public void toggle() {
        if (this.visible) {
            hide();
        } else {
            show();
        }
    }

    public boolean isOn() {
        return this.visible;
    }

    public void deleteFastboard() {
        if (this.fastBoard == null) return;
        this.fastBoard.delete();
    }

    protected abstract void updateLines();

}
