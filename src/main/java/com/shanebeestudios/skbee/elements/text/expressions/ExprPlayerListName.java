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
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Player List Name/Header/Footer")
@Description("Represents the player list name/header/footer of a player.")
@Examples({"set component player list name of player to mini message from \"<rainbow>%player%\"",
    "reset component player list name of player",
    "set {_comp} to component player list name of player",
    "set component player list header of all players to mini message from \"<rainbow>MY SERVER!!!\""})
@Since("3.5.8")
public class ExprPlayerListName extends SimplePropertyExpression<Player, ComponentWrapper> {

    static {
        register(ExprPlayerListName.class, ComponentWrapper.class,
            "component (player|tab)[ ]list (name|:header|:footer)", "players");
    }

    private int type;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.type = parseResult.hasTag("header") ? 1 : parseResult.hasTag("footer") ? 2 : 0;
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable ComponentWrapper convert(Player player) {
        Component component = switch (this.type) {
            case 1 -> player.playerListHeader();
            case 2 -> player.playerListFooter();
            default -> player.playerListName();
        };
        return ComponentWrapper.fromComponent(component);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.RESET) return CollectionUtils.array(ComponentWrapper.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue", "DataFlowIssue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Component component = delta != null && delta[0] instanceof ComponentWrapper componentWrapper ? componentWrapper.getComponent() : null;
        if (component == null && this.type != 0) component = Component.empty();

        for (Player player : getExpr().getArray(event)) {
            switch (this.type) {
                case 1 -> player.sendPlayerListHeader(component);
                case 2 -> player.sendPlayerListFooter(component);
                default -> player.playerListName(component);
            }
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        String type = this.type == 1 ? "header" : this.type == 2 ? "footer" : "name";
        return "component player list " + type;
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

}
