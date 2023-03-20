package com.shanebeestudios.skbee.elements.fishing.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.jetbrains.annotations.NotNull;

@Name("Fish Hook - In Open Water")
@Description({"Check if the fish hook is in open water.",
        "Open water is defined by a 5x4x5 area of water, air and lily pads.",
        "If in open water, treasure items may be caught."})
@Examples("if fish hook is in open water:")
@Since("INSERT VERSION")
public class CondFishHookInOpenWater extends PropertyCondition<Entity> {

    static {
        register(CondFishHookInOpenWater.class, "in open water", "entities");
    }

    @Override
    public boolean check(Entity entity) {
        return entity instanceof FishHook fishHook && fishHook.isInOpenWater();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "in open water";
    }

}
