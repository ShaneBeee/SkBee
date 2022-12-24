package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@Name("Entity - Is Ticking")
@Description("Check if an entity is currently ticking. If they're in a chunk that is not ticking, this will be false.")
@Examples("loop all entities where [input is ticking]:")
@Since("2.6.0")
public class CondEntityTicking extends PropertyCondition<Entity> {

    private static final boolean TICKING_METHOD_EXISTS = Skript.methodExists(Entity.class, "isTicking");

    static {
        register(CondEntityTicking.class, "ticking", "entities");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!TICKING_METHOD_EXISTS) {
            Skript.error("'is ticking' does not exist on your server software. May require Paper MC server.");
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public boolean check(Entity entity) {
        return entity.isTicking();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "ticking";
    }

}
