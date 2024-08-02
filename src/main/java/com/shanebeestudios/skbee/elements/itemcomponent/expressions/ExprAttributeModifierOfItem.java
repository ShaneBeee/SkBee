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
import com.shanebeestudios.skbee.api.util.ItemUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Name("ItemComponent - Attribute Modifiers of Item")
@Description({"Get/change the attribute modifiers of an item. Requires Minecraft 1.20.5+",
    "The `default` option will return the vanilla modifiers of an item.",
    "`modifier` will return a single modifier (will default to the 1st element of modifiers of that attribute type).",
    "`modifiers` will return a list of modifiers of that attribute type.",
    "**CHANGERS:**",
    "- `add` = Will add a modifier of an attribute type to an item.",
    "- `remove` = Remove a specific modifier of an attribute type from an item.",
    "- `delete` = Will delete all modifiers of an attribute type from an item."})
@Examples({"set {_mods::*} to attack damage modifier of player's tool",
    "set {_mod} to first element of attack damage modifier of player's tool"})
@Since("INSERT VERSION")
public class ExprAttributeModifierOfItem extends SimpleExpression<AttributeModifier> {

    static {
        Skript.registerExpression(ExprAttributeModifierOfItem.class, AttributeModifier.class, ExpressionType.COMBINED,
            "[:default] %attributetype% [attribute] modifier[:s] of %itemtypes%");
    }

    private boolean single;
    private boolean defaultMods;
    private Expression<Attribute> attribute;
    private Expression<ItemType> itemType;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.single = !parseResult.hasTag("s");
        this.defaultMods = parseResult.hasTag("default");
        this.attribute = (Expression<Attribute>) exprs[0];
        this.itemType = (Expression<ItemType>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected AttributeModifier @Nullable [] get(Event event) {
        Attribute attribute = this.attribute.getSingle(event);
        if (attribute == null) return null;

        List<AttributeModifier> modifiers = new ArrayList<>();
        for (ItemType itemType : this.itemType.getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();
            Collection<AttributeModifier> attributeModifiers = this.defaultMods ?
                itemType.getMaterial().getDefaultAttributeModifiers().get(attribute) : itemMeta.getAttributeModifiers(attribute);

            if (attributeModifiers == null || attributeModifiers.isEmpty()) continue;

            if (this.single) {
                modifiers.add(attributeModifiers.iterator().next());
            } else {
                modifiers.addAll(attributeModifiers);
            }
        }
        return modifiers.toArray(new AttributeModifier[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (this.defaultMods) {
            Skript.error("Default Attribute Modifiers of an item cannot be changed.");
            return null;
        }
        if (mode == ChangeMode.DELETE) return CollectionUtils.array();
        else if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE)
            return CollectionUtils.array(AttributeModifier.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Attribute attribute = this.attribute.getSingle(event);
        if (attribute == null) return;

        List<AttributeModifier> modifiers = new ArrayList<>();
        if (delta != null) {
            for (Object object : delta) {
                if (object instanceof AttributeModifier modifier) modifiers.add(modifier);
            }
        }

        for (ItemType itemType : this.itemType.getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();
            if (mode == ChangeMode.DELETE) {
                itemMeta.removeAttributeModifier(attribute);
            } else if (!modifiers.isEmpty()) {
                for (AttributeModifier modifier : modifiers) {
                    if (mode == ChangeMode.ADD) {
                        if (!ItemUtils.hasAttributeModifier(itemMeta, attribute, modifier)) {
                            itemMeta.addAttributeModifier(attribute, modifier);
                        }
                    } else if (mode == ChangeMode.REMOVE) {
                        itemMeta.removeAttributeModifier(attribute, modifier);
                    }
                }
            }
            itemType.setItemMeta(itemMeta);
        }
    }

    @Override
    public boolean isSingle() {
        return this.single && this.itemType.isSingle();
    }

    @Override
    public @NotNull Class<? extends AttributeModifier> getReturnType() {
        return AttributeModifier.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return (this.defaultMods ? "default " : "") + this.attribute.toString(e, d) +
            " attribute modifier[s] of " + this.itemType.toString(e, d);
    }

}
