package com.shanebeestudios.skbee.elements.registry.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registry.KeyUtils;
import com.shanebeestudios.skbee.api.registry.RegistryUtils;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Registry - TagKey from Registry")
@Description({"Get a TagKey from a registry.",
    "You can check if a tag key contains an object. Think of it like a list."})
@Examples({"set {_tagkey} to tag key \"minecraft:wool\" from block registry",
    "if {_tagkey} contains player's tool:",
    "set {_tagkey} to tag key \"my_pack:cool_enchantments\" from enchantment registry",
    "set {_tagkey} to item registry tag key \"minecraft:swords\"",
    "if biome registry tag key \"minecraft:is_forest\" contains biome at player:"})
@Since("3.8.0")
@SuppressWarnings({"rawtypes"})
public class ExprRegistryTagKeyFrom extends SimpleExpression<TagKey> {

    static {
        Skript.registerExpression(ExprRegistryTagKeyFrom.class, TagKey.class, ExpressionType.COMBINED,
            "tag key %string% from %registrykey%",
            "%registrykey% tag key %string%");
    }

    private Expression<String> key;
    private Expression<RegistryKey<Keyed>> registryKey;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.key = (Expression<String>) exprs[matchedPattern];
        this.registryKey = (Expression<RegistryKey<Keyed>>) exprs[matchedPattern ^ 1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected TagKey<?> @Nullable [] get(Event event) {
        String keyString = this.key.getSingle(event);
        RegistryKey<Keyed> registryKey = this.registryKey.getSingle(event);
        if (keyString == null || registryKey == null) return null;
        Key key = KeyUtils.getKey(keyString);
        if (key == null) return null;

        TagKey<Keyed> tagKey = TagKey.create(registryKey, key);

        // Only return the TagKey if the registry actually has that key
        Registry<Keyed> registry = RegistryUtils.getRegistry(registryKey);
        if (registry.hasTag(tagKey)) return new TagKey[]{tagKey};

        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends TagKey> getReturnType() {
        return TagKey.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return new SyntaxStringBuilder(e, d)
            .append("tag key", this.key, "from", this.registryKey).toString();
    }

}
