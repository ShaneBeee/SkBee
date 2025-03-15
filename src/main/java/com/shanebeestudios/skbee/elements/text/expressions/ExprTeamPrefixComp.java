package com.shanebeestudios.skbee.elements.text.expressions;

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
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Team Prefix/Suffix")
@Description("Get/set prefix/suffix of teams using components.")
@Examples("set component team prefix of team of player to mini message from \"<color:grey>[<color:aqua>OWNER<color:grey>] \"")
@Since("2.4.0")
public class ExprTeamPrefixComp extends SimplePropertyExpression<Team, ComponentWrapper> {

    static {
        if (!Util.IS_RUNNING_FOLIA && SkBee.getPlugin().getPluginConfig().ELEMENTS_SCOREBOARD) {
            register(ExprTeamPrefixComp.class, ComponentWrapper.class, "component team (prefix|1:suffix)", "teams");
        }
    }

    private boolean prefix;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.prefix = parseResult.mark == 0;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable ComponentWrapper convert(Team team) {
        return ComponentWrapper.fromComponent(prefix ? team.prefix() : team.suffix());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(ComponentWrapper.class);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta[0] instanceof ComponentWrapper component) {
            if (mode == ChangeMode.SET) {
                for (Team team : getExpr().getArray(event)) {
                    if (prefix) {
                        component.setTeamPrefix(team);
                    } else {
                        component.setTeamSuffix(team);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "component team prefix";
    }
}
