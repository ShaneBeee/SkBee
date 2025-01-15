package com.shanebeestudios.skbee.elements.damagesource.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
@Name("DamageSource - Damage Entity")
@Description({"Damage entities using a damage source. ",
    "This has the same functionality as Minecraft's `/damage` command.",
    "Requires MC 1.20.4+"})
@Examples({"set {_source} to damage source from arrow directly by (random element of all entities)",
    "set {_source} to damage source of dragon breath",
    "set {_source} to damage source of magic",
    "set {_source} to damage source of mob_attack_no_aggro caused by target entity of player",
    "damage player by 100 with {_source}"})
@Since("3.3.0")
public class EffEntityDamageSource extends Effect {

    static {
        Skript.registerEffect(EffEntityDamageSource.class,
            "damage %entities% by %number% with %damagesource%");
    }

    private Expression<Entity> entities;
    private Expression<Number> amount;
    private Expression<DamageSource> damageSource;


    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) exprs[0];
        this.amount = (Expression<Number>) exprs[1];
        this.damageSource = (Expression<DamageSource>) exprs[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        DamageSource damageSource = this.damageSource.getSingle(event);
        Number amountNum = this.amount.getSingle(event);
        if (damageSource == null) {
            error("Damage source is not set");
            return;
        }
        if (amountNum == null) {
            error("Amount is not set");
            return;
        }

        double amount = amountNum.doubleValue();
        for (Entity entity : this.entities.getArray(event)) {
            if (entity instanceof Damageable damageable) {
                damageable.damage(amount, damageSource);
            }
        }

    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "damage " + this.entities.toString(e, d) + " by " + this.amount.toString(e, d) +
            " with " + this.damageSource.toString(e, d);
    }

}
