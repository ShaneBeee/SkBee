package tk.shanebee.bee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.event.Event;

@Name("EntityBlockStorage - Max Entities")
@Description({"Get/Set the max amount of entities which can be stored in a block.",
        "As of 1.15 this only includes beehives/bee nests! Requires Spigot/Paper 1.15.2+"})
@Examples({"set {_m} to max entities of target block of player",
        "set max entities of target block of player to 20",
        "set max entity storage of event-block to 5"})
@Since("1.0.0")
public class ExprEntityBlockStorageMax extends SimplePropertyExpression<Block, Long> {

    static {
        if (Skript.classExists("org.bukkit.block.EntityBlockStorage")) {
            register(ExprEntityBlockStorageMax.class, Long.class, "max entit(ies|y storage)", "blocks");
        }
    }

    @Override
    public Long convert(Block block) {
        BlockState state = block.getState();
        if (state instanceof EntityBlockStorage) {
            return ((long) ((EntityBlockStorage<?>) state).getMaxEntities());
        }
        return null;
    }

    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.RESET) {
            return new Class[]{Long.class};
        }
        return null;
    }

    @Override
    public void change(Event e, Object[] delta, ChangeMode mode) {
        for (Block block : getExpr().getArray(e)) {
            int change = delta == null ? getDefault(block) : ((Number) delta[0]).intValue();
            BlockState state = block.getState();
            if (state instanceof EntityBlockStorage) {
                EntityBlockStorage<?> storage = ((EntityBlockStorage<?>) state);
                int newVal = storage.getMaxEntities();
                switch (mode) {
                    case RESET:
                    case SET:
                        newVal = change;
                        break;
                    case ADD:
                        newVal += change;
                        break;
                    case REMOVE:
                        newVal -= change;
                        break;
                }
                storage.setMaxEntities(Math.max(1, newVal));
                storage.update(true, false);
            }
        }
    }

    @Override
    public Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    protected String getPropertyName() {
        return "max entities of entity storage block";
    }

    // Simple util method for getting default max entities for a block
    // Future MC versions may include more blocks (like the possible termite block)
    private int getDefault(Block block) {
        switch (block.getType().toString()) {
            case "BEEHIVE":
            case "BEE_NEST":
                return 3;
            default:
                return 0;
        }
    }

}
