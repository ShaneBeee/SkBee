package com.shanebeestudios.skbee.elements.property.type;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import com.shanebeestudios.skbee.api.property.Property;
import com.shanebeestudios.skbee.api.property.PropertyRegistry;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Type {

    public static void register(Registration reg) {
        reg.newType(Property.class, "property")
            .user("propert(y|ies)")
            .name("Property")
            .description("Represents the diffrent kinds of properties.",
                "See [**Property Wiki**](https://github.com/ShaneBeee/SkBee/wiki/Properties) for available properties.")
            .supplier(PropertyRegistry.supplier())
            .parser(new Parser<>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return context == ParseContext.DEFAULT;
                }

                @Override
                @Nullable
                public Property<?, ?> parse(String string, ParseContext context) {
                    return PropertyRegistry.properties().get(string);
                }

                @Override
                public @NotNull String toString(Property property, int flags) {
                    return property.getName();
                }

                @Override
                public @NotNull String toVariableNameString(Property property) {
                    return "property:" + property.getName();
                }
            })
            .register();
    }

}
