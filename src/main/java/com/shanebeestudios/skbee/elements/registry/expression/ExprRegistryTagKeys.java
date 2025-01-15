package com.shanebeestudios.skbee.elements.registry.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Registry - TagKeys from Registry")
@Description("Get all the tag keys that belong to a registry.")
@Examples({"loop tag keys of block registry:",
    "set {_keys::*} to tag keys of biome registry"})
@Since("3.8.0")
@SuppressWarnings({"UnstableApiUsage", "rawtypes"})
public class ExprRegistryTagKeys extends SimpleExpression<TagKey> {

    static {
        Skript.registerExpression(ExprRegistryTagKeys.class, TagKey.class, ExpressionType.COMBINED,
            "tag keys (of|from) %registrykey%");
    }

    private Expression<RegistryKey<Keyed>> registryKey;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.registryKey = (Expression<RegistryKey<Keyed>>) exprs[0];
        return true;
    }

    @Override
    protected TagKey<?> @Nullable [] get(Event event) {
        RegistryKey<Keyed> registryKey = this.registryKey.getSingle(event);
        if (registryKey == null) return null;

        Registry<?> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
        List<TagKey<?>> tagKeys = new ArrayList<>();
        registry.getTags().forEach(tag -> tagKeys.add(tag.tagKey()));
        return tagKeys.toArray(new TagKey[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends TagKey> getReturnType() {
        return TagKey.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "tag keys of " + this.registryKey.toString(e, d);
    }

}
