package com.shanebeestudios.skbee.elements.structure.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprStructureEntities extends SimpleExpression<Entity> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprStructureEntities.class, Entity.class,
                "structure template entities of %structuretemplate%")
            .name("Structure - Template Entities")
            .description("Get a list of entities in a structure template.",
                "This cannot be modified.")
            .examples("loop structure template entities of {_structure}:",
                "set {_size} to size of structure template entities of {_structure}")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<StructureWrapper> structure;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.structure = (Expression<StructureWrapper>) expressions[0];
        return true;
    }

    @Override
    protected Entity @Nullable [] get(Event event) {
        StructureWrapper structure = this.structure.getSingle(event);
        if (structure == null) return null;
        return structure.getBukkitStructure().getEntities().toArray(new Entity[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "structure template entities of " + this.structure.toString(event, debug);
    }

}
