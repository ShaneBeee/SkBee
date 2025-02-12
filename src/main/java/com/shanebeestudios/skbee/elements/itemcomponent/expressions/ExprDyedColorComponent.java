package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Dyed Color")
@Description({"Represents the dyed color component of an item.",
    "This will work on leather armor, or items that have a dye component on their item model.",
    "The `hidden` option will hide this value in the item's tooltip.",
    "See [**ItemModel**](https://minecraft.wiki/w/Data_component_format#dyed_color) on McWiki for more details.",
    "Requires Paper 1.21.3+",
    "",
    "**Changers**:",
    "`set` = Will set the dyed color of the item.",
    "`delete` = Will delete the dyed color of this item.",
    "`reset` = Will reset the dyed color back to the original value."})
@Examples({"set dyed color of player's tool to red",
    "set dyed color of player's tool to rgb(255,100,3)",
    "set hidden dyed color of player's tool to yellow",
    "delete dyed color of player's tool",
    "reset dyed color of {_item}"})
@Since("INSERT VERSION")
@SuppressWarnings("UnstableApiUsage")
public class ExprDyedColorComponent extends SimplePropertyExpression<Object, Color> {

    static {
        register(ExprDyedColorComponent.class, Color.class, "[:hidden] dyed color [component]", "itemstacks/itemtypes/slots");
    }

    private boolean hidden;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.hidden = parseResult.hasTag("hidden");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Color convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null) {
            if (itemStack.hasData(DataComponentTypes.DYED_COLOR)) {
                DyedItemColor data = itemStack.getData(DataComponentTypes.DYED_COLOR);
                if (data != null) {
                    org.bukkit.Color color = data.color();
                    return SkriptColor.fromBukkitColor(color);
                }
            }
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Color.class);
        else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        Color color = delta != null && delta[0] instanceof Color col ? col : null;
        DyedItemColor dyedItemColor = null;
        if (color != null) {
            dyedItemColor = DyedItemColor.dyedItemColor(color.asBukkitColor(), !this.hidden);
        }

        ItemComponentUtils.modifyComponent(getExpr().getArray(event), mode, DataComponentTypes.DYED_COLOR, dyedItemColor);
    }

    @Override
    protected String getPropertyName() {
        return "dyed color component";
    }

    @Override
    public Class<? extends Color> getReturnType() {
        return Color.class;
    }

}
