package com.shanebeestudios.skbee.elements.villager.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffVillagerEffects extends Effect {

    private static final boolean CAN_ZOMBIFY = Skript.methodExists(Villager.class, "zombify");
    private static final boolean CAN_WAKEUP = Skript.methodExists(Villager.class, "wakeup");

    public static void register(Registration reg) {
        reg.newEffect(EffVillagerEffects.class,
                "zombify %livingentities%",
                "wake[ ]up %livingentities%",
                "make %livingentities% shake [(his|their)] head[s]",
                "make %livingentities% sleep at %location%")
            .name("Villager - Effects")
            .description("A few effects to make villagers do things.")
            .examples("zombify last spawned villager",
                "wake up all villagers",
                "make target entity shake his head",
                "make target entity sleep at location(100, 64, 100, world \"world\")")
            .since("1.17.0")
            .register();
    }

    private Expression<LivingEntity> entities;
    private Expression<Location> location;
    private int pattern;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (pattern == 0 && !CAN_ZOMBIFY) {
            Skript.error("'zombify %villager%' is not available on your server version.");
            return false;
        }
        if (pattern == 1 && !CAN_WAKEUP) {
            Skript.error("'wakeup %villager%' is not available on your server version.");
            return false;
        }
        this.entities = (Expression<LivingEntity>) exprs[0];
        this.location = matchedPattern == 3 ? (Expression<Location>) exprs[1] : null;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        Location location = this.location != null ? this.location.getSingle(event) : null;
        for (LivingEntity entity : this.entities.getArray(event)) {
            if (entity instanceof Villager villager) {
                switch (this.pattern) {
                    case 0 -> villager.zombify();
                    case 1 -> {
                        if (villager.isSleeping()) villager.wakeup();
                    }
                    case 2 -> villager.shakeHead();
                    case 3 -> sleepVillager(villager, location);
                }
            }
        }
    }

    private void sleepVillager(Villager villager, Location location) {
        if (location != null) {
            // Villager#sleep() can only happen in the same world
            if (villager.getLocation().getWorld() != location.getWorld()) {
                villager.teleport(location);
            }
            villager.sleep(location);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String entity = this.entities.toString(e, d);
        return switch (this.pattern) {
            case 0 -> "zombify " + entity;
            case 1 -> "wakeup " + entity;
            case 2 -> "shake head of " + entity;
            case 3 -> "make " + entity + " sleep at " + this.location.toString(e, d);
            default -> "null";
        };
    }

}
