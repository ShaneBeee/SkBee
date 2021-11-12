package com.shanebeestudios.skbee.elements.bound.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;

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
                        String bound = fields.getObject("boundID", String.class);
                        return SkBee.getPlugin().getBoundConfig().getBoundFromID(bound);
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return true;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }

                }));
    }

}
