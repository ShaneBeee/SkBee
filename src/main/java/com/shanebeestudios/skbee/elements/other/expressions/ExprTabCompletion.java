package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Name("Tab Completions")
@Description({"Set the tab completions used in a tab complete event.",
    "You can specify which position in the command arguments also.",
    "You can also remove texts from tab completions."})
@Examples({"on tab complete of \"/mycommand\":",
    "\tset tab completions for position 1 to \"one\", \"two\" and \"three\"",
    "\tset tab completions for position 2 to 1, 2 and 3",
    "\tset tab completions for position 3 to all players",
    "\tset tab completions for position 4 to (indexes of {blocks::*})", "",
    "on tab complete:",
    "\tif event-string contains \"/ver\":",
    "\t\tremove \"PermissionsEx\" from tab completions"})
@Since("1.7.0")
public class ExprTabCompletion extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprTabCompletion.class, String.class, ExpressionType.SIMPLE,
            "[skbee] tab completion[s] [(of|for) (last:last position|position %-number%)]");
    }

    private Expression<Number> position;
    private boolean last;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(TabCompleteEvent.class)) {
            Skript.error("Tab completions are only usable in a tab complete event.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.position = (Expression<Number>) exprs[0];
        this.last = parseResult.hasTag("last");
        return true;
    }

    @Override
    protected String @NotNull [] get(@NotNull Event event) {
        return ((TabCompleteEvent) event).getCompletions().toArray(new String[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(@NotNull ChangeMode mode) {
        return switch (mode) {
            case DELETE -> CollectionUtils.array();
            case SET, REMOVE, ADD, REMOVE_ALL -> CollectionUtils.array(Object[].class);
            default -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(@NotNull Event event, @Nullable Object[] objects, @NotNull ChangeMode mode) {
        if (!(event instanceof TabCompleteEvent tabCompleteEvent)) return;

        Number number = this.position != null ? this.position.getSingle(event) : -1;
        int position = 1;
        if (number != null) {
            position = number.intValue();
        }

        String buff = tabCompleteEvent.getBuffer();
        String[] buffers = buff.split(" ");
        if (this.last) position = buffers.length;
        String last = buff.substring(buff.length() - 1);

        if (position == -1 || (position == buffers.length && last.equalsIgnoreCase(" ")) || (position + 1 == buffers.length && !last.equalsIgnoreCase(" "))) {
            switch (mode) {
                case SET, ADD -> {
                    String arg;
                    if (position == buffers.length) {
                        arg = "";
                    } else {
                        arg = buffers[position];
                    }

                    List<String> completions = mode == ChangeMode.SET ? new ArrayList<>() : new ArrayList<>(tabCompleteEvent.getCompletions());
                    for (Object o : objects) {
                        String object = Classes.toString(o);
                        if (StringUtils.contains(object, arg, false)) {
                            completions.add(object);
                        }
                    }
                    tabCompleteEvent.setCompletions(completions);

                }
                case REMOVE -> {
                    for (Object object : objects) {
                        try {
                            assert object != null;
                            tabCompleteEvent.getCompletions().remove(object.toString());
                        } catch (Exception ignore) {
                        } // Had a little issue when removing from a blank list
                    }
                }
                case DELETE -> tabCompleteEvent.setCompletions(Collections.singletonList(""));
            }
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String pos = this.last ? " for last position" : this.position != null ? " for position " + this.position.toString(e, d) : "";
        return "tab completions" + pos;
    }

}
