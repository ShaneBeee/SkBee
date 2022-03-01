package com.shanebeestudios.skbee.api.attribute;

import com.shanebeestudios.skbee.api.util.EnumUtils;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;

public class AttributeUtils {

    private static final EnumUtils<EquipmentSlot> SLOT_ENUM = new EnumUtils<>(EquipmentSlot.class);
    private static final EnumUtils<Operation> OPERATION_ENUM = new EnumUtils<>(Operation.class);

    public static EnumUtils<EquipmentSlot> getSlotEnum() {
        return SLOT_ENUM;
    }

    public static EnumUtils<Operation> getOperationEnum() {
        return OPERATION_ENUM;
    }

    public static String modifierToString(AttributeModifier modifier) {
        String uuid = modifier.getUniqueId().toString();
        String name = modifier.getName();
        double amount = modifier.getAmount();
        String operation = OPERATION_ENUM.toString(modifier.getOperation(), 0);
        String slot = "";
        if (modifier.getSlot() != null) {
            slot = SLOT_ENUM.toString(modifier.getSlot(), 0);
        }
        return String.format("Attribute modifier named \"%s\" with id '%s', amount '%s', operation '%s' in slot '%s'",
                name, uuid, amount, operation, slot);
    }

}
