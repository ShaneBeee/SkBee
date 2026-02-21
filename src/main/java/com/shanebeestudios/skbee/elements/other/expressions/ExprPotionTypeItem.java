package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ExprPotionTypeItem extends SimplePropertyExpression<Object, PotionType> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprPotionTypeItem.class, PotionType.class,
                "potion type", "itemstacks/itemtypes/slots")
            .name("PotionType of Item")
            .description("Get/set/delete the potion type of an item.",
                "This is not the same as the potion effect type, this is the base potion Minecraft uses for the potion items in the creative menu.")
            .examples("set potion type of player's tool to strong_leaping",
                "if potion type of player's tool = strong leaping:",
                "delete potion type of player's tool")
            .since("3.8.1")
            .register();
    }

    @Override
    public @Nullable PotionType convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack == null) {
            return null;
        }
        if (itemStack.hasData(DataComponentTypes.POTION_CONTENTS)) {
            PotionContents data = itemStack.getData(DataComponentTypes.POTION_CONTENTS);
            if (data != null) return data.potion();
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(PotionType.class);
        else if (mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Object from, Object @Nullable [] delta, ChangeMode mode) {
        PotionType potionType = delta != null && delta[0] instanceof PotionType pt ? pt : null;

        ItemUtils.modifyItems(from, itemStack -> {
            if (potionType != null) {
                PotionContents potionContents = PotionContents.potionContents().potion(potionType).build();
                itemStack.setData(DataComponentTypes.POTION_CONTENTS, potionContents);
            } else {
                itemStack.unsetData(DataComponentTypes.POTION_CONTENTS);
            }
        });
    }

    @Override
    protected String getPropertyName() {
        return "potion type";
    }

    @Override
    public Class<? extends PotionType> getReturnType() {
        return PotionType.class;
    }

}
