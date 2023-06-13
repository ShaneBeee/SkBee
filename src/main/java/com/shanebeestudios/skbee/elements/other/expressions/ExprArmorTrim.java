package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.other.type.Types;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ArmorTrim - Item")
@Description({"Represents the armor trim of an item. You can get, set, add or delete/reset.",
        "Requires Minecraft 1.19.4+"})
@Examples({"add armorTrim(gold_material, eye_pattern) to armor trim of player's tool",
        "set armor trim of player's helmet to armorTrim(gold_material, eye_pattern)",
        "delete armor trim of player's leggings",
        "reset armor trim of player's boots"})
@Since("INSERT VERSION")
public class ExprArmorTrim extends SimplePropertyExpression<ItemType, ArmorTrim> {

    static {
        if (Types.HAS_ARMOR_TRIM) {
            register(ExprArmorTrim.class, ArmorTrim.class, "armor trim", "itemtypes");
        }
    }

    @Override
    public @Nullable ArmorTrim convert(ItemType itemType) {
        if (itemType.getItemMeta() instanceof ArmorMeta armorMeta) {
            return armorMeta.getTrim();
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) return CollectionUtils.array();
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD) return CollectionUtils.array(ArmorTrim.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        ArmorTrim trim = null;
        if (delta != null && delta[0] instanceof ArmorTrim armorTrim) trim = armorTrim;
        for (ItemType itemType : getExpr().getArray(event)) {
            if (itemType.getItemMeta() instanceof ArmorMeta armorMeta) {
                armorMeta.setTrim(trim);
                itemType.setItemMeta(armorMeta);
            }
        }
    }

    @Override
    public @NotNull Class<? extends ArmorTrim> getReturnType() {
        return ArmorTrim.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "armor trim";
    }

}
