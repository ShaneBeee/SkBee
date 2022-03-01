package com.shanebeestudios.skbee.api.attribute;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an object holding an {@link Attribute} and {@link AttributeModifier}
 */
public class AttributePair {

    private Attribute attribute;
    private AttributeModifier attributeModifier;

    public AttributePair(@NotNull Attribute attribute, @Nullable AttributeModifier attributeModifier) {
        this.attribute = attribute;
        this.attributeModifier = attributeModifier;
    }

    @NotNull
    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(@NotNull Attribute attribute) {
        this.attribute = attribute;
    }

    @Nullable
    public AttributeModifier getModifier() {
        return attributeModifier;
    }

    public void setModifier(@Nullable AttributeModifier attributeModifier) {
        this.attributeModifier = attributeModifier;
    }

    public boolean hasModifier() {
        return this.attributeModifier != null;
    }

    @Override
    public String toString() {
        return "AttributePair{" +
                "attribute=" + attribute +
                ", attributeModifier=" + attributeModifier +
                '}';
    }

}
