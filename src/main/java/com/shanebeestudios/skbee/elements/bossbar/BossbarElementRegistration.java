package com.shanebeestudios.skbee.elements.bossbar;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.bossbar.expressions.ExprBossBarAll;
import com.shanebeestudios.skbee.elements.bossbar.expressions.ExprBossBarByID;
import com.shanebeestudios.skbee.elements.bossbar.expressions.ExprBossBarCreate;
import com.shanebeestudios.skbee.elements.bossbar.expressions.ExprBossBarEntity;
import com.shanebeestudios.skbee.elements.bossbar.expressions.ExprBossBarProperties;
import com.shanebeestudios.skbee.elements.bossbar.types.Types;

public class BossbarElementRegistration {

    public static void register(Registration reg) {
        // EXPRESSIONS
        ExprBossBarAll.register(reg);
        ExprBossBarByID.register(reg);
        ExprBossBarCreate.register(reg);
        ExprBossBarEntity.register(reg);
        ExprBossBarProperties.register(reg);

        // TYPES
        Types.register(reg);
    }
}
