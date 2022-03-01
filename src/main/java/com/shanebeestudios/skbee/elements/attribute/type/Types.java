package com.shanebeestudios.skbee.elements.attribute.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.attribute.AttributePair;
import com.shanebeestudios.skbee.api.attribute.AttributeUtils;
import com.shanebeestudios.skbee.api.util.EnumParser;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;

public class Types {

    static {
        if (Classes.getExactClassInfo(EquipmentSlot.class) == null) {
            EnumUtils<EquipmentSlot> SLOT_ENUM = AttributeUtils.getSlotEnum();
            Classes.registerClass(new ClassInfo<>(EquipmentSlot.class, "equipmentslot")
                    .user("equipment ?slots?")
                    .name("Equipment Slot")
                    .usage(SLOT_ENUM.getAllNames())
                    .parser(new EnumParser<>(SLOT_ENUM)));
        }

        if (Classes.getExactClassInfo(Operation.class) == null) {
            EnumUtils<Operation> OPERATION_ENUM = AttributeUtils.getOperationEnum();
            Classes.registerClass(new ClassInfo<>(Operation.class, "attributeoperation")
                    .user("attribute ?operations?")
                    .name("Attribute Operation")
                    .usage(OPERATION_ENUM.getAllNames())
                    .parser(new EnumParser<>(OPERATION_ENUM)));
        }

        if (Classes.getExactClassInfo(AttributeModifier.class) == null) {
            Classes.registerClass(new ClassInfo<>(AttributeModifier.class, "attributemodifier")
                    .user("attribute ?modifiers?")
                    .name("Attribute Modifier")
                    .parser(new Parser<AttributeModifier>() {

                        @Override
                        public boolean canParse(ParseContext context) {
                            return false;
                        }

                        @Override
                        public AttributeModifier parse(String s, ParseContext context) {
                            return null;
                        }

                        @Override
                        public String toString(AttributeModifier modifier, int flags) {
                            return AttributeUtils.modifierToString(modifier);
                        }

                        @Override
                        public String toVariableNameString(AttributeModifier attributeModifier) {
                            return "attributemodifier:" + toString(attributeModifier, 0);
                        }

                        public String getVariableNamePattern() {
                            return "attributemodifier://s";
                        }

                    }));
        }

        Classes.registerClass(new ClassInfo<>(AttributePair.class, "attributepair")
                .user("attribute ?pairs?")
                .name("Attribute Pair")
                .parser(new Parser<AttributePair>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public AttributePair parse(String s, ParseContext context) {
                        return null;
                    }

                    @Override
                    public String toString(AttributePair o, int flags) {
                        return "attribute pair of " + Classes.toString(o.getAttribute()) + " with modifier " +
                                Classes.toString(o.getModifier());
                    }

                    @Override
                    public String toVariableNameString(AttributePair attributePair) {
                        return "attributepair:" + toString(attributePair, 0);
                    }

                    public String getVariableNamePattern() {
                        return "attributepair://s";
                    }
                }));
    }

}
