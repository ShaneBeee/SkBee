package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

@Name("PotionType of Item")
@Description({"Get/set/delete the potion type of an item.",
    "This is not the same as the potion effect type, this is the base potion Minecraft uses for the potion items in the creative menu."})
@Examples({"set potion type of player's tool to strong_leaping",
    "if potion type of player's tool = strong leaping:",
    "delete potion type of player's tool"})
@Since("3.8.1")
public class ExprPotionTypeItem extends SimplePropertyExpression<Object, PotionType> {

    private static final boolean HAS_COMPONENTS = Skript.classExists("io.papermc.paper.datacomponent.DataComponentType");

    static {
        register(ExprPotionTypeItem.class, PotionType.class,
            "potion type", "itemstacks/itemtypes/slots");
    }

    @Override
    public @Nullable PotionType convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack == null) {
            return null;
        }
        if (HAS_COMPONENTS) {
            return ItemComponentUtils.getPotionType(itemStack);
        } else if (itemStack.getItemMeta() instanceof PotionMeta potionMeta) {
            return potionMeta.getBasePotionType();
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
            if (HAS_COMPONENTS) {
                ItemComponentUtils.setPotionType(itemStack, potionType);
            } else {
                if (itemStack.getItemMeta() instanceof PotionMeta meta) {
                    meta.setBasePotionType(potionType);
                    itemStack.setItemMeta(meta);
                }
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
