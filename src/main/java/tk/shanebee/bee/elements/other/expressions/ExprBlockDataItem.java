package tk.shanebee.bee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Block Data - Item")
@Description({"Get block data from an item. You can get a string of block data, all the tags in a block data or a specific tag. ",
        "You can also set a block data for an item or set a specific tag for block data. This syntax is only available for MC 1.14+"})
@Examples({"set item data of player's tool to block data of target block",
        "set item data of player's tool to \"minecraft:campfire[lit=false,waterlogged=true]\"",
        "set item data tag \"lit\" of player's tool to false"})
@Since("1.0.0")
public class ExprBlockDataItem extends SimpleExpression<Object> {

    static {
        if (Skript.classExists("org.bukkit.inventory.meta.BlockDataMeta")) {
            PropertyExpression.register(ExprBlockDataItem.class, Object.class,
                    "item [block[ ]](data|state) [(1¦tags|2¦tag %-string%)]", "itemtypes");
        }
    }

    private Expression<String> tag;
    private Expression<ItemType> itemTypes;
    private int parse;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.tag = (Expression<String>) exprs[0];
        this.itemTypes = (Expression<ItemType>) exprs[1];
        this.parse = parseResult.mark;
        return true;
    }

    @Override
    protected Object[] get(Event event) {
        List<Object> list = new ArrayList<>();
        for (ItemType itemType : itemTypes.getAll(event)) {
            ItemStack stack = itemType.getRandom();
            if (!(stack.getItemMeta() instanceof BlockDataMeta) || !((BlockDataMeta) stack.getItemMeta()).hasBlockData()) {
                return null;
            }
            BlockDataMeta meta = ((BlockDataMeta) stack.getItemMeta());
            Material material = stack.getType();
            if (parse == 2) {
                String tag = getTag(meta.getBlockData(getBlockForm(material)).getAsString(), this.tag.getSingle(event));
                if (tag == null) return null;

                if (isBoolean(tag)) {
                    list.add(Boolean.valueOf(tag));
                } else if (isNumber(tag)) {
                    list.add(Integer.parseInt(tag));
                } else {
                    list.add(tag);
                }
            } else if (parse == 1) {
                String[] data = getData(meta.getBlockData(getBlockForm(material)).getAsString());
                if (data != null) {
                    list.addAll(Arrays.asList(data));
                }
            } else {
                list.add(meta.getBlockData(getBlockForm(material)).getAsString());
            }
        }
        return list.toArray();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Object.class);
        }
        return null;
    }

    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
        String obj = delta == null ? "" : delta[0].toString();
        for (ItemType itemType : itemTypes.getAll(e)) {
            BlockData blockData;
            switch (parse) {
                // Tag "string"
                case 2:
                    String newData = getBlockForm(itemType.getMaterial()).getKey() + "[" + tag.getSingle(e) + "=" + obj + "]";
                    try {
                        blockData = Bukkit.createBlockData(newData);
                        BlockDataMeta meta = ((BlockDataMeta) itemType.getItemMeta());
                        BlockData oldData;
                        if (!meta.hasBlockData()) {
                            oldData = getBlockForm(itemType.getMaterial()).createBlockData();
                        } else {
                            oldData = meta.getBlockData(itemType.getMaterial());
                        }
                        blockData = oldData.merge(blockData);
                        meta.setBlockData(blockData);
                        itemType.setItemMeta(meta);
                    } catch (IllegalArgumentException ex) {
                        Skript.error("Could not parse block data: " + newData, ErrorQuality.SEMANTIC_ERROR);
                    }
                    break;
                // Tags
                case 1:
                    // Dont think this will work, so we shall ignore it
                    return;
                // Block Data
                default:
                    try {
                        blockData = Bukkit.createBlockData(obj);
                        BlockDataMeta meta = (BlockDataMeta) itemType.getItemMeta();
                        meta.setBlockData(blockData);
                        itemType.setItemMeta(meta);
                    } catch (IllegalArgumentException ex) {
                        Skript.error("Could not parse block data: " + obj, ErrorQuality.SEMANTIC_ERROR);
                    }
            }
        }

    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public boolean isSingle() {
        return parse != 1;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "item block data" + (parse == 1 ? " tags" : parse == 2 ? " tag " +
                tag.toString(e, d) : "") + " of item " + itemTypes.toString(e, d);
    }

    // Utils
    private String getTag(String data, String tag) {
        String[] sp = getData(data);
        if (sp != null) {
            for (String string : sp) {
                String[] s = string.split("=");
                if (s[0].equals(tag)) {
                    return s[1];
                }
            }
        }
        return null;
    }

    private String[] getData(String data) {
        String[] splits1 = data.split("\\[");
        if (splits1.length >= 2) {
            String[] splits2 = splits1[1].split("]");

            return splits2[0].split(",");
        }
        return null;
    }

    private boolean isNumber(String string) {
        return string.matches("\\d+");
    }

    private boolean isBoolean(String string) {
        return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
    }

    private Material getBlockForm(Material item) {
        if (item.isBlock()) return item;
        switch (item) {
            case WHEAT_SEEDS:
                return Material.WHEAT;
            case POTATO:
                return Material.POTATOES;
            case CARROT:
                return Material.CARROTS;
            case BEETROOT_SEEDS:
                return Material.BEETROOTS;
            case PUMPKIN_SEEDS:
                return Material.PUMPKIN_STEM;
            case MELON_SEEDS:
                return Material.MELON_STEM;
            case SWEET_BERRIES:
                return Material.SWEET_BERRY_BUSH;
            default:
                return item;
        }
    }

}
