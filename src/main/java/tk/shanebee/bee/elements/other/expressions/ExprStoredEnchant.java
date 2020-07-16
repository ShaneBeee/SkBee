package tk.shanebee.bee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Changer.ChangerUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.EnchantmentType;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Name("Stored Enchantments")
@Description("Get/set the stored enchantments of an enchanted book.")
@Examples({"set {_a::*} to stored enchants of player's tool",
        "set stored enchants of player's tool to sharpness 3 and unbreaking 2",
        "add sharpness 1 to stored enchants of player's tool",
        "delete stored enchants of player's tool",
        "remove sharpness from stored enchants of player's tool"})
@Since("1.0.0")
public class ExprStoredEnchant extends SimpleExpression<EnchantmentType> {

    static {
        PropertyExpression.register(ExprStoredEnchant.class, EnchantmentType.class,
                "stored enchant[ment]s", "itemstacks/itemtypes");
    }

    private Expression<?> itemTypes;


    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.itemTypes = exprs[0];
        return true;
    }

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

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case ADD:
            case REMOVE:
            case DELETE:
                return CollectionUtils.array(Enchantment[].class, EnchantmentType[].class);
        }
        return null;
    }

    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
        EnchantmentType[] enchants = new EnchantmentType[delta != null ? delta.length : 0];

        if (delta != null && delta.length != 0) {
            for (int i = 0; i < delta.length; i++) {
                if (delta[i] instanceof EnchantmentType)
                    enchants[i] = (EnchantmentType) delta[i];
                else
                    enchants[i] = new EnchantmentType((Enchantment) delta[i]);
            }
        }

        for (Object item : this.itemTypes.getAll(e)) {
            ItemMeta meta = (item instanceof ItemStack) ? ((ItemStack) item).getItemMeta() : ((ItemType) item).getItemMeta();
            if (!(meta instanceof EnchantmentStorageMeta)) return;
            EnchantmentStorageMeta storageMeta = ((EnchantmentStorageMeta) meta);

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
                itemTypes.change(e, itemDelta, ChangeMode.SET);
            } else {
                Object[] itemDelta = item instanceof ItemStack ? new ItemType[]{new ItemType((ItemStack) item)} :
                        new ItemStack[]{((ItemType) item).getRandom()};
                itemTypes.change(e, itemDelta, ChangeMode.SET);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends EnchantmentType> getReturnType() {
        return EnchantmentType.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "stored enchantments of " + this.itemTypes.toString(e, d);
    }

}
