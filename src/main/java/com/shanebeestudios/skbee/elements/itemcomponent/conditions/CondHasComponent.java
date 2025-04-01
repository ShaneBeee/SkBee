package com.shanebeestudios.skbee.elements.itemcomponent.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - Has Components")
@Description({"Check if an item has a specific item component.",
    "See [Data Component Format](https://minecraft.wiki/w/Data_component_format) on McWiki for more details."})
@Examples({"if player's tool has item component \"minecraft:tooltip_display\":",
    "if {_i} doesnt have data component \"minecraft:glider\":"})
@Since("INSERT VERSION")
public class CondHasComponent extends Condition {

    static {
        Skript.registerCondition(CondHasComponent.class,
            "%itemstacks/itemtypes/slots% (has|neg:doesn't have) (item|data) component %datacomponenttypes%");
    }

    private Expression<Object> items;
    private Expression<DataComponentType> dataComponentTypes;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.items = (Expression<Object>) exprs[0];
        this.dataComponentTypes = (Expression<DataComponentType>) exprs[1];
        setNegated(parseResult.hasTag("neg"));
        return true;
    }

    @Override
    public boolean check(Event event) {
        return this.items.check(event, object -> {
            ItemStack itemStack = ItemUtils.getItemStackFromObjects(object);
            if (itemStack != null) {
                return dataComponentTypes.check(event, itemStack::hasData, isNegated());
            }
            return false;
        });
    }

    @Override
    public String toString(Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d);
        builder.append(this.items);
        builder.append(isNegated() ? "doesn't have" : "has");
        builder.append(this.dataComponentTypes);
        return builder.toString();
    }

}
