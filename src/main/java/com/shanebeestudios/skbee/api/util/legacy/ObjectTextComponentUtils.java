package com.shanebeestudios.skbee.api.util.legacy;

import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

// TODO remove after lowest support is 1.21.9+
public class ObjectTextComponentUtils {

    public static ComponentWrapper getSpriteObject(Key atlas, Key sprite) {
        SpriteObjectContents spriteObject = ObjectContents.sprite(atlas, sprite);
        ObjectComponent objectComponent = Component.object(spriteObject);
        return ComponentWrapper.fromComponent(objectComponent);
    }

    public static ComponentWrapper getPlayerHead(Object playerData) {
        PlayerHeadObjectContents playerHeadObject = null;
        if (playerData instanceof String name) {
            playerHeadObject = ObjectContents.playerHead(name);
        } else if (playerData instanceof UUID uuid) {
            playerHeadObject = ObjectContents.playerHead(uuid);
        } else if (playerData instanceof OfflinePlayer offlinePlayer) {
            playerHeadObject = ObjectContents.playerHead(offlinePlayer);
        }
        if (playerHeadObject == null) return null;

        ObjectComponent objectComponent = Component.object(playerHeadObject);
        return ComponentWrapper.fromComponent(objectComponent);
    }
}
