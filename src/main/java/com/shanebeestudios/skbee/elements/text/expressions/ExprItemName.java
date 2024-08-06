package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Item Name")
@Description({"Get/set the component name of an Item.",
    "`(custom|display) name` = Get/set the `custom_name` component of an item just like you would with Skript's name expression.",
    "`item name` = Get/set the `item_name` component of an item (Requires Minecraft 1.20.5+).",
    "Unlike setting the custom/display name of an item, this name cannot be changed through an anvil,",
    "and does not show in some labels, such as banner markers and item frames.",
    "See [**McWiki**](https://minecraft.wiki/w/Data_component_format#item_name) for more details."})
@Examples({"set component custom name of player's tool to translate component of \"item.minecraft.diamond_sword\"",
    "delete component custom name of player's tool",
    "set component item name of player's tool to mini message from \"Stickaxe\"",
    "delete component item name of player's tool"})
@Since("2.4.0")
public class ExprItemName extends SimplePropertyExpression<ItemType, ComponentWrapper> {

    static {
        register(ExprItemName.class, ComponentWrapper.class,
            "component (:item|(custom|display)) name", "itemtypes");
    }

    private boolean itemName;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.itemName = parseResult.hasTag("item");
        if (this.itemName && !ComponentWrapper.HAS_ITEM_NAME) {
            Skript.error("'custom item name' requires Minecraft 1.20.5+");
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable ComponentWrapper convert(ItemType itemType) {
        Component nameComponent;
        if (this.itemName) {
            nameComponent = itemType.getItemMeta().itemName();
        } else {
            nameComponent = itemType.getItemMeta().displayName();
        }
        return ComponentWrapper.fromComponent(nameComponent);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ComponentWrapper.class);
        else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Component component = delta != null && delta[0] instanceof ComponentWrapper comp ? comp.getComponent() : null;

        for (ItemType itemType : getExpr().getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();
            if (this.itemName) itemMeta.itemName(component);
            else itemMeta.displayName(component);
            itemType.setItemMeta(itemMeta);
        }
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        String type = this.itemName ? "item" : "custom";
        return "component" + type + " name";
    }

}
