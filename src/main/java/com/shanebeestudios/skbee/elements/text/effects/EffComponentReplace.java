package com.shanebeestudios.skbee.elements.text.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Text Component - Replace Text")
@Description({"Replace a string with another string or text component in a text component. Supports regex patterns."})
@Examples({"component replace \"puppy\" with \"***\" in {_comp}",
        "component replace \"\\d+\" with \"0\" in {_comp}"})
@Since("INSERT VERSION")
public class EffComponentReplace extends Effect {

    static {
        Skript.registerEffect(EffComponentReplace.class,
                "component replace %strings% with %string/textcomponent% in %textcomponents%");
    }

    private Expression<String> toReplace;
    private Expression<Object> replacement;
    private Expression<ComponentWrapper> components;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.toReplace = (Expression<String>) exprs[0];
        this.replacement = (Expression<Object>) exprs[1];
        this.components = (Expression<ComponentWrapper>) exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        Object replacement = this.replacement.getSingle(event);
        String replacementString = replacement instanceof String s ? s : null;
        ComponentWrapper replacementComp = replacement instanceof ComponentWrapper w ? w : null;
        for (ComponentWrapper component : this.components.getArray(event)) {
            for (String s : this.toReplace.getArray(event)) {
                if (replacementString != null) component.replace(s, replacementString);
                else if (replacementComp != null) component.replace(s, replacementComp);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String toReplace = this.toReplace.toString(e, d);
        String replace = this.replacement.toString(e, d);
        String comp = this.components.toString(e, d);
        return "component replace " + toReplace + " with " + replace + " in " + comp;
    }

}
