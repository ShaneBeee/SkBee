package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.Experiments;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Intangible Projectile")
@Description({"If applied, a projectile item can't be picked up by a player when fired, except in creative mode.",
    "If the item does not have this component, it will return null, not false.",
    "See [**Intangible Projectile Component**](https://minecraft.wiki/w/Data_component_format#intangible_projectile) on McWiki for more details.",
    "Requires Paper 1.21.3+ and `item_component` feature.",
    "",
    "**Changers**:",
    "- `set` = If set to true, the component will be applied, otherwise removed.",
    "- `reset` = Reset back to default state.",
    "- `delete` = Will delete any value (vanilla or not)."})
@Examples({"set intangible projectile component of player's tool to true",
    "delete intangible projectile component of player's tool",
    "reset intangible projectile component of player's tool"})
@Since("3.8.0")
@SuppressWarnings("UnstableApiUsage")
public class ExprIntangibleProjectileComponent extends SimplePropertyExpression<Object, Boolean> {

    static {
        register(ExprIntangibleProjectileComponent.class, Boolean.class,
            "intangible projectile component", "itemstacks/itemtypes/slots");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (!getParser().hasExperiment(Experiments.ITEM_COMPONENT)) {
            Skript.error("requires '" + Experiments.ITEM_COMPONENT.codeName() + "' feature.");
            return false;
        }
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Boolean convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null) {
            if (itemStack.hasData(DataComponentTypes.INTANGIBLE_PROJECTILE)) return true;
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET -> CollectionUtils.array(Boolean.class);
            case DELETE, RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        boolean glider = delta != null && delta[0] instanceof Boolean b ? b : false;

        ItemUtils.modifyItems(getExpr().getArray(event), itemStack -> {
            if (mode == ChangeMode.SET) {
                if (glider) itemStack.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
                else itemStack.unsetData(DataComponentTypes.INTANGIBLE_PROJECTILE);
            } else if (mode == ChangeMode.DELETE) {
                itemStack.unsetData(DataComponentTypes.INTANGIBLE_PROJECTILE);
            } else if (mode == ChangeMode.RESET) {
                itemStack.resetData(DataComponentTypes.INTANGIBLE_PROJECTILE);
            }
        });
    }

    @Override
    protected String getPropertyName() {
        return "intangible projectile component";
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

}
