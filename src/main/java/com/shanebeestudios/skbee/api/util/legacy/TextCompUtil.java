package com.shanebeestudios.skbee.api.util.legacy;

import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.format.ShadowColor;

/**
 * Util class for TextComponents
 */
public class TextCompUtil {

    /**
     * Set the shadow color of this component
     * (Requires Paper 1.21.4+)
     *
     * @param color Shadow color of this component
     */
    public static void setShadowColor(ComponentWrapper component, Color color) {
        ShadowColor shadowColor = ShadowColor.shadowColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        component.setComponent(component.getComponent().shadowColor(shadowColor));
    }

    /**
     * Get the shadow color of this component
     * (Requires Paper 1.21.4+)
     *
     * @return Shadow color of this component
     */
    public static Color getShadowColor(ComponentWrapper component) {
        ShadowColor shadowColor = component.getComponent().shadowColor();
        if (shadowColor == null) {
            return null;
        }
        return ColorRGB.fromRGB(shadowColor.red(), shadowColor.green(), shadowColor.blue());
    }

}
