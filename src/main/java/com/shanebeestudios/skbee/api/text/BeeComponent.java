package com.shanebeestudios.skbee.api.text;

import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.SkriptColor;
import com.shanebeestudios.skbee.api.util.ChatUtil;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

@SuppressWarnings("PatternValidation")
public class BeeComponent {

    // STATIC
    public static BeeComponent empty() {
        return new BeeComponent(Component.empty());
    }

    public static BeeComponent fromText(String text) {
        return new BeeComponent(Component.text(text));
    }

    public static BeeComponent fromMiniMessage(String text) {
        return new BeeComponent(MiniMessage.miniMessage().deserialize(text));
    }

    public static BeeComponent fromKeybind(String keybind) {
        return new BeeComponent(Component.keybind(keybind));
    }

    public static BeeComponent fromTranslate(String translate) {
        return new BeeComponent(Component.translatable(translate));
    }

    public static BeeComponent fromTranslate(String translate, Object... objects) {
        // TODO figure out objects
        return new BeeComponent(Component.translatable(translate));
    }

    public static BeeComponent fromComponent(Component component) {
        return new BeeComponent(component);
    }

    public static BeeComponent fromComponents(@Nullable BeeComponent... components) {
        Component component = Component.empty();
        for (BeeComponent beeComponent : components) {
            if (beeComponent == null) continue;
            component = component.append(beeComponent.component);
        }
        return new BeeComponent(component);
    }

    // CLASS
    private Component component;

    public BeeComponent(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public void append(BeeComponent beeComponent) {
        this.component = this.component.append(beeComponent.component);
    }

    public void setHoverEvent(HoverEvent<?> hoverEvent) {
        this.component = this.component.hoverEvent(hoverEvent);
    }

    public HoverEvent<?> getHoverEvent() {
        return this.component.hoverEvent();
    }

    public void setClickEvent(ClickEvent clickEvent) {
        this.component = this.component.clickEvent(clickEvent);
    }

    public ClickEvent getClickEvent() {
        return this.component.clickEvent();
    }

    public void setColor(Color color) {
        this.component = this.component.color(ChatUtil.getTextColorFromColor(color));
    }

    public Color getColor() {
        TextColor textColor = this.component.color();
        if (textColor == null) {
            return null;
        }
        SkriptColor skriptColor = ChatUtil.getSkriptColorFromTextColor(textColor);
        if (skriptColor != null) {
            return skriptColor;
        }
        return new ColorRGB(textColor.red(), textColor.green(), textColor.blue());
    }

    public void setBold(boolean bold) {
        this.component = this.component.decoration(TextDecoration.BOLD, bold);
    }

    public boolean isBold() {
        return this.component.decoration(TextDecoration.BOLD).toString().equals("true");
    }

    public void setItalic(boolean italic) {
        this.component = this.component.decoration(TextDecoration.ITALIC, italic);
    }

    public boolean isItalic() {
        return this.component.decoration(TextDecoration.ITALIC).toString().equals("true");
    }

    public void setObfuscated(boolean obfuscated) {
        this.component = this.component.decoration(TextDecoration.OBFUSCATED, obfuscated);
    }

    public boolean isObfuscated() {
        return this.component.decoration(TextDecoration.OBFUSCATED).toString().equals("true");
    }

    public void setStrikethrough(boolean strikethrough) {
        this.component = this.component.decoration(TextDecoration.STRIKETHROUGH, strikethrough);
    }

    public boolean isStrikethrough() {
        return this.component.decoration(TextDecoration.STRIKETHROUGH).toString().equals("true");
    }

    public void setUnderlined(boolean underlined) {
        this.component = this.component.decoration(TextDecoration.UNDERLINED, underlined);
    }

    public boolean isUnderlined() {
        return this.component.decoration(TextDecoration.UNDERLINED).toString().equals("true");
    }

    public void setFont(String font) {
        this.component = this.component.font(Key.key(font));
    }

    public String getFont() {
        Key font = this.component.font();
        if (font != null) {
            return font.asString();
        } else {
            return null;
        }
    }

    public void setInsertion(String insertion) {
        this.component = this.component.insertion(insertion);
    }

    public String getInsertion() {
        return this.component.insertion();
    }

    public void sendMessage(@Nullable Player sender, CommandSender receiver) {
        if (sender != null) {
            receiver.sendMessage(Identity.identity(sender.getUniqueId()), this.component);
        } else {
            receiver.sendMessage(this.component);
        }
    }

    public void sendActionBar(CommandSender receiver) {
        receiver.sendActionBar(this.component);
    }

    public void broadcast(Player sender) {
        Bukkit.getConsoleSender().sendMessage(this.component);
        Bukkit.getOnlinePlayers().forEach(player ->
                player.sendMessage(Identity.identity(sender.getUniqueId()), this.component));
    }

    public void setBlockLine(Block block, int line) {
        if (block.getState() instanceof Sign sign) {
            sign.line(line, this.component);
            sign.update();
        }
    }

    @Nullable
    public static BeeComponent getSignLine(Block block, int line) {
        if (block.getState() instanceof Sign sign) {
            return BeeComponent.fromComponent(sign.line(line));
        }
        return null;
    }

    public void setEntityName(Entity entity, boolean alwaysOn) {
        entity.customName(this.component);
        if (alwaysOn) {
            entity.setCustomNameVisible(true);
        }
    }

    public static void sendTitle(Player[] players, @NotNull Object title, @Nullable Object subtitle, long stay, long fadeIn, long fadeOut) {
        Component titleComponent;
        Component subtitleComponent;
        if (title instanceof BeeComponent beeComponent) {
            titleComponent = beeComponent.getComponent();
        } else if (title instanceof String string) {
            titleComponent = Component.text(string);
        } else {
            titleComponent = Component.text("");
        }

        if (subtitle instanceof BeeComponent beeComponent) {
            subtitleComponent = beeComponent.getComponent();
        } else if (subtitle instanceof String string) {
            subtitleComponent = Component.text(string);
        } else {
            subtitleComponent = Component.text("");
        }

        Times times = Times.times(Duration.ofMillis(fadeIn * 50), Duration.ofMillis(stay * 50),
                Duration.ofMillis(fadeOut * 50));

        Title titleTitle = Title.title(titleComponent, subtitleComponent, times);

        for (Player player : players) {
            player.showTitle(titleTitle);
        }
    }

    public String toString() {
        return LegacyComponentSerializer.builder().build().serialize(this.component);
    }

}
