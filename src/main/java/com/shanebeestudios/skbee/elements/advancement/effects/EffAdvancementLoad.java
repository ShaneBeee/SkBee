package com.shanebeestudios.skbee.elements.advancement.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.SkBeeMetrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffAdvancementLoad extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffAdvancementLoad.class,
                "load advancement %string% with (key|id) %string%")
            .name("Advancement - Load")
            .description("Load an advancement represented by the specified string into the server.",
                "The advancement format is governed by Minecraft.",
                "It is currently a JSON object, as described by [**Advancement Definition**](https://minecraft.wiki/w/Advancement_definition) on McWiki.",
                "NOTE: Bukkit has marked this as 'Unsafe', so please use at your own risk.",
                "Watch console for errors when loading an advancement.")
            .examples("# This example was written for Minecraft 26.1.x",
                "on load:",
                "\tset {_display} to \"{\"\"icon\"\":{\"\"id\"\":\"\"minecraft:dirt\"\"},\"\"title\"\":\"\"Custom Stuff\"\",\"\"description\"\":\"\"Some Custom Stuff\"\",\"\"announce_to_chat\"\":false,\"\"show_toast\"\":false,\"\"background\"\":\"\"minecraft:gui/advancements/backgrounds/stone\"\"}\"",
                "\tset {_criteria} to \"{\"\"trigger\"\":\"\"minecraft:inventory_changed\"\",\"\"conditions\"\":{\"\"items\"\":[{\"\"items\"\":\"\"minecraft:crafting_table\"\"}]}}\"",
                "\tset {_parent} to \"{\"\"display\"\":%{_display}%,\"\"criteria\"\":{\"\"test\"\":%{_criteria}%}}\"",
                "\tload advancement {_parent} with key \"something:root\"",
                "",
                "\tset {_display} to \"{\"\"icon\"\":{\"\"id\"\":\"\"minecraft:carrot\"\"},\"\"title\"\":\"\"Carrot Picker Upper\"\",\"\"description\"\":{\"\"color\"\":\"\"green\"\",\"\"text\"\":\"\"Pickup a carrot\"\"},\"\"frame\"\":\"\"task\"\"}\"",
                "\tset {_criteria} to \"{\"\"trigger\"\":\"\"minecraft:inventory_changed\"\",\"\"conditions\"\":{\"\"items\"\":[{\"\"items\"\":\"\"minecraft:carrot\"\"}]}}\"",
                "\tset {_json} to \"{\"\"parent\"\":\"\"something:root\"\",\"\"display\"\":%{_display}%,\"\"criteria\"\":{\"\"test_carrot\"\":%{_criteria}%}}\"",
                "\tload advancement {_json} with key \"something:carrot_picker_upper\"")
            .since("1.17.0")
            .register();
    }

    private Expression<String> advancement;
    private Expression<String> key;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        SkBeeMetrics.Features.ADVANCEMENTS.used();
        this.advancement = (Expression<String>) expressions[0];
        this.key = (Expression<String>) expressions[1];
        return true;
    }

    @SuppressWarnings({"deprecation", "CallToPrintStackTrace"})
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
