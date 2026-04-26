package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.legacy.LegacyUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Registry;
import org.bukkit.damage.DamageType;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes", "UnstableApiUsage"})
public class ExprDamageResistantComponent extends SimpleExpression<DamageType> {

    private static final Registry<DamageType> DAMAGE_TYPE_REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);

    public static void register(Registration reg) {
        if (!LegacyUtils.IS_RUNNING_MC_26_1_1) return;

        reg.newCombinedExpression(ExprDamageResistantComponent.class, DamageType.class,
                "damage resistant [component] of %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Damage Resistant")
            .description("Represents the damage resistant component of an item.",
                "**Requires Minecraft 26.1+**",
                "If specified, this item is invulnerable to the specified damage types when in entity form or equipped.",
                "See [**Damage Resistant**](https://minecraft.wiki/w/Data_component_format#damage_resistant) on McWiki for more details.",
                "",
                "**Changers**:",
                "`set` = Will set the damage resistant component of the item (Accepts a DamageType TagKey or a list of DamageTypes).",
                "`delete` = Will delete the damage resistant component of this item.",
                "`reset` = Will reset the damage resistant component back to the original value.")
            .examples("set {_damageTypes::*} to damage resistent component of player's tool",
                "set damage resistent component of player's tool to tag key \"minecraft:is_fire\" from damage_type registry",
                "reset damage resistent component of player's tool",
                "delete damage resistent component of player's tool")
            .since("3.18.0")
            .register();
    }

    private Expression<?> items;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.items = expressions[0];
        return true;
    }

    @Override
    protected DamageType @Nullable [] get(Event event) {
        List<DamageType> damageTypes = new ArrayList<>();
        for (Object from : this.items.getArray(event)) {
            ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
            if (itemStack != null && itemStack.hasData(DataComponentTypes.DAMAGE_RESISTANT)) {
                DamageResistant data = itemStack.getData(DataComponentTypes.DAMAGE_RESISTANT);
                if (data != null) {
                    for (TypedKey<DamageType> type : data.types()) {
                        DamageType damageType = DAMAGE_TYPE_REGISTRY.get(type);
                        if (damageType != null) damageTypes.add(damageType);
                    }
                }
            }
        }
        return damageTypes.toArray(new DamageType[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(TagKey.class, DamageType[].class);
        else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        DamageResistant damageResistant = null;
        if (delta != null && delta.length > 0) {
            List<TypedKey<DamageType>> types = new ArrayList<>();
            if (delta[0] instanceof TagKey tagKey && tagKey.registryKey() == RegistryKey.DAMAGE_TYPE) {
                Tag tag = DAMAGE_TYPE_REGISTRY.getTag(tagKey);
                for (Object object : tag) {
                    if (object instanceof DamageType damageType) {
                        TypedKey<DamageType> key = TypedKey.create(RegistryKey.DAMAGE_TYPE, damageType.getKey());
                        types.add(key);
                    }
                }
            } else {
                for (Object object : delta) {
                    if (object instanceof DamageType damageType) {
                        TypedKey<DamageType> key = TypedKey.create(RegistryKey.DAMAGE_TYPE, damageType.getKey());
                        types.add(key);
                    }
                }
            }
            RegistryKeySet<DamageType> typedKeys = RegistrySet.keySet(RegistryKey.DAMAGE_TYPE, types);
            damageResistant = DamageResistant.damageResistant(typedKeys);
        }

        ItemComponentUtils.modifyComponent(this.items.getArray(event), mode,
            DataComponentTypes.DAMAGE_RESISTANT, damageResistant);
    }

    @Override
    public Class<? extends DamageType> getReturnType() {
        return DamageType.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "damage resistant component of " + this.items.toString(event, debug);
    }

}
