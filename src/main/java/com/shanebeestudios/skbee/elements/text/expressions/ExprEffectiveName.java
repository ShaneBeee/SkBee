package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Effective Name")
@Description({"Gets the effective name of an item stack shown to the player.",
    "It takes into account the display name (with italics) from the item meta, the potion effect, translatable name, rarity etc.",
    "Requires PaperMC 1.21.4+"})
@Examples("broadcast effective name of player's tool")
@Since("INSERT VERSION")
public class ExprEffectiveName extends SimplePropertyExpression<ItemStack, ComponentWrapper> {

    private static final boolean HAS_EFFECTIVE_NAME = Skript.methodExists(ItemStack.class, "effectiveName");

    static {
        if (HAS_EFFECTIVE_NAME)
            registerDefault(ExprEffectiveName.class, ComponentWrapper.class, "[component] effective name", "itemstacks");
    }

    @Override
    public @Nullable ComponentWrapper convert(ItemStack from) {
        return ComponentWrapper.fromComponent(from.effectiveName());
    }

    @Override
    protected String getPropertyName() {
        return "effective name";
    }

    @Override
    public Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

}
