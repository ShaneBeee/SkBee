package com.shanebeestudios.skbee.api.scoreboard;

import com.google.gson.JsonParseException;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.scoreboard.numbers.FixedFormat;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import io.papermc.paper.scoreboard.numbers.StyledFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for Scoreboard stuff
 * <p>Javadocs copied from AdventureAPI</p>
 */
public class NumberFormatUtils {

    /**
     * Creates a scoreboard number format that applies a custom formatting to the score number.
     *
     * @param style the style to apply on the number
     * @return a styled number format
     */
    public static NumberFormat getNumberFormatStyled(Style style) {
        return NumberFormat.styled(style);
    }

    /**
     * Creates a blank scoreboard number format that removes the score number entirely.
     *
     * @return a blank number format
     */
    public static NumberFormat getNumberFormatBlank() {
        return NumberFormat.blank();
    }

    /**
     * Creates a scoreboard number format that replaces the score number with a chat component.
     *
     * @param score the score to replace the number with
     * @return a fixed number format
     */
    public static NumberFormat getNumberFormatFixed(String score) {
        ComponentWrapper comp = ComponentWrapper.fromText(score);
        return NumberFormat.fixed(comp.getComponent());
    }

    @Nullable
    public static NumberFormat getJsonFormat(String score) {
        try {
            Component deserialize = JSONComponentSerializer.json().deserialize(score);
            return NumberFormat.styled(deserialize.style());
        } catch (JsonParseException ig) {
            return null;
        }
    }

    public static String getStringifiedNumberFormat(NumberFormat numberFormat) {
        if (numberFormat instanceof FixedFormat fixedFormat) {
            return ComponentWrapper.fromComponent(fixedFormat.component()).toString();
        } else if (numberFormat instanceof StyledFormat styledFormat) {
            Style style = styledFormat.style();
            TextComponent component = Component.text("", style);
            String s = JSONComponentSerializer.json().serialize(component);
            return s.replace(",\"text\":\"\"", "");
        }
        return "blank";
    }

}
