package com.shanebeestudios.skbee.elements.itemcomponent.types;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Math2;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.registry.KeyUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ApplyStatusEffects;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ClearAllStatusEffects;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.RemoveStatusEffects;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.TeleportRandomly;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.skriptlang.skript.common.function.DefaultFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class Types {

    public static void register(Registration reg) {
        reg.newType(ConsumeEffect.class, "consumeeffect")
            .user("consume ?effects?")
            .name("ItemComponent - Consume Effect")
            .description("Represents an effect that is used in a consumable/death_protection item component.",
                "There are 5 consume effects: apply_effects, remove_effects, clear_all_effects, teleport_randomly and play_sound.",
                "Each of these has a function with the same name.")
            .parser(new Parser<>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(ConsumeEffect consumeEffect, int flags) {
                    return getConsumeEffectString(consumeEffect);
                }

                @Override
                public String toVariableNameString(ConsumeEffect consumeEffect) {
                    return toString(consumeEffect, 0);
                }
            })
            .since("3.8.0")
            .register();

        reg.newRegistryType(Registry.DATA_COMPONENT_TYPE, DataComponentType.class,
                true, "datacomponenttype")
            .user("data ?component ?types?")
            .name("Data Component Type")
            .description("Represents the different types of data components.",
                "See [**Data Components**](https://minecraft.wiki/w/Data_component_format#List_of_components) on McWiki for more detailed info.")
            .since("3.11.0")
            .register();

        // Functions (ConsumeEffects)
        DefaultFunction<ConsumeEffect> applyEffectsFunc = DefaultFunction.builder(reg.getAddon(), "apply_effects", ConsumeEffect.class)
            .parameter("potion effects", PotionEffect[].class)
            .parameter("probability", Number.class)
            .build(params -> {
                List<PotionEffect> effects = Arrays.asList(params.get("potion effects"));
                float prob = Math2.fit(0, ((Number) params.get("probability")).floatValue(), 1);
                return ConsumeEffect.applyStatusEffects(effects, prob);
            });
        reg.newFunction(applyEffectsFunc)
            .name("Consume Effects - Apply Effects")
            .description("Create an 'apply_effects' consume effect.",
                "This will create a list of potion effects to be applied when consumed with a chance between 0 and 1.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_p::1} to potion effect of night vision for 10 seconds",
                "set {_p::2} to potion effect of slow mining for 5 seconds",
                "set {_c} to apply_effects({_p::*}, 0.5)")
            .since("3.8.0")
            .register();

        DefaultFunction<ConsumeEffect> removeEffectsFunc = DefaultFunction.builder(reg.getAddon(), "remove_effects", ConsumeEffect.class)
            .parameter("potionEffectTypes", PotionEffectType[].class)
            .build(params -> {
                List<TypedKey<PotionEffectType>> keys = new ArrayList<>();
                for (PotionEffectType pet : ((PotionEffectType[]) params.get("potionEffectTypes"))) {
                    keys.add(TypedKey.create(RegistryKey.MOB_EFFECT, pet.key()));
                }
                RegistryKeySet<PotionEffectType> keySet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, keys);
                return ConsumeEffect.removeEffects(keySet);
            });

        reg.newFunction(removeEffectsFunc)
            .name("Consume Effects - Remove Effects")
            .description("Create a 'remove_effects' consume effect.",
                "This will create a list of potion effect types to be removed when consumed.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_c} to remove_effects(night vision, poison)")
            .since("3.8.0")
            .register();

        DefaultFunction<ConsumeEffect> clearFunc = DefaultFunction.builder(reg.getAddon(), "clear_all_effects", ConsumeEffect.class)
            .build(params -> ConsumeEffect.clearAllStatusEffects());

        reg.newFunction(clearFunc)
            .name("Consume Effects - Clear Effects")
            .description("Create a 'clear_all_effects' consume effect.",
                "This will clear all effects when consumed.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_c} to clear_all_effects()")
            .since("3.8.0")
            .register();

        DefaultFunction<ConsumeEffect> teleportFunc = DefaultFunction.builder(reg.getAddon(), "teleport_randomly", ConsumeEffect.class)
            .parameter("diameter", Number.class)
            .build(params -> {
                Number diameter = params.get("diameter");
                return ConsumeEffect.teleportRandomlyEffect(diameter.floatValue());
            });

        reg.newFunction(teleportFunc)
            .name("Consume Effects - Teleport Randomly")
            .description("Create a 'teleport_randomly' consume effect.",
                "This will make the consumer teleport randomly when consumed.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_c} to teleport_randomly(10)")
            .since("3.8.0")
            .register();

        DefaultFunction<ConsumeEffect> playSoundFunc = DefaultFunction.builder(reg.getAddon(), "play_sound", ConsumeEffect.class)
            .parameter("sound", String.class)
            .build(params -> {
                String sound = params.get("sound");
                Key key = KeyUtils.getKey(sound);
                if (key == null) return null;

                return ConsumeEffect.playSoundConsumeEffect(key);
            });

        reg.newFunction(playSoundFunc)
            .name("Consume Effects - Play Sound")
            .description("Create a 'play_sound' consume effect.",
                "Will play a sound when consumed.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_c} to play_sound(\"minecraft:block.stone.break\")")
            .since("3.8.0")
            .register();
    }

    private static String getConsumeEffectString(ConsumeEffect consumeEffect) {
        if (consumeEffect instanceof ApplyStatusEffects applyStatusEffects) {
            List<String> effects = new ArrayList<>();
            for (PotionEffect effect : applyStatusEffects.effects()) {
                effects.add(Classes.toString(effect));
            }
            return String.format("apply_effects [%s] with probability %s",
                String.join(", ", effects),
                applyStatusEffects.probability());
        } else if (consumeEffect instanceof RemoveStatusEffects removeStatusEffects) {
            List<String> names = new ArrayList<>();
            for (TypedKey<PotionEffectType> removeEffect : removeStatusEffects.removeEffects()) {
                names.add(removeEffect.key().value());
            }
            return String.format("remove_effects [%s]", String.join(", ", names));
        } else if (consumeEffect instanceof ClearAllStatusEffects) {
            return "clear_all_effects";
        } else if (consumeEffect instanceof TeleportRandomly teleportRandomly) {
            return "teleport_randomly within diameter " + teleportRandomly.diameter();
        } else if (consumeEffect instanceof ConsumeEffect.PlaySound playSound) {
            return "play_sound '" + playSound.sound().key() + "'";
        }
        return consumeEffect.toString();
    }

}
