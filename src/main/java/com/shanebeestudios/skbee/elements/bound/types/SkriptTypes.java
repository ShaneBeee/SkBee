package com.shanebeestudios.skbee.elements.bound.types;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;

@SuppressWarnings({"unused"})
public class SkriptTypes {

    public static void register(Registration reg) {
        reg.newType(Bound.class, "bound")
            .user("bound")
            .name("Bound")
            .description("Represents a 3D bounding box between 2 points")
            .defaultExpression(new EventValueExpression<>(Bound.class))
            .since("1.0.0")
            .parser(new Parser<Bound>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public @NotNull String toString(Bound bound, int flags) {
                    String greater = getLoc(bound.getGreaterCorner());
                    String lesser = getLoc(bound.getLesserCorner());
                    String world = bound.getWorldKey().toString();
                    return String.format((bound.isTemporary() ? "temporary bound" : "bound") + " '%s' between %s and %s in world \"%s\"",
                        bound.getId(), lesser, greater, world);
                }

                @Override
                public @NotNull String toVariableNameString(Bound bound) {
                    return String.format("bound:%s", bound.getId());
                }

                public String getVariableNamePattern() {
                    return "bound:.+";
                }
            })
            .serializer(new Serializer<>() {
                @Override
                public @NotNull Fields serialize(Bound bound) {
                    Fields fields = new Fields();
                    fields.putObject("boundID", bound.getId());
                    return fields;
                }

                @Override
                public void deserialize(Bound bound, Fields fields) {
                    assert false;
                }

                @Override
                protected Bound deserialize(Fields fields) throws StreamCorruptedException {
                    String boundID = fields.getObject("boundID", String.class);
                    Bound bound = SkBee.getPlugin().getBoundConfig().getBoundFromID(boundID);
                    if (bound == null) {
                        throw new StreamCorruptedException("Unable to find bound with id '" + boundID + "'");
                    }
                    return bound;
                }

                @Override
                public boolean mustSyncDeserialization() {
                    return true;
                }

                @Override
                protected boolean canBeInstantiated() {
                    return false;
                }

            })
            .changer(new Changer<>() {
                @Nullable
                @Override
                public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
                    if (mode == ChangeMode.DELETE) {
                        return CollectionUtils.array();
                    }
                    return null;
                }

                @Override
                public void change(Bound[] bounds, @Nullable Object[] delta, ChangeMode mode) {
                    if (mode == ChangeMode.DELETE) {
                        BoundConfig boundConfig = SkBee.getPlugin().getBoundConfig();
                        for (Bound bound : bounds) {
                            boundConfig.removeBound(bound);
                        }
                    }
                }
            })
            .register();
    }

    private static String getLoc(@NotNull Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return String.format("location(%.2f, %.2f, %.2f)", x, y, z);
    }

}
