package tk.shanebee.bee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;

public class TabEvent extends SkriptEvent {

    static {
        if (Skript.classExists("org.bukkit.event.server.TabCompleteEvent")) {
            Skript.registerEvent("Tab Complete", TabEvent.class, TabCompleteEvent.class,
                    "[skbee] tab complete [(of|for) %strings%]")
                    .description("Called when a player attempts to tab complete the arguments of a command. ",
                            "event-string = the command.")
                    .examples("on tab complete of \"/mycommand\"",
                            "\tset tab completions for position 1 to \"one\", \"two\" and \"three\"",
                            "\tset tab completions for position 2 to 1, 2 and 3",
                            "\tset tab completions for position 3 to all players",
                            "\tset tab completions for position 4 to (indexes of {blocks::*})", "",
                            "on tab complete:",
                            "\tif event-string contains \"/ver\":",
                            "\t\tclear tab completions")
                    .since("1.7.0");
            EventValues.registerEventValue(TabCompleteEvent.class, Player.class, new Getter<Player, TabCompleteEvent>() {
                @Nullable
                @Override
                public Player get(@NotNull TabCompleteEvent event) {
                    CommandSender sender = event.getSender();
                    if (sender instanceof Player) {
                        return ((Player) sender).getPlayer();
                    }
                    return null;
                }
            }, 0);
            EventValues.registerEventValue(TabCompleteEvent.class, String.class, new Getter<String, TabCompleteEvent>() {
                @Nullable
                @Override
                public String get(@NotNull TabCompleteEvent event) {
                    return event.getBuffer().split(" ")[0];
                }
            }, 0);
        }
    }

    private String[] commands;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, @NotNull ParseResult parseResult) {
        commands = args[0] == null ? null : ((Literal<String>)args[0]).getAll();
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        if (commands == null) return true;

        TabCompleteEvent tabEvent = ((TabCompleteEvent) event);
        String command = tabEvent.getBuffer().split(" ")[0];
        for (String s : commands) {
            if (s.equalsIgnoreCase(command)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "tab complete" + (commands == null ? "" : " for " + Arrays.toString(commands));
    }

}
