package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public class ExprHoverEvent extends SimpleExpression<HoverEvent> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprHoverEvent.class, HoverEvent.class, "[a] [new] hover event showing %strings/textcomps/itemstacks/entities%")
            .name("TextComponent - Hover Event")
            .description("Create a new hover event. Can show texts, text components, an item or an entity to a player.", "'showing %itemtype%' requires Minecraft 1.18.2+")
            .examples("set {_t} to text component from \"Check out my cool tool!\"",
                "add hover event showing player's tool to {_t}",
                "send component {_t} to player")
            .since("1.5.0")
            .register();
    }

    private Expression<?> object;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.object = exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected HoverEvent[] get(Event event) {
        if (this.object.isSingle() && this.object.getSingle(event) instanceof Entity entity) {
            Key key = entity.getType().key();
            UUID uuid = entity.getUniqueId();
            ShowEntity showEntity = ShowEntity.showEntity(key, uuid);
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

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "hover event showing " + this.object.toString(e, d);
    }

}
