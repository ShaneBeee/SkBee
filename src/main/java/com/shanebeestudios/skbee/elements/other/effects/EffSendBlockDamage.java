package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Send Block Damage")
@Description({"Send fake block damage to a player.",
        "\nEntity = the entity who is damaging the block. Only 1 entity can damage a block at a time."})
@Examples("make player see damage of target block as 0.5")
@Since("2.6.0")
public class EffSendBlockDamage extends Effect {

    static {
        Skript.registerEffect(EffSendBlockDamage.class,
                "make %players% see damage of %block% as %number% [(by|from) %-entity%]");
    }

    private Expression<Player> players;
    private Expression<Block> block;
    private Expression<Number> damage;
    private Expression<Entity> damager;

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
        this.damager = (Expression<Entity>) exprs[3];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        float damage = 0.0f;
        Number damageNum = this.damage.getSingle(event);
        if (damageNum != null) damage = damageNum.floatValue();

        damage = MathUtil.clamp(damage, 0.0f, 1.0f);

        Entity entity = this.damager != null ? this.damager.getSingle(event) : null;
        int entityID = -1;
        if (entity != null) entityID = entity.getEntityId();

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
