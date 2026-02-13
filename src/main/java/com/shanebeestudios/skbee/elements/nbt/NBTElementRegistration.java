package com.shanebeestudios.skbee.elements.nbt;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.nbt.sections.SecExprBlankNBTCompound;
import com.shanebeestudios.skbee.elements.nbt.sections.SecModifyNBT;
import com.shanebeestudios.skbee.elements.nbt.types.Types;

public class NBTElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS

        // EFFECTS

        // EXPRESSIONS

        // SECTIONS
        SecExprBlankNBTCompound.register(reg);
        SecModifyNBT.register(reg);

        // TYPES
        Types.register(reg);
    }

}
