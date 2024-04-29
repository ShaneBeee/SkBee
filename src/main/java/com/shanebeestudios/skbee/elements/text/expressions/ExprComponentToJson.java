package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Json String")
@Description("Get the serialized json string of a text component. Useful in NBT.")
@Examples({"set {_m} to mini message from \"<rainbow>HI MOM, I MADE IT ON THE DOCS\"",
        "set {_j} to serialized json string of {_m}"})
@Since("3.5.0")
public class ExprComponentToJson extends SimplePropertyExpression<ComponentWrapper,String> {

    static {
        register(ExprComponentToJson.class, String.class, "serialized json string", "textcomponents");
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
