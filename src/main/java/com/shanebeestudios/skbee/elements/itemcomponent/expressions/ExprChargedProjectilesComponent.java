package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ExprChargedProjectilesComponent extends SimpleExpression<Object> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprChargedProjectilesComponent.class, Object.class,
                "charged projectiles [component] of %itemstacks/itemtypes/slots%",
                "%itemstacks/itemtypes/slots%'[s] charged projectiles [component]")
            .name("ItemComponent - Charged Projectiles")
            .description("The items loaded as projectiles into a crossbow. If not present, the crossbow is not charged.",
                "See [**Charged Projectiles Component**](https://minecraft.wiki/w/Data_component_format#charged_projectiles) on McWiki for more details.",
                "Requires Paper 1.21.3+",
                "",
                "**Changers**:",
                "- `set` = Set the items to be loaded by the crossbow.",
                "- `reset` = Reset back to default state.",
                "- `delete` = Will delete any value (vanilla or not).")
            .examples("set charged projectiles of player's tool to an arrow and a stick",
                "delete charged projectiles component of player's tool",
                "reset charged projectiles component of player's tool")
            .since("3.8.0")
            .register();
    }

    private Expression<?> items;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.items = exprs[0];
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        List<ItemStack> projectiles = new ArrayList<>();

        for (Object object : this.items.getArray(event)) {
            ItemStack itemStack = ItemUtils.getItemStackFromObjects(object);
            if (itemStack != null) {
                if (itemStack.hasData(DataComponentTypes.CHARGED_PROJECTILES)) {
                    ChargedProjectiles data = itemStack.getData(DataComponentTypes.CHARGED_PROJECTILES);
                    if (data != null) projectiles.addAll(data.projectiles());
                }
            }
        }
        return projectiles.toArray(new ItemStack[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET -> CollectionUtils.array(ItemStack[].class, ItemType[].class, Slot[].class);
            case DELETE, RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        ChargedProjectiles.Builder builder = ChargedProjectiles.chargedProjectiles();

        if (delta != null) {
            for (Object object : delta) {
                ItemStack itemStack = ItemUtils.getItemStackFromObjects(object);
                if (itemStack != null) builder.add(itemStack);
            }
        }

        ChargedProjectiles chargedProjectiles = builder.build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack -> {
            if (mode == ChangeMode.SET) {
                itemStack.setData(DataComponentTypes.CHARGED_PROJECTILES, chargedProjectiles);
            } else if (mode == ChangeMode.DELETE) {
                itemStack.unsetData(DataComponentTypes.CHARGED_PROJECTILES);
            } else if (mode == ChangeMode.RESET) {
                itemStack.resetData(DataComponentTypes.CHARGED_PROJECTILES);
            }
        });
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "charged projectiles component of " + this.items.toString(e, d);
    }

}
