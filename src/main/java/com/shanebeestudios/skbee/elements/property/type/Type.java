package com.shanebeestudios.skbee.elements.property.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.property.Property;
import com.shanebeestudios.skbee.api.property.PropertyRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Type {

    static {
        Classes.registerClass(new ClassInfo<>(Property.class, "property")
            .user("propert(y|ies)")
            .name("Property")
            .description("Represents the diffrent kinds of properties.",
                "See [**Property Wiki**](https://github.com/ShaneBeee/SkBee/wiki/Properties) for available properties.")
            .parser(new Parser<>() {

                @Override
                public boolean canParse(ParseContext context) {
                    return context == ParseContext.DEFAULT;
                }

                @Override
                @Nullable
                public Property<?, ?> parse(String string, ParseContext context) {
                    return PropertyRegistry.PROPERTIES.get(string);
                }

                @Override
                public @NotNull String toString(Property property, int flags) {
                    return property.getName();
                }

                @Override
                public @NotNull String toVariableNameString(Property property) {
                    return "property:" + property.getName();
                }
            }));
    }

}
