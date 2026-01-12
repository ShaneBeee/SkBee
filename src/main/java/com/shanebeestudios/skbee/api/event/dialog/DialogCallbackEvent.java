package com.shanebeestudios.skbee.api.event.dialog;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.connection.PlayerConnection;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class DialogCallbackEvent extends Event {

    private final @Nullable NBTCompound nbtCompound;
    private final Audience audience;

    public DialogCallbackEvent(@Nullable NBTCompound nbtCompound, @NotNull Audience audience) {
        this.nbtCompound = nbtCompound;
        this.audience = audience;
    }

    public @Nullable NBTCompound getNbtCompound() {
        return this.nbtCompound;
    }

    public @NotNull Audience getAudience() {
        return this.audience;
    }

    public @Nullable Player getPlayer() {
        if (this.audience instanceof Player player) return player;
        return null;
    }

    public @Nullable String getName() {
        if (this.audience instanceof Player player) return player.getName();
        else if (this.audience instanceof PlayerConfigurationConnection connection) return connection.getProfile().getName();
        return null;
    }

    public @Nullable UUID getUUID() {
        if (this.audience instanceof Player player) return player.getUniqueId();
        else if (this.audience instanceof PlayerConfigurationConnection connection)
            return connection.getProfile().getId();
        return null;
    }

    public @Nullable PlayerConnection getConnection() {
        if (this.audience instanceof PlayerConnection playerConnection) return playerConnection;
        return null;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("This event should never be called!");
    }

}
