package com.shanebeestudios.skbee.elements.board.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.board.objects.BeeTeam;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - Prefix/Suffix")
@Description("Get/set the prefix/suffix of a team.")
@Examples({"on load:",
        "\tregister new team \"a-team\"",
        "\tset team prefix of team named \"a-team\" to \"[A-TEAM] \""})
@Since("1.15.0")
public class ExprTeamPrefix extends SimplePropertyExpression<BeeTeam, String> {

    static {
        register(ExprTeamPrefix.class, String.class, "[[sk]bee] team (0¦prefix|1¦suffix)", "beeteams");
    }

    private int pattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        setExpr((Expression<? extends BeeTeam>) exprs[0]);
        return true;
    }

    @Nullable
    @Override
    public String convert(@NotNull BeeTeam beeTeam) {
        if (pattern == 0) {
            return beeTeam.getPrefix();
        } else {
            return beeTeam.getSuffix();
        }
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(String.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        BeeTeam[] team = getExpr().getArray(event);
        String name = mode == ChangeMode.SET ? (String) delta[0] : null;
        for (BeeTeam beeTeam : team) {
            if (pattern == 0) {
                beeTeam.setPrefix(name);
            } else {
                beeTeam.setSuffix(name);
            }
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "team " + (pattern == 0 ? "prefix" : "suffix");
    }

}
