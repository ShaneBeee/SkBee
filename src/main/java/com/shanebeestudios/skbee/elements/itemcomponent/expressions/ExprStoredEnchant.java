package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.EnchantmentType;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.config.SkBeeMetrics;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class ExprStoredEnchant extends SimpleExpression<EnchantmentType> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprStoredEnchant.class, EnchantmentType.class,
                "stored enchant[ment]s", "itemstacks/itemtypes/slots")
            .name("ItemComponent - Stored Enchantments")
            .description("Get/set the stored enchantments of an enchanted book.")
            .examples("set {_a::*} to stored enchants of player's tool",
                "set stored enchants of player's tool to sharpness 3 and unbreaking 2",
                "add sharpness 1 to stored enchants of player's tool",
                "delete stored enchants of player's tool",
                "remove sharpness from stored enchants of player's tool")
            .since("1.0.0")
            .register();
    }

    private Expression<?> items;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        SkBeeMetrics.Features.ITEM_COMPONENTS.used();
        this.items = expressions[0];
        return true;
    }

    @Override
    protected EnchantmentType[] get(Event event) {
        List<EnchantmentType> enchants = new ArrayList<>();
        ItemComponentUtils.takeAPeakAtComponent(this.items.getArray(event), DataComponentTypes.STORED_ENCHANTMENTS, ie -> {
            ie.enchantments().forEach((e, i) -> enchants.add(new EnchantmentType(e, i)));
        });
        return enchants.toArray(new EnchantmentType[0]);
    }

    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE, DELETE -> CollectionUtils.array(Enchantment[].class, EnchantmentType[].class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object[] delta, ChangeMode mode) {
        EnchantmentType[] enchants = new EnchantmentType[delta != null ? delta.length : 0];

        if (delta != null && delta.length != 0) {
            for (int i = 0; i < delta.length; i++) {
                if (delta[i] instanceof EnchantmentType)
                    enchants[i] = (EnchantmentType) delta[i];
                else
                    enchants[i] = new EnchantmentType((Enchantment) delta[i]);
            }
        }

        DataComponentType.Valued<ItemEnchantments> dataType = DataComponentTypes.STORED_ENCHANTMENTS;
        ItemComponentUtils.modifyComponent(this.items.getArray(event), dataType, (ie, itemStack) -> {
            Map<Enchantment, @IntRange(from = 1L, to = 255L) Integer> enchantments = new java.util.HashMap<>(ie.enchantments());

            switch (mode) {
                case SET -> {
                    enchantments.clear();
                    for (EnchantmentType enchant : enchants) {
                        enchantments.put(enchant.getType(), enchant.getLevel());
                    }
                    itemStack.setData(dataType, ItemEnchantments.itemEnchantments(enchantments));
                }
                case ADD -> {
                    for (EnchantmentType enchant : enchants) {
                        enchantments.put(enchant.getType(), enchant.getLevel());
                    }
                    itemStack.setData(dataType, ItemEnchantments.itemEnchantments(enchantments));
                }
                case REMOVE -> {
                    for (EnchantmentType enchant : enchants) {
                        enchantments.remove(enchant.getType());
                    }
                    itemStack.setData(dataType, ItemEnchantments.itemEnchantments(enchantments));
                }
                case DELETE -> itemStack.setData(dataType, ItemEnchantments.itemEnchantments());
            }
        });
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends EnchantmentType> getReturnType() {
        return EnchantmentType.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "stored enchantments of " + this.items.toString(e, d);
    }

}
