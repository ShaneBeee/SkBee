package com.shanebeestudios.skbee.elements.structure.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Structure - Object")
@Description({"Create a new, empty structure or load a structure from file. ",
        "If the file you have specified is not available, it will be created upon saving.",
        "Structures without a namespace (ex: \"tree\") will load from/save to \"(main world folder)/generated/skbee/structures/\".",
        "Structures with a namespace (ex:\"myname:house\") will load from/save to \"(main world folder)/generated/myname/structures/\".",
        "To create folders, simply add a slash in your name, ex: \"buildings/house\".",
        "Requires MC 1.17.1+"})
@Examples({"set {_s} to structure named \"my-server:houses/house1\"",
        "set {_s} to structure named \"my-house\"",
        "set {_s::*} to structures named \"house1\" and \"house2\""})
@Since("1.12.0")
public class ExprStructureObject extends SimpleExpression<StructureWrapper> {

    private static final StructureManager STRUCTURE_MANAGER;

    static {
        STRUCTURE_MANAGER = SkBee.getPlugin().getStructureManager();
        Skript.registerExpression(ExprStructureObject.class, StructureWrapper.class, ExpressionType.SIMPLE,
                "structure[s] (named|with id|with key) %strings%");
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

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "structure[s] with id " + fileString.toString(e, d);
    }

}
