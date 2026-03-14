package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

@SuppressWarnings({"rawtypes", "UnstableApiUsage"})
public class ExprDamageResistantComponent extends SimplePropertyExpression<Object, TagKey> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprDamageResistantComponent.class, TagKey.class,
                "damage resistant [component]", "itemstacks/itemtypes/slots")
            .name("ItemComponent - Damage Resistant")
            .description("Represents the damage resistant component of an item.",
                "If specified, this item is invulnerable to the specified damage types when in entity form or equipped.",
                "See [**Damage Resistant**](https://minecraft.wiki/w/Data_component_format#damage_resistant) on McWiki for more details.",
                "",
                "**Changers**:",
                "`set` = Will set the damage resistant component of the item (Accepts a DamageType TagKey).",
                "`delete` = Will delete the damage resistant component of this item.",
                "`reset` = Will reset the damage resistant component back to the original value.")
            .examples("set {_key} to damage resistent component of player's tool",
                "set damage resistent component of player's tool to tag key \"minecraft:is_fire\" from damage_type registry",
                "reset damage resistent component of player's tool",
                "delete damage resistent component of player's tool")
            .since("3.18.0")
            .register();
    }

    @Override
    public @Nullable TagKey convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.DAMAGE_RESISTANT)) {
            DamageResistant data = itemStack.getData(DataComponentTypes.DAMAGE_RESISTANT);
            if (data != null) {
                return data.types();
            }
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(TagKey.class);
        else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        DamageResistant damageResistant = null;
        if (delta != null && delta[0] instanceof TagKey tagKey && tagKey.registryKey() == RegistryKey.DAMAGE_TYPE) {
            damageResistant = DamageResistant.damageResistant(tagKey);
        }

        ItemComponentUtils.modifyComponent(getExpr().getArray(event), mode,
            DataComponentTypes.DAMAGE_RESISTANT, damageResistant);
    }

    @Override
    protected String getPropertyName() {
        return "damage resistant component";
    }

    @Override
    public Class<? extends TagKey> getReturnType() {
        return TagKey.class;
    }

}
