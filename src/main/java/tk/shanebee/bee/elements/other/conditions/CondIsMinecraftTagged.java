package tk.shanebee.bee.elements.other.conditions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Is Minecraft Tagged")
@Description("Check if an item is tagged with a Minecraft tag.")
@Examples({"if player's tool is tagged with minecraft tag \"carpets\":",
        "if target block is not tagged as minecraft tag \"fence_gates\"",
        "if clicked block is tagged as minecraft block tag \"doors\" or minecraft block tag \"fence_gates\""})
@Since("INSERT VERSION")
public class CondIsMinecraftTagged extends Condition {

    static {
        PropertyCondition.register(CondIsMinecraftTagged.class, "tagged (with|as) %minecrafttags%", "itemtypes");
    }

    private Expression<ItemType> itemTypes;
    private Expression<Tag> tags;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        itemTypes = (Expression<ItemType>) exprs[0];
        tags = (Expression<Tag>) exprs[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        return itemTypes.check(event, item -> {
            Material material = item.getMaterial();
            return tags.check(event, tag -> tag.isTagged(material));
        }, isNegated());
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return itemTypes.toString(e, d) + " is tagged with " + tags.toString(e, d);
    }

}
