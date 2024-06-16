package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Send Equipment Change")
@Description("Send an equipment change for an entity. This will not actually change the entity's equipment in any way.")
@Examples({"make player see hand slot of target entity as diamond sword",
    "make all players see off hand slot of player as shield"})
@Since("3.4.0")
public class EffEquipmentChange extends Effect {

    static {
        Skript.registerEffect(EffEquipmentChange.class,
            "make %players% see %equipmentslots% of %livingentities% as %itemtype%");
    }

    private Expression<Player> players;
    private Expression<EquipmentSlot> slots;
    private Expression<LivingEntity> entities;
    private Expression<ItemType> itemtype;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.players = (Expression<Player>) exprs[0];
        this.slots = (Expression<EquipmentSlot>) exprs[1];
        this.entities = (Expression<LivingEntity>) exprs[2];
        this.itemtype = (Expression<ItemType>) exprs[3];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        ItemType itemType = this.itemtype.getSingle(event);
        if (itemType == null) return;
        ItemStack itemStack = itemType.getRandom();

        for (Player player : this.players.getArray(event)) {
            for (LivingEntity livingEntity : this.entities.getArray(event)) {
                for (EquipmentSlot slot : this.slots.getArray(event)) {
                    player.sendEquipmentChange(livingEntity, slot, itemStack);
                }
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String players = this.players.toString(e, d);
        String slot = this.slots.toString(e, d);
        String entities = this.entities.toString(e, d);
        String item = this.itemtype.toString(e, d);
        return String.format("make %s see %s of %s as %s", players, slot, entities, item);
    }

}
