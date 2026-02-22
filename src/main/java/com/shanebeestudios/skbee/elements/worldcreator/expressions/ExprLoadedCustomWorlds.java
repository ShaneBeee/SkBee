package com.shanebeestudios.skbee.elements.worldcreator.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprLoadedCustomWorlds extends SimpleExpression<World> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprLoadedCustomWorlds.class, World.class,
                "[all] loaded (custom|skbee) worlds")
            .name("Loaded Custom Worlds")
            .description("Represents all loaded custom worlds created using SkBee.",
                "This will not include worlds created by other plugins, such as MultiVerse.")
            .examples("loop all loaded custom worlds:")
            .since("3.5.3")
            .register();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable World[] get(Event event) {
        return SkBee.getPlugin().getBeeWorldConfig().getLoadedCustomWorlds().toArray(new World[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends World> getReturnType() {
        return World.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "all loaded custom worlds";
    }

}
