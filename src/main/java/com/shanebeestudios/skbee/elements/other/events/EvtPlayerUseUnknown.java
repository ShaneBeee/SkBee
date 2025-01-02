package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class EvtPlayerUseUnknown extends SkriptEvent {

    static {
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent")) {
            Skript.registerEvent("Player Click Unknown Entity", EvtPlayerUseUnknown.class, PlayerUseUnknownEntityEvent.class,
                    "[player] click unknown entity [with (main|:off) hand]",
                    "[player] right[( |-)]click unknown entity [with (main|:off) hand]",
                    "[player] left[( |-)]click unknown entity [with (main|:off) hand]")
                .description("Represents an event that is called when a player right-clicks an unknown entity.",
                    "Useful for dealing with virtual entities (entities that aren't actually spawned on the server).",
                    "This event may be called multiple times per interaction (this is a server issue).",
                    "\n`event-vector` = Returns the position relative to the entity that was clicked if available. (Requires Paper 1.20.1+)",
                    "\n`event-number` = Returns the entity id of the unknown entity that was interacted with. (Not sure if this usefull or not)",
                    "\nRequires PaperMC.")
                .examples("oh right click unknown entity:",
                    "\tteleport player to spawn of world \"world\"")
                .since("2.17.0");

            EventValues.registerEventValue(PlayerUseUnknownEntityEvent.class, Number.class, PlayerUseUnknownEntityEvent::getEntityId, EventValues.TIME_NOW);
            if (Skript.methodExists(PlayerUseUnknownEntityEvent.class, "getClickedRelativePosition")) {
                EventValues.registerEventValue(PlayerUseUnknownEntityEvent.class, Vector.class, event -> {
                    try {
                        return event.getClickedRelativePosition();
                    } catch (NullPointerException ignore) {
                        return null;
                    }
                }, EventValues.TIME_NOW);
            }
        }
    }

    private int pattern;
    private boolean attack;
    private boolean offHand;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.attack = matchedPattern == 0 || matchedPattern == 2;
        this.offHand = parseResult.hasTag("off");
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof PlayerUseUnknownEntityEvent useEvent) {
            boolean offHand = this.offHand && useEvent.getHand() == EquipmentSlot.OFF_HAND;
            boolean mainHand = !this.offHand && useEvent.getHand() == EquipmentSlot.HAND;
            if (useEvent.isAttack() && this.attack) {
                return offHand || mainHand;
            } else if (!useEvent.isAttack() && !this.attack) {
                return offHand || mainHand;
            }
        }
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String click = this.pattern == 0 ? "click" : this.pattern == 1 ? "right click" : "left click";
        String hand = this.offHand ? "off hand" : "main hand";
        return "player " + click + " unknown entity with " + hand;
    }

}
