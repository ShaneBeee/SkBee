package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Player List Name")
@Description("Represents the player list name of a player.")
@Examples({"set component player list name of player to mini message from \"<rainbow>%player%\"",
    "reset component player list name of player",
    "set {_comp} to component player list name of player"})
@Since("INSERT VERSION")
public class ExprPlayerListName extends SimplePropertyExpression<Player, ComponentWrapper> {

    static {
        register(ExprPlayerListName.class, ComponentWrapper.class,
            "component (player|tab)[ ]list name", "players");
    }

    @Override
    public @Nullable ComponentWrapper convert(Player player) {
        return ComponentWrapper.fromComponent(player.playerListName());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.RESET) return CollectionUtils.array(ComponentWrapper.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Component component = delta != null && delta[0] instanceof ComponentWrapper componentWrapper ? componentWrapper.getComponent() : null;

        for (Player player : getExpr().getArray(event)) {
            player.playerListName(component);
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "component player list name";
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

}
