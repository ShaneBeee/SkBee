package com.shanebeestudios.skbee.elements.bound.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;

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
