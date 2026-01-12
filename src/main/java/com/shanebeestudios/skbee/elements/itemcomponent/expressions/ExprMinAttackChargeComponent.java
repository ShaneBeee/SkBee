package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - Minimum Attack Charge")
@Description({"Represents the minimum attack charge component of an item.",
    "The minimum attack charge on the attack indicator required to attack with this item. Must be a non-negative float between 0.0 and 1.0",
    "See [**Minimum Attack Charge**](https://minecraft.wiki/w/Data_component_format#minimum_attack_charge) on McWiki for more details.",
    "Requires Minecraft 1.21.11+",
    "",
    "**Changers**:",
    "`set` = Will set the minimum attack charge of the item.",
    "`delete` = Will delete the minimum attack charge of this item.",
    "`reset` = Will reset the minimum attack charge back to the original value."})
@Examples({"set minimum attack charge of player's tool to 0.5",
    "delete minimum attack charge of player's tool",
    "reset minimum attack charge component of player's tool"})
@Since("INSERT VERSION")
public class ExprMinAttackChargeComponent extends SimplePropertyExpression<Object, Number> {

    static {
        if (Util.IS_RUNNING_MC_1_21_11) {
            register(ExprMinAttackChargeComponent.class, Number.class,
                "min[imum] attack charge [component]", "itemstacks/itemtypes/slots");
        }
    }

    @Override
    public @Nullable Number convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.MINIMUM_ATTACK_CHARGE)) {
            return itemStack.getData(DataComponentTypes.MINIMUM_ATTACK_CHARGE);
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
        else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        Number chargeNum = delta != null && delta[0] instanceof Number num ? num : null;

        float charge = chargeNum != null ? chargeNum.floatValue() : 1.0f;
        charge = MathUtil.clamp(charge, 0.0f, 1.0f);

        ItemComponentUtils.modifyComponent(getExpr().getArray(event), mode, DataComponentTypes.MINIMUM_ATTACK_CHARGE, charge);
    }

    @Override
    protected String getPropertyName() {
        return "minimum attack charge component";
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

}
