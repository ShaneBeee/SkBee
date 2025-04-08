package com.shanebeestudios.skbee.elements.itemcomponent.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - Clear/Reset Components")
@Description({"Clear/reset components of an item. Requires Minecraft 1.21+"})
@Examples({"clear food component of player's tool",
    "clear tool component of player's tool",
    "reset attribute modifier component of player's tool"})
@Since("INSERT VERSION")
public class EffClearComponent extends Effect {

    static {
        Skript.registerEffect(EffClearComponent.class,
            "(clear|remove|unset|:reset) %datacomponenttypes% (item|data) component[s] of %itemstacks/itemtypes/slots%");
    }

    private boolean remove;
    private Expression<DataComponentType> dataTypes;
    private Expression<?> itemTypes;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.remove = !parseResult.hasTag("reset");
        this.dataTypes = (Expression<DataComponentType>) exprs[0];
        this.itemTypes = exprs[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        DataComponentType[] dataTypes = this.dataTypes.getArray(event);
        ItemUtils.modifyItems(this.itemTypes.getArray(event), itemStack -> {
            for (DataComponentType dataType : dataTypes) {
                if (this.remove) {
                    itemStack.unsetData(dataType);
                } else {
                    itemStack.resetData(dataType);
                }
            }
        });
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return new SyntaxStringBuilder(e, d)
            .append(this.remove ? "remove" : "reset")
            .append(this.dataTypes).append("component[s] of")
            .append(this.itemTypes)
            .toString();
    }

}
