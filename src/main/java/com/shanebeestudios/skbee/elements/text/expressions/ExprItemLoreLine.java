package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExprItemLoreLine extends SimpleExpression<ComponentWrapper> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprItemLoreLine.class, ComponentWrapper.class,
                "line %integer% of [the] component [item] lore of %itemstack/itemtype/slot%",
                "%itemstack/itemtype/slot%'[s] line %integer% of component [item] lore")
            .name("TextComponent - Item Lore Lines")
            .description("Get/set/delete specific lines of lore of an item using text components.")
            .examples("set {_lore} to line 3 of component lore of {_item}",
                "set line 1 of component lore of player's tool to mini message from \"Look Mah, I'm in Minecraft!\"",
                "delete line 6 of component lore of player's tool")
            .since("3.9.0")
            .register();
    }

    private Expression<Integer> line;
    private Expression<?> item;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.line = (Expression<Integer>) exprs[matchedPattern];
        this.item = LiteralUtils.defendExpression(exprs[matchedPattern ^ 1]);
        return true;
    }


    @Override
    protected ComponentWrapper @Nullable [] get(Event event) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(this.item.getSingle(event));
        Integer line = this.line.getSingle(event);
        if (itemStack == null || line == null) return null;

        List<Component> lore = itemStack.lore();
        line = line - 1;
        if (lore != null && lore.size() > line && line >= 0) {
            return new ComponentWrapper[]{ComponentWrapper.fromComponent(lore.get(line))};
        }

        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ComponentWrapper.class);
        else if (mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        Integer l = this.line.getSingle(event);
        if (l == null) return;
        int line = l - 1;

        ItemUtils.modifyItems(this.item.getSingle(event), itemStack -> {
            List<Component> lore = itemStack.lore();
            if (lore == null) lore = Collections.emptyList();

            if (mode == ChangeMode.DELETE) {
                List<Component> newLore = new ArrayList<>();
                for (int i = 0; i < lore.size(); i++) {
                    if (i != line) newLore.add(lore.get(i));
                }
                itemStack.lore(newLore);
            } else {
                Component[] loreArray = lore.toArray(new Component[Math.max(lore.size(), line + 1)]);
                loreArray[line] = delta != null && delta[0] instanceof ComponentWrapper cw ? cw.getComponent() : Component.empty();
                for (int i = 0; i < loreArray.length; i++) {
                    if (loreArray[i] == null) loreArray[i] = Component.empty();
                }
                itemStack.lore(Arrays.asList(loreArray));
            }
        });
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return new SyntaxStringBuilder(e, d)
            .append("line", this.line)
            .append("of component item lore of", this.item)
            .toString();
    }

}
