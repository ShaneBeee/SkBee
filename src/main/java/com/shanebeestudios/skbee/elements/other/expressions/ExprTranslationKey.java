package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.ChatUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Translation Key")
@Description({"Gets the translation key from an object. Requires PaperMC."})
@Examples("set {_t} to translation key of player's tool")
@Since("2.10.0")
public class ExprTranslationKey extends SimplePropertyExpression<Object, String> {

    static {
        register(ExprTranslationKey.class, String.class, "translation key[s]", "objects");
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
