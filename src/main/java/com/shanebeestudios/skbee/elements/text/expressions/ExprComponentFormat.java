package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.ChatUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
@Name("Text Component - Format")
@Description({"Change formatting options of text components. Most of these are pretty straight forward. Insertion means the text ",
        "that will copy to chat when a player shift-clicks the component (Might not be available on all versions). Color supports color ",
        "names as well as RGB color codes via Skript's RGB function (RGB = Minecraft 1.16+) (see examples)."})
@Examples({"set {_t} to text component from \"my fancy text component\"",
        "set bold format of {_t} to true",
        "set color format of {_t} to aqua",
        "set color format of {_t} to rgb(100, 0, 160)",
        "set insertion format of {_t} to \"ooooo\""})
@Since("1.5.1")
public class ExprComponentFormat extends PropertyExpression<BaseComponent, Object> {

    private static final boolean HAS_SKRIPT_COLOR = Skript.classExists("ch.njol.skript.util.SkriptColor");
    private static final int COLOR = 0, BOLD = 1, ITALIC = 2, OBFUSCATED = 3, STRIKETHROUGH = 4, UNDERLINE = 5, FONT = 6, INSERT = 7;

    static {
        if (Skript.methodExists(BaseComponent.class, "setInsertion", String.class)) {
            register(ExprComponentFormat.class, Object.class,
                    "(color|1¦bold|2¦italic|3¦(obfuscate[d]|magic)|4¦strikethrough|5¦underline[d]|6¦font|7¦insert[ion]) format",
                    "basecomponents");
        } else {
            register(ExprComponentFormat.class, Object.class,
                    "(color|1¦bold|2¦italic|3¦(obfuscate[d]|magic)|4¦strikethrough|5¦underline[d]|6¦font) format",
                    "basecomponents");
        }
    }

    private int pattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        pattern = parseResult.mark;
        setExpr((Expression<BaseComponent>) exprs[0]);
        return true;
    }

    @Override
    protected Object @NotNull [] get(@NotNull Event e, BaseComponent @NotNull [] source) {
        return get(source, component -> {
            switch (pattern) {
                case COLOR:
                    SkriptColor skriptColor = ChatUtil.getSkriptColorByBungee(component.getColor());
                    if (skriptColor != null) {
                        return skriptColor;
                    } else {
                        return ChatUtil.getColorRGBFromBungee(component.getColor());
                    }
                case BOLD:
                    return component.isBold();
                case ITALIC:
                    return component.isItalic();
                case OBFUSCATED:
                    return component.isObfuscated();
                case STRIKETHROUGH:
                    return component.isStrikethrough();
                case UNDERLINE:
                    return component.isUnderlined();
                case FONT:
                    return component.getFont();
                case INSERT:
                    return component.getInsertion();
            }
            return null;
        });
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Object.class);
        return null;
    }

    @Override
    public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        Object object = delta != null ? delta[0] : null;
        if (object == null) return;

        switch (pattern) {
            case COLOR:
                Color color = (Color) object;
                ChatColor chatColor;
                if (HAS_SKRIPT_COLOR && color instanceof SkriptColor skriptColor) {
                    chatColor = ChatUtil.getBungeeFromSkriptColor(skriptColor);
                } else {
                    chatColor = ChatUtil.getBungeeFromColor(color);
                }
                for (BaseComponent component : getExpr().getArray(e)) {
                    component.setColor(chatColor);
                }
                break;
            case BOLD:
                for (BaseComponent component : getExpr().getArray(e)) {
                    boolean bold = !(object instanceof Boolean) || (boolean) object;
                    component.setBold(bold);
                }
                break;
            case ITALIC:
                for (BaseComponent component : getExpr().getArray(e)) {
                    boolean italic = !(object instanceof Boolean) || (boolean) object;
                    component.setItalic(italic);
                }
                break;
            case OBFUSCATED:
                for (BaseComponent component : getExpr().getArray(e)) {
                    boolean obfuscated = !(object instanceof Boolean) || (boolean) object;
                    component.setObfuscated(obfuscated);
                }
                break;
            case STRIKETHROUGH:
                for (BaseComponent component : getExpr().getArray(e)) {
                    boolean strike = !(object instanceof Boolean) || (boolean) object;
                    component.setStrikethrough(strike);
                }
                break;
            case UNDERLINE:
                for (BaseComponent component : getExpr().getArray(e)) {
                    boolean underline = !(object instanceof Boolean) || (boolean) object;
                    component.setUnderlined(underline);
                }
                break;
            case FONT:
                String font = object instanceof String ? ((String) object) : object.toString();
                for (BaseComponent baseComponent : getExpr().getArray(e)) {
                    baseComponent.setFont(font);
                }
            case INSERT:
                String insert = object instanceof String ? ((String) object) : object.toString();
                for (BaseComponent component : getExpr().getArray(e)) {
                    component.setInsertion(insert);
                }
                break;
        }
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return switch (pattern) {
            case COLOR -> Color.class;
            case INSERT, FONT -> String.class;
            default -> Boolean.class;
        };
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String[] type = new String[]{"color", "bold", "italic", "obfuscated", "strikethrough", "underline", "font", "insertion"};
        return type[pattern] + " format of " + getExpr().toString(e, d);
    }

}
