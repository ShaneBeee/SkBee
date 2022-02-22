package com.shanebeestudios.skbee.elements.board.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.EnumParser;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import com.shanebeestudios.skbee.elements.board.objects.BeeTeam;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

@SuppressWarnings("unused")
public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(BeeTeam.class, "beeteam")
                .user("(bee )?teams?")
                .name("Team")
                .description("Represents a scoreboard team."));

        if (Classes.getExactClassInfo(Option.class) == null) {
            EnumUtils<Option> TEAM_OPTIONS = new EnumUtils<>(Option.class);
            Classes.registerClass(new ClassInfo<>(Option.class, "teamoption")
                    .user("team ?options?")
                    .name("Team - Option")
                    .usage(TEAM_OPTIONS.getAllNames())
                    .description("Represents an option for a team.")
                    .parser(new EnumParser<>(TEAM_OPTIONS)));
        }

        if (Classes.getExactClassInfo(OptionStatus.class) == null) {
            EnumUtils<OptionStatus> TEAM_OPTION_STATUS = new EnumUtils<>(OptionStatus.class);
            Classes.registerClass(new ClassInfo<>(OptionStatus.class, "teamoptionstatus")
                    .user("team ?option ?status")
                    .name("Team - Option Status")
                    .usage(TEAM_OPTION_STATUS.getAllNames())
                    .description("Represents an option status for a team option")
                    .parser(new EnumParser<>(TEAM_OPTION_STATUS)));
        }
    }

}
