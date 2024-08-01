package com.shanebeestudios.skbee.elements.itemcomponent.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.attribute.AttributeModifier;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(AttributeModifier.class, "attributemodifier")
            .user("attribute ?modifiers?")
            .name("ItemComponent - Attribute Modifier")
            .description("Represents an attribute modifier from an item."));

        Classes.registerClass(new EnumWrapper<>(AttributeModifier.Operation.class).getClassInfo("attributeoperation")
            .user("attribute ?operations?")
            .name("ItemComponent - Attribute Modifier Operation")
            .description("Represents the different operations of an attribute modifer.",
                "NOTE: These are auto-generated and may differ between server versions."));
    }

}
