package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.text.BeeComponent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Text Component - Item Lore")
@Description("Get/set the lore of an item using text components.")
@Examples("set component lore of player's tool to mini message from \"<rainbow>OOO RAINBOW LORE\"")
@Since("2.4.0")
public class ExprLore extends SimpleExpression<BeeComponent> {

    static {
        Skript.registerExpression(ExprLore.class, BeeComponent.class, ExpressionType.PROPERTY,
                "[the] component [item] lore of %itemstack/itemtype/slot%",
                "%itemstack/itemtype/slot%'[s] component [item] lore");
    }

    private Expression<?> item;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.item = exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable BeeComponent[] get(Event event) {
        ItemMeta meta;
        Object item = this.item.getSingle(event);
        if (item instanceof ItemType itemType) {
            meta = itemType.getItemMeta();
        } else if (item instanceof ItemStack itemStack) {
            meta = itemStack.getItemMeta();
        } else if (item instanceof Slot slot) {
            ItemStack slotItem = slot.getItem();
            if (slotItem != null) {
                meta = slotItem.getItemMeta();
            } else {
                return null;
            }
        } else {
            return null;
        }

        List<BeeComponent> components = new ArrayList<>();
        List<Component> lore = meta.lore();
        if (lore != null) {
            lore.forEach(component -> components.add(BeeComponent.fromComponent(component)));
        }
        return components.toArray(new BeeComponent[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD) {
            return CollectionUtils.array(BeeComponent.class);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Object item = this.item.getSingle(event);
        ItemMeta itemMeta;
        if (item instanceof ItemType itemType) {
            itemMeta = itemType.getItemMeta();
        } else if (item instanceof ItemStack itemStack) {
            itemMeta = itemStack.getItemMeta();
        } else if (item instanceof Slot slot) {
            ItemStack slotItem = slot.getItem();
            if (slotItem == null) return;
            itemMeta = slotItem.getItemMeta();
        } else {
            return;
        }

        if (itemMeta == null) return;

        List<Component> lores = mode == ChangeMode.ADD ? itemMeta.lore() : new ArrayList<>();
        if (lores == null) {
            lores = new ArrayList<>();
        }

        for (Object object : delta) {
            if (object instanceof BeeComponent component) {
                lores.add(component.getComponent());
            }
        }
        itemMeta.lore(lores);
        if (item instanceof ItemType itemType) {
            itemType.setItemMeta(itemMeta);
        } else if (item instanceof Slot slot) {
            slot.getItem().setItemMeta(itemMeta);
        } else {
            ItemStack itemStack = (ItemStack) item;
            itemStack.setItemMeta(itemMeta);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends BeeComponent> getReturnType() {
        return BeeComponent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "component item lore of " + this.item.toString(e, d);
    }

}
