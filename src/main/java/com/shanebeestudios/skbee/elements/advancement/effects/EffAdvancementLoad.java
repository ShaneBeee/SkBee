package com.shanebeestudios.skbee.elements.advancement.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Advancement - Load")
@Description({"Load an advancement represented by the specified string into the server.",
        "The advancement format is governed by Minecraft and has no specified layout.",
        "It is currently a JSON object, as described by [**McWiki**](https://minecraft.wiki/w/Advancement).",
        "Loaded advancements will be stored and persisted across server restarts and reloads.",
        "NOTE: Bukkit has marked this as 'Unsafe', so please use at your own risk.",
        "Watch console for errors when loading an advancement."})
@Examples("¯\\_(ツ)_/¯")
@Since("1.17.0")
public class EffAdvancementLoad extends Effect {

    static {
        Skript.registerEffect(EffAdvancementLoad.class,
                "load advancement %string% with (key|id) %string%");
    }

    private Expression<String> advancement;
    private Expression<String> key;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.advancement = (Expression<String>) exprs[0];
        this.key = (Expression<String>) exprs[1];
        return true;
    }

    @SuppressWarnings({"deprecation"})
    @Override
    protected void execute(Event event) {
        String key = this.key.getSingle(event);
        String advancement = this.advancement.getSingle(event);
        if (key == null || advancement == null) return;

        NamespacedKey namespacedKey = Util.getNamespacedKey(key, true);
        if (namespacedKey != null) {
            try {
                Bukkit.getUnsafe().loadAdvancement(namespacedKey, advancement);
            } catch (Exception ex) {
                if (SkBee.isDebug()) {
                    ex.printStackTrace();
                } else {
                    Util.skriptError("Unable to load advancement with key: '%s'", namespacedKey);
                    Util.skriptError("Error: " + ex.getMessage());
                    Util.skriptError("Enable DEBUG in SkBee config for more info!");
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "load advancement " + this.advancement.toString(e, d) + " with key " + this.key.toString(e, d);
    }

}
