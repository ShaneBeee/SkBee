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
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.damage.DamageType;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - Damage Type")
@Description({"Represents the damage type component of an item.",
    "See [**Damage Type**](https://minecraft.wiki/w/Data_component_format#damage_type) on McWiki for more details.",
    "Requires Minecraft 1.21.11+",
    "",
    "**Changers**:",
    "`set` = Will set the damage type of the item.",
    "`delete` = Will delete the damage type of this item.",
    "`reset` = Will reset the damage type back to the original value."})
@Examples({"set damage type of player's tool to cactus",
    "delete damage type of player's tool",
    "reset damage type component of player's tool"})
@Since("INSERT VERSION")
public class ExprDamageTypeComponent extends SimplePropertyExpression<Object, DamageType> {

    static {
        if (Util.IS_RUNNING_MC_1_21_11) {
            register(ExprDamageTypeComponent.class, DamageType.class,
                "damage type [component]", "itemstacks/itemtypes/slots");
        }
    }

    @Override
    public @Nullable DamageType convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.DAMAGE_TYPE)) {
            return itemStack.getData(DataComponentTypes.DAMAGE_TYPE);
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(DamageType.class);
        else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        DamageType damageType = delta != null && delta[0] instanceof DamageType dt ? dt : null;

        ItemComponentUtils.modifyComponent(getExpr().getArray(event), mode, DataComponentTypes.DAMAGE_TYPE, damageType);
    }

    @Override
    protected String getPropertyName() {
        return "damage type component";
    }

    @Override
    public Class<? extends DamageType> getReturnType() {
        return DamageType.class;
    }

}
