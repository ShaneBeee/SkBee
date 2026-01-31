package com.shanebeestudios.skbee.elements.registry.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registry.RegistryHolder;
import com.shanebeestudios.skbee.api.registry.RegistryHolders;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Registry - Tag Key Values")
@Description("Get all values from a tag key.")
@Examples({"set {_biomes::*} to tag key values of tag key \"minecraft:has_structure/mineshaft\" from biome registry",
    "loop tag key values of {_tagKey}:"})
@Since("3.16.0")
public class ExprRegistryTagKeyValues extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprRegistryTagKeyValues.class, Object.class, ExpressionType.COMBINED,
            "tag[ ]key values of %tagkey%");
    }

    private Expression<TagKey<?>> tagKey;
    private Class<?> returnType = null;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.tagKey = (Expression<TagKey<?>>) exprs[0];
        if (this.tagKey instanceof Literal<TagKey<?>> literal) {
            this.returnType = RegistryHolders.getRegistryHolder(literal.getSingle().registryKey()).getReturnType();
        }
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected Object @Nullable [] get(Event event) {
        TagKey<?> tagKey = this.tagKey.getSingle(event);
        if (tagKey == null) return null;

        RegistryKey<?> registryKey = tagKey.registryKey();
        RegistryHolder<?, ?> registryHolder = RegistryHolders.getRegistryHolder(registryKey);
        List<?> tagValues = registryHolder.getTagValues((TagKey) tagKey);
        return tagValues.toArray();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        if (this.returnType != null) return this.returnType;
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "tag key values of " + this.tagKey.toString(e, d);
    }

}
