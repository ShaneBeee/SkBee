package com.shanebeestudios.skbee.elements.bound.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.event.Event;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;

@Name("Bound - Contains Location")
@Description("Check if a location is within the bounds of a bounding box.")
@Examples({"on break:",
        "\tif location of event-block is within bound with id \"spawn.bound\":",
        "\t\tcancel event", "",
        "on damage of a player:",
        "\tif victim is within bound {spawn}:",
        "\t\tcancel event"})
@Since("1.0.0")
public class CondBoundContainsLocation extends Condition {

    static {
        PropertyCondition.register(CondBoundContainsLocation.class, "[with]in [bound] %bound%", "locations");
    }

    private Expression<Bound> bound;
    private Expression<Location> locations;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        bound = (Expression<Bound>) exprs[1];
        locations = (Expression<Location>) exprs[0];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        return locations.check(event, location -> bound.getSingle(event).isInRegion(location), isNegated());
    }

    @Override
    public String toString(Event e, boolean d) {
        return PropertyCondition.toString(this, PropertyCondition.PropertyType.BE, e, d, locations,
                "in the bound " + bound.toString(e, d));
    }

}
