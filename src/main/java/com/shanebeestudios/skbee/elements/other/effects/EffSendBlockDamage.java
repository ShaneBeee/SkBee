package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffSendBlockDamage extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffSendBlockDamage.class,
                "make %players% see damage of %block% as %number% [(by|from) %-entity/number%]")
            .name("Send Block Damage")
            .description("Send fake block damage to a player.",
                "\nNumber = The amount of damage (a number between 0 and 1) to be applied to the block.",
                "\nEntity = the entity who is damaging the block. An entity can only damage 1 block at a time.",
                "\nBy Entity/Number = The entity/entityID which damaged the block.")
            .examples("make player see damage of target block as 0.5",
                "make player see damage of target block as 0.5 by random element of all entities",
                "make player see damage of target block as 0.5 from random integer between 1 and 10000")
            .since("2.6.0")
            .register();
    }

    private Expression<Player> players;
    private Expression<Block> block;
    private Expression<Number> damage;
    private Expression<?> damager;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.methodExists(Player.class, "sendBlockDamage", Location.class, float.class)) {
            Skript.error("This effect requires a PaperMC server.");
            return false;
        }
        this.players = (Expression<Player>) exprs[0];
        this.block = (Expression<Block>) exprs[1];
        this.damage = (Expression<Number>) exprs[2];
        this.damager = exprs[3];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        float damage = 0.0f;
        Number damageNum = this.damage.getSingle(event);
        if (damageNum != null) damage = damageNum.floatValue();

        damage = MathUtil.clamp(damage, 0.0f, 1.0f);

        int entityID = -1;

        if (this.damager != null) {
            Object damagerObj = this.damager.getSingle(event);
            if (damagerObj instanceof Entity entity) entityID = entity.getEntityId();
            else if (damagerObj instanceof Number number) entityID = number.intValue();
        }

        Block block = this.block.getSingle(event);
        if (block == null) return;
        Location location = block.getLocation();

        for (Player player : this.players.getArray(event)) {
            if (entityID > 0) {
                player.sendBlockDamage(location, damage, entityID);
            } else {
                player.sendBlockDamage(location, damage);
            }
        }

    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String ent = this.damager != null ? " from " + this.damager.toString(e, d) : "";
        return "make " + this.players.toString(e, d) + " see damage of " + this.block.toString(e, d)
            + " as " + this.damage.toString(e, d) + ent;
    }

}
