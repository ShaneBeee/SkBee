package com.shanebeestudios.skbee.elements.structure.conditions;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CondStructureExists extends Condition {

    private static StructureManager STRUCTURE_MANAGER;

    public static void register(Registration reg) {
        STRUCTURE_MANAGER = SkBee.getPlugin().getStructureManager();
        reg.newCondition(CondStructureExists.class, "structure %string% exists",
                "structure %string% (doesn't|does not) exist")
            .name("Structure - Exists")
            .description("Check if a structure exists. This can be helpful to prevent overriding structures.")
            .examples("if structure \"my_structures:house\" exists:",
                "if structure \"my_structures:town_hall\" doesn't exist:")
            .since("2.3.0")
            .register();
    }

    private Expression<String> structure;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.structure = (Expression<String>) exprs[0];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return this.structure.check(event, STRUCTURE_MANAGER::structureExists, isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String reg = isNegated() ? " doesn't exist" : " exists";
        return "structure " + this.structure.toString(e, d) + reg;
    }

}
