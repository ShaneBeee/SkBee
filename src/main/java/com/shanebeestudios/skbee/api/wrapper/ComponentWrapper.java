package com.shanebeestudios.skbee.api.wrapper;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.SkriptColor;
import ch.njol.skript.util.slot.Slot;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.ChatUtil;
import com.shanebeestudios.skbee.api.util.legacy.TextCompUtil;
import com.shanebeestudios.skbee.api.util.Util;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Wrapper for {@link Component Adventure API Components}
 */
@SuppressWarnings({"PatternValidation", "CallToPrintStackTrace"})
public class ComponentWrapper {

    /**
     * Check if ItemMeta supports 'itemName' ('item_name' component
     */
    public static final boolean HAS_ITEM_NAME = Skript.methodExists(ItemMeta.class, "itemName");

    /**
     * Create an empty component
     *
     * @return Empty component wrapper
     */
    public static ComponentWrapper empty() {
        return new ComponentWrapper(Component.empty());
    }

    /**
     * Create a component from text
     * <p>Will convert '&amp;' and '§' as color codes.</p>
     *
     * @param text Text to add to component
     * @return Component from text
     */
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

    /**
     * Create a component from text
     * <p>This will not convert any colors</p>
     *
     * @param text Text to add to component
     * @return Component from text
     */
    public static ComponentWrapper fromRawText(String text) {
        return new ComponentWrapper(Component.text(text));
    }

    /**
     * Create a {@link MiniMessage mini message} from text
     *
     * @param text      Mini message formatted text
     * @param resolvers TagResolver replacements
     * @return Component from text
     */
    @SuppressWarnings("NullableProblems")
    public static ComponentWrapper fromMiniMessage(@NotNull String text, @Nullable TagResolver... resolvers) {
        // Skript 2.9+ no longer requires double hash, so let's manage that
        String string = text.replaceAll("##(\\w{6})", "#$1");
        // MiniMessage doesn't like these
        if (text.contains("&")) {
            TextComponent deserialize = LegacyComponentSerializer.legacyAmpersand().deserialize(string);
            string = PlainTextComponentSerializer.plainText().serialize(deserialize);
        }
        if (text.contains("§")) {
            TextComponent deserialize = LegacyComponentSerializer.legacySection().deserialize(string);
            string = PlainTextComponentSerializer.plainText().serialize(deserialize);
        }
        if (resolvers == null) {
            return new ComponentWrapper(MiniMessage.miniMessage().deserialize(string));
        }
        return new ComponentWrapper(MiniMessage.miniMessage().deserialize(string, resolvers));
    }

    /**
     * Create a component from a Minecraft keybind
     *
     * @param keybind Keybind to create component from
     * @return Component from keybind
     */
    public static ComponentWrapper fromKeybind(String keybind) {
        return new ComponentWrapper(Component.keybind(keybind));
    }

    /**
     * Create a component from a Minecraft translatable string
     *
     * @param translate String to translate
     * @return Component from translation
     */
    public static ComponentWrapper fromTranslate(String translate) {
        return new ComponentWrapper(Component.translatable(translate));
    }

    /**
     * Create a component from a Minecraft translatable string
     *
     * @param translate String to translate
     * @param objects   Objects to add into translation
     * @return Component from translation
     */
    public static ComponentWrapper fromTranslate(String translate, @Nullable String fallback, Object... objects) {
        List<Component> comps = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof ComponentWrapper component) {
                comps.add(component.component);
            } else if (object instanceof String string) {
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
        return new ComponentWrapper(Component.translatable(translate, fallback, comps));
    }

    /**
     * Deserialize a json string into a component
     *
     * @param json Json string to deserialize
     * @return Component from json string
     */
    public static ComponentWrapper fromJson(String json) {
        Component deserialize = JSONComponentSerializer.json().deserialize(json);
        return new ComponentWrapper(deserialize);
    }

    @Nullable
    private static Component getItem(Object object) {
        ItemStack itemStack = null;
        Material material = null;
        if (object instanceof ItemStack is) {
            itemStack = is;
        } else if (object instanceof ItemType itemType) {
            itemStack = itemType.getRandom();
            if (itemStack != null) material = itemStack.getType();
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
            if (material == null) material = itemStack.getType();
            return Component.translatable(material);
        }
    }

    /**
     * Wrap a component into a component wrapper
     *
     * @param component Component to wrap
     * @return Wrapped component
     */
    public static ComponentWrapper fromComponent(Component component) {
        return new ComponentWrapper(component);
    }

    /**
     * Merge components into new component
     *
     * @param components Components to merge
     * @return Merged components
     */
    public static ComponentWrapper fromComponents(@Nullable ComponentWrapper... components) {
        return fromComponents(components, null);
    }

    /**
     * Merge components into new component
     *
     * @param components Components to merge
     * @param delimiter  Delimiter between components
     * @return Merged components
     */
    public static ComponentWrapper fromComponents(@Nullable ComponentWrapper[] components, @Nullable ComponentWrapper delimiter) {
        Component component = Component.empty();
        if (components != null && components.length > 0) {
            Component delimiterComp = delimiter != null ? delimiter.component : null;
            assert components[0] != null;
            component = component.append(components[0].component);
            int end = components.length;
            for (int i = 1; i < end; ++i) {
                if (components[i] == null) continue;
                if (delimiterComp != null) {
                    component = component.append(delimiterComp);
                }
                //noinspection DataFlowIssue
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

    /**
     * Get base component from this wrapper
     *
     * @return Base component
     */
    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    /**
     * Get the children of a component
     *
     * @return List of children
     */
    public List<ComponentWrapper> getChildren() {
        List<ComponentWrapper> children = new ArrayList<>();
        this.component.children().forEach(child -> children.add(fromComponent(child)));
        return children;
    }

    /**
     * Append onto the end of this component
     *
     * @param componentWrapper Component wrapper to append
     */
    public void append(ComponentWrapper componentWrapper) {
        this.component = this.component.append(componentWrapper.component);
    }

    /**
     * Set the hover event of this component
     *
     * @param hoverEvent HoverEvent to add to this component
     */
    public void setHoverEvent(HoverEvent<?> hoverEvent) {
        this.component = this.component.hoverEvent(hoverEvent);
    }

    /**
     * Get the hover event of this component
     *
     * @return Hover event of this component
     */
    public HoverEvent<?> getHoverEvent() {
        return this.component.hoverEvent();
    }

    /**
     * Set the click event of this component
     *
     * @param clickEvent Click event to set
     */
    public void setClickEvent(ClickEvent clickEvent) {
        this.component = this.component.clickEvent(clickEvent);
    }

    /**
     * Get the click event of this component
     *
     * @return Click event of this component
     */
    public ClickEvent getClickEvent() {
        return this.component.clickEvent();
    }

    /**
     * Set the color of this component
     *
     * @param color Color to set
     */
    public void setColor(Color color) {
        this.component = this.component.color(ChatUtil.getTextColorFromColor(color));
    }

    /**
     * Get the color of this component
     *
     * @return Color of this component
     */
    @Nullable
    public Color getColor() {
        TextColor textColor = this.component.color();
        if (textColor == null) {
            return null;
        }
        SkriptColor skriptColor = ChatUtil.getSkriptColorFromTextColor(textColor);
        if (skriptColor != null) {
            return skriptColor;
        }
        return ColorRGB.fromRGB(textColor.red(), textColor.green(), textColor.blue());
    }

    /**
     * Set the shadow color of this component
     *
     * @param color Shadow color of this component
     */
    public void setShadowColor(Color color) {
        // remove this check and Util class after support is 1.21.4+
        if (!Util.IS_RUNNING_MC_1_21_4) return;
        TextCompUtil.setShadowColor(this, color);
    }

    /**
     * Get the shadow color of this component
     *
     * @return Shadow color of this component
     */
    public Color getShadowColor() {
        if (!Util.IS_RUNNING_MC_1_21_4) return null;
        return TextCompUtil.getShadowColor(this);
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

    @Nullable
    public String getFont() {
        Key font = this.component.font();
        if (font != null) {
            return font.asString();
        } else {
            return null;
        }
    }

    public void setFallback(String fallback) {
        if (this.component instanceof TranslatableComponent translatable) {
            this.component = translatable.fallback(fallback);
        }
    }

    public String getFallback() {
        if (this.component instanceof TranslatableComponent translatable) {
            return translatable.fallback();
        }
        return null;
    }

    public void setInsertion(String insertion) {
        this.component = this.component.insertion(insertion);
    }

    public String getInsertion() {
        return this.component.insertion();
    }

    /**
     * Replaces a string with a string/component
     * @param regex Whether the provided pattern string is treated as regex
     * @param replaceFirst Whether it should only replace the first instance
     * @param caseSensitive Whether the regex should be parsed as case-sensitive
     * @param patterns The string patterns that will be matched against
     * @param replacement The string/component that will replace the provided pattern
     */
    public void replace(boolean regex, boolean replaceFirst, boolean caseSensitive, Object replacement, String ...patterns) {
        TextReplacementConfig.Builder replacementConfig = TextReplacementConfig.builder();
        if (replacement instanceof String string) {
            replacementConfig.replacement(string);
        } else if (replacement instanceof ComponentWrapper componentWrapper) {
            replacementConfig.replacement(componentWrapper.getComponent());
        } else {
            replacementConfig.replacement(Classes.toString(replacement));
        }
        if (replaceFirst)
            replacementConfig.once();
        for (String pattern : patterns) {
            if (regex) {
                try {
                    replacementConfig.match(pattern);
                } catch (PatternSyntaxException exception) {
                    if (SkBee.isDebug()) exception.printStackTrace();
                }
            } else if (!caseSensitive) {
                // Java doesn't use a vararg for pattern flags 🙄
                Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE|Pattern.LITERAL);
                replacementConfig.match(compiledPattern);
            } else {
                replacementConfig.matchLiteral(pattern);
            }
            this.component = this.component.replaceText(replacementConfig.build());
        }
    }

    /**
     * Send a message to an audience
     *
     * @param audience Who is to receive the message
     */
    public void sendMessage(Audience audience) {
        audience.sendMessage(this.component);
    }

    /**
     * Send an action bar
     *
     * @param audience Who to receive
     */
    public void sendActionBar(Audience audience) {
        audience.sendActionBar(this.component);
    }

    /**
     * Broadcast to all players and console
     */
    public void broadcast() {
        sendMessage(Bukkit.getServer());
    }

    /**
     * Set lines of a sign
     *
     * @param block Sign to change
     * @param line  Line to change
     * @param side What side should be modified
     */
    public void setBlockLine(Block block, int line, Side side) {
        if (block.getState() instanceof Sign sign) {
            sign.getSide(side).line(line, this.component);
            sign.update();
        }
    }

    /**
     * Get a component from a sign
     *
     * @param block Sign to get lines from
     * @param line  Line to get
     * @param side Whether front or back of sign
     * @return Component from sign lines
     */
    @Nullable
    public static ComponentWrapper getSignLine(Block block, int line, Side side) {
        if (block.getState() instanceof Sign sign) {
            Component lineComponent = sign.getSide(side).line(line);
            if (!lineComponent.equals(Component.empty())) {
                return ComponentWrapper.fromComponent(lineComponent);
            }
        }
        return null;
    }

    /**
     * Set the name of an entity
     *
     * @param entity  Entity to change name
     * @param display Whether or not always visible
     */
    public void setEntityName(Entity entity, boolean display) {
        if (entity instanceof Player player) {
            player.displayName(this.component);
        } else {
            entity.customName(this.component);
            entity.setCustomNameVisible(display);
        }
    }

    /**
     * Set the name of the inventory
     * <p>NOTE: This is not permanent, this will just rename the open inventory view</p>
     *
     * @param inventory Inventory to change name
     */
    @SuppressWarnings("deprecation")
    public void setInventoryName(Inventory inventory) {
        List<HumanEntity> viewers = inventory.getViewers();
        if (viewers.isEmpty()) return;

        viewers.forEach(player -> {
            InventoryView view = player.getOpenInventory();
            if (view.getTopInventory().getType().isCreatable()) {
                view.setTitle(toString());
            }
        });
    }

    public void setTeamPrefix(Team team) {
        team.prefix(getComponent());
    }

    public void setTeamSuffix(Team team) {
        team.suffix(getComponent());
    }

    /**
     * Send a title to audiences
     *
     * @param audiences Audiences to send to
     * @param title     Title to send
     * @param subtitle  Subtitle to send
     * @param stay      How long to stay for
     * @param fadeIn    How long for fading in
     * @param fadeOut   How long for fading out
     */
    public static void sendTitle(Audience[] audiences, @NotNull Object title, @Nullable Object subtitle, long stay, long fadeIn, long fadeOut) {
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

        for (Audience audience : audiences) {
            audience.showTitle(titleTitle);
        }
    }

    /**
     * Send a sign change to a player
     * <p>This is client side and will not affect the server</p>
     *
     * @param player            Player to send sign change to
     * @param location          Location of the sign
     * @param componentWrappers Components to show on sign
     * @param color             Color to show on sign
     * @param isGlowing         If the sign should glow
     */
    @SuppressWarnings("deprecation")
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ComponentWrapper cw)) return false;
        return cw.toString().equals(this.toString());
    }

    public String toString() {
        if (this.component == null) return "";
        return LegacyComponentSerializer.legacySection().serialize(this.component);
    }

    /**
     * Convert to a serialized json string
     *
     * @return Serialized json string
     */
    public String toJsonString() {
        if (this.component == null) return "{}";
        return JSONComponentSerializer.json().serialize(this.component);
    }

}
