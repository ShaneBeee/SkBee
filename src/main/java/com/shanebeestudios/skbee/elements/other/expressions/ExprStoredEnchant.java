package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Changer.ChangerUtils;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.EnchantmentType;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExprStoredEnchant extends SimpleExpression<EnchantmentType> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprStoredEnchant.class, EnchantmentType.class,
                "stored enchant[ment]s", "itemstacks/itemtypes")
            .name("Stored Enchantments")
            .description("Get/set the stored enchantments of an enchanted book.")
            .examples("set {_a::*} to stored enchants of player's tool",
                "set stored enchants of player's tool to sharpness 3 and unbreaking 2",
                "add sharpness 1 to stored enchants of player's tool",
                "delete stored enchants of player's tool",
                "remove sharpness from stored enchants of player's tool")
            .since("1.0.0")
            .register();
    }

    private Expression<?> itemTypes;


    @SuppressWarnings({"NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.itemTypes = exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected EnchantmentType[] get(Event event) {
        List<EnchantmentType> enchants = new ArrayList<>();
        for (Object item : this.itemTypes.getAll(event)) {
            ItemMeta meta = (item instanceof ItemType) ? ((ItemType) item).getItemMeta() : ((ItemStack) item).getItemMeta();
            if (meta instanceof EnchantmentStorageMeta) {
                Map<Enchantment, Integer> stored = ((EnchantmentStorageMeta) meta).getStoredEnchants();
                for (Enchantment ench : stored.keySet()) {
                    enchants.add(new EnchantmentType(ench, stored.get(ench)));
                }
            }
        }
        return enchants.toArray(new EnchantmentType[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE, DELETE -> CollectionUtils.array(Enchantment[].class, EnchantmentType[].class);
            default -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
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

        for (Object item : this.itemTypes.getAll(event)) {
            ItemMeta meta = (item instanceof ItemStack) ? ((ItemStack) item).getItemMeta() : ((ItemType) item).getItemMeta();
            if (!(meta instanceof EnchantmentStorageMeta storageMeta)) return;

            switch (mode) {
                case SET:
                    for (Enchantment ench : storageMeta.getStoredEnchants().keySet()) {
                        storageMeta.removeStoredEnchant(ench);
                    }
                case ADD:
                    for (EnchantmentType enchant : enchants) {
                        if (enchant.getType() == null) continue;
                        storageMeta.addStoredEnchant(enchant.getType(), enchant.getLevel(), true);
                    }
                    break;
                case REMOVE:
                    for (EnchantmentType enchant : enchants) {
                        if (enchant.getType() == null) continue;
                        storageMeta.removeStoredEnchant(enchant.getType());
                    }
                    break;
                case DELETE:
                    for (Enchantment ench : storageMeta.getStoredEnchants().keySet()) {
                        storageMeta.removeStoredEnchant(ench);
                    }
                    break;
                default:
                    return;
            }
            if ((item instanceof ItemStack)) {
                ((ItemStack) item).setItemMeta(storageMeta);
            } else {
                ((ItemType) item).setItemMeta(storageMeta);
            }
            if (ChangerUtils.acceptsChange(itemTypes, ChangeMode.SET, item.getClass())) {
                Object[] itemDelta = item instanceof ItemStack ? new ItemStack[]{(ItemStack) item} : new ItemType[]{(ItemType) item};
                itemTypes.change(event, itemDelta, ChangeMode.SET);
            } else {
                Object[] itemDelta = item instanceof ItemStack ? new ItemType[]{new ItemType((ItemStack) item)} :
                        new ItemStack[]{((ItemType) item).getRandom()};
                itemTypes.change(event, itemDelta, ChangeMode.SET);
            }
        }
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
        return "stored enchantments of " + this.itemTypes.toString(e, d);
    }

}
