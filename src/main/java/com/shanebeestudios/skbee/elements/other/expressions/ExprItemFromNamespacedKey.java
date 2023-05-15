package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.BukkitUnsafe;
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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("ItemType From NamespacedKey")
@Description("Get an ItemType from a Minecraft namespaced key.")
@Examples("set {_i} to itemtype from namespaced key from \"minecraft:stone\"")
@Since("INSERT VERSION")
public class ExprItemFromNamespacedKey extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprItemFromNamespacedKey.class, ItemType.class, ExpressionType.COMBINED,
                "item[ ]type[s] (from|of) %namespacedkeys%");
    }

    private Expression<NamespacedKey> namespacedKeys;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.namespacedKeys = (Expression<NamespacedKey>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ItemType[] get(Event event) {
        List<ItemType> itemTypes = new ArrayList<>();
        for (NamespacedKey namespacedKey : this.namespacedKeys.getArray(event)) {
            Material material = BukkitUnsafe.getMaterialFromMinecraftId(namespacedKey.toString());
            if (material == null) continue;
            ItemType itemType = new ItemType(material);
            itemTypes.add(itemType);
        }
        return itemTypes.toArray(new ItemType[0]);
    }

    @Override
    public boolean isSingle() {
        return this.namespacedKeys.isSingle();
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "item type[s] from " + this.namespacedKeys.toString(e, d);
    }

}
