package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprItemLore extends SimpleExpression<ComponentWrapper> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprItemLore.class, ComponentWrapper.class,
                "[the] component [item] lore of %itemstack/itemtype/slot%",
                "%itemstack/itemtype/slot%'[s] component [item] lore")
            .name("TextComponent - Item Lore")
            .description("Get/set the lore of an item using text components.")
            .examples("set component lore of player's tool to mini message from \"<rainbow>OOO RAINBOW LORE\"")
            .since("2.4.0")
            .register();
    }

    private Expression<?> item;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.item = exprs[0];
        return true;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected ComponentWrapper @Nullable [] get(Event event) {
        List<ComponentWrapper> components = ItemUtils.getValue(this.item.getSingle(event), itemStack -> {
            List<ComponentWrapper> lores = new ArrayList<>();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasLore()) {
                for (Component component : itemMeta.lore()) {
                    lores.add(ComponentWrapper.fromComponent(component));
                }
            }
            return lores;
        });
        if (components == null) return null;
        return components.toArray(new ComponentWrapper[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD) {
            return CollectionUtils.array(ComponentWrapper[].class, String[].class);
        } else if (mode == ChangeMode.DELETE) {
            return CollectionUtils.array();
        }
        return null;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        List<Component> lores = new ArrayList<>();

        if (delta != null) {
            for (Object object : delta) {
                if (object instanceof ComponentWrapper component) {
                    lores.add(component.getComponent());
                } else if (object instanceof String string) {
                    lores.add(ComponentWrapper.fromText(string).getComponent());
                }
            }
        }

        ItemUtils.modifyItems(this.item.getSingle(event), itemStack -> {
            List<Component> components = new ArrayList<>();
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (mode == ChangeMode.ADD && itemMeta.hasLore()) {
                components.addAll(itemMeta.lore());
            }
            if (!lores.isEmpty()) {
                components.addAll(lores);
            }
            itemMeta.lore(components.isEmpty() ? null : components);
            itemStack.setItemMeta(itemMeta);
        });

    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "component item lore of " + this.item.toString(e, d);
    }

}
