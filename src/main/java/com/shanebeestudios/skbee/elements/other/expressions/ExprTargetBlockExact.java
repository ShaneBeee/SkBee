package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@Name("Target Block Exact")
@Description({"Returns the exact target block of a living entity.",
        "Unlike Skript's target block expression, this takes the blocks' precise collision shapes into account.",
        "An example would be a torch, Skript's expression won't account for the actual shape of the block.",
        "Will also ignore fluids. Skript's expression is only for players, this one is for all living entities."})
@Examples({"set exact target block of player to stone",
        "set {_t} to exact target block of last spawned entity"})
@Since("INSERT VERSION")
public class ExprTargetBlockExact extends SimplePropertyExpression<LivingEntity, Block> {

    static {
        Skript.registerExpression(ExprTargetBlockExact.class, Block.class, ExpressionType.PROPERTY,
                "exact target[ed] block [of %livingentities%]");
    }

    @Nullable
    @Override
    public Block convert(LivingEntity livingEntity) {
        return livingEntity.getTargetBlockExact(SkriptConfig.maxTargetBlockDistance.value(), FluidCollisionMode.NEVER);
    }

    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    protected String getPropertyName() {
        return "exact target block";
    }

}
