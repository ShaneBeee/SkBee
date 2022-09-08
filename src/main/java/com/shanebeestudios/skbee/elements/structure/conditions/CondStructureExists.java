package com.shanebeestudios.skbee.elements.structure.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.structure.StructureBeeManager;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Structure - Exists")
@Description("Check if a structure exists. This can be helpful to prevent overriding structures.")
@Examples({"if structure \"my_structures:house\" exists:",
        "if structure \"my_structures:town_hall\" doesn't exist:"})
@Since("2.3.0")
public class CondStructureExists extends Condition {

    private static final StructureBeeManager STRUCTURE_BEE_MANAGER;

    static {
        STRUCTURE_BEE_MANAGER = SkBee.getPlugin().getStructureBeeManager();
        Skript.registerCondition(CondStructureExists.class, "structure %string% exists",
                "structure %string% (doesn't|does not) exist");
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
        return this.structure.check(event, STRUCTURE_BEE_MANAGER::structureExists, isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String reg = isNegated() ? " doesn't exist" : " exists";
        return "structure " + this.structure.toString(e, d) + reg;
    }

}
