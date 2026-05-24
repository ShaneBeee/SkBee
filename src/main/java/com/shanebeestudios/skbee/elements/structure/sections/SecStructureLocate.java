package com.shanebeestudios.skbee.elements.structure.sections;

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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.util.StructureSearchResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SecStructureLocate extends Section {

    public static class LocateStructureEvent extends Event {

        private final @Nullable StructureSearchResult structureSearchResult;
        private final @Nullable CommandSender sender;

        public LocateStructureEvent(@Nullable StructureSearchResult structureSearchResult, @Nullable CommandSender sender) {
            this.structureSearchResult = structureSearchResult;
            this.sender = sender;
        }

        public boolean found() {
            return this.structureSearchResult != null;
        }

        public @Nullable Structure getStructure() {
            if (this.structureSearchResult == null) return null;
            return this.structureSearchResult.getStructure();
        }

        public @Nullable Location getLocation() {
            if (this.structureSearchResult == null) return null;
            return this.structureSearchResult.getLocation();
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
        reg.newSection(SecStructureLocate.class,
                "locate [:unexplored] structure %structure/structuretype% (within|in) radius %number% (around|of) %location%")
            .name("Structure - Locate Structure")
            .description("Locates a Structure/StructureType within a specified radius around a given location.",
                "The `unexplored` option will only find unexplored structures.",
                "NOTE: The bigger the radius, the longer it takes to find a structure and the longer this will freeze the main thread.",
                "Once the structure is found, the location and structure will be available in the section.")
            .examples("locate structure minecraft:village_savanna in radius 5000 around player:",
                "\tteleport player to block above highest block at event-location")
            .since("INSERT VERSION")
            .register();

        reg.newEventValue(LocateStructureEvent.class, Location.class)
            .description("Represents the location of the structure found.")
            .converter(LocateStructureEvent::getLocation)
            .register();
        reg.newEventValue(LocateStructureEvent.class, Structure.class)
            .description("Represents the structure found.")
            .converter(LocateStructureEvent::getStructure)
            .register();
        reg.newEventValue(LocateStructureEvent.class, CommandSender.class)
            .description("Represents the command sender from a parent event.")
            .converter(LocateStructureEvent::getSender)
            .register();
        reg.newEventValue(LocateStructureEvent.class, boolean.class)
            .description("Represents whether a structure was found.")
            .patterns("found")
            .converter(LocateStructureEvent::found)
            .register();
    }

    private Expression<?> structure;
    private Expression<Number> radius;
    private Expression<Location> location;
    private boolean unexplored;
    private Trigger trigger;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.structure = expressions[0];
        this.radius = (Expression<Number>) expressions[1];
        this.location = (Expression<Location>) expressions[2];
        this.unexplored = parseResult.hasTag("unexplored");
        this.trigger = loadCode(sectionNode, "locate structure", LocateStructureEvent.class);
        return true;
    }

    @SuppressWarnings("PatternVariableHidesField")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Number single = this.radius.getSingle(event);
        if (single == null) return super.walk(event, false);

        int radius = single.intValue();
        Location origin = this.location.getSingle(event);
        if (origin == null) return super.walk(event, false);

        Object object = this.structure.getSingle(event);
        if (object instanceof Structure structure) {
            StructureSearchResult result = origin.getWorld().locateNearestStructure(origin, structure, radius, this.unexplored);
            LocateStructureEvent locateStructureEvent = new LocateStructureEvent(result, getSender(event));
            Variables.withLocalVariables(event, locateStructureEvent, () -> {
                TriggerItem.walk(this.trigger, locateStructureEvent);
            });
        } else if (object instanceof StructureType structureType) {
            StructureSearchResult result = origin.getWorld().locateNearestStructure(origin, structureType, radius, this.unexplored);
            LocateStructureEvent locateStructureEvent = new LocateStructureEvent(result, getSender(event));
            Variables.withLocalVariables(event, locateStructureEvent, () -> {
                TriggerItem.walk(this.trigger, locateStructureEvent);
            });
        }

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "locate structure " + this.structure.toString(event, debug) +
            " within radius " + this.radius.toString(event, debug) +
            " around " + this.location.toString(event, debug);
    }

    @SuppressWarnings({"deprecation", "removal"})
    private static CommandSender getSender(Event event) {
        // TODO use newer methods
        return EventValues.getEventValue(event, CommandSender.class, 0);
    }

}
