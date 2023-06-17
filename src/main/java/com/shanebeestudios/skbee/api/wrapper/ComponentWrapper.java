package com.shanebeestudios.skbee.api.wrapper;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.SkriptColor;
import ch.njol.skript.util.slot.Slot;
import com.shanebeestudios.skbee.api.util.ChatUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for {@link Component Adventure API Components}
 */
@SuppressWarnings({"PatternValidation"})
public class ComponentWrapper {

    // STATIC
    private static final boolean HAS_SIDES = Skript.classExists("org.bukkit.block.sign.SignSide");

    public static ComponentWrapper empty() {
        return new ComponentWrapper(Component.empty());
    }

    public static ComponentWrapper fromText(String text) {
        Component component;
        if (text.contains("§")) {
            component = LegacyComponentSerializer.legacySection().deserialize(text);
        } else if (text.contains("&")) {
            component = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        } else {
            component = Component.text(text);
        }
        return new ComponentWrapper(component);
    }

    public static ComponentWrapper fromMiniMessage(String text) {
        String string = text;
        // MiniMessage doesn't like these
        if (text.contains("&")) {
            TextComponent deserialize = LegacyComponentSerializer.legacyAmpersand().deserialize(string);
            string = PlainTextComponentSerializer.plainText().serialize(deserialize);
        }
        if (text.contains("§")) {
            TextComponent deserialize = LegacyComponentSerializer.legacySection().deserialize(string);
            string = PlainTextComponentSerializer.plainText().serialize(deserialize);
        }
        return new ComponentWrapper(MiniMessage.miniMessage().deserialize(string));
    }

    public static ComponentWrapper fromKeybind(String keybind) {
        return new ComponentWrapper(Component.keybind(keybind));
    }

    public static ComponentWrapper fromTranslate(String translate) {
        return new ComponentWrapper(Component.translatable(translate));
    }

    public static ComponentWrapper fromTranslate(String translate, Object... objects) {
        List<Component> comps = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof String string) {
                comps.add(Component.text(string));
            } else if (object instanceof Entity entity) {
                comps.add(entity.name());
            } else if (object instanceof ItemType || object instanceof ItemStack || object instanceof Slot) {
                comps.add(getItem(object));
            } else if (object instanceof Translatable translatable) {
                comps.add(Component.translatable(translatable));
            } else {
                String objectString = Classes.toString(object);
                comps.add(Component.text(objectString));
            }
        }
        return new ComponentWrapper(Component.translatable(translate, comps));
    }

    private static Component getItem(Object object) {
        ItemStack itemStack = null;
        if (object instanceof ItemStack is) {
            itemStack = is;
        } else if (object instanceof ItemType itemType) {
            itemStack = itemType.getRandom();
        } else if (object instanceof Slot slot) {
            itemStack = slot.getItem();
        }
        if (itemStack == null) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.hasDisplayName()) {
            return itemMeta.displayName();
        } else {
            return Component.translatable(itemStack);
        }
    }

    public static ComponentWrapper fromComponent(Component component) {
        return new ComponentWrapper(component);
    }

    public static ComponentWrapper fromComponents(@Nullable ComponentWrapper... components) {
        return fromComponents(components, null);
    }

    public static ComponentWrapper fromComponents(@Nullable ComponentWrapper[] components, @Nullable String delimiter) {
        Component component = Component.empty();
        if (components != null && components.length > 0) {
            Component delimiterComp = delimiter != null ? Component.text(delimiter) : null;
            assert components[0] != null;
            component = component.append(components[0].component);
            int end = components.length;
            for (int i = 1; i < end; ++i) {
                if (components[i] == null) continue;
                if (delimiterComp != null) {
                    component = component.append(delimiterComp);
                }
                component = component.append(components[i].component);
            }
        }
        return new ComponentWrapper(component);
    }

    // CLASS
    private Component component;

    public ComponentWrapper(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public void append(ComponentWrapper componentWrapper) {
        this.component = this.component.append(componentWrapper.component);
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

    @SuppressWarnings("deprecation") // Maybe attend to this later?!?!
    public void sendMessage(@Nullable Player sender, Audience receiver) {
        Identity identity = sender != null ? sender.identity() : Identity.nil();
        receiver.sendMessage(identity, this.component);
    }

    public void sendActionBar(CommandSender receiver) {
        receiver.sendActionBar(this.component);
    }

    public void broadcast(@Nullable Player sender) {
        sendMessage(sender, Bukkit.getServer());
    }

    @SuppressWarnings("deprecation") // Remove once we drop 1.19.x support
    public void setBlockLine(Block block, int line, boolean front) {
        if (block.getState() instanceof Sign sign) {
            if (!front && HAS_SIDES) {
                sign.getSide(Side.BACK).line(line, this.component);
            } else {
                sign.line(line, this.component);
            }
            sign.update();
        }
    }

    @SuppressWarnings("deprecation") // Remove once we drop 1.19.x support
    @Nullable
    public static ComponentWrapper getSignLine(Block block, int line, boolean front) {
        if (block.getState() instanceof Sign sign) {
            Component lineComponent;
            if (!front && HAS_SIDES) {
                lineComponent = sign.getSide(Side.BACK).line(line);
            } else {
                lineComponent = sign.line(line);
            }
            return ComponentWrapper.fromComponent(lineComponent);
        }
        return null;
    }

    public void setEntityName(Entity entity, boolean alwaysOn) {
        entity.customName(this.component);
        if (alwaysOn) {
            entity.setCustomNameVisible(true);
        }
    }

    public void setItemName(ItemType itemType) {
        ItemMeta itemMeta = itemType.getItemMeta();
        itemMeta.displayName(this.component);
        itemType.setItemMeta(itemMeta);
    }

    public void setInventoryName(Inventory inventory) {
        if (inventory.getViewers().isEmpty()) return;

        List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());

        InventoryHolder holder = inventory.getHolder();
        InventoryType type = inventory.getType();
        Inventory copy;
        if (type == InventoryType.CHEST) {
            copy = Bukkit.createInventory(holder, inventory.getSize(), this.component);
        } else {
            copy = Bukkit.createInventory(holder, type, this.component);
        }
        copy.setContents(inventory.getContents());
        viewers.forEach(viewer -> viewer.openInventory(copy));
    }

    public void setTeamPrefix(Team team) {
        team.prefix(getComponent());
    }

    public void setTeamSuffix(Team team) {
        team.suffix(getComponent());
    }

    public static void sendTitle(Player[] players, @NotNull Object title, @Nullable Object subtitle, long stay, long fadeIn, long fadeOut) {
        Component titleComponent;
        Component subtitleComponent;
        if (title instanceof ComponentWrapper componentWrapper) {
            titleComponent = componentWrapper.getComponent();
        } else if (title instanceof String string) {
            titleComponent = Component.text(string);
        } else {
            titleComponent = Component.text("");
        }

        if (subtitle instanceof ComponentWrapper componentWrapper) {
            subtitleComponent = componentWrapper.getComponent();
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

    public static void sendSignChange(Player player, Location location, ComponentWrapper[] componentWrappers, @Nullable DyeColor color, boolean isGlowing) {
        List<Component> components = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (componentWrappers.length > i) {
                components.add(componentWrappers[i].component);
            } else {
                components.add(Component.text(""));
            }
        }
        if (color == null) {
            player.sendSignChange(location, components, isGlowing);
        } else {
            player.sendSignChange(location, components, color, isGlowing);
        }
    }

    public String toString() {
        return LegacyComponentSerializer.legacySection().serialize(this.component);
    }

}
