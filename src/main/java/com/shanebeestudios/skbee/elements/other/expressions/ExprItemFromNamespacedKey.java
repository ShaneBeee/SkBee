package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("ItemType From NamespacedKey/BlockData")
@Description("Get an ItemType from a Minecraft namespaced key or BlockData.")
@Examples({"set {_i} to itemtype from namespaced key from \"minecraft:stone\"",
    "set {_i} to itemtype from block data of target block"})
@Since("2.10.0")
public class ExprItemFromNamespacedKey extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprItemFromNamespacedKey.class, ItemType.class, ExpressionType.PROPERTY,
            "item[ ]type[s] (from|of) %namespacedkeys/blockdatas%");
    }

    private Expression<?> objects;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objects = exprs[0];
        return true;
    }

    @Override
    protected @Nullable ItemType[] get(Event event) {
        List<ItemType> itemTypes = new ArrayList<>();
        for (Object object : this.objects.getArray(event)) {
            if (object instanceof NamespacedKey namespacedKey) {
                Material material = Registry.MATERIAL.get(namespacedKey);
                if (material == null) continue;
                itemTypes.add(new ItemType(material));
            } else if (object instanceof BlockData blockData) {
                Material material = blockData.getMaterial();
                itemTypes.add(new ItemType(material));
            }
        }

        return itemTypes.toArray(new ItemType[0]);
    }

    @Override
    public boolean isSingle() {
        return this.objects.isSingle();
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "item type[s] from " + this.objects.toString(e, d);
    }

}
