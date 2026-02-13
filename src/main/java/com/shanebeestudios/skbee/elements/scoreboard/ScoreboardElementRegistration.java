package com.shanebeestudios.skbee.elements.scoreboard;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.scoreboard.conditions.CondObjModifiable;
import com.shanebeestudios.skbee.elements.scoreboard.conditions.CondTeamRegistered;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprAllTeams;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprCriteriaCreate;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprObjCreate;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprObjDisplaySlot;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprObjFromCriteria;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprObjFromDisplaySlot;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprObjGet;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprObjName;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprObjNumberFormat;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprObjScore;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprScoreboard;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprScoreboardPlayer;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprTeam;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprTeamColor;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprTeamEntries;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprTeamName;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprTeamOption;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprTeamPrefix;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprTeamState;
import com.shanebeestudios.skbee.elements.scoreboard.expressions.ExprTeamWithId;
import com.shanebeestudios.skbee.elements.scoreboard.type.Types;

public class ScoreboardElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS
        CondObjModifiable.register(reg);
        CondTeamRegistered.register(reg);

        // EXPRESSIONS
        ExprAllTeams.register(reg);
        ExprCriteriaCreate.register(reg);
        ExprObjCreate.register(reg);
        ExprObjDisplaySlot.register(reg);
        ExprObjFromCriteria.register(reg);
        ExprObjFromDisplaySlot.register(reg);
        ExprObjGet.register(reg);
        ExprObjName.register(reg);
        ExprObjNumberFormat.register(reg);
        ExprObjScore.register(reg);
        ExprScoreboard.register(reg);
        ExprScoreboardPlayer.register(reg);
        ExprTeam.register(reg);
        ExprTeamColor.register(reg);
        ExprTeamEntries.register(reg);
        ExprTeamName.register(reg);
        ExprTeamOption.register(reg);
        ExprTeamPrefix.register(reg);
        ExprTeamState.register(reg);
        ExprTeamWithId.register(reg);

        // TYPES
        Types.register(reg);
    }

}
