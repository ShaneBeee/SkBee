package com.shanebeestudios.skbee.elements.structure.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprStructureLastPlacedLocation extends SimplePropertyExpression<StructureWrapper, Location> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprStructureLastPlacedLocation.class, Location.class,
                "last placed location", "structures")
            .name("Structure - Last Placed Location")
            .description("Represents the location of the last place a structure was placed using the place structure effect.",
                "**NOTE**: This will only be saved to file if you use the save effect after placing a structure,",
                "otherwise it will not persist thru stop/restart.")
            .examples("set {_s} to structure named \"test\"",
                "place structure {_s} above traget block",
                "save structure {_s}",
                "set {_last} to last placed location of {_s}")
            .since("2.10.0")
            .register();
    }

    @Override
    public @Nullable Location convert(StructureWrapper structureWrapper) {
        return structureWrapper.getLastPlacedLocation();
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "last placed location";
    }

}
