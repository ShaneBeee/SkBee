package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        EventValues.registerEventValue(EntityPotionEffectEvent.class, PotionEffect.class, EntityPotionEffectEvent::getOldEffect, EventValues.TIME_PAST);
        EventValues.registerEventValue(EntityPotionEffectEvent.class, PotionEffect.class, EntityPotionEffectEvent::getNewEffect, EventValues.TIME_NOW);
        EventValues.registerEventValue(EntityPotionEffectEvent.class, PotionEffectType.class, EntityPotionEffectEvent::getModifiedType, EventValues.TIME_NOW);
        EventValues.registerEventValue(EntityPotionEffectEvent.class, Cause.class, EntityPotionEffectEvent::getCause, EventValues.TIME_NOW);
    }

    private static final Action[] ACTIONS = new Action[]{Action.ADDED, Action.CHANGED, Action.CLEARED, Action.REMOVED};
    private int eventAction;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.eventAction = matchedPattern;
        return true;
    }

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
