package com.shanebeestudios.skbee.elements.team.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import org.bukkit.scoreboard.Team;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(Team.class, "team")
                .user("teams?")
                .name("Team")
                .description("Represents a scoreboard team.")
                .since("1.16.0"));

        if (Classes.getExactClassInfo(Team.Option.class) == null) {
            EnumUtils<Team.Option> TEAM_OPTIONS = new EnumUtils<>(Team.Option.class);
            Classes.registerClass(new ClassInfo<>(Team.Option.class, "teamoption")
                    .user("team ?options?")
                    .name("Team - Option")
                    .usage(TEAM_OPTIONS.getAllNames())
                    .description("Represents an option for a team.")
                    .since("1.16.0")
                    .parser(TEAM_OPTIONS.getParser()));
        }

        if (Classes.getExactClassInfo(Team.OptionStatus.class) == null) {
            EnumUtils<Team.OptionStatus> TEAM_OPTION_STATUS = new EnumUtils<>(Team.OptionStatus.class);
            Classes.registerClass(new ClassInfo<>(Team.OptionStatus.class, "teamoptionstatus")
                    .user("team ?option ?status")
                    .name("Team - Option Status")
                    .usage(TEAM_OPTION_STATUS.getAllNames())
                    .description("Represents an option status for a team option")
                    .since("1.16.0")
                    .parser(TEAM_OPTION_STATUS.getParser()));
        }
    }

}
