package tk.shanebee.bee.elements.structure.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Converter;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.elements.structure.StructureBee;

@Name("Structure - Properties")
@Description({"Represents different properties of a structure, including mirroring, rotation, inclusion of entities and integrity.",
        "These properties are only used for placing the structure in a world, they are NOT saved to the structure file.",
        "Mirror determines which way the structure mirrors, either 'none', 'front back' or 'left right'.",
        "Rotation determines which way the structure is rotated, either 'none', 'clockwise 90', 'clockwise 180' or 'counterclockwise 90'.",
        "Integrity determines how damaged the building should look by randomly skipping blocks to place. This value can range from 0 to 1.",
        "With 0 removing all blocks and 1 spawning the structure in pristine condition.",
        "Include entities determines if saved entities should be spawned into the structure (true by default).",
        "Size returns a vector offset from the starting point of the structure. This cannot be changed."})
@Examples({"set rotation of {_s} to clockwise 90",
        "set {_r} to rotation of {_s}",
        "set {_v} to size of {_s}",
        "set include entities of structure {_s} to false",
        "set integrity of structure {_s} to 0.75"})
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

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        structures = (Expression<StructureBee>) exprs[0];
        setExpr(structures);
        return true;
    }

    @Override
    protected Object[] get(Event e, StructureBee[] source) {
        return get(source, new Converter<StructureBee, Object>() {
            @Nullable
            @Override
            public Object convert(StructureBee structure) {
                switch (pattern) {
                    case 0:
                        return structure.getMirror();
                    case 1:
                        return structure.getRotation();
                    case 2:
                        return structure.getIntegrity();
                    case 3:
                        return structure.isIncludeEntities();
                    case 4:
                        return structure.getSize();
                }
                return null;
            }
        });
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            switch (pattern) {
                case 0:
                    return new Class[]{Mirror.class};
                case 1:
                    return new Class[]{StructureRotation.class};
                case 2:
                    return new Class[]{Number.class};
                case 3:
                    return new Class[]{Boolean.class};
                default:
                    return null;
            }
        }
        return super.acceptChange(mode);
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            Object object = delta[0];
            for (StructureBee structure : getExpr().getArray(e)) {
                switch (pattern) {
                    case 0:
                        structure.setMirror(((Mirror) object));
                        break;
                    case 1:
                        structure.setRotation(((StructureRotation) object));
                        break;
                    case 2:
                        Number num = (Number) object;
                        float integrity = 1f;
                        if (num != null) {
                            float v = num.floatValue();
                            if (v >= 0 || v <= 1) {
                                integrity = v;
                            }
                        }
                        structure.setIntegrity(integrity);
                        break;
                    case 3:
                        structure.setIncludeEntities(((Boolean) object));
                        break;
                }
            }
        }
    }

    @Override
    public Class<?> getReturnType() {
        switch (pattern) {
            case 0:
                return Mirror.class;
            case 1:
                return StructureRotation.class;
            case 2:
                return Number.class;
            case 3:
                return Boolean.class;
            case 4:
                return Vector.class;
            default:
                return null;
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        String property = "";
        switch (pattern) {
            case 0:
                property = "mirror";
                break;
            case 1:
                property = "rotation";
                break;
            case 2:
                property = "integrity";
                break;
            case 3:
                property = "include entities";
                break;
            case 4:
                property = "size";
        }
        return String.format("%s property of structure %s", property, this.structures.toString(e, d));
    }

}
