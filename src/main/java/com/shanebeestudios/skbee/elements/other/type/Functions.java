package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Timespan;
import com.github.shanebeee.skr.Registration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Color;
import org.skriptlang.skript.common.function.DefaultFunction;

//@SuppressWarnings({"removal", "deprecation", "rawtypes", "UnstableApiUsage"})
@SuppressWarnings("UnstableApiUsage")
public class Functions {

    public static void register(Registration reg) {

        // FUNCTIONS
        DefaultFunction<Color> bukkitColor = DefaultFunction.builder(reg.getAddon(), "bukkitColor", Color.class)
            .parameter("alpha", Number.class)
            .parameter("red", Number.class)
            .parameter("green", Number.class)
            .parameter("blue", Number.class)
            .build(args -> {
                int alpha = ((Number) args.get("alpha")).intValue();
                int red = ((Number) args.get("red")).intValue();
                int green = ((Number) args.get("green")).intValue();
                int blue = ((Number) args.get("blue")).intValue();

                alpha = Math.clamp(alpha, 0, 255);
                red = Math.clamp(red, 0, 255);
                green = Math.clamp(green, 0, 255);
                blue = Math.clamp(blue, 0, 255);
                return Color.fromARGB(alpha, red, green, blue);
            });

        reg.newFunction(bukkitColor)
            .name("Bukkit Color")
            .description("Creates a new Bukkit Color using alpha (transparency), red, green and blue channels.",
                "Number values must be between 0 and 255.")
            .examples("set {_color} to bukkitColor(50,155,100,10)")
            .since("2.8.0")
            .register();

        if (Classes.getExactClassInfo(Timespan.TimePeriod.class) == null) {
            reg.newEnumType(Timespan.TimePeriod.class, "timespanperiod", true)
                .name("Time Period")
                .user("time ?span ?periods?")
                .description("Represents the time periods of a Timespan.")
                .since("3.9.0")
                .register();
        }

        DefaultFunction<Timespan> timeFunc = DefaultFunction.builder(reg.getAddon(), "timespan", Timespan.class)
            .parameter("time", Number.class)
            .parameter("timePeriod", Timespan.TimePeriod.class)
            .build(args -> {
                Timespan.TimePeriod timePeriod = args.get("timePeriod");
                long millis = (long) (timePeriod.getTime() * ((Number) args.get("time")).floatValue());
                if (millis >= 0) {
                    return new Timespan(Timespan.TimePeriod.MILLISECOND, millis);
                }
                return null;
            });
        reg.newFunction(timeFunc)
            .name("TimeSpan")
            .description("Create a new Timespan.")
            .examples("set {_time} to timespan(1, minute)",
                "set {_time} to timespan(10, minutes)",
                "set {_time} to timespan(3, ticks)",
                "set {_time} to timespan(1, hour) + timespan(10, minutes)")
            .since("3.9.0")
            .register();

        reg.newFunction(DefaultFunction.builder(reg.getAddon(), "formattedTimespan", String.class)
                .parameter("timespan", Timespan.class)
                .parameter("format", String.class)
                .build(args -> {
                    Timespan timespan = args.get("timespan");
                    String format = args.get("format");
                    return DurationFormatUtils.formatDuration(timespan.getAs(Timespan.TimePeriod.MILLISECOND), format);
                }))
            .name("Formatted Timespan")
            .description("Formats a Timespan into a string using a format.",
                "**Available Formats**:",
                " - `y` = years",
                " - `M` = months",
                " - `d` = days",
                " - `H` = hours",
                " - `m` = minutes",
                " - `s` = seconds",
                " - `S` = milliseconds",
                " - `'text'` = arbitrary text content",
                "**Note**: It's not currently possible to include a single-quote in a format.",
                "Token values are printed using decimal digits.",
                "A token character can be repeated to ensure that the field occupies a certain minimum size.",
                "Values will be left-padded with 0 unless padding is disabled in the method invocation.")
            .examples("set {_formatted} to formattedTimespan(1 hour, \"HH:mm:ss\")",
                "set {_formatted} to formattedTimespan(1 hour, \"HH:mm:ss.SSS\")",
                "set {_formatted} to formattedTimespan({_ts}, \"H 'hours and' m 'minutes'\")")
            .since("3.19.0")
            .register();
    }

}
