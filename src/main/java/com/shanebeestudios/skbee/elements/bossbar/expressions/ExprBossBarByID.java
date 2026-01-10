package com.shanebeestudios.skbee.elements.bossbar.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.BossBarUtils;
import com.shanebeestudios.skbee.api.util.Util;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BossBar - From ID")
@Description("Get an already created BossBar from ID (this will NOT create a new one).")
@Examples({"delete boss bar with id \"le-bar\"",
    "set {_bar} to boss bar with id \"le-bar\"",
    "add all players to {_bar}"})
@Since("2.14.1")
public class ExprBossBarByID extends SimpleExpression<BossBar> {

    static {
        Skript.registerExpression(ExprBossBarByID.class, BossBar.class, ExpressionType.COMBINED,
            "boss[ ]bar (named|with id|from id) %string%");
    }

    private Expression<String> key;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.key = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected BossBar @Nullable [] get(Event event) {
        String name = this.key.getSingle(event);
        if (name == null) return null;
        NamespacedKey key = Util.getNamespacedKey(name, true);
        if (key != null) {
            BossBar bossBar = BossBarUtils.getByKey(key);
            if (bossBar != null) return new BossBar[]{bossBar};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends BossBar> getReturnType() {
        return BossBar.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "bossbar with id " + this.key.toString(e, d);
    }

}
