package com.shanebeestudios.skbee.elements.dialog;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.dialog.effects.EffOpenDialog;
import com.shanebeestudios.skbee.elements.dialog.sections.actions.SecDynamicActionButton;
import com.shanebeestudios.skbee.elements.dialog.sections.actions.SecDynamicCallbackActionButton;
import com.shanebeestudios.skbee.elements.dialog.sections.actions.SecDynamicRunCommandActionButton;
import com.shanebeestudios.skbee.elements.dialog.sections.actions.SecStaticActionButton;
import com.shanebeestudios.skbee.elements.dialog.sections.bodies.SecItemBody;
import com.shanebeestudios.skbee.elements.dialog.sections.bodies.SecPlainMessageBody;
import com.shanebeestudios.skbee.elements.dialog.sections.dialogs.SecConfirmationDialogRegister;
import com.shanebeestudios.skbee.elements.dialog.sections.dialogs.SecDialogListDialogRegister;
import com.shanebeestudios.skbee.elements.dialog.sections.dialogs.SecMultiDialogRegister;
import com.shanebeestudios.skbee.elements.dialog.sections.dialogs.SecNoticeDialogRegister;
import com.shanebeestudios.skbee.elements.dialog.sections.inputs.SecBooleanInput;
import com.shanebeestudios.skbee.elements.dialog.sections.inputs.SecNumberRangeInput;
import com.shanebeestudios.skbee.elements.dialog.sections.inputs.SecSingleOptionInput;
import com.shanebeestudios.skbee.elements.dialog.sections.inputs.SecSingleOptionInputOptions;
import com.shanebeestudios.skbee.elements.dialog.sections.inputs.SecTextInput;

public class DialogElementRegestration {

    public static void register(Registration reg) {
        // EFFECTS
        EffOpenDialog.register(reg);

        // SECTIONS
        // Actions
        SecStaticActionButton.register(reg);
        SecDynamicActionButton.register(reg);
        SecDynamicCallbackActionButton.register(reg);
        SecDynamicRunCommandActionButton.register(reg);

        // Bodies
        SecItemBody.register(reg);
        SecPlainMessageBody.register(reg);

        // Dialogs
        SecMultiDialogRegister.register(reg);
        SecNoticeDialogRegister.register(reg);
        SecDialogListDialogRegister.register(reg);
        SecConfirmationDialogRegister.register(reg);

        // Inputs
        SecTextInput.register(reg);
        SecBooleanInput.register(reg);
        SecNumberRangeInput.register(reg);
        SecSingleOptionInput.register(reg);
        SecSingleOptionInputOptions.register(reg);
    }

}
