package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("World AutoSave")
@Description({"Turn on/off world auto saving. This will prevent changes in the world to be saved to file.",
        "\nThis doesn't appear to work when the server stops, so you may need to manually unload your world."})
@Examples("set world autosave of world of player to false")
@Since("2.10.0")
public class ExprWorldAutoSave extends SimplePropertyExpression<World, Boolean> {

    static {
        register(ExprWorldAutoSave.class, Boolean.class, "world auto[ ]save", "worlds");
    }

    @Override
    public @Nullable Boolean convert(World world) {
        return world.isAutoSave();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Boolean bool && mode == ChangeMode.SET) {
            for (World world : getExpr().getArray(event)) {
                world.setAutoSave(bool);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "world auto save";
    }

}
