package com.shanebeestudios.skbee.elements.nbt;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.nbt.conditions.CondHasNBTTag;
import com.shanebeestudios.skbee.elements.nbt.conditions.CondNBTFileExists;
import com.shanebeestudios.skbee.elements.nbt.conditions.CondNBTIsBlank;
import com.shanebeestudios.skbee.elements.nbt.effects.EffSaveNBTFile;
import com.shanebeestudios.skbee.elements.nbt.effects.EffSetBlockNBT;
import com.shanebeestudios.skbee.elements.nbt.effects.EffSpawnEntityNBT;
import com.shanebeestudios.skbee.elements.nbt.effects.EffTagDelete;
import com.shanebeestudios.skbee.elements.nbt.expressions.ExprItemFromNBT;
import com.shanebeestudios.skbee.elements.nbt.expressions.ExprItemWithNBT;
import com.shanebeestudios.skbee.elements.nbt.expressions.ExprNBTEventValue;
import com.shanebeestudios.skbee.elements.nbt.expressions.ExprNBTUuid;
import com.shanebeestudios.skbee.elements.nbt.expressions.ExprNbtCompound;
import com.shanebeestudios.skbee.elements.nbt.expressions.ExprPrettyNBT;
import com.shanebeestudios.skbee.elements.nbt.expressions.ExprTagOfNBT;
import com.shanebeestudios.skbee.elements.nbt.expressions.ExprTagTypeOfNBT;
import com.shanebeestudios.skbee.elements.nbt.expressions.ExprTagsOfNBT;
import com.shanebeestudios.skbee.elements.nbt.sections.SecExprBlankNBTCompound;
import com.shanebeestudios.skbee.elements.nbt.sections.SecModifyNBT;
import com.shanebeestudios.skbee.elements.nbt.types.Types;

public class NBTElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS
        CondHasNBTTag.register(reg);
        CondNBTFileExists.register(reg);
        CondNBTIsBlank.register(reg);

        // EFFECTS
        EffSaveNBTFile.register(reg);
        EffSetBlockNBT.register(reg);
        EffSpawnEntityNBT.register(reg);
        EffTagDelete.register(reg);

        // EXPRESSIONS
        ExprItemFromNBT.register(reg);
        ExprItemWithNBT.register(reg);
        ExprNbtCompound.register(reg);
        ExprNBTEventValue.register(reg);
        ExprNBTUuid.register(reg);
        ExprPrettyNBT.register(reg);
        ExprTagOfNBT.register(reg);
        ExprTagsOfNBT.register(reg);
        ExprTagTypeOfNBT.register(reg);

        // SECTIONS
        SecExprBlankNBTCompound.register(reg);
        SecModifyNBT.register(reg);

        // TYPES
        Types.register(reg);
    }

}
