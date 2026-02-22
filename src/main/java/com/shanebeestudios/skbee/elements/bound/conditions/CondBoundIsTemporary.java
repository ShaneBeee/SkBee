package com.shanebeestudios.skbee.elements.bound.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.registration.Registration;

public class CondBoundIsTemporary extends PropertyCondition<Bound> {

    public static void register(Registration reg) {
        PropertyCondition.register(CondBoundIsTemporary.class, "[a] temporary bound[s]", "bounds");
        reg.newCondition(CondBoundIsTemporary.class,
                "%bounds% (is|are) [a] temporary bound[s]",
                "%bounds% (isn't|is not|aren't|are not) [a] temporary bound[s]")
            .name("Bound - Is Temporary")
            .description("Check if a bound is temporary.")
            .examples("if bound with id \"temporary-bound\" is a temporary bound:")
            .since("2.10.0")
            .register();
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
