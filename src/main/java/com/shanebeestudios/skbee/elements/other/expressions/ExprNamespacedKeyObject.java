package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("NamespacedKey - From Object")
@Description("Get the namespaced key of an object.")
@Examples({"set {_key} to mc key of target block",
        "set {_key} to namespaced key of player's tool",
        "set {_key} to minecraft key of biome at player"})
@Since("2.6.0")
public class ExprNamespacedKeyObject extends SimplePropertyExpression<Object, NamespacedKey> {

    static {
        register(ExprNamespacedKeyObject.class, NamespacedKey.class,
                "(mc|minecraft|namespaced|resource)[ ](key|id[entifier]|location)", "objects");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        Expression<Object> objects = LiteralUtils.defendExpression(exprs[0]);
        setExpr(objects);
        return LiteralUtils.canInitSafely(objects);
    }

    @Override
    public @Nullable NamespacedKey convert(Object object) {
        if (object instanceof Keyed keyed) {
            return keyed.getKey();
        } else if (object instanceof ItemType itemType) {
            return itemType.getMaterial().getKey();
        } else if (object instanceof ItemStack itemStack) {
            return itemStack.getType().getKey();
        } else if (object instanceof Slot slot) {
            ItemStack item = slot.getItem();
            if (item != null) {
                return item.getType().getKey();
            }
        } else if (object instanceof Block block) {
            return block.getBlockData().getMaterial().getKey();
        } else if (object instanceof BlockData blockData) {
            return blockData.getMaterial().getKey();
        } else if (object instanceof Entity entity) {
            return entity.getType().getKey();
        }
        return null;
    }

    @Override
    public @NotNull Class<? extends NamespacedKey> getReturnType() {
        return NamespacedKey.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "namespaced key";
    }

}
