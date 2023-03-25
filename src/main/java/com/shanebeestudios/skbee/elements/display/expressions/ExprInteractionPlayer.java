package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Interaction - Last Attack/Interaction Player")
@Description({"Represents the last player to attack/interact with an Interaction Entity.", Types.McWiki_INTERACTION})
@Examples("set {_player} to last attack player of {_int}")
@Since("2.8.1")
public class ExprInteractionPlayer extends SimplePropertyExpression<Entity, OfflinePlayer> {

    static {
        register(ExprInteractionPlayer.class, OfflinePlayer.class,
                "last (attack|i:interaction) [offline[ ]]player[s]", "entities");
    }

    private boolean interact;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.interact = parseResult.hasTag("i");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable OfflinePlayer convert(Entity entity) {
        if (entity instanceof Interaction interaction) {
            if (this.interact) {
                Interaction.PreviousInteraction lastInteraction = interaction.getLastInteraction();
                if (lastInteraction != null) return lastInteraction.getPlayer();
            } else {
                Interaction.PreviousInteraction lastAttack = interaction.getLastAttack();
                if (lastAttack != null) return lastAttack.getPlayer();
            }
        }
        return null;
    }

    @Override
    public @NotNull Class<? extends Player> getReturnType() {
        return Player.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "last " + (this.interact ? "interaction" : "attack") + " player";
    }

}
