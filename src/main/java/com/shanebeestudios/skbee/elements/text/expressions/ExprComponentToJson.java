package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprComponentToJson extends SimplePropertyExpression<ComponentWrapper, String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprComponentToJson.class, String.class,
                "serialized json string", "textcomponents")
            .name("TextComponent - Json String")
            .description("Get the serialized json string of a text component. Useful in NBT.")
            .examples("set {_m} to mini message from \"<rainbow>HI MOM, I MADE IT ON THE DOCS\"",
                "set {_j} to serialized json string of {_m}")
            .since("3.5.0")
            .register();
    }

    @Override
    public @Nullable String convert(ComponentWrapper wrapper) {
        return wrapper.toJsonString();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "serialized json string";
    }

}
