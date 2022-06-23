package com.shanebeestudios.skbee.elements.villager.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.Merchant;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Merchant - Create")
@Description("Creates a new Merchant object with a title.")
@Examples("set {_merch} to new merchant named \"Le-Merchant\"")
@Since("INSERT VERSION")
public class ExprMerchant extends SimpleExpression<Merchant> {

    static {
        Skript.registerExpression(ExprMerchant.class, Merchant.class, ExpressionType.SIMPLE,
                "[new ]merchant named %string%");
    }

    private Expression<String> name;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.name = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
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
