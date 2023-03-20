package com.shanebeestudios.skbee.elements.display.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAligment;
import org.jetbrains.annotations.Nullable;

public class Types {

    public static final String McWIKI = "See <link>https://minecraft.fandom.com/wiki/Display#Entity_data</link> for more details.";

    // TYPES
    static {
        Classes.registerClass(new ClassInfo<>(Display.class, "displayentity")
                .user("display ?entit(y|ies)")
                .name("DisplayEntity - Display Entity")
                .description("Represents a Minecraft Display Entity.",
                        "See <link>https://minecraft.fandom.com/wiki/Display</link> for more details.")
                .since("INSERT VERSION"));

        Classes.registerClass(new ClassInfo<>(Display.Brightness.class, "displaybrightness")
                .user("display ?brightness(es)?")
                .name("DisplayEntity - Display Brightness")
                .description("Represents the brightness attributes of a Display Entity.", McWIKI)
                .since("INSERT VERSION"));

        EnumUtils<Billboard> BILLBOARD_ENUM = new EnumUtils<>(Billboard.class);
        Classes.registerClass(new ClassInfo<>(Billboard.class, "displaybillboard")
                .user("display ?billboards?")
                .name("DisplayEntity - Billboard")
                .description("Represents the Billboard of a Display Entity.", McWIKI)
                .usage(BILLBOARD_ENUM.getAllNames())
                .since("INSERT VERSION")
                .parser(BILLBOARD_ENUM.getParser()));

        EnumUtils<TextAligment> TEXT_ALIGNMENT_ENUM = new EnumUtils<>(TextAligment.class, "aligned", "");
        Classes.registerClass(new ClassInfo<>(TextAligment.class, "textalignment")
                .user("text ?alignments?")
                .name("DisplayEntity - Text Alignment")
                .description("Represents the text alignment of a Text Display Entity.",
                        "NOTE: While I understand these names do not directly align with Minecraft,",
                        "I had to prefix them to deal with conflict issues.", McWIKI)
                .usage(TEXT_ALIGNMENT_ENUM.getAllNames())
                .since("INSERT VERSION")
                .parser(TEXT_ALIGNMENT_ENUM.getParser()));

        Classes.registerClass(new ClassInfo<>(Color.class, "bukkitcolor")
                .user("bukkit ?colors?")
                .name("Bukkit Color")
                .description("Represents a Bukkit color. This is different than a Skript color",
                        "as it adds an alpha channel.")
                .since("INSERT VERSION"));
    }

    // FUNCTIONS
    static {
        //noinspection DataFlowIssue
        Functions.registerFunction(new SimpleJavaFunction<>("displayBrightness", new Parameter[]{
                new Parameter<>("blockLight", DefaultClasses.NUMBER, true, null),
                new Parameter<>("skyLight", DefaultClasses.NUMBER, true, null)

        }, Classes.getExactClassInfo(Display.Brightness.class), true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public Display.@Nullable Brightness[] executeSimple(Object[][] params) {
                if (params[0].length == 0 || params[1].length == 0) {
                    return null;
                }
                int block = ((Number) params[0][0]).intValue();
                int sky = ((Number) params[1][0]).intValue();
                block = MathUtil.clamp(block, 0, 15);
                sky = MathUtil.clamp(sky, 0, 15);
                return new Display.Brightness[]{new Display.Brightness(block, sky)};
            }
        }
                .description("Creates a new display brightness object for use on a Display Entity.",
                        "Number values must be between 0 and 15.", McWIKI)
                .examples("set {_db} to displayBrightness(10,10)")
                .since("INSERT VERSION"));

        //noinspection DataFlowIssue
        Functions.registerFunction(new SimpleJavaFunction<>("bukkitColor", new Parameter[]{
                new Parameter<>("alpha", DefaultClasses.NUMBER, true, null),
                new Parameter<>("red", DefaultClasses.NUMBER, true, null),
                new Parameter<>("green", DefaultClasses.NUMBER, true, null),
                new Parameter<>("blue", DefaultClasses.NUMBER, true, null)
        }, Classes.getExactClassInfo(Color.class), true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public @Nullable Color[] executeSimple(Object[][] params) {
                int alpha = ((Number) params[0][0]).intValue();
                int red = ((Number) params[1][0]).intValue();
                int green = ((Number) params[2][0]).intValue();
                int blue = ((Number) params[3][0]).intValue();
                alpha = MathUtil.clamp(alpha, 0, 255);
                red = MathUtil.clamp(red, 0, 255);
                green = MathUtil.clamp(green, 0, 255);
                blue = MathUtil.clamp(blue, 0, 255);
                return new Color[]{Color.fromARGB(alpha, red, green, blue)};
            }
        }
                .description("Creates a new Bukkit Color using alpha, red, green and blue channels.",
                        "Number values must be between 0 and 255.")
                .examples("set {_color} to bukkitColor(50,155,100,10)")
                .since("INSERT VERSION"));
    }

}
