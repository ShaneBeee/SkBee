package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "ConstantConditions"})
public class EvtPotionEffect extends SkriptEvent {

    static {
        Skript.registerEvent("Entity Potion Effect", EvtPotionEffect.class, EntityPotionEffectEvent.class,
                        "[entity] potion effect added",
                        "[entity] potion effect changed",
                        "[entity] potion effect cleared",
                        "[entity] potion effect removed")
                .description("Called when a potion effect is modified on an entity.",
                        "ADDED = When the potion effect is added because the entity didn't have it's type.",
                        "CHANGED = When the entity already had the potion effect type, but the effect is changed.",
                        "CLEARED = When the effect is removed due to all effects being removed.",
                        "REMOVED = When the potion effect type is completely removed.",
                        "event-potioneffect = new effect.",
                        "past event-potioneffect = old effect.",
                        "event-potioneffecttype = type of potion effect.")
                .examples("on potion effect added:",
                        "\tif event-potioneffecttype = night vision:",
                        "\t\tcancel event",
                        "\t\tsend \"NO NIGHT VISION FOR YOU!!!\"",
                        "",
                        "on potion effect added:",
                        "\tif event-potioneffectcause = totem_effect:",
                        "\t\tteleport player to {spawn}",
                        "",
                        "on potion effect changed:",
                        "\tremove event-potioneffecttype from player",
                        "\t",
                        "on potion effect cleared:",
                        "\tif event-entity is a player:",
                        "\t\tbroadcast \"ALL EFFECTS CLEARED FOR: %event-entity%\"",
                        "",
                        "on potion effect removed:",
                        "\tif event-potioneffecttype = night vision:",
                        "\t\tkill event-entity")
                .since("1.17.0");

        EventValues.registerEventValue(EntityPotionEffectEvent.class, PotionEffect.class, new Getter<>() {
            @Override
            public @Nullable PotionEffect get(EntityPotionEffectEvent event) {
                return event.getOldEffect();
            }
        }, -1);

        EventValues.registerEventValue(EntityPotionEffectEvent.class, PotionEffect.class, new Getter<>() {
            @Override
            public @Nullable PotionEffect get(EntityPotionEffectEvent event) {
                return event.getNewEffect();
            }
        }, 0);

        EventValues.registerEventValue(EntityPotionEffectEvent.class, PotionEffectType.class, new Getter<>() {
            @Override
            public @Nullable PotionEffectType get(EntityPotionEffectEvent event) {
                return event.getModifiedType();
            }
        }, 0);

        EventValues.registerEventValue(EntityPotionEffectEvent.class, Cause.class, new Getter<>() {
            @Override
            public @Nullable Cause get(EntityPotionEffectEvent event) {
                return event.getCause();
            }
        }, 0);
    }

    private static final Action[] ACTIONS = new Action[]{Action.ADDED, Action.CHANGED, Action.CLEARED, Action.REMOVED};
    private int eventAction;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.eventAction = matchedPattern;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        if (event instanceof EntityPotionEffectEvent potionEvent) {
            return potionEvent.getAction() == ACTIONS[this.eventAction];
        }
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "entity potion effect " + ACTIONS[this.eventAction];
    }

}
