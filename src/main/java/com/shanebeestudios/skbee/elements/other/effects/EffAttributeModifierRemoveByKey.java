package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.google.common.collect.Multimap;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Attribute Modifier - Remove By Key")
@Description({"Remove attribute modifiers by key from items/entities.",
    "You can optionally specify the attribute types you want to remove.",
    "Will default to all attribute types."})
@Examples({"remove attribute modifier with key \"my:key\" from player's tool",
    "remove generic scale attribute modifier with key \"my:key\" from player's tool"})
@Since("INSERT VERSION")
public class EffAttributeModifierRemoveByKey extends Effect {

    static {
        Skript.registerEffect(EffAttributeModifierRemoveByKey.class,
            "remove [%-attributetypes%] attribute modifier with key %string/namespacedkey% from %itemtypes/entities%");
    }

    private Expression<Attribute> attributeTypes;
    private Expression<?> key;
    private Expression<?> objects;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.attributeTypes = (Expression<Attribute>) exprs[0];
        this.key = exprs[1];
        this.objects = exprs[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        List<Attribute> attributes = new ArrayList<>();
        if (this.attributeTypes != null) {
            attributes.addAll(Arrays.asList(this.attributeTypes.getArray(event)));
        } else {
            attributes.addAll(RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).stream().toList());
        }

        NamespacedKey key;
        Object keyObject = this.key.getSingle(event);
        if (keyObject instanceof NamespacedKey nsk) key = nsk;
        else if (keyObject instanceof String string) key = Util.getNamespacedKey(string, false);
        else return;

        if (key == null) return;

        for (Object object : this.objects.getArray(event)) {
            if (object instanceof LivingEntity entity) {
                for (Attribute attribute : attributes) {
                    AttributeInstance attributeInstance = entity.getAttribute(attribute);
                    if (attributeInstance == null) continue;
                    attributeInstance.removeModifier(key);
                }
            } else if (object instanceof ItemType itemType) {
                ItemMeta itemMeta = itemType.getItemMeta();
                Multimap<Attribute, AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers();
                if (attributeModifiers == null) continue;

                attributeModifiers.forEach((attribute, attributeModifier) -> {
                    if (attributes.contains(attribute) && attributeModifier.getKey().equals(key)) {
                        itemMeta.removeAttributeModifier(attribute, attributeModifier);
                    }
                });
                itemType.setItemMeta(itemMeta);
            }
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d);
        builder.append("remove");
        if (this.attributeTypes != null) {
            builder.append(this.attributeTypes);
        }
        builder.append("attribute modifier with key", this.key);
        builder.append("from", this.objects);

        return builder.toString();
    }

}
