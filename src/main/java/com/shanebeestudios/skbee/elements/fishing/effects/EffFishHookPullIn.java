package com.shanebeestudios.skbee.elements.fishing.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Fish Hook - Pull In")
@Description("Pulls in the entity hooked to this fish hook.")
@Examples("pull in hooked entity of {_fishHook}")
@Since("2.8.0")
public class EffFishHookPullIn extends Effect {

    static {
        Skript.registerEffect(EffFishHookPullIn.class, "pull in hooked entit(y|ies) of %entities%");
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
        return "pull in " + this.entities.toString(e,d);
    }

}
