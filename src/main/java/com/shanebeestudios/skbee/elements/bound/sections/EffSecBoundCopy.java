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
import org.jetbrains.annotations.NotNull;
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

    private static final BoundConfig BOUND_CONFIG = SkBee.getPlugin().getBoundConfig();

    static {
        Skript.registerSection(EffSecBoundCopy.class,
            "create [a] copy of [bound] %bound% (with|using) [the] id %string%");
    }

    private Expression<Bound> bound;
    private Expression<String> id;
    private Trigger trigger;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> list) {
        this.bound = (Expression<Bound>) exprs[0];
        this.id = (Expression<String>) exprs[1];
        if (sectionNode != null) {
            AtomicBoolean delayed = new AtomicBoolean(false);
            Runnable afterLoading = () -> delayed.set(!getParser().getHasDelayBefore().isFalse());
            this.trigger = loadCode(sectionNode, "bound copy", afterLoading, BoundCreateEvent.class);
            if (delayed.get()) {
                Skript.error("Delays can't be within a Copy Bound section.");
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);

        Bound boundToCopy = this.bound.getSingle(event);
        String id = this.id.getSingle(event);
        if (id == null || boundToCopy == null) return super.walk(event, false);
        Bound newBound = boundToCopy.copy(id);
        ExprLastCreatedBound.lastCreated = newBound;
        if (this.trigger != null) {
            BoundCreateEvent boundCreateEvent = new BoundCreateEvent(newBound);
            Variables.setLocalVariables(boundCreateEvent, localVars);
            TriggerItem.walk(this.trigger, boundCreateEvent);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(boundCreateEvent));
            Variables.removeLocals(boundCreateEvent);
        }
        BOUND_CONFIG.saveBound(newBound, true);
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "create a copy of bound " + this.bound.toString(event, debug) + " with id " + this.id.toString(event, debug);
    }

}
