package com.shanebeestudios.skbee.elements.worldcreator.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;

@Name("World Exists")
@Description("Check if a world exists in your world directory.")
@Examples("if world \"my-world\" exists:")
@Since("1.8.0")
public class CondWorldExists extends Condition {

    static {
        Skript.registerCondition(CondWorldExists.class, "world %string% (0¦exists|1¦(does not|doesn't) exist)");
    }

    private Expression<String> world;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        world = (Expression<String>) exprs[0];
        setNegated(parseResult.mark == 1);
        return true;
    }

    @Override
    public boolean check(@NotNull Event e) {
        return world.check(e, w -> {
            File file = new File(Bukkit.getWorldContainer(), w);
            return file.exists() && file.isDirectory();
        }, isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return String.format("%s%s", this.world.toString(e, d), isNegated() ? " does not exist" : " exists");
    }

}
