package com.shanebeestudios.skbee.elements.bound.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.event.bound.BoundCreateEvent;
import com.shanebeestudios.skbee.config.BoundConfig;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprLastCreatedBound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Name("Bound - Copy Bound")
@Description("Create an exact replica of an existing bound with a new id.")
@Examples({"create a copy of bound with id \"some_bound\" with id \"some_bound_copy\"",
        "create a copy of bound with id \"some_bound\" with id \"some_bound_copy\":",
        "\tresize the bound between {_pos1} and {_pos2}",
        "\tadd {_player} to bound owners of bound"})
@Since("2.15.0")
public class EffSecBoundCopy extends EffectSection {

    private static final BoundConfig boundConfig;

    static {
        boundConfig = SkBee.getPlugin().getBoundConfig();
        Skript.registerSection(EffSecBoundCopy.class,
                "create [a] copy of [bound] %bound% (with|using) [the] id %string%");
    }

    private Expression<Bound> bound;
    private Expression<String> id;
    private Trigger trigger;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> list) {
        bound = (Expression<Bound>) exprs[0];
        id = (Expression<String>) exprs[1];
        if (sectionNode != null) {
            AtomicBoolean delayed = new AtomicBoolean(false);
            trigger = loadCode(sectionNode, "bound copy", BoundCreateEvent.class);
            if (delayed.get()) {
                Skript.error("Delays can't be within a Copy Bound section.");
                return false;
            }
        }
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);

        Bound bound = this.bound.getSingle(event);
        String id = this.id.getSingle(event);
        if (id == null || bound == null) return super.walk(event, false);
        bound = bound.copy(bound, id);
        ExprLastCreatedBound.lastCreated = bound;
        boundConfig.saveBound(bound);
        if (trigger != null) {
            BoundCreateEvent boundCreateEvent = new BoundCreateEvent(bound);
            Variables.setLocalVariables(boundCreateEvent, localVars);
            TriggerItem.walk(trigger, boundCreateEvent);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(boundCreateEvent));
            Variables.removeLocals(boundCreateEvent);
        }
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "create a copy of bound " + bound.toString(event, debug) + " with id " + id.toString(event,debug);
    }

}
