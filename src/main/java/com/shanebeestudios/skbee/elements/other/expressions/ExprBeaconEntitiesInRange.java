package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Beacon - Entities in Effect Range")
@Description("Returns a list of entities in the effect range of a beacon.")
@Examples({"command /effected_entities:",
        "\ttrigger:",
        "\t\tset {_block} to target block of player",
        "\t\tsend all entities in beacon effect range of {_block}"})
@Since("2.16.0")
public class ExprBeaconEntitiesInRange extends SimpleExpression<LivingEntity> {

    static {
        Skript.registerExpression(ExprBeaconEntitiesInRange.class, LivingEntity.class, ExpressionType.SIMPLE,
                "[all [[of] the]|the] %*entitydatas% in [beacon] effect range of %blocks%");
    }

    private EntityData<?>[] entities;
    private Expression<Block> blocks;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        blocks = (Expression<Block>) exprs[1];
        entities = ((Literal<EntityData<?>>) exprs[0]).getArray();
        return true;
    }

    @Override
    @Nullable
    protected LivingEntity[] get(Event event) {
        List<LivingEntity> entities = new ArrayList<>();
        for (Block block : this.blocks.getArray(event)) {
            if (block.getState() instanceof Beacon beacon) {
                for (LivingEntity entity : beacon.getEntitiesInRange()) {
                    if (entities.contains(entity)) continue;
                    entities.add(entity);
                }
            }
        }
        return entities.stream().filter(entity -> {
            for (EntityData<?> Entitydata : this.entities) {
                if (Entitydata.isSupertypeOf(EntityData.fromEntity(entity))) {
                    return true;
                }
            }
            return false;
        }).toList().toArray(new LivingEntity[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends LivingEntity> getReturnType() {
        return LivingEntity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return Classes.toString(this.entities, false) + " in beacon effect range of " + this.blocks.toString(event, debug);
    }

}
