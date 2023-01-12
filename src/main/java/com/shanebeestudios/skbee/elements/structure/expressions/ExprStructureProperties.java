package com.shanebeestudios.skbee.elements.structure.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.structure.StructureBee;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

@Name("Structure - Properties")
@Description({"Represents different properties of a structure, including mirroring, rotation, inclusion of entities and integrity.",
        "These properties are only used for placing the structure in a world, they are NOT saved to the structure file.",
        "Mirror determines which way the structure mirrors, either 'none', 'front back' or 'left right'.",
        "Rotation determines which way the structure is rotated, either 'none', 'clockwise 90', 'clockwise 180' or 'counterclockwise 90'.",
        "Integrity determines how damaged the building should look by randomly skipping blocks to place. This value can range from 0 to 1.",
        "With 0 removing all blocks and 1 spawning the structure in pristine condition.",
        "Include entities determines if saved entities should be spawned into the structure (true by default).",
        "Size returns a vector offset from the starting point of the structure. This cannot be changed.",
        "\nNOTE: `reset` will reset the value back to default. (added in v-INSERT VERSION)",
        "Requires MC 1.17.1+"})
@Examples({"set rotation of {_s} to clockwise 90",
        "set {_r} to rotation of {_s}",
        "set {_v} to size of {_s}",
        "set include entities of structure {_s} to false",
        "set integrity of structure {_s} to 0.75",
        "reset rotation of {_s}",
        "reset integrity of {_s}"})
@Since("1.12.0")
public class ExprStructureProperties extends PropertyExpression<StructureBee, Object> {

    static {
        Skript.registerExpression(ExprStructureProperties.class, Object.class, ExpressionType.PROPERTY,
                "mirror of [structure] %structures%",
                "rotation of [structure] %structures%",
                "integrity of [structure] %structures%",
                "include entities of [structure] %structures%",
                "size of [structure] %structures%");
    }

    private int pattern;
    private Expression<StructureBee> structures;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        structures = (Expression<StructureBee>) exprs[0];
        setExpr(structures);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object[] get(Event event, StructureBee[] source) {
        return get(source, structure -> switch (pattern) {
            case 0 -> structure.getMirror();
            case 1 -> structure.getRotation();
            case 2 -> structure.getIntegrity();
            case 3 -> structure.isIncludeEntities();
            case 4 -> structure.getSize();
            default -> null;
        });
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
            return switch (pattern) {
                case 0 -> new Class[]{Mirror.class};
                case 1 -> new Class[]{StructureRotation.class};
                case 2 -> new Class[]{Number.class};
                case 3 -> new Class[]{Boolean.class};
                default -> null;
            };
        }
        return super.acceptChange(mode);
    }

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        boolean reset = mode == ChangeMode.RESET;
        if (mode == ChangeMode.SET || reset) {
            for (StructureBee structure : getExpr().getArray(event)) {
                switch (pattern) {
                    case 0 -> structure.setMirror(reset ? Mirror.NONE : (Mirror) delta[0]);
                    case 1 -> structure.setRotation(reset ? StructureRotation.NONE : (StructureRotation) delta[0]);
                    case 2 -> {
                        Number num = reset ? 1 : (Number) delta[0];
                        float integrity = 1f;
                        if (num != null) {
                            float v = num.floatValue();
                            if (v >= 0 || v <= 1) {
                                integrity = v;
                            }
                        }
                        structure.setIntegrity(integrity);
                    }
                    case 3 -> structure.setIncludeEntities(reset || (Boolean) delta[0]);
                }
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> getReturnType() {
        return switch (pattern) {
            case 0 -> Mirror.class;
            case 1 -> StructureRotation.class;
            case 2 -> Number.class;
            case 3 -> Boolean.class;
            case 4 -> Vector.class;
            default -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        String property = switch (pattern) {
            case 0 -> "mirror";
            case 1 -> "rotation";
            case 2 -> "integrity";
            case 3 -> "include entities";
            case 4 -> "size";
            default -> "";
        };
        return String.format("%s property of structure %s", property, this.structures.toString(e, d));
    }

}
