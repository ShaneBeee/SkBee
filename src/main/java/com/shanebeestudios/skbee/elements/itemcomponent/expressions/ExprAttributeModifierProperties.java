package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Attribute Modifier Properties")
@Description({"Represents the different components of an attribute modifier.",
    "NOTE: These cannot be changed.",
    "`id` = The unique NamespacedKey of a modifier (Requires Minecraft 1.21+).",
    "`name` = The name used to identify this modifier (For Minecraft 1.20.6 and below).",
    "`uuid` = The uuid used to identify this modifier (For Minecraft 1.20.6 and below).",
    "`amount` = Amount of change from the modifier.",
    "`slot` = Equipment Slot Group the item must be in for the modifier to take effect (Minecraft 1.20.6+ uses Equipment Slot Group, other versions use Equipment Slot).",
    "`operation` = The operation of a modifier. See [**McWiki**](https://minecraft.wiki/w/Attribute#Operations) for more details."})
@Examples({"set {_id} to modifier id of {_modifier}",
    "set {_slot} to modifier slot of {_modifier}",
    "if modifier amount of {_mod} > 1:"})
@Since("INSERT VERSION")
public class ExprAttributeModifierProperties extends SimplePropertyExpression<AttributeModifier, Object> {

    static {
        register(ExprAttributeModifierProperties.class, Object.class,
            "[attribute] modifier (0:id|1:name|2:uuid|3:amount|4:slot|5:operation)", "attributemodifiers");
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        if (this.pattern == 0 && !ItemUtils.HAS_KEY) {
            Skript.error("'modifier id' requires Minecraft 1.21+");
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @SuppressWarnings({"deprecation", "removal"})
    @Override
    public @Nullable Object convert(AttributeModifier modifier) {
        return switch (this.pattern) {
            case 1 -> modifier.getName();
            case 2 -> modifier.getUniqueId().toString();
            case 3 -> modifier.getAmount();
            case 4 -> {
                if (ItemUtils.HAS_EQUIPMENT_SLOT_GROUP) {
                    yield modifier.getSlotGroup();
                } else {
                    yield modifier.getSlot();
                }
            }
            case 5 -> modifier.getOperation();
            default -> {
                if (ItemUtils.HAS_KEY) {
                    yield modifier.getKey();
                } else {
                    yield null;
                }
            }
        };
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "modifier" + switch (this.pattern) {
            case 1 -> "name";
            case 2 -> "uuid";
            case 3 -> "amount";
            case 4 -> "slot";
            case 5 -> "operation";
            default -> "id";
        };
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return switch (this.pattern) {
            case 1, 2 -> String.class;
            case 3 -> Number.class;
            case 4 -> {
                if (ItemUtils.HAS_EQUIPMENT_SLOT_GROUP) {
                    yield EquipmentSlotGroup.class;
                } else {
                    yield EquipmentSlot.class;
                }
            }
            case 5 -> Operation.class;
            default -> NamespacedKey.class;
        };
    }

}
