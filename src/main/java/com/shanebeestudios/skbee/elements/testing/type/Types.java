package com.shanebeestudios.skbee.elements.testing.type;

import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.wrapper.RegistryClassInfo;
import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.Registry;

@SuppressWarnings("UnstableApiUsage")
public class Types {

    static {
        Classes.registerClass(RegistryClassInfo.create(Registry.DATA_COMPONENT_TYPE, DataComponentType.class,
            false, "datacomponenttype")
            .user("data ?component ?types?")
            .name("Data Component Type"));
    }

}
