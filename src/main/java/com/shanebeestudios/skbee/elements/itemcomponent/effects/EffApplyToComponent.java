package com.shanebeestudios.skbee.elements.itemcomponent.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.registry.KeyUtils;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecConsumableComponent.ConsumeEffectsEvent;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecPotionContentsComponent.PotionContentsEvent;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static com.shanebeestudios.skbee.elements.itemcomponent.sections.SecDeathProtectionComponent.DeathProtectionEvent;

@SuppressWarnings("UnstableApiUsage")
public class EffApplyToComponent extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffApplyToComponent.class,
                "apply -> %potioneffects%",
                "apply -> %potioneffects% with probability %-number%",
                "apply -> remove effects %potioneffecttypes/typedkeys%",
                "apply -> clear all effects",
                "apply -> teleport randomly within [[a] diameter [of]] %number% [blocks|meters]",
                "apply -> play sound %string/typedkey%")
            .name("ItemComponent - Apply Effects")
            .description("Used to apply potion/consume effects in a potion contents's `custom_effects` section, " +
                    "death protection's' `death_effects` section and consumable's `on_consume_effects` section.",
                "",
                "**Patterns**:",
                "- `%potioneffects%` = Used to apply a potion effect in a potion contents section.",
                "- `%potioneffects% with probability %-number%` = " +
                    "Used to apply a potion/consume effect in a death protection/consumable section.",
                "- `remove effects %potioneffecttypes/typedkeys%` = Used to apply a `remove effects` consume effect in a death protection/consumable section.",
                "- `clear all effects` = Used to apply a `clear all effects` consume effect in a death protection/consumable section.",
                "- `teleport randomly within [[a] diameter [of]] %number% [blocks|meters]` = " +
                    "Used to apply a `teleport randomly` consume effect in a death protection/consumable section.",
                "- `play sound %string/typedkey%` - Used to apply a `play sound` consume effect in a death protection/consumable section.")
            .examples("See examples of the respective sections that use this effect.")
            .since("3.8.1")
            .register();
    }

    private int pattern;
    private Expression<?> effects;
    private Expression<?> potionEffectTypes;
    private Expression<Number> numberExp;
    private Expression<?> sound;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (this.pattern == 0 && !getParser().isCurrentEvent(PotionContentsEvent.class)) {
            Skript.error("'apply effect -> potion effects' can only be used in a potion contents section.");
            return false;
        }
        if (this.pattern > 0 && !getParser().isCurrentEvent(ConsumeEffectsEvent.class, DeathProtectionEvent.class)) {
            Skript.error("'apply effect' can only be used in a consumeable/deathprotection sections.");
            return false;
        }

        if (this.pattern < 2) {
            this.effects = exprs[0];
        }
        if (this.pattern == 2) {
            this.potionEffectTypes = exprs[0];
        }
        if (this.pattern == 1) {
            this.numberExp = (Expression<Number>) exprs[1];
        } else if (this.pattern == 4) {
            this.numberExp = (Expression<Number>) exprs[0];
        }
        if (this.pattern == 5) {
            this.sound = exprs[0];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (event instanceof PotionContentsEvent potionEvent) {
            PotionContents.Builder builder = potionEvent.getPotionContentsBuilder();
            for (Object object : this.effects.getArray(event)) {
                if (object instanceof PotionEffect potionEffect) {
                    builder.addCustomEffect(potionEffect);
                }
            }
        } else if (event instanceof ConsumeEffectsEvent consumeEvent) {
            Consumable.Builder builder = consumeEvent.getConsumableBuilder();
            ConsumeEffect effect = createEffect(event);
            if (effect != null) builder.addEffect(effect);
        } else if (event instanceof DeathProtectionEvent deathEvent) {
            DeathProtection.Builder builder = deathEvent.getDeathProtectionBuilder();
            ConsumeEffect effect = createEffect(event);
            if (effect != null) builder.addEffect(effect);
        }
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    private ConsumeEffect createEffect(Event event) {
        return switch (this.pattern) {
            case 1 -> {
                float prob = this.numberExp != null ? this.numberExp.getOptionalSingle(event).orElse(1.0f).floatValue() : 1.0f;
                List<PotionEffect> effects = new ArrayList<>();
                for (Object object : this.effects.getArray(event)) {
                    if (object instanceof PotionEffect potionEffect) effects.add(potionEffect);
                }
                yield ConsumeEffect.applyStatusEffects(effects, Math.clamp(prob, 0f, 1.0f));
            }
            case 2 -> {
                List<TypedKey<PotionEffectType>> keys = new ArrayList<>();
                for (Object object : this.potionEffectTypes.getArray(event)) {
                    if (object instanceof PotionEffectType potionEffectType) {
                        keys.add(TypedKey.create(RegistryKey.MOB_EFFECT, potionEffectType.key()));
                    } else if (object instanceof TypedKey<?> typedKey && typedKey.registryKey() == RegistryKey.MOB_EFFECT) {
                        keys.add((TypedKey<PotionEffectType>) typedKey);
                    }
                }
                RegistryKeySet<PotionEffectType> keySet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, keys);
                yield ConsumeEffect.removeEffects(keySet);
            }
            case 3 -> ConsumeEffect.clearAllStatusEffects();
            case 4 -> {
                float diameter = this.numberExp != null ? this.numberExp.getOptionalSingle(event).orElse(16f).floatValue() : 16f;
                yield ConsumeEffect.teleportRandomlyEffect(diameter);
            }
            case 5 -> {
                Key key = null;
                Object object = this.sound.getSingle(event);
                if (object instanceof String string) {
                    key = KeyUtils.getKey(string);
                } else if (object instanceof TypedKey<?> typedKey && typedKey.registryKey() == RegistryKey.SOUND_EVENT) {
                    key = typedKey.key();
                }
                if (key == null) key = Key.key("block.stone.break");
                yield ConsumeEffect.playSoundConsumeEffect(key);

            }
            default -> null;
        };
    }

    @Override
    public String toString(Event e, boolean d) {
        return switch (this.pattern) {
            case 0 -> "apply -> " + this.effects.toString(e, d);
            case 1 -> "apply " + this.effects.toString(e, d) + " with duration " + this.numberExp.toString(e, d);
            case 2 -> "remove effects " + this.potionEffectTypes.toString(e, d);
            case 3 -> "clear all effects";
            case 4 ->
                "teleport randomly" + (this.numberExp != null ? " within diameter of " + this.numberExp.toString(e, d) : "");
            case 5 -> "play sound " + this.sound.toString(e, d);
            default -> null;
        };
    }

}
