package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.ChatUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprTranslationKey extends SimplePropertyExpression<Object, String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprTranslationKey.class, String.class, "translation key[s]", "objects")
            .name("Translation Key")
            .description("Gets the translation key from an object. Requires PaperMC.")
            .examples("set {_t} to translation key of player's tool")
            .since("2.10.0")
            .register();
    }

    @Override
    public @Nullable String convert(Object itemType) {
        return ChatUtil.getTranslation(itemType);
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "translation key";
    }

}
