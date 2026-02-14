package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EffEquipmentChange extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffEquipmentChange.class,
                "make %players% see %equipmentslots% of %livingentities% as %itemtype%")
            .name("Send Equipment Change")
            .description("Send an equipment change for an entity. This will not actually change the entity's equipment in any way.")
            .examples("make player see hand slot of target entity as diamond sword",
                "make all players see off hand slot of player as shield")
            .since("3.4.0")
            .register();
    }

    private Expression<Player> players;
    private Expression<EquipmentSlot> slots;
    private Expression<LivingEntity> entities;
    private Expression<ItemType> itemtype;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.players = (Expression<Player>) exprs[0];
        this.slots = (Expression<EquipmentSlot>) exprs[1];
        this.entities = (Expression<LivingEntity>) exprs[2];
        this.itemtype = (Expression<ItemType>) exprs[3];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ItemType itemType = this.itemtype.getSingle(event);
        if (itemType == null) {
            return;
        }
        ItemStack itemStack = itemType.getRandom();

        for (Player player : this.players.getArray(event)) {
            for (LivingEntity livingEntity : this.entities.getArray(event)) {
                for (EquipmentSlot slot : this.slots.getArray(event)) {
                    player.sendEquipmentChange(livingEntity, slot, itemStack);
                }
            }
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String players = this.players.toString(e, d);
        String slot = this.slots.toString(e, d);
        String entities = this.entities.toString(e, d);
        String item = this.itemtype.toString(e, d);
        return String.format("make %s see %s of %s as %s", players, slot, entities, item);
    }

}
