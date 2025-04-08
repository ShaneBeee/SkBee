package com.shanebeestudios.skbee.elements.itemcomponent.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.util.Math2;
import com.shanebeestudios.skbee.api.registry.KeyUtils;
import com.shanebeestudios.skbee.api.wrapper.RegistryClassInfo;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class Types {

    private static final ClassInfo<PotionEffect> POTION_EFFECTS = Classes.getExactClassInfo(PotionEffect.class);
    private static final ClassInfo<PotionEffectType> POTION_EFFECT_TYPE = Classes.getExactClassInfo(PotionEffectType.class);

    static {
        ClassInfo<ConsumeEffect> CONSUME_EFFECT_INFO = new ClassInfo<>(ConsumeEffect.class, "consumeeffect")
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
            .since("3.8.0");
        Classes.registerClass(CONSUME_EFFECT_INFO);

        Classes.registerClass(RegistryClassInfo.create(Registry.DATA_COMPONENT_TYPE, DataComponentType.class,
                false, "datacomponenttype")
            .user("data ?component ?types?")
            .name("Data Component Type")
            .description("Represents the different types of data components.")
            .since("INSERT VERSION"));

        // Functions (ConsumeEffects)
        Functions.registerFunction(new SimpleJavaFunction<>("apply_effects", new Parameter[]{
            new Parameter<>("potion effects", POTION_EFFECTS, false, null),
            new Parameter<>("probability", DefaultClasses.NUMBER, true, null)
        }, CONSUME_EFFECT_INFO, true) {
            @Override
            public ConsumeEffect @Nullable [] executeSimple(Object[][] params) {
                List<PotionEffect> effects = Arrays.asList((PotionEffect[]) params[0]);
                float prob = Math2.fit(0, ((Number) params[1][0]).floatValue(), 1);
                ApplyStatusEffects applyStatusEffects = ConsumeEffect.applyStatusEffects(effects, prob);
                return new ConsumeEffect[]{applyStatusEffects};
            }
        }
            .description("Create an 'apply_effects' consume effect.",
                "This will create a list of potion effects to be applied when consumed with a chance between 0 and 1.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_p::1} to potion effect of night vision for 10 seconds",
                "set {_p::2} to potion effect of slow mining for 5 seconds",
                "set {_c} to apply_effects({_p::*}, 0.5)")
            .since("3.8.0"));

        Functions.registerFunction(new SimpleJavaFunction<>("remove_effects", new Parameter[]{
            new Parameter<>("potion effect types", POTION_EFFECT_TYPE, false, null)
        }, CONSUME_EFFECT_INFO, true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public ConsumeEffect @Nullable [] executeSimple(Object[][] params) {
                List<TypedKey<PotionEffectType>> keys = new ArrayList<>();
                for (Object object : params[0]) {
                    if (object instanceof PotionEffectType pet) {
                        keys.add(TypedKey.create(RegistryKey.MOB_EFFECT, pet.key()));
                    }
                }
                RegistryKeySet<PotionEffectType> keySet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, keys);
                return new ConsumeEffect[]{ConsumeEffect.removeEffects(keySet)};
            }
        }
            .description("Create a 'remove_effects' consume effect.",
                "This will create a list of potion effect types to be removed when consumed.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_c} to remove_effects(night vision, poison)")
            .since("3.8.0"));

        Functions.registerFunction(new SimpleJavaFunction<>("clear_all_effects", new Parameter[]{
        }, CONSUME_EFFECT_INFO, true) {
            @Override
            public ConsumeEffect @Nullable [] executeSimple(Object[][] params) {
                return new ConsumeEffect[]{ConsumeEffect.clearAllStatusEffects()};
            }
        }
            .description("Create a 'clear_all_effects' consume effect.",
                "This will clear all effects when consumed.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_c} to clear_all_effects()")
            .since("3.8.0"));

        Functions.registerFunction(new SimpleJavaFunction<>("teleport_randomly", new Parameter[]{
            new Parameter<>("diameter", DefaultClasses.NUMBER, true, null)
        }, CONSUME_EFFECT_INFO, true) {
            @Override
            public ConsumeEffect @Nullable [] executeSimple(Object[][] params) {
                Number diameter = (Number) params[0][0];
                TeleportRandomly teleportRandomly = ConsumeEffect.teleportRandomlyEffect(diameter.floatValue());
                return new ConsumeEffect[]{teleportRandomly};
            }
        }
            .description("Create a 'teleport_randomly' consume effect.",
                "This will make the consumer teleport randomly when consumed.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_c} to teleport_randomly(10)")
            .since("3.8.0"));

        Functions.registerFunction(new SimpleJavaFunction<>("play_sound", new Parameter[]{
            new Parameter<>("sound", DefaultClasses.STRING, true, null)
        }, CONSUME_EFFECT_INFO, true) {
            @Override
            public ConsumeEffect @Nullable [] executeSimple(Object[][] params) {
                String sound = (String) params[0][0];
                Key key = KeyUtils.getKey(sound);
                if (key == null) return null;

                ConsumeEffect.PlaySound playSound = ConsumeEffect.playSoundConsumeEffect(key);
                return new ConsumeEffect[]{playSound};
            }
        }
            .description("Create a 'play_sound' consume effect.",
                "Will play a sound when consumed.",
                "This can be used in a death protection/consumable component.")
            .examples("set {_c} to play_sound(\"minecraft:block.stone.break\")")
            .since("3.8.0"));
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
