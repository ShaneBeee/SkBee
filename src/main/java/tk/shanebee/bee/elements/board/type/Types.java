package tk.shanebee.bee.elements.board.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import org.bukkit.scoreboard.Team;
import tk.shanebee.bee.api.EnumParser;
import tk.shanebee.bee.api.EnumUtils;
import tk.shanebee.bee.elements.board.objects.BeeTeam;

@SuppressWarnings("unused")
public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(BeeTeam.class, "beeteam")
                .user("(bee )?teams?")
                .name("Team")
                .description("Represents a scoreboard team."));

        EnumUtils<Team.Option> TEAM_OPTIONS = new EnumUtils<>(Team.Option.class);
        Classes.registerClass(new ClassInfo<>(Team.Option.class, "teamoption")
                .user("team ?options?")
                .name("Team - Option")
                .usage(TEAM_OPTIONS.getAllNames())
                .description("Represents an option for a team.")
                .parser(new EnumParser<>(TEAM_OPTIONS)));

        EnumUtils<Team.OptionStatus> TEAM_OPTION_STATUS = new EnumUtils<>(Team.OptionStatus.class);
        Classes.registerClass(new ClassInfo<>(Team.OptionStatus.class, "teamoptionstatus")
                .user("team ?option ?status")
                .name("Team - Option Status")
                .usage(TEAM_OPTION_STATUS.getAllNames())
                .description("Represents an option status for a team option")
                .parser(new EnumParser<>(TEAM_OPTION_STATUS)));
    }

}
