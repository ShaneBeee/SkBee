package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.BiomeSearchResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SecLocateBiome extends Section {

    public static class LocateBiomeEvent extends Event {

        private final @Nullable BiomeSearchResult result;
        private final @Nullable CommandSender sender;

        public LocateBiomeEvent(@Nullable BiomeSearchResult result, @Nullable CommandSender sender) {
            this.result = result;
            this.sender = sender;
        }

        public boolean found() {
            return this.result != null;
        }

        public @Nullable Biome getBiome() {
            if (this.result == null) return null;
            return this.result.getBiome();
        }

        public @Nullable Location getLocation() {
            if (this.result == null) return null;
            return this.result.getLocation();
        }

        public @Nullable CommandSender getSender() {
            return this.sender;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException();
        }
    }

    public static void register(Registration reg) {
        reg.newSection(SecLocateBiome.class,
                "locate biome %biomes/tagkeys% (within|in) radius %number% (around|of) %location%")
            .name("Locate Biome")
            .description("Locates a biome within a specified radius around a given location.",
                "This accepts a single biome, list of biomes or a biome registry tag key.",
                "NOTE: The bigger the radius, the longer it takes to find a biome and the longer this will freeze the main thread.",
                "Once the biome is found, the location and biome will be available in the section.",
                "NOTE: While it might not be deemed thread safe, this does work off the main thread, use with caution.")
            .examples("locate biome beer:plains/temperate_plains in radius 5000 around player:",
                "\tteleport player to block above highest block at event-location",
                "",
                "# Async Example",
                "async run task 0 ticks later:",
                "\tlocate biome (biome registry tag key \"minecraft:is_beach\") in radius 5000 around player:",
                "\t\t# Pass back to the main thread so we can teleport the player",
                "\t\twait 1 tick",
                "\t\tteleport player to block above highest block at event-location")
            .since("INSERT VERSION")
            .register();

        reg.newEventValue(LocateBiomeEvent.class, Location.class)
            .description("Represents the location of the biome found.")
            .converter(LocateBiomeEvent::getLocation)
            .register();
        reg.newEventValue(LocateBiomeEvent.class, Biome.class)
            .description("Represents the biome found.")
            .converter(LocateBiomeEvent::getBiome)
            .register();
        reg.newEventValue(LocateBiomeEvent.class, CommandSender.class)
            .description("Represents the command sender from a parent event.")
            .converter(LocateBiomeEvent::getSender)
            .register();
        reg.newEventValue(LocateBiomeEvent.class, boolean.class)
            .description("Represents whether a biome was found.")
            .patterns("found")
            .converter(LocateBiomeEvent::found)
            .register();
    }

    private Expression<?> biome;
    private Expression<Number> radius;
    private Expression<Location> location;
    private Trigger trigger;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.biome = expressions[0];
        this.radius = (Expression<Number>) expressions[1];
        this.location = (Expression<Location>) expressions[2];
        this.trigger = loadCode(sectionNode, "locate biome", LocateBiomeEvent.class);
        return true;
    }

    @SuppressWarnings({"UnstableApiUsage", "unchecked", "PatternVariableHidesField"})
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Number single = this.radius.getSingle(event);
        if (single == null) return super.walk(event, false);

        int radius = single.intValue();
        Location origin = this.location.getSingle(event);
        if (origin == null) return super.walk(event, false);

        List<Biome> biomes = new ArrayList<>();
        Registry<Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
        for (Object object : this.biome.getArray(event)) {
            if (object instanceof Biome biome) {
                biomes.add(biome);
            } else if (object instanceof TagKey<?> tagKey) {
                if (tagKey.registryKey() != RegistryKey.BIOME) continue;

                biomes.addAll(registry.getTagValues((TagKey<Biome>) tagKey));

            }
        }
        BiomeSearchResult result = origin.getWorld().locateNearestBiome(origin, radius, biomes.toArray(new Biome[0]));
        LocateBiomeEvent locateStructureEvent = new LocateBiomeEvent(result, getSender(event));
        Variables.withLocalVariables(event, locateStructureEvent, () -> {
            TriggerItem.walk(this.trigger, locateStructureEvent);
        });

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "locate biome " + this.biome.toString(event, debug) +
            " within radius " + this.radius.toString(event, debug) +
            " around " + this.location.toString(event, debug);
    }

    @SuppressWarnings({"deprecation", "removal"})
    private static CommandSender getSender(Event event) {
        // TODO use newer methods
        return EventValues.getEventValue(event, CommandSender.class, 0);
    }

}
