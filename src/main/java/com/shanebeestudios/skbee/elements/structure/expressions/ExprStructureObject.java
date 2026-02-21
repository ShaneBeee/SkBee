package com.shanebeestudios.skbee.elements.structure.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprStructureObject extends SimpleExpression<StructureWrapper> {

    private static StructureManager STRUCTURE_MANAGER;

    public static void register(Registration reg) {
        STRUCTURE_MANAGER = SkBee.getPlugin().getStructureManager();
        reg.newSimpleExpression(ExprStructureObject.class, StructureWrapper.class,
                "structure[s] (named|with id|with key) %strings%")
            .name("Structure - Object")
            .description("Create a new, empty structure or load a structure from file. ",
                "If the file you have specified is not available, it will be created upon saving.",
                "Structures without a namespace (ex: \"tree\") will default to the \"minecraft\" namespace and load from/save to \"(main world folder)/generated/minecraft/structures/\".",
                "Structures with a namespace (ex:\"myname:house\") will load from/save to \"(main world folder)/generated/myname/structures/\".",
                "To create folders, simply add a slash in your name, ex: \"buildings/house\".",
                "Changes made to structures will not automatically be saved to file, you will need to use the save structure effect.")
            .examples("set {_s} to structure with id \"my-server:houses/house1\"",
                "set {_s} to structure with id \"my-house\"",
                "set {_s} to structure with id \"minecraft:village/taiga/houses/taiga_cartographer_house_1\"",
                "set {_s::*} to structures with id \"house1\" and \"house2\"")
            .since("1.12.0")
            .register();
    }

    @SuppressWarnings("null")
    private Expression<String> fileString;

    @SuppressWarnings({"unchecked", "null", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        fileString = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected StructureWrapper[] get(Event event) {
        List<StructureWrapper> structures = new ArrayList<>();
        for (String file : fileString.getAll(event)) {
            assert STRUCTURE_MANAGER != null;
            StructureWrapper structure = STRUCTURE_MANAGER.getStructure(file);
            if (structure != null) {
                structures.add(structure);
            }
        }
        return structures.toArray(new StructureWrapper[0]);
    }

    @Override
    public boolean isSingle() {
        return fileString.isSingle();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<? extends StructureWrapper> getReturnType() {
        return StructureWrapper.class;
    }

    @SuppressWarnings({"NullableProblems"})
    @Override
    public String toString(Event e, boolean d) {
        return "structure[s] with id " + fileString.toString(e, d);
    }

}
