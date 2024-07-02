package com.shanebeestudios.skbee.elements.text.expressions;

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
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import net.kyori.adventure.text.event.HoverEvent.ShowEntity;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("rawtypes")
@Name("TextComponent - Hover Event")
@Description({"Create a new hover event. Can show texts, text components, an item or an entity to a player.",
    "'showing %itemtype%' requires Minecraft 1.18.2+"})
@Examples({"set {_t} to text component from \"Check out my cool tool!\"",
    "add hover event showing player's tool to {_t}",
    "send component {_t} to player"})
@Since("1.5.0")
public class ExprHoverEvent extends SimpleExpression<HoverEvent> {

    private static final boolean HAS_SHOW_ENITY = Skript.methodExists(ShowEntity.class, "showEntity", Key.class, UUID.class);

    static {
        Skript.registerExpression(ExprHoverEvent.class, HoverEvent.class, ExpressionType.COMBINED,
            "[a] [new] hover event showing %strings/textcomponents/itemstacks/entities%");
    }

    private Expression<?> object;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.object = exprs[0];
        return true;
    }

    @SuppressWarnings({"NullableProblems", "UnstableApiUsage", "deprecation"})
    @Nullable
    @Override
    protected HoverEvent[] get(Event event) {
        if (this.object.isSingle() && this.object.getSingle(event) instanceof Entity entity) {
            Key key = entity.getType().key();
            UUID uuid = entity.getUniqueId();
            ShowEntity showEntity;
            if (HAS_SHOW_ENITY) showEntity = ShowEntity.showEntity(key, uuid);
            else showEntity = ShowEntity.of(key, uuid);
            return new HoverEvent[]{HoverEvent.hoverEvent(Action.SHOW_ENTITY, showEntity)};
        } else if (this.object.isSingle() && this.object.getSingle(event) instanceof ItemStack itemStack) {
            if (itemStack.getType().isAir() || !itemStack.getType().isItem()) {
                itemStack = new ItemStack(Material.STONE);
            }
            return new HoverEvent[]{itemStack.asHoverEvent()};
        } else {
            List<ComponentWrapper> components = new ArrayList<>();
            for (Object object : this.object.getArray(event)) {
                if (object instanceof String string) {
                    components.add(ComponentWrapper.fromText(string));
                } else if (object instanceof ComponentWrapper component) {
                    components.add(component);
                }
            }
            ComponentWrapper newLine = ComponentWrapper.fromText("\n");
            ComponentWrapper hover = ComponentWrapper.fromComponents(components.toArray(new ComponentWrapper[0]), newLine);
            return new HoverEvent[]{HoverEvent.hoverEvent(Action.SHOW_TEXT, hover.getComponent())};
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends HoverEvent> getReturnType() {
        return HoverEvent.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "hover event showing " + this.object.toString(e, d);
    }

}
