package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

@Name("Beacon - Primary/Secondary Effect")
@Description("The primary/secondary potion effect of a beacon.")
@Examples({"on right click on a beacon with a diamond:",
        "\tset primary beacon effect of clicked block to strength",
        "\tclear secondary beacon potion effect"})
@Since("INSERT VERSION")
public class ExprBeaconEffect extends SimplePropertyExpression<Block, PotionEffect> {

    static {
        register(ExprBeaconEffect.class, PotionEffect.class, "(primary|:secondary) beacon [potion] effect", "blocks");
    }

    private boolean useSecondary;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        useSecondary = parseResult.hasTag("secondary");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable PotionEffect convert(Block block) {
        if (block.getState() instanceof Beacon beacon)
            return useSecondary ? beacon.getSecondaryEffect() : beacon.getPrimaryEffect();
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET -> CollectionUtils.array(PotionEffectType.class);
            case DELETE -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        PotionEffectType potionEffectType = delta != null ? (PotionEffectType) delta[0] : null;
        for (Block block : getExpr().getArray(event)) {
            if (block.getState() instanceof Beacon beacon) {
                if (useSecondary) {
                    beacon.setSecondaryEffect(potionEffectType);
                } else {
                    beacon.setPrimaryEffect(potionEffectType);
                }
                beacon.update();
            }
        }
    }

    @Override
    public Class<? extends PotionEffect> getReturnType() {
        return PotionEffect.class;
    }

    @Override
    protected String getPropertyName() {
        return (useSecondary ? "secondary" : "primary") + " beacon effect";
    }

}
