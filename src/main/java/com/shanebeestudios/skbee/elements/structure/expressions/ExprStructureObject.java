package com.shanebeestudios.skbee.elements.structure.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import com.shanebeestudios.skbee.config.SkBeeMetrics;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprStructureObject extends SimpleExpression<StructureWrapper> {

    private static StructureManager STRUCTURE_MANAGER;

    public static void register(Registration reg) {
        STRUCTURE_MANAGER = SkBee.getPlugin().getStructureManager();
        reg.newSimpleExpression(ExprStructureObject.class, StructureWrapper.class,
                "structure[s] (named|with id[s]|with key[s]) %strings%",
                "structure template[s] (named|with id[s]|with key[s]) %strings%")
            .name("Structure - Template Object")
            .description("Create a new, empty structure template or load a structure template from file.",
                "If the file you have specified is not available, it will be created upon saving.",
                "Structures without a namespace (ex: \"tree\") will default to the \"minecraft\" namespace and load from/save to \"(main world folder)/generated/minecraft/structure/\".",
                "Structures with a namespace (ex:\"myname:house\") will load from/save to \"(main world folder)/generated/myname/structure/\".",
                "To create folders, simply add a slash in your name, ex: \"buildings/house\".",
                "Changes made to structures will not automatically be saved to file, you will need to use the save structure effect.")
            .examples("set {_s} to structure template with id \"my-server:houses/house1\"",
                "set {_s} to structure template with id \"my-house\"",
                "set {_s} to structure template with id \"minecraft:village/taiga/houses/taiga_cartographer_house_1\"",
                "set {_s::*} to structure templates with id \"house1\" and \"house2\"")
            .since("1.12.0")
            .register();
    }

    private Expression<String> fileString;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        SkBeeMetrics.Features.STRUCTURE_TEMPLATES.used();
        if (matchedPattern == 0) {
            Skript.warning("'structure' alone is deprecated and you should use 'structure template' instead.");
        }
        this.fileString = (Expression<String>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected StructureWrapper[] get(Event event) {
        List<StructureWrapper> structures = new ArrayList<>();
        for (String file : this.fileString.getAll(event)) {
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
        return this.fileString.isSingle();
    }

    @Override
    public Class<? extends StructureWrapper> getReturnType() {
        return StructureWrapper.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "structure template[s] with id " + this.fileString.toString(e, d);
    }

}
