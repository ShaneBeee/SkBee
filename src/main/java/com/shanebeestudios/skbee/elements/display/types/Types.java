package com.shanebeestudios.skbee.elements.display.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
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
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.TextDisplay.TextAligment;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Types {

    public static final String McWIKI = "See <link>https://minecraft.fandom.com/wiki/Display#Entity_data</link> for more details.";
    public static ClassInfo<Transformation> TRANSFORMATION;
    public static ClassInfo<Quaternionf> VECTOR4;


    // TYPES
    static {
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

        EnumUtils<TextAligment> TEXT_ALIGNMENT_ENUM = new EnumUtils<>(TextAligment.class, "", "aligned");
        Classes.registerClass(new ClassInfo<>(TextAligment.class, "textalignment")
                .user("text ?alignments?")
                .name("DisplayEntity - Text Alignment")
                .description("Represents the text alignment of a Text Display Entity.",
                        "NOTE: While I understand these names do not directly align with Minecraft,",
                        "I had to suffix them to deal with conflict issues.", McWIKI)
                .usage(TEXT_ALIGNMENT_ENUM.getAllNames())
                .since("INSERT VERSION")
                .parser(TEXT_ALIGNMENT_ENUM.getParser()));

        EnumUtils<ItemDisplayTransform> TRANSFORM_ENUM = new EnumUtils<>(ItemDisplayTransform.class, "", "transform");
        Classes.registerClass(new ClassInfo<>(ItemDisplayTransform.class, "itemdisplaytransform")
                .user("item ?display ?transforms?")
                .name("DisplayEntity - Item Display Transform")
                .description("Represents the item display transform of an Item Display Entity.", McWIKI)
                .usage(TRANSFORM_ENUM.getAllNames())
                .since("INSERT VERSION")
                .parser(TRANSFORM_ENUM.getParser()));

        TRANSFORMATION = new ClassInfo<>(Transformation.class, "transformation")
                .user("transformations?")
                .name("DisplayEntity - Transformation")
                .description("Represents a transformation of a Display Entity.", McWIKI)
                .since("INSERT VERSION");
        Classes.registerClass(TRANSFORMATION);

        Classes.registerClass(new ClassInfo<>(Color.class, "bukkitcolor")
                .user("bukkit ?colors?")
                .name("Bukkit Color")
                .description("Represents a Bukkit color. This is different than a Skript color",
                        "as it adds an alpha channel.")
                .since("INSERT VERSION"));

        VECTOR4 = new ClassInfo<>(Quaternionf.class, "vector4")
                .user("vector4s?")
                .name("Vector4")
                .description("Represents a Quaternion (like a vector but with 4 values).")
                .since("INSERT VERSION")
                .parser(new Parser<>() {

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Quaternionf vec4f, int flags) {
                        float x = vec4f.x;
                        float y = vec4f.y;
                        float z = vec4f.z;
                        float w = vec4f.w;
                        return String.format("Vector4f(x=%s, y=%s, z=%s, w=%s)", x,y,z,w);
                    }

                    @Override
                    public @NotNull String toVariableNameString(Quaternionf vec4f) {
                        return toString(vec4f, 0);
                    }
                });
        Classes.registerClass(VECTOR4);
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

        Functions.registerFunction(new SimpleJavaFunction<>("vector4", new Parameter[]{
                new Parameter<>("x", DefaultClasses.NUMBER, true, null),
                new Parameter<>("y", DefaultClasses.NUMBER, true, null),
                new Parameter<>("z", DefaultClasses.NUMBER, true, null),
                new Parameter<>("w", DefaultClasses.NUMBER, true, null)
        }, VECTOR4, true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public @Nullable Quaternionf[] executeSimple(Object[][] params) {
                float x = ((Number) params[0][0]).floatValue();
                float y = ((Number) params[1][0]).floatValue();
                float z = ((Number) params[2][0]).floatValue();
                float w = ((Number) params[3][0]).floatValue();
                return new Quaternionf[]{new Quaternionf(x, y, z, w)};
            }
        }
                .description("Creates a new Vector4(Quaternion).")
                .examples("set {_v} to vector4(1,0,0,0)")
                .since("INSERT VERSION"));

        Functions.registerFunction(new SimpleJavaFunction<>("transformation", new Parameter[]{
                new Parameter<>("translation", DefaultClasses.VECTOR, true, null),
                new Parameter<>("scale", DefaultClasses.VECTOR, true, null),
                new Parameter<>("leftRotation", VECTOR4, true, null),
                new Parameter<>("rightRotation", VECTOR4, true, null)
        }, TRANSFORMATION, true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public Transformation[] executeSimple(Object[][] params) {
                Vector3f translation = converToVector3f((Vector) params[0][0]);
                Vector3f scale = converToVector3f((Vector) params[1][0]);
                Quaternionf leftRotation = (Quaternionf) params[2][0];
                Quaternionf rightRotation = (Quaternionf) params[3][0];
                return new Transformation[]{new Transformation(translation, leftRotation, scale, rightRotation)};
            }
        }
                .description("Creates a new Transformation")
                .examples("on load:",
                        "\tset {_trans} to vector(0,1,0)",
                        "\tset {_scale} to vector(1,1,1)",
                        "\tset {_lr} to vector4(1,1,1,1)",
                        "\tset {_rr} to vector4(2,2,2,2)",
                        "\tset {_transform} to transformation({_trans}, {_scale}, {_lr}, {_rr})")
                .since("INSERT VERSION"));
    }

    public static Vector3f converToVector3f(Vector vector) {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();
        return new Vector3f((float) x, (float) y, (float) z);
    }

    public static Vector converToVector(Vector3f vector3f) {
        float x = vector3f.x;
        float y = vector3f.y;
        float z = vector3f.z;
        return new Vector(x, y, z);
    }

}
