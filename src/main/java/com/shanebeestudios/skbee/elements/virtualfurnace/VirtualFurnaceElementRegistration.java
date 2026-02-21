package com.shanebeestudios.skbee.elements.virtualfurnace;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.virtualfurnace.effects.EffFurnaceFuel;
import com.shanebeestudios.skbee.elements.virtualfurnace.effects.EffFurnaceRecipe;
import com.shanebeestudios.skbee.elements.virtualfurnace.events.EvtVirtualFurnace;
import com.shanebeestudios.skbee.elements.virtualfurnace.expressions.ExprVirtualFurnaceAllFurnaces;
import com.shanebeestudios.skbee.elements.virtualfurnace.expressions.ExprVirtualFurnaceCreate;
import com.shanebeestudios.skbee.elements.virtualfurnace.expressions.ExprVirtualFurnaceInventory;
import com.shanebeestudios.skbee.elements.virtualfurnace.expressions.ExprVirtualFurnaceItem;
import com.shanebeestudios.skbee.elements.virtualfurnace.expressions.ExprVirtualFurnaceMachineFromID;
import com.shanebeestudios.skbee.elements.virtualfurnace.expressions.ExprVirtualFurnaceMachineID;
import com.shanebeestudios.skbee.elements.virtualfurnace.expressions.ExprVirtualFurnaceMachineName;
import com.shanebeestudios.skbee.elements.virtualfurnace.expressions.ExprVirtualFurnacePropertiesCreate;
import com.shanebeestudios.skbee.elements.virtualfurnace.expressions.ExprVirtualFurnacePropertiesDefault;
import com.shanebeestudios.skbee.elements.virtualfurnace.type.Types;

public class VirtualFurnaceElementRegistration {

    public static void register(Registration reg) {
        // EFFECTS
        EffFurnaceFuel.register(reg);
        EffFurnaceRecipe.register(reg);

        // EVENTS
        EvtVirtualFurnace.register(reg);

        // EXPRESSIONS
        ExprVirtualFurnaceAllFurnaces.register(reg);
        ExprVirtualFurnaceCreate.register(reg);
        ExprVirtualFurnaceInventory.register(reg);
        ExprVirtualFurnaceItem.register(reg);
        ExprVirtualFurnaceMachineFromID.register(reg);
        ExprVirtualFurnaceMachineID.register(reg);
        ExprVirtualFurnaceMachineName.register(reg);
        ExprVirtualFurnacePropertiesCreate.register(reg);
        ExprVirtualFurnacePropertiesDefault.register(reg);

        // TYPES
        Types.register(reg);
    }

}
