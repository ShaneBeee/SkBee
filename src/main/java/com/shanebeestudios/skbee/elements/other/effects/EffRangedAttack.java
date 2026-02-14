package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.destroystokyo.paper.entity.RangedEntity;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffRangedAttack extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffRangedAttack.class, "make %livingentity% range attack %livingentities% [with charge %-number%]")
            .name("Ranged Attack")
            .description("Make a RangedEntity perform a ranged attack. Requires PaperMC.",
                "\n`with charge` = How \"charged\" the attack is (how far back the bow was pulled for Bow attacks).",
                "This should be a value between 0 and 1, represented as targetDistance/maxDistance.",
                "\nRangedEntities: Drowned, Illusioner, Llama, Piglin, Pillager, Skeleton, Snowman, Stray, TraderLlama, Witch, Wither, WitherSkeleton.")
            .examples("make last spawned entity range attack player with charge 0.5")
            .since("2.17.0")
            .register();
    }

    private Expression<LivingEntity> entity;
    private Expression<LivingEntity> targets;
    private Expression<Number> charge;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entity = (Expression<LivingEntity>) exprs[0];
        this.targets = (Expression<LivingEntity>) exprs[1];
        this.charge = (Expression<Number>) exprs[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        LivingEntity entity = this.entity.getSingle(event);
        if (!(entity instanceof RangedEntity rangedEntity)) {
            return;
        }

        float charge = 1.0f;
        if (this.charge != null) {
            Number chargeNum = this.charge.getSingle(event);
            if (chargeNum != null) charge = chargeNum.floatValue();
        }
        charge = MathUtil.clamp(charge, 0, 1);

        for (LivingEntity livingEntity : this.targets.getArray(event)) {
            rangedEntity.rangedAttack(livingEntity, charge);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String charge = this.charge != null ? (" with charge " + this.charge.toString(e, d)) : "";
        return "make " + this.entity.toString(e, d) + " range attack " + this.targets.toString(e, d) + charge;
    }

}
