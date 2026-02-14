package com.shanebeestudios.skbee.elements.worldcreator.conditions;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;

public class CondWorldExists extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondWorldExists.class,
                "world file %string% (exists|1:(does not|doesn't) exist)")
            .name("World Exists")
            .description("Check if a world exists in your world directory.")
            .examples("if world file \"my-world\" exists:")
            .since("1.8.9")
            .register();
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
        return world.check(e, this::isAWorld, isNegated());
    }

    private boolean isAWorld(String worldName) {
        File worldFile = new File(Bukkit.getWorldContainer(), worldName);
        if (worldFile.exists() && worldFile.isDirectory()) {
            File levelDat = new File(worldFile, "level.dat");
            return levelDat.exists();
        }
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return String.format("world file %s %s", this.world.toString(e, d), isNegated() ? "does not exist" : "exists");
    }

}
