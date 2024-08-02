package com.shanebeestudios.skbee.elements.other.expressions;

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
import com.shanebeestudios.skbee.api.util.EntityUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Name("Attribute Modifiers of Item/LivingEntity")
@Description({"Get/change the attribute modifiers of an item/living entity.",
    "`default` = This option will return the vanilla modifiers of an item (only used for GET).",
    "`transient` = Non-persisent attribute modifier, will not save to the entity's NBT (only used for ADD) (Requires PaperMC).",
    "`modifier` will return a single modifier (will default to the 1st element of modifiers of that attribute type if more than 1 exists).",
    "`modifiers` will return a list of modifiers of that attribute type.",
    "**CHANGERS:**",
    "- `add` = Will add a modifier of an attribute type to an item/living entity.",
    "- `remove` = Remove a specific modifier of an attribute type from an item/living entity.",
    "- `delete` = Will delete all modifiers of an attribute type from an item/living entity."})
@Examples({"set {_mods::*} to attack damage modifier of player's tool",
    "set {_mod} to first element of attack damage modifier of player's tool",
    "set {_mod} to gravity attribute modifier of player",
    "delete gravity attribute modifier of player",
    "add scale modifier of player's tool to scale modifier of player"})
@Since("INSERT VERSION")
public class ExprAttributeModifierOfItem extends SimpleExpression<AttributeModifier> {

    static {
        Skript.registerExpression(ExprAttributeModifierOfItem.class, AttributeModifier.class, ExpressionType.COMBINED,
            "[:default] [:transient] %attributetype% [attribute] modifier[:s] of %itemtypes/livingentities%");
    }

    private boolean single;
    private boolean defaultMods;
    private boolean trans;
    private Expression<Attribute> attribute;
    private Expression<?> objects;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.single = !parseResult.hasTag("s");
        this.defaultMods = parseResult.hasTag("default");
        this.trans = parseResult.hasTag("transient");
        if (this.trans && !EntityUtils.HAS_TRANSIENT) {
            Skript.error("'transient' requires a PaperMC server.");
            return false;
        }
        this.attribute = (Expression<Attribute>) exprs[0];
        this.objects = exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected AttributeModifier @Nullable [] get(Event event) {
        Attribute attribute = this.attribute.getSingle(event);
        if (attribute == null) return null;

        List<AttributeModifier> modifiers = new ArrayList<>();
        for (Object object : this.objects.getArray(event)) {
            if (object instanceof ItemType itemType) {
                ItemMeta itemMeta = itemType.getItemMeta();
                Collection<AttributeModifier> attributeModifiers = this.defaultMods ?
                    itemType.getMaterial().getDefaultAttributeModifiers().get(attribute) : itemMeta.getAttributeModifiers(attribute);

                if (attributeModifiers == null || attributeModifiers.isEmpty()) continue;

                if (this.single) {
                    modifiers.add(attributeModifiers.iterator().next());
                } else {
                    modifiers.addAll(attributeModifiers);
                }
            } else if (object instanceof LivingEntity entity) {
                AttributeInstance attributeInstance = entity.getAttribute(attribute);
                if (attributeInstance == null) continue;

                Collection<AttributeModifier> attributeModifiers = attributeInstance.getModifiers();
                if (attributeModifiers.isEmpty()) continue;

                if (this.single) {
                    modifiers.add(attributeModifiers.iterator().next());
                } else {
                    modifiers.addAll(attributeModifiers);
                }
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
            return CollectionUtils.array(AttributeModifier[].class);
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

        for (Object object : this.objects.getArray(event)) {
            if (object instanceof ItemType itemType) {
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
            } else if (object instanceof LivingEntity entity) {
                AttributeInstance attributeInstance = entity.getAttribute(attribute);
                if (attributeInstance == null) continue;

                if (mode == ChangeMode.DELETE) {
                    attributeInstance.getModifiers().clear();
                } else if (!modifiers.isEmpty()) {
                    for (AttributeModifier modifier : modifiers) {
                        if (mode == ChangeMode.ADD) {
                            if (!EntityUtils.hasAttributeModifier(entity, attribute, modifier)) {
                                if (this.trans) {
                                    attributeInstance.addTransientModifier(modifier);
                                } else {
                                    attributeInstance.addModifier(modifier);
                                }
                            }
                        } else if (mode == ChangeMode.REMOVE) {
                            attributeInstance.removeModifier(modifier);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.single && this.objects.isSingle();
    }

    @Override
    public @NotNull Class<? extends AttributeModifier> getReturnType() {
        return AttributeModifier.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String def = this.defaultMods ? "default " : "";
        String trans = this.trans ? "transient " : "";
        return def + trans + this.attribute.toString(e, d) + " attribute modifier[s] of " + this.objects.toString(e, d);
    }

}
