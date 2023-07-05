package com.shanebeestudios.skbee.elements.display.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Types {

    public static final String McWIKI = "See <link>https://minecraft.fandom.com/wiki/Display#Entity_data</link> for more details.";
    public static final String McWiki_INTERACTION = "See <link>https://minecraft.fandom.com/wiki/Interaction#Entity_data</link> for more details.";
    public static ClassInfo<Transformation> TRANSFORMATION;
    public static ClassInfo<Quaternionf> QUATERNION;


    // TYPES
    static {
        Classes.registerClass(new ClassInfo<>(Display.Brightness.class, "displaybrightness")
                .user("display ?brightness(es)?")
                .name("DisplayEntity - Display Brightness")
                .description("Represents the brightness attributes of a Display Entity.", McWIKI)
                .since("2.8.0"));

        EnumWrapper<Billboard> BILLBOARD_ENUM = new EnumWrapper<>(Billboard.class);
        Classes.registerClass(new ClassInfo<>(Billboard.class, "displaybillboard")
                .user("display ?billboards?")
                .name("DisplayEntity - Billboard")
                .description("Represents the Billboard of a Display Entity.", McWIKI)
                .usage(BILLBOARD_ENUM.getAllNames())
                .since("2.8.0")
                .parser(BILLBOARD_ENUM.getParser()));

        EnumWrapper<TextAlignment> TEXT_ALIGNMENT_ENUM = new EnumWrapper<>(TextAlignment.class, "", "aligned");
        Classes.registerClass(new ClassInfo<>(TextAlignment.class, "textalignment")
                .user("text ?alignments?")
                .name("DisplayEntity - Text Alignment")
                .description("Represents the text alignment of a Text Display Entity.",
                        "NOTE: While I understand these names do not directly align with Minecraft,",
                        "I had to suffix them to deal with conflict issues.", McWIKI)
                .usage(TEXT_ALIGNMENT_ENUM.getAllNames())
                .since("2.8.0")
                .parser(TEXT_ALIGNMENT_ENUM.getParser()));

        EnumWrapper<ItemDisplayTransform> TRANSFORM_ENUM = new EnumWrapper<>(ItemDisplayTransform.class, "", "transform");
        Classes.registerClass(new ClassInfo<>(ItemDisplayTransform.class, "itemdisplaytransform")
                .user("item ?display ?transforms?")
                .name("DisplayEntity - Item Display Transform")
                .description("Represents the item display transform of an Item Display Entity.", McWIKI)
                .usage(TRANSFORM_ENUM.getAllNames())
                .since("2.8.0")
                .parser(TRANSFORM_ENUM.getParser()));

        TRANSFORMATION = new ClassInfo<>(Transformation.class, "transformation")
                .user("transformations?")
                .name("DisplayEntity - Transformation")
                .description("Represents a transformation of a Display Entity.", McWIKI)
                .since("2.8.0");
        Classes.registerClass(TRANSFORMATION);

        Classes.registerClass(new ClassInfo<>(Color.class, "bukkitcolor")
                .user("bukkit ?colors?")
                .name("Bukkit Color")
                .description("Represents a Bukkit color. This is different than a Skript color",
                        "as it adds an alpha channel.")
                .since("2.8.0")
                .parser(new Parser<>() {

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Color bukkitColor, int flags) {
                        int alpha = bukkitColor.getAlpha();
                        int red = bukkitColor.getRed();
                        int green = bukkitColor.getGreen();
                        int blue = bukkitColor.getBlue();
                        return String.format("BukkitColor(a=%s,r=%s,g=%s,b=%s)", alpha, red, green, blue);
                    }

                    @Override
                    public @NotNull String toVariableNameString(Color bukkitColor) {
                        return toString(bukkitColor, 0);
                    }
                }));

        QUATERNION = new ClassInfo<>(Quaternionf.class, "quaternion")
                .user("quaternions?")
                .name("Quaternion")
                .description("Represents a Quaternion (like a vector but with 4 values).")
                .since("2.8.0")
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
                        return String.format("Quaternion(x=%s, y=%s, z=%s, w=%s)", x, y, z, w);
                    }

                    @Override
                    public @NotNull String toVariableNameString(Quaternionf vec4f) {
                        return toString(vec4f, 0);
                    }
                });
        Classes.registerClass(QUATERNION);
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
                .since("2.8.0"));

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
                .since("2.8.0"));

        Functions.registerFunction(new SimpleJavaFunction<>("vector4", new Parameter[]{
                new Parameter<>("x", DefaultClasses.NUMBER, true, null),
                new Parameter<>("y", DefaultClasses.NUMBER, true, null),
                new Parameter<>("z", DefaultClasses.NUMBER, true, null),
                new Parameter<>("w", DefaultClasses.NUMBER, true, null)
        }, QUATERNION, true) {
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
                .description("Creates a new Vector4(Quaternion).",
                        "Use Quaternion instead, this was just a placeholder! Will be removed in the future!")
                .examples("set {_v} to vector4(1,0,0,0)")
                .since("2.8.0 (DEPRECATED)"));

        Functions.registerFunction(new SimpleJavaFunction<>("quaternion", new Parameter[]{
                new Parameter<>("x", DefaultClasses.NUMBER, true, null),
                new Parameter<>("y", DefaultClasses.NUMBER, true, null),
                new Parameter<>("z", DefaultClasses.NUMBER, true, null),
                new Parameter<>("w", DefaultClasses.NUMBER, true, null)
        }, QUATERNION, true) {
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
                .description("Creates a new Quaternion.")
                .examples("set {_v} to quaternion(1,0,0,0)")
                .since("2.8.1"));

        Functions.registerFunction(new SimpleJavaFunction<>("axisAngle", new Parameter[]{
                new Parameter<>("angle", DefaultClasses.NUMBER, true, null),
                new Parameter<>("x", DefaultClasses.NUMBER, true, null),
                new Parameter<>("y", DefaultClasses.NUMBER, true, null),
                new Parameter<>("z", DefaultClasses.NUMBER, true, null)
        }, QUATERNION, true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public @Nullable Quaternionf[] executeSimple(Object[][] params) {
                float angle = ((Number) params[0][0]).floatValue();
                float x = ((Number) params[1][0]).floatValue();
                float y = ((Number) params[2][0]).floatValue();
                float z = ((Number) params[3][0]).floatValue();
                AxisAngle4f axisAngle4f = new AxisAngle4f(angle, x, y, z);
                return new Quaternionf[]{new Quaternionf(axisAngle4f)};
            }
        }
                .description("Creates a new AxisAngle4f (Will be converted and returned as a Quaternion).",
                        "I have no clue what this is, ask ThatOneWizard!")
                .examples("set {_v} to axisAngle(0.25,0,0,1)")
                .since("2.8.1"));

        Functions.registerFunction(new SimpleJavaFunction<>("axisAngleFromVector", new Parameter[]{
                new Parameter<>("angle", DefaultClasses.NUMBER, true, null),
                new Parameter<>("vector", DefaultClasses.VECTOR, true, null)
        }, QUATERNION, true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public @Nullable Quaternionf[] executeSimple(Object[][] params) {
                float angle = ((Number) params[0][0]).floatValue();
                Vector vector = (Vector) params[1][0];
                AxisAngle4d axisAngle4f = new AxisAngle4d((angle * Math.PI / 180),
                        vector.getX(), vector.getY(), vector.getZ());
                return new Quaternionf[]{new Quaternionf(axisAngle4f)};
            }
        }
                .description("Creates a new AxisAngle4f using a vector and angle (Will be converted and returned as a Quaternion).",
                        "I have no clue what this is, ask ThatOneWizard!")
                .examples("set {_v} to betterAxisAngle(0.25, vector(0,0,1))")
                .since("INSERT VERSION"));

        Functions.registerFunction(new SimpleJavaFunction<>("transformation", new Parameter[]{
                new Parameter<>("translation", DefaultClasses.VECTOR, true, null),
                new Parameter<>("scale", DefaultClasses.VECTOR, true, null),
                new Parameter<>("leftRotation", QUATERNION, true, null),
                new Parameter<>("rightRotation", QUATERNION, true, null)
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
                        "\tset {_lr} to quaternion(1,1,1,1)",
                        "\tset {_rr} to axisAngle(2,2,2,2)",
                        "\tset {_transform} to transformation({_trans}, {_scale}, {_lr}, {_rr})")
                .since("2.8.0"));
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
