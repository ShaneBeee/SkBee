package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ExprEffectiveName extends SimplePropertyExpression<ItemStack, ComponentWrapper> {

    private static final boolean HAS_EFFECTIVE_NAME = Skript.methodExists(ItemStack.class, "effectiveName");

    public static void register(Registration reg) {
        if (HAS_EFFECTIVE_NAME)
            reg.newPropertyExpression(ExprEffectiveName.class, ComponentWrapper.class,
                    "[component] effective name", "itemstacks")
                .name("TextComponent - Effective Name")
                .description("Gets the effective name of an item stack shown to the player. " +
                    "It takes into account the display name (with italics) from the item meta, the potion effect, translatable name, rarity etc.")
                .examples("broadcast effective name of player's tool")
                .since("3.13.0")
                .register();
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
