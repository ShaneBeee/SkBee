package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprClientBrand extends SimplePropertyExpression<Player, String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprClientBrand.class, String.class, "client brand", "players")
            .name("Client Brand")
            .description("Returns player's client brand name. If the client didn't send this information, the brand name will be null.",
                "For the Notchian client this name defaults to \"vanilla\". Some modified clients report other names such as \"forge\".",
                "Requires a PaperMC server.")
            .examples("broadcast client brand of player")
            .since("2.16.0")
            .register();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.methodExists(Player.class, "getClientBrandName")) {
            Skript.error("'client brand' expression requires a PaperMC server.");
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable String convert(Player player) {
        return player.getClientBrandName();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "client brand";
    }

}
