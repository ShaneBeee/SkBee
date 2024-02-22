package com.shanebeestudios.skbee.elements.objective.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.google.gson.JsonParseException;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Scoreboard - Objective Number Format")
@Description({"Represents the way an objective score can be formatted.",
        "There are three format types: Blank (nothing there), Fixed (a string of your choosing) and Styled (colored numbers).",
        "NOTES:",
        "`set` = You can set the format to any string you'd like (fixed) or a json component (styled number).",
        "`delete` = Will leave it blank (nothing will be there, just nothing).",
        "`reset` = Will reset back to its original format.",
        "Json Formatting: <link>https://minecraft.wiki/w/Raw_JSON_text_format</link>",
        "Requires Paper 1.20.4+"})
@Examples({"# Format to a string",
        "set number format of {-obj} to \"Look Im Fancy!!\"",
        "set number format of {-obj} for player to \"Im a lil less fancy!\"",
        "",
        "# Format the number with color/style",
        "set number format of {-obj} for player to \"{color:red,bold:true}\"",
        "set number format of {-obj} to \"{color:\"\"##0DEAE3\"\",bold:true}\"",
        "",
        "# Format to blank",
        "delete number format of {-obj}",
        "delete number format of {-obj} for player",
        "",
        "# Reset formatting",
        "reset number format of {-obj}",
        "reset number format of {-obj} for player"})
@Since("INSERT VERSION")
public class ExprObjNumberFormat extends SimpleExpression<String> {

    private static final boolean HAS_NUMBER_FORMAT = Skript.methodExists(Objective.class, "numberFormat");

    static {
        Skript.registerExpression(ExprObjNumberFormat.class, String.class, ExpressionType.COMBINED,
                "number format of %objective% [for %-entities/strings%]");
    }

    private Expression<Objective> objectives;
    private Expression<?> entries;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!HAS_NUMBER_FORMAT) {
            Skript.error("Number Format requires a Paper 1.20.4+ server.");
            return false;
        }
        this.objectives = (Expression<Objective>) exprs[0];
        this.entries = exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable String[] get(Event event) {
        List<String> formats = new ArrayList<>();
        Object[] entries = this.entries != null ? this.entries.getArray(event) : null;
        for (Objective objective : this.objectives.getArray(event)) {
            if (entries == null) {
                NumberFormat numberFormat = objective.numberFormat();
                if (numberFormat != null) formats.add(numberFormat.toString());
            } else {
                for (Object entry : entries) {
                    String stringScore = getStringScore(objective, entry);
                    if (stringScore != null) formats.add(stringScore);
                }
            }
        }
        return formats.toArray(new String[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(String.class);
        else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        String format = null;

        if (delta != null && delta[0] instanceof String string) {
            format = string;
        } else if (mode == ChangeMode.DELETE) {
            format = "$blank";
        }

        Object[] entries = this.entries != null ? this.entries.getArray(event) : null;
        for (Objective objective : this.objectives.getArray(event)) {
            if (entries == null) {
                setStringScore(objective, null, format);
            } else {
                for (Object entry : entries) {
                    setStringScore(objective, entry, format);
                }
            }
        }
    }

    @Override
    public boolean isSingle() {
        if (this.entries == null) {
            return this.objectives.isSingle();
        }
        return this.objectives.isSingle() && this.entries.isSingle();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String entries = this.entries != null ? (" for entries " + this.entries.toString(e, d)) : "";
        return "number format of " + this.objectives.toString(e, d) + entries;
    }

    // STATIC STUFF
    private static @Nullable String getStringScore(Objective objective, Object entry) {
        String stringEntiry = null;
        if (entry instanceof Player player) {
            stringEntiry = player.getName();
        } else if (entry instanceof Entity entity) {
            stringEntiry = entity.getUniqueId().toString();
        } else if (entry instanceof String string) {
            stringEntiry = string;
        }
        if (stringEntiry != null) {
            NumberFormat numberFormat = objective.getScore(stringEntiry).numberFormat();
            if (numberFormat != null) return numberFormat.toString();
        }
        return null;
    }

    private static void setStringScore(Objective objective, @Nullable Object entry, @Nullable String score) {
        String stringEntiry = null;
        if (entry instanceof Player player) {
            stringEntiry = player.getName();
        } else if (entry instanceof Entity entity) {
            stringEntiry = entity.getUniqueId().toString();
        } else if (entry instanceof String string) {
            stringEntiry = string;
        }

        NumberFormat numberFormat;
        if (score == null) {
            numberFormat = null;
        } else if (score.equalsIgnoreCase("$blank")) {
            numberFormat = NumberFormat.blank();
        } else {
            if (score.startsWith("{") && score.endsWith("}")) {
                try {
                    // I don't like this, but it's the only way to get a Style from json
                    // It needs to have some sort of text to be able to deserialize
                    score = score.replace("}", "") + ",\"text\":\"\"}";
                    Component deserialize = JSONComponentSerializer.json().deserialize(score);
                    numberFormat = NumberFormat.styled(deserialize.style());
                } catch (JsonParseException ignore) {
                    numberFormat = null;
                }
            } else {
                ComponentWrapper comp = ComponentWrapper.fromText(score);
                numberFormat = NumberFormat.fixed(comp.getComponent());
            }
        }

        if (stringEntiry != null) {
            objective.getScore(stringEntiry).numberFormat(numberFormat);
        } else {
            objective.numberFormat(numberFormat);
        }
    }

}
