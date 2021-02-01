package tk.shanebee.bee.elements.board.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.elements.board.objects.BeeTeam;

@Name("Team - Get Team")
@Description("Get the team by name or of an entity.")
@Examples({"set {_t} to team \"a-team\"",
        "set {_t} to team of player"})
@Since("INSERT VERSION")
public class ExprTeam extends SimpleExpression<BeeTeam> {

    static {
        Skript.registerExpression(ExprTeam.class, BeeTeam.class, ExpressionType.SIMPLE,
                "[[sk]bee] team %string%",
                "[[sk]bee] team of %entity%");
    }

    private int pattern;
    private Expression<String> name;
    private Expression<Entity> entity;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.name = pattern == 0 ? (Expression<String>) exprs[0] : null;
        this.entity = pattern == 1 ? (Expression<Entity>) exprs[0] : null;
        return true;
    }

    @Nullable
    @Override
    protected BeeTeam[] get(@NotNull Event event) {
        if (pattern == 0) {
            String name = this.name.getSingle(event);
            if (name == null) return null;

            return new BeeTeam[]{SkBee.getPlugin().getBeeTeams().getBeeTeam(name)};
        } else if (pattern == 1) {
            Entity entity = this.entity.getSingle(event);
            if (entity == null) return null;

            return new BeeTeam[]{SkBee.getPlugin().getBeeTeams().getBeeTeamByEntry(entity)};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends BeeTeam> getReturnType() {
        return BeeTeam.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String from = pattern == 0 ? this.name.toString(e, d) : "of " + this.entity.toString(e, d);
        return "team " + from;
    }

}
