package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprEntityOrigin extends SimplePropertyExpression<Entity, Location> {

    private static final boolean HAS_ORIGIN = Skript.methodExists(Entity.class, "getOrigin");

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprEntityOrigin.class, Location.class, "origin[ location]", "entities")
            .name("Entity Origin Location")
            .description("Represents the location where an entity originates from. Requires PaperMC.")
            .examples("set {_loc} to origin of last spawned entity",
                "set {_loc} origin location of {_entity}",
                "if distance between {_entity} and origin of {_entity} > 10:")
            .since("3.3.0")
            .register();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!HAS_ORIGIN) {
            Skript.error("Entity origin requires a PaperMC server.");
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Location convert(Entity entity) {
        return entity.getOrigin();
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "origin location";
    }

}
