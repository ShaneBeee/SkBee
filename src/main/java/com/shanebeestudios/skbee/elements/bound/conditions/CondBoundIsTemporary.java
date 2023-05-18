package com.shanebeestudios.skbee.elements.bound.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;

@Name("Bound - Is Temporary")
@Description("Check if a bound is temporary.")
@Examples("if bound with id \"temporary-bound\" is temporary:")
@Since("INSERT VERSION")
public class CondBoundIsTemporary extends PropertyCondition<Bound> {

    static {
        register(CondBoundIsTemporary.class, "temporary", "bound");
    }

    @Override
    public boolean check(Bound bound) {
        return bound.isTemporary();
    }

    @Override
    protected String getPropertyName() {
        return "temporary";
    }

}
