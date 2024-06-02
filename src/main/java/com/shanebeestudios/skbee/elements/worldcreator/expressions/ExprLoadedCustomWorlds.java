package com.shanebeestudios.skbee.elements.worldcreator.expressions;

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
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Loaded Custom Worlds")
@Description({"Represents all loaded custom worlds created using SkBee.",
    "This will not include worlds created by other plugins, such as MultiVerse."})
@Examples("loop all loaded custom worlds:")
@Since("INSERT VERSION")
public class ExprLoadedCustomWorlds extends SimpleExpression<World> {

    static {
        Skript.registerExpression(ExprLoadedCustomWorlds.class, World.class, ExpressionType.SIMPLE,
            "[all] loaded (custom|skbee) worlds");
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
