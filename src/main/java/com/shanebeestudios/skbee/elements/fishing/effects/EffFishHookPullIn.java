package com.shanebeestudios.skbee.elements.fishing.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffFishHookPullIn extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffFishHookPullIn.class, "pull in hooked entit(y|ies) of %entities%")
            .name("Fish Hook - Pull In")
            .description("Pulls in the entity hooked to this fish hook.")
            .examples("pull in hooked entity of {_fishHook}")
            .since("2.8.0")
            .register();
    }

    private Expression<Entity> entities;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        for (Entity entity : this.entities.getArray(event)) {
            if (entity instanceof FishHook fishHook) {
                fishHook.pullHookedEntity();
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "pull in " + this.entities.toString(e, d);
    }

}
