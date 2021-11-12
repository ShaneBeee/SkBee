package tk.shanebee.bee.elements.nbt.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBT.NBTApi;
import tk.shanebee.bee.api.NBT.NBTApi.ObjectType;

@Name("NBT - Item/Inventory Slot/Entity/Block/File")
@Description({"NBT of items, inventory slots, entities, tile entities (such as a furnace, hopper, brewing stand, banner, etc) or files. ",
        "Supports get, set, add and reset. Reset will only properly work on an item, not entities or blocks. ",
        "Set should not be used on entities or blocks, it's best to use add. Using set can quite often screw up the entity/block's location. ",
        "The optional 'full' part (added in 1.4.10) will only work on items/slots. When using this, it will return the full NBT of said item, ",
        "including the item amount as well as the item type."})
@Examples({"set {_nbt} to nbt of player's tool", "set {_f} to full nbt of player's tool",
        "add \"{Enchantments:[{id:\"\"sharpness\"\",lvl:5}]}\" to nbt of player's tool",
        "reset nbt of player's tool", "set {_nbt} to nbt of target entity", "set {_nbt} to event-entity",
        "add \"{CustomName:\"\"{\\\"\"text\\\"\":\\\"\"&bMyNewName\\\"\"}\"\"}\" to nbt of target entity",
        "add \"{RequiredPlayerRange:0s}\" to targeted block's nbt", "add \"{SpawnData:{id:\"\"minecraft:wither\"\"}}\" to nbt of clicked block",
        "set {_nbt} to file-nbt of \"world/playerdata/some-uuid-here.dat\""})
@Since("1.0.0")
public class ExprObjectNBT extends SimplePropertyExpression<Object, Object> {

    private static final NBTApi NBT_API;

    static {
        register(ExprObjectNBT.class, Object.class, "[(1¦full )][(entity|item|slot|block|tile[(-| )]entity|file)(-| )]nbt",
                "block/entity/itemstack/itemtype/slot/string");
        NBT_API = SkBee.getPlugin().getNbtApi();
    }

    private boolean full;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        full = parseResult.mark == 1;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public String convert(@NotNull Object object) {
        ObjectType objectType = null;
        if (object instanceof Slot) {
            objectType = full ? ObjectType.SLOT_FULL : ObjectType.SLOT;
        } else if (object instanceof ItemStack) {
            objectType = full ? ObjectType.ITEM_STACK_FULL : ObjectType.ITEM_STACK;
        } else if (object instanceof ItemType) {
            objectType = full ? ObjectType.ITEM_TYPE_FULL : ObjectType.ITEM_TYPE;
        } else if (object instanceof Entity) {
            objectType = ObjectType.ENTITY;
        } else if (object instanceof Block) {
            objectType = ObjectType.BLOCK;
        } else if (object instanceof String) {
            objectType = ObjectType.FILE;
        }
        if (objectType != null)
            return NBT_API.getNBT(object, objectType);
        return null;
    }

    @Override
    public Class<?>[] acceptChange(final @NotNull ChangeMode mode) {
        if (mode == ChangeMode.ADD || mode == ChangeMode.SET || mode == ChangeMode.RESET)
            return CollectionUtils.array(Object.class);
        return null;
    }

    @Override
    public void change(@NotNull Event event, Object[] delta, @NotNull ChangeMode mode) {
        Object object = getExpr().getSingle(event);
        if (object == null) return;

        Object nbtObject = delta != null ? delta[0] : null;

        String value = "{}";
        if (nbtObject != null) {
            value = nbtObject instanceof NBTCompound ? nbtObject.toString() : nbtObject instanceof String ? (String) nbtObject : "{}";
        }
        if (!NBTApi.validateNBT(value)) {
            return;
        }
        ObjectType objectType = null;
        if (object instanceof Slot) {
            objectType = ObjectType.SLOT;
        } else if (object instanceof ItemStack) {
            objectType = ObjectType.ITEM_STACK;
        } else if (object instanceof ItemType) {
            objectType = ObjectType.ITEM_TYPE;
        } else if (object instanceof Entity) {
            objectType = ObjectType.ENTITY;
        } else if (object instanceof Block) {
            objectType = ObjectType.BLOCK;
        } else if (object instanceof String) {
            objectType = ObjectType.FILE;
        }
        switch (mode) {
            case ADD:
                if (objectType != null)
                    NBT_API.addNBT(object, value, objectType);
                break;
            case SET:
            case RESET:
                if (objectType != null)
                    NBT_API.setNBT(object, value, objectType);
                break;
            default:
                assert false;
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "object nbt";
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

}
