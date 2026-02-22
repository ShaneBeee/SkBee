package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprComponentFormat extends PropertyExpression<ComponentWrapper, Object> {

    private static final int COLOR = 0, BOLD = 1, ITALIC = 2, OBFUSCATED = 3, STRIKETHROUGH = 4,
        UNDERLINE = 5, FONT = 6, INSERT = 7, FALLBACK = 8, SHADOW_COLOR = 9;

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprComponentFormat.class, Object.class,
                "(color|1:bold|2:italic|3:(obfuscate[d]|magic)|4:strikethrough|5:underline[d]|6:font|7:insert[ion]|8:fallback|9:shadow color) format",
                "textcomponents")
            .name("TextComponent - Format")
            .description("Change formatting options of text components. Most of these are pretty straight forward. ",
                "Insertion means the text that will copy to chat when a player shift-clicks the component (Might not be available on all versions). ",
                "Color supports color names as well as RGB color codes via Skript's RGB function (see examples).",
                "Fallback is the fallback text used in a translation component when the client cannot find said translation.")
            .examples("set {_t} to text component from \"my fancy text component\"",
                "set bold format of {_t} to true",
                "set color format of {_t} to aqua",
                "set color format of {_t} to rgb(100, 0, 160)",
                "set shadow color format of {_t} to rgb(100,10,255,150)",
                "set insertion format of {_t} to \"ooooo\"",
                "set fallback format of {_t} to \"Le Fallback\"")
            .since("1.5.1")
            .register();
    }

    private int pattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        setExpr((Expression<ComponentWrapper>) exprs[0]);
        return true;
    }

    @Override
    protected Object @NotNull [] get(@NotNull Event e, ComponentWrapper @NotNull [] source) {
        return get(source, component -> switch (this.pattern) {
            case COLOR -> component.getColor();
            case BOLD -> component.isBold();
            case ITALIC -> component.isItalic();
            case OBFUSCATED -> component.isObfuscated();
            case STRIKETHROUGH -> component.isStrikethrough();
            case UNDERLINE -> component.isUnderlined();
            case FONT -> component.getFont();
            case INSERT -> component.getInsertion();
            case FALLBACK -> component.getFallback();
            case SHADOW_COLOR -> component.getShadowColor();
            default -> null;
        });
    }

    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(getReturnType());
        return null;
    }

    @SuppressWarnings({"ConstantValue"})
    @Override
    public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        Object object = delta != null ? delta[0] : null;
        if (object == null) return;

        switch (this.pattern) {
            case COLOR:
                if (object instanceof Color color) {
                    for (ComponentWrapper component : getExpr().getArray(e)) {
                        component.setColor(color);
                    }
                }
                break;
            case BOLD:
                for (ComponentWrapper component : getExpr().getArray(e)) {
                    boolean bold = !(object instanceof Boolean) || (boolean) object;
                    component.setBold(bold);
                }
                break;
            case ITALIC:
                for (ComponentWrapper component : getExpr().getArray(e)) {
                    boolean italic = !(object instanceof Boolean) || (boolean) object;
                    component.setItalic(italic);
                }
                break;
            case OBFUSCATED:
                for (ComponentWrapper component : getExpr().getArray(e)) {
                    boolean obfuscated = !(object instanceof Boolean) || (boolean) object;
                    component.setObfuscated(obfuscated);
                }
                break;
            case STRIKETHROUGH:
                for (ComponentWrapper component : getExpr().getArray(e)) {
                    boolean strike = !(object instanceof Boolean) || (boolean) object;
                    component.setStrikethrough(strike);
                }
                break;
            case UNDERLINE:
                for (ComponentWrapper component : getExpr().getArray(e)) {
                    boolean underline = !(object instanceof Boolean) || (boolean) object;
                    component.setUnderlined(underline);
                }
                break;
            case FONT:
                String font = object instanceof String ? ((String) object) : object.toString();
                for (ComponentWrapper componentWrapper : getExpr().getArray(e)) {
                    componentWrapper.setFont(font);
                }
            case INSERT:
                String insert = object instanceof String ? ((String) object) : object.toString();
                for (ComponentWrapper component : getExpr().getArray(e)) {
                    component.setInsertion(insert);
                }
                break;
            case FALLBACK:
                String fallback = object instanceof String ? ((String) object) : object.toString();
                for (ComponentWrapper component : getExpr().getArray(e)) {
                    component.setFallback(fallback);
                }
            case SHADOW_COLOR:
                if (object instanceof Color color) {
                    for (ComponentWrapper component : getExpr().getArray(e)) {
                        component.setShadowColor(color);
                    }
                }
        }
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return switch (this.pattern) {
            case COLOR, SHADOW_COLOR -> Color.class;
            case INSERT, FONT, FALLBACK -> String.class;
            default -> Boolean.class;
        };
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String[] type = new String[]{"color", "bold", "italic", "obfuscated", "strikethrough", "underline", "font", "insertion", "fallback", "shadow color"};
        return type[this.pattern] + " format of " + getExpr().toString(e, d);
    }

}
