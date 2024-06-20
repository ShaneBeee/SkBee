package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.EnchantmentType;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("NamespacedKey - Get")
@Description({"Get the namespaced key of an object or from a string.",
    "\nNote when getting key from string:",
    "NamespacedKeys are a string based key which consists of two components - a namespace and a key (ex: \"namespace:key\").",
    "Namespaces may only contain lowercase alphanumeric characters, periods, underscores, and hyphens.",
    "Minecraft uses the \"minecraft:\" namespace for built in objects.",
    "If a namespace is not provided, the Minecraft namespace will be used by default -> \"minecraft:your_key\"",
    "Keys may only contain lowercase alphanumeric characters, periods, underscores, hyphens, and forward slashes.",
    "Keep an eye on your console when using namespaced keys as errors will spit out when they're invalid.",
    "For more info please check out [**McWiki**](https://minecraft.wiki/w/Resource_location)."})
@Examples({"set {_key} to mc key of target block",
    "set {_key} to namespaced key of player's tool",
    "set {_key} to minecraft key of biome at player",
    "set {_n} to namespaced key from \"minecraft:log\"",
    "set {_custom} to namespaced key from \"my_server:custom_log\"",
    "set {_n} to namespaced key from \"le_test\""})
@Since("2.6.0")
public class ExprNamespacedKeyObject extends SimplePropertyExpression<Object, NamespacedKey> {

    static {
        Skript.registerExpression(ExprNamespacedKeyObject.class, NamespacedKey.class, ExpressionType.COMBINED,
            "(minecraft|mc|namespaced|resource)[ ](key|id[entifier]|location)[s] [(from|of)] %objects%");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        Expression<Object> objects = LiteralUtils.defendExpression(exprs[0]);
        setExpr(objects);
        return LiteralUtils.canInitSafely(objects);
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public @Nullable NamespacedKey convert(Object object) {
        if (object instanceof String string) {
            return Util.getNamespacedKey(string, true);
        } else if (object instanceof Keyed keyed) {
            return keyed.getKey();
        } else if (object instanceof Block block) {
            return block.getBlockData().getMaterial().getKey();
        } else if (object instanceof BlockData blockData) {
            return blockData.getMaterial().getKey();
        } else if (object instanceof Entity entity) {
            return entity.getType().getKey();
        } else if (object instanceof EntityData<?> entityData) {
            EntityType entityType = EntityUtils.toBukkitEntityType(entityData);
            if (entityType != null) return entityType.getKey();
        } else if (object instanceof ItemType itemType) {
            return itemType.getMaterial().getKey();
        } else if (object instanceof ItemStack itemStack) {
            return itemStack.getType().getKey();
        } else if (object instanceof Slot slot) {
            ItemStack item = slot.getItem();
            if (item != null) return item.getType().getKey();
        } else if (object instanceof EnchantmentType enchantmentType) {
            Enchantment type = enchantmentType.getType();
            if (type != null) return type.getKey();
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
