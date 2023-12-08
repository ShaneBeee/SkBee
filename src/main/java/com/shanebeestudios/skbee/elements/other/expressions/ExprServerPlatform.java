package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Server Platform")
@Description({"Returns the platform the server is running on, ex: CraftBukkit, Spigot, Paper.",
        "\nNOTE: This is returned from Skript itself, results may vary when not running a standard Bukkit based server."})
@Examples({"set {_platform} to server platform",
        "parse if server platform = \"Paper\":",
        "\tset {_m} to mini message from \"<gradient:##0EFF87:##0EE2FF>OH HI THERE\"",
        "else:",
        "\tset {_m} to \"&bOH HI THERE\"",
        "send {_m} to player"})
@Since("3.0.1")
public class ExprServerPlatform extends SimpleLiteral<String> {

    private static final String name = Skript.getServerPlatform().name;

    static {
        Skript.registerExpression(ExprServerPlatform.class, String.class, ExpressionType.SIMPLE,
                "server platform");
    }

    public ExprServerPlatform() {
        super(name, false);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "server platform";
    }

}
