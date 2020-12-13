package tk.shanebee.bee.elements.other.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.JavaFunction;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.api.util.ParticleUtil;
import tk.shanebee.bee.api.util.Util;

public class Types {

    static {
        if (Skript.isRunningMinecraft(1, 13)) {
            // Only register if no other addons have registered this class
            if (Classes.getExactClassInfo(Particle.class) == null) {
                Classes.registerClass(new ClassInfo<>(Particle.class, "particle")
                        .user("particles?")
                        .name("Particle")
                        .description("Represents a particle which can be used in the 'Particle Spawn' effect. Some particles require extra data,",
                                "these are distinguished by their data type within the square brackets.")
                        .usage(ParticleUtil.getNamesAsString())
                        .examples("play 1 of soul at location of player",
                                "play 10 of dust using dustOption(green, 10) at location of player",
                                "play 3 of item using player's tool at location of player",
                                "play 1 of block using dirt at location of player")
                        .since("1.9.0")
                        .parser(new Parser<Particle>() {

                            @Nullable
                            @Override
                            public Particle parse(String s, ParseContext context) {
                                return ParticleUtil.parse(s.replace(" ", "_"));
                            }

                            @Override
                            public String toString(Particle particle, int flags) {
                                return ParticleUtil.getName(particle);
                            }

                            @Override
                            public String toVariableNameString(Particle particle) {
                                return "particle:" + toString(particle, 0);
                            }

                            @Override
                            public String getVariableNamePattern() {
                                return "particle://s";
                            }
                        }));
            } else {
                Util.log("It looks like another addon registered 'particle' already. ");
                Util.log("You may have to use their particles in SkBee's 'particle spawn' effect.");
            }

            Classes.registerClass(new ClassInfo<>(DustOptions.class, "dustoption")
                    .name(ClassInfo.NO_DOC).user("dust options?"));

            // Function to create DustOptions
            Functions.registerFunction(new JavaFunction<DustOptions>("dustOption", new Parameter[]{
                    new Parameter<>("color", Classes.getExactClassInfo(Color.class), true, null),
                    new Parameter<>("size", Classes.getExactClassInfo(Number.class), true, null)
            }, Classes.getExactClassInfo(DustOptions.class), true) {
                @Nullable
                @Override
                public DustOptions[] execute(FunctionEvent e, Object[][] params) {
                    return new DustOptions[]{new DustOptions(((Color) params[0][0]).asBukkitColor(), ((Number) params[1][0]).floatValue())};
                }
            }.description("Creates a new dust option to be used with 'dust' particle. Color can either be a regular color or an RGB color using",
                    "Skript's rgb() function. Size is the size the particle will be.")
                    .examples("set {_c} to dustOption(red, 1.5)", "set {_c} to dustOption(rgb(1, 255, 1), 3)")
                    .since("1.9.0"));
        }
    }

}
