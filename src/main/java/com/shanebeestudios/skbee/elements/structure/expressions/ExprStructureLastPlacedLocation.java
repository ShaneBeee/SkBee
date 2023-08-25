package com.shanebeestudios.skbee.elements.structure.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Structure - Last Placed Location")
@Description({"Represents the location of the last place a structure was placed using the place structure effect.",
        "\nNOTE: This will only be saved to file if you use the save effect after placing a structure,",
        "otherwise it will not persist thru stop/restart."})
@Examples({"set {_s} to structure named \"test\"",
        "place structure {_s} above traget block",
        "save structure {_s}",
        "set {_last} to last placed location of {_s}"})
@Since("2.10.0")
public class ExprStructureLastPlacedLocation extends SimplePropertyExpression<StructureWrapper, Location> {

    static {
        register(ExprStructureLastPlacedLocation.class, Location.class, "last placed location", "structures");
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
