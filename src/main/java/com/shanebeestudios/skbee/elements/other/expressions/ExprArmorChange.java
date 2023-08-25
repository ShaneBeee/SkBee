package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent.SlotType;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

@Name("Armor Change Event - Item")
@Description("Get the old/new item in an armor change event. Defaults to new. " +
        "When using set, it will set the slot that was changed, old/new will make no difference in this case. " +
        "Requires Paper 1.12.2+")
@Examples({"on player armor change:",
        "\tif new armor item is any helmet:",
        "\t\tset armor item to a diamond helmet"})
@Since("1.3.1")
@SuppressWarnings("NullableProblems")
public class ExprArmorChange extends SimpleExpression<ItemType> {

    static {
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerArmorChangeEvent")) {
            Skript.registerExpression(ExprArmorChange.class, ItemType.class, ExpressionType.SIMPLE,
                    "[(0¦new|1¦old)] armor item");
        }
    }

    private boolean old;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(PlayerArmorChangeEvent.class)) {
            Skript.error("The expression 'armor item' can only be used in an armor change event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        old = parseResult.mark == 1;
        return true;
    }

    @Nullable
    @Override
    protected ItemType[] get(Event e) {
        ItemStack item;
        if (old) {
            item = ((PlayerArmorChangeEvent) e).getOldItem();
        } else {
            item = ((PlayerArmorChangeEvent) e).getNewItem();
        }
        return item != null ? new ItemType[]{new ItemType(item)} : null;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(ItemType[].class);
        } else {
            return null;
        }
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        ItemType item = delta instanceof ItemType[] ? ((ItemType[]) delta)[0] : null;
        if (item == null) return;

        ItemStack itemStack = item.getRandom();
        PlayerInventory playerInv = ((PlayerArmorChangeEvent) e).getPlayer().getInventory();
        SlotType type = ((PlayerArmorChangeEvent) e).getSlotType();
        if (type == SlotType.HEAD) {
            playerInv.setHelmet(itemStack);
        } else if (type == SlotType.CHEST) {
            playerInv.setChestplate(itemStack);
        } else if (type == SlotType.LEGS) {
            playerInv.setLeggings(itemStack);
        } else if (type == SlotType.FEET) {
            playerInv.setBoots(itemStack);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return (old ? "old" : "new") + " armor item";
    }

}
