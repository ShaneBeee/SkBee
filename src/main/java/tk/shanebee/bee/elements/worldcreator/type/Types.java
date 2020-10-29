package tk.shanebee.bee.elements.worldcreator.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.EnumUtils;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.elements.worldcreator.objects.BeeWorldCreator;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(BeeWorldCreator.class, "worldcreator")
                .usage("world creator")
                .name("World Creator")
                .description("Used to create new worlds.")
                .examples("set {_creator} to new world creator named \"my-world\"")
                .since("1.8.0"));

        EnumUtils<Environment> environments = new EnumUtils<>(Environment.class, "environment types");
        Classes.registerClass(new ClassInfo<>(Environment.class, "environment")
                .user("environments?")
                .name("Environment")
                .description("The environment of a world.")
                .usage(environments.getAllNames())
                .examples("set environment of {_creator} to nether")
                .since("1.8.0")
                .parser(new Parser<Environment>() {

                    @Nullable
                    @Override
                    public Environment parse(@NotNull String string, @NotNull ParseContext context) {
                        return environments.parse(string);
                    }

                    @Override
                    public @NotNull String toString(Environment o, int flags) {
                        return environments.toString(o, flags);
                    }

                    @Override
                    public @NotNull String toVariableNameString(Environment o) {
                        return o.name();
                    }

                    @Override
                    public @NotNull String getVariableNamePattern() {
                        return "\\S+";
                    }
                }));

        EnumUtils<WorldType> worldTypes = new EnumUtils<>(WorldType.class, "world types");
        Classes.registerClass(new ClassInfo<>(WorldType.class, "worldtype")
                .user("world ?types?")
                .name("World Type")
                .description("The type of a world")
                .usage(worldTypes.getAllNames())
                .examples("set world type of {_creator} to flat")
                .since("1.8.0")
                .parser(new Parser<WorldType>() {

                    @Nullable
                    @Override
                    public WorldType parse(@NotNull String string, @NotNull ParseContext context) {
                        return worldTypes.parse(string);
                    }

                    @Override
                    public @NotNull String toString(@NotNull WorldType o, int flags) {
                        return worldTypes.toString(o, flags);
                    }

                    @Override
                    public @NotNull String toVariableNameString(WorldType o) {
                        return o.name();
                    }

                    @Override
                    public @NotNull String getVariableNamePattern() {
                        return "\\S+";
                    }
                }));
    }

}
