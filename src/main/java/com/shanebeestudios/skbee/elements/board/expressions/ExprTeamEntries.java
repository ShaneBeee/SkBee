package com.shanebeestudios.skbee.elements.board.expressions;

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
import com.shanebeestudios.skbee.elements.board.objects.BeeTeam;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Team - Entries")
@Description("Represents the entries of a team. You can add/remove players/entities to teams.")
@Examples({"add player to team entries of team named \"a-team\"",
        "add all villagers to team entries of team named \"villagers\"",
        "remove all players from team entries of team named \"team-players\""})
@Since("INSERT VERSION")
public class ExprTeamEntries extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprTeamEntries.class, Entity.class, ExpressionType.PROPERTY,
                "[[sk]bee] team entries of %beeteam%",
                "%beeteam%'[s] [[sk]bee] team entries");
    }

    private Expression<BeeTeam> team;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.team = (Expression<BeeTeam>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected Entity[] get(Event event) {
        BeeTeam team = this.team.getSingle(event);
        if (team != null) {
            return team.getEntries().toArray(new Entity[0]);
        }
        return null;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        switch (mode) {
            case ADD:
            case REMOVE:
                return CollectionUtils.array(Entity[].class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        BeeTeam team = this.team.getSingle(event);
        if (team != null) {
            for (Object o : delta) {
                if (mode == ChangeMode.ADD) {
                    team.addEntry(((Entity) o));
                } else if (mode == ChangeMode.REMOVE) {
                    team.removeEntry(((Entity) o));
                }
            }
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return null;
    }
}
