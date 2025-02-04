package com.shanebeestudios.skbee.elements.itemcomponent.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@Name("ItemComponent - Clear Components")
@Description({"Clear components of an item. Requires Minecraft 1.21+ and `item_component` feature.",
    "**NOTE**: This will **NOT** clear vanilla components, it will only clear custom added components."})
@Examples({"clear food component of player's tool",
    "clear tool component of player's tool",
    "clear attribute modifier components of player's tool"})
@Since("3.5.8")
public class EffClearComponent extends Effect {

    static {
        Skript.registerEffect(EffClearComponent.class, "clear (:food|:tool|attribute modifier) component[s] of %itemtypes%");
    }

    private int type;
    private Expression<ItemType> itemTypes;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.type = parseResult.hasTag("food") ? 0 : parseResult.hasTag("tool") ? 1 : 2;
        this.itemTypes = (Expression<ItemType>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        for (ItemType itemType : this.itemTypes.getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();
            switch (this.type) {
                case 0 -> itemMeta.setFood(null);
                case 1 -> itemMeta.setTool(null);
                default -> itemMeta.setAttributeModifiers(null);
            }
            itemType.setItemMeta(itemMeta);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String type = switch (this.type) {
            case 0 -> "food";
            case 1 -> "tool";
            default -> "attribute modifiers";
        };
        return "clear " + type + " components of " + this.itemTypes.toString(e,d);
    }

}
