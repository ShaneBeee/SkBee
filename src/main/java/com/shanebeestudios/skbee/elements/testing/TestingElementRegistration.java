package com.shanebeestudios.skbee.elements.testing;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.testing.elements.CondClassInfoRegistered;
import com.shanebeestudios.skbee.elements.testing.elements.EffTestLog;
import com.shanebeestudios.skbee.elements.testing.elements.ExprLastRuntimeLogs;
import com.shanebeestudios.skbee.elements.testing.elements.SecTestSections;
import com.shanebeestudios.skbee.elements.testing.elements.SecTryCatch;
import com.shanebeestudios.skbee.elements.testing.type.Types;

public class TestingElementRegistration {

    public static void register(Registration reg) {
        CondClassInfoRegistered.register(reg);
        EffTestLog.register(reg);
        ExprLastRuntimeLogs.register(reg);
        SecTestSections.register(reg);
        SecTryCatch.register(reg);
        Types.register(reg);
    }

}
