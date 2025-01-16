package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.Experiments;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("ItemComponent - Bundle Contents")
@Description("Represents the contents of a bundle item.")
@Examples({"add 100 diamonds to bundle contents of player's tool",
    "remove all diamonds from bundle contents of player's tool",
    "delete bundle contents of player's tool"})
@Since("3.6.0")
public class ExprBundleContents extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprBundleContents.class, ItemType.class, ExpressionType.COMBINED,
            "bundle contents of %itemtypes%",
            "%itemtypes%'[s] bundle contents");
    }

    private Expression<ItemType> itemTypes;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().hasExperiment(Experiments.ITEM_COMPONENT)) {
            Skript.error("requires '" + Experiments.ITEM_COMPONENT.codeName() + "' feature.");
            return false;
        }
        this.itemTypes = (Expression<ItemType>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ItemType[] get(Event event) {
        List<ItemType> itemTypes = new ArrayList<>();
        for (ItemType itemType : this.itemTypes.getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();
            if (itemMeta instanceof BundleMeta bundleMeta) {
                bundleMeta.getItems().forEach(itemStack -> itemTypes.add(new ItemType(itemStack)));
            }
        }
        return itemTypes.toArray(new ItemType[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE)
            return CollectionUtils.array(ItemType[].class);
        else if (mode == ChangeMode.DELETE)
            return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        List<ItemStack> itemStacks = null;
        List<ItemType> changedTypes = new ArrayList<>();
        if (delta != null) {
            for (Object object : delta) {
                if (object instanceof ItemType itemType) {
                    changedTypes.add(itemType);
                }
            }
            itemStacks = ItemUtils.addItemTypesToList(changedTypes, null);
        }
        for (ItemType itemType : this.itemTypes.getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();
            if (!(itemMeta instanceof BundleMeta bundleMeta)) continue;

            if (mode == ChangeMode.SET) {
                bundleMeta.setItems(itemStacks);
            } else if (mode == ChangeMode.ADD) {
                List<ItemStack> contents = ItemUtils.addItemTypesToList(changedTypes, bundleMeta.getItems());
                bundleMeta.setItems(contents);
            } else if (mode == ChangeMode.REMOVE) {
                List<ItemStack> i = ItemUtils.removeItemTypesFromList(bundleMeta.getItems(), changedTypes);
                bundleMeta.setItems(i);
            } else if (mode == ChangeMode.DELETE) {
                bundleMeta.setItems(itemStacks);
            }

            itemType.setItemMeta(itemMeta);
        }
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "bundle contents of " + this.itemTypes.toString(e, d);
    }

}
