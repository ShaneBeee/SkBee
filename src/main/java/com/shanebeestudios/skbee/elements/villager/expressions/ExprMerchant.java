package com.shanebeestudios.skbee.elements.villager.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.Merchant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprMerchant extends SimpleExpression<Merchant> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprMerchant.class, Merchant.class,
                "[new ]merchant named %string%")
            .name("Merchant - Create")
            .description("Creates a new Merchant object with a title.")
            .examples("set {_merch} to new merchant named \"Le-Merchant\"")
            .since("1.17.0")
            .register();
    }

    private Expression<String> name;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.name = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings({"deprecation"})
    @Override
    protected @Nullable Merchant[] get(Event event) {
        String name = this.name.getSingle(event);
        if (name != null) {
            Merchant merchant = Bukkit.createMerchant(name);
            return new Merchant[]{merchant};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Merchant> getReturnType() {
        return Merchant.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "new merchant named " + name.toString(e, d);
    }

}
