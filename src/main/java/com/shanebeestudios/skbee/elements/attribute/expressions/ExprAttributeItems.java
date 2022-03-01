package com.shanebeestudios.skbee.elements.attribute.expressions;

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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.shanebeestudios.skbee.api.attribute.AttributePair;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Attribute - Item Attributes")
@Description({"Returns a list of attributes currently on an item. Attributes can be added/set/removed.",
        "The default option gets default attributes of an item. This is not PER item, this is per material of item, and cannot be changed."})
@Examples("set {_a::*} to default attributes of player's tool")
@Since("INSERT VERSION")
public class ExprAttributeItems extends SimpleExpression<AttributePair> {

    static {
        if (Skript.methodExists(Material.class, "getDefaultAttributeModifiers", EquipmentSlot.class)) {
            Skript.registerExpression(ExprAttributeItems.class, AttributePair.class, ExpressionType.COMBINED,
                    "(|1¦default) attribute(s| modifiers) of %itemtype% [using %-equipmentslot%]");
        } else {
            Skript.registerExpression(ExprAttributeItems.class, AttributePair.class, ExpressionType.COMBINED,
                    "attribute(s| modifiers) of %itemtype% [using %-equipmentslot%]");
        }
    }

    private Expression<ItemType> itemType;
    private Expression<EquipmentSlot> equipmentSlot;
    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.itemType = (Expression<ItemType>) exprs[0];
        this.equipmentSlot = (Expression<EquipmentSlot>) exprs[1];
        this.pattern = parseResult.mark;
        return true;
    }

    @Override
    protected @Nullable AttributePair[] get(Event event) {
        List<AttributePair> pairs = new ArrayList<>();
        EquipmentSlot equipmentSlot = this.equipmentSlot != null ? this.equipmentSlot.getSingle(event) : null;
        ItemType itemType = this.itemType.getSingle(event);
        if (itemType == null) return null;

        Multimap<Attribute, AttributeModifier> attributeModifiers;
        if (pattern == 0) {
            ItemMeta itemMeta = itemType.getItemMeta();
            attributeModifiers = itemMeta.getAttributeModifiers();
            if (equipmentSlot != null) {
                attributeModifiers = itemMeta.getAttributeModifiers(equipmentSlot);
            }
        } else {
            if (equipmentSlot == null) {
                equipmentSlot = EquipmentSlot.HAND;
            }
            attributeModifiers = itemType.getMaterial().getDefaultAttributeModifiers(equipmentSlot);

        }
        if (attributeModifiers == null) {
            return null;
        }
        attributeModifiers.forEach((attribute, attributeModifier) -> {
            AttributePair attributePair = new AttributePair(attribute, attributeModifier);
            pairs.add(attributePair);
        });
        return pairs.toArray(new AttributePair[0]);
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (pattern == 0) {
            return CollectionUtils.array(AttributePair[].class, Attribute.class);
        } else {
            return null;
        }
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        ItemType itemType = this.itemType.getSingle(event);
        ItemMeta itemMeta = itemType.getItemMeta();
        AttributePair[] pairs = ((AttributePair[]) delta);

        // CURRENT
        Multimap<Attribute, AttributeModifier> currentAttributes = ArrayListMultimap.create();
        if (this.equipmentSlot == null || this.equipmentSlot.getSingle(event) == null) {
            if (itemMeta.getAttributeModifiers() != null) {
                currentAttributes.putAll(itemMeta.getAttributeModifiers());
            }
        } else {
            EquipmentSlot slot = this.equipmentSlot.getSingle(event);
            currentAttributes.putAll(itemMeta.getAttributeModifiers(slot));
        }

        // NEW
        Multimap<Attribute, AttributeModifier> newAttributes = ArrayListMultimap.create();
        if (pairs != null) {
            for (AttributePair pair : pairs) {
                if (pair.hasModifier()) {
                    newAttributes.put(pair.getAttribute(), pair.getModifier());
                }
            }
        }

        switch (mode) {
            case SET:
                itemMeta.setAttributeModifiers(newAttributes);
                break;
            case ADD:
                currentAttributes.putAll(newAttributes);
                itemMeta.setAttributeModifiers(currentAttributes);
                break;
            case DELETE:
                itemMeta.setAttributeModifiers(null);
                break;
            case REMOVE:
                if (pairs != null) {
                    for (AttributePair pair : pairs) {
                        itemMeta.removeAttributeModifier(pair.getAttribute());
                    }
                } else if (delta[0] instanceof Attribute) {
                    for (Attribute attribute : ((Attribute[]) delta)) {
                        itemMeta.removeAttributeModifier(attribute);
                    }
                }
                break;

        }
        itemType.setItemMeta(itemMeta);

    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends AttributePair> getReturnType() {
        return AttributePair.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        String def = pattern == 1 ? "default " : "";
        String eq = (this.equipmentSlot != null ? " using " + this.equipmentSlot.toString(e, d) : "");
        return def + "attributes of " + this.itemType.toString(e, d) + eq;
    }

}
