package com.shanebeestudios.skbee.elements.bound.types;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.elements.bound.config.BoundConfig;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;

@SuppressWarnings({"deprecation", "unused"})
public class SkriptTypes {

    static {
        Classes.registerClass(new ClassInfo<>(Bound.class, "bound")
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
                    public String toString(Bound bound, int flags) {
                        String greater = Classes.toString(bound.getGreaterCorner());
                        String lesser = Classes.toString(bound.getLesserCorner());
                        return String.format("Bound '%s' between %s and %s",
                                bound.getId(), lesser, greater);
                    }

                    @Override
                    public String toVariableNameString(Bound bound) {
                        return String.format("bound:%s", bound.getId());
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "bound:.+";
                    }
                })
                .serializer(new Serializer<Bound>() {
                    @Override
                    public Fields serialize(Bound bound) {
                        Fields fields = new Fields();
                        fields.putObject("boundID", bound.getId());
                        return fields;
                    }

                    @Override
                    public void deserialize(Bound bound, Fields fields) {
                        assert false;
                    }

                    @Override
                    public Bound deserialize(String s) {
                        return null;
                    }

                    @Override
                    protected Bound deserialize(Fields fields) throws StreamCorruptedException {
                        String boundID = fields.getObject("boundID", String.class);
                        Bound bound = SkBee.getPlugin().getBoundConfig().getBoundFromID(boundID);
                        if (bound == null) {
                            throw new NullPointerException("Unable to find bound with id '" + boundID + "'");
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
                .changer(new Changer<Bound>() {
                    @Nullable
                    @Override
                    public Class<?>[] acceptChange(ChangeMode mode) {
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
                }));
    }

}
