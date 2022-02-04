package com.shanebeestudios.skbee.elements.board.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.board.objects.BeeTeam;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - Color")
@Description("Represents the color of a team. This will be the color of glow (if glowing) as well as display name color.")
@Examples("set team color of team named \"a-team\" to blue")
@Since("1.15.0")
public class ExprTeamColor extends SimplePropertyExpression<BeeTeam, Color> {

    static {
        register(ExprTeamColor.class, Color.class, "[[sk]bee] team color", "beeteams");
    }

    @Nullable
    @Override
    public Color convert(BeeTeam beeTeam) {
        return beeTeam.getColor();
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        switch (mode) {
            case SET:
            case RESET:
                return CollectionUtils.array(Color.class);
        }
        return null;
    }

    @Override
    public void change(@NotNull Event event, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        Color color = ((Color) delta[0]);

        SkriptColor skriptColor = color instanceof SkriptColor ? (SkriptColor) color : null;

        for (BeeTeam beeTeam : getExpr().getArray(event)) {
            if (mode == ChangeMode.SET && skriptColor != null) {
                beeTeam.setColor(skriptColor);
            } else {
                beeTeam.setColor(SkriptColor.WHITE);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "team color";
    }

}
