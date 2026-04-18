package com.shanebeestudios.skbee.elements.virtualfurnace.events;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.vf.api.event.machine.FurnaceCookEvent;
import com.shanebeestudios.vf.api.event.machine.FurnaceExtractEvent;
import com.shanebeestudios.vf.api.event.machine.FurnaceFuelBurnEvent;
import com.shanebeestudios.vf.api.machine.Machine;
import com.shanebeestudios.vf.api.recipe.Fuel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.skriptlang.skript.lang.converter.Converter;

@SuppressWarnings("unused")
public class EvtVirtualFurnace extends SkriptEvent {

    public static void register(Registration reg) {
        // SMELT EVENT
        reg.newEvent(EvtVirtualFurnace.class, FurnaceCookEvent.class,
                "virtual furnace (smelt|cook)[ed|ing] [of %-itemtypes%]")
            .name("VirtualFurnace - Smelt")
            .description("Called when a virtual furnace smelts an item in its input slot.")
            .examples("on virtual furnace smelt:",
                "\tif event-item is a diamond:",
                "\t\tcancel event")
            .since("3.15.0")
            .register();

        reg.newEventValue(FurnaceCookEvent.class, Machine.class)
            .converter(FurnaceCookEvent::getFurnace)
            .register();
        reg.newEventValue(FurnaceCookEvent.class, ItemStack.class)
            .converter(FurnaceCookEvent::getSource)
            .register();
        reg.newEventValue(FurnaceCookEvent.class, ItemType.class)
            .converter(from -> new ItemType(from.getSource()))
            .register();

        // EXTRACT EVENT
        reg.newEvent(EvtVirtualFurnace.class, FurnaceExtractEvent.class,
                "virtual furnace [item] extract[ion] [of %-itemtypes%]")
            .name("VirtualFurnace - Extract")
            .description("Called when a player extracts an item from a virtual furnace.")
            .examples("on virtual furnace extract:",
                "\tif event-item is an iron nugget:",
                "\t\tsend \"Congrats\" to player")
            .since("3.15.0")
            .register();

        reg.newEventValue(FurnaceExtractEvent.class, Machine.class)
            .converter(FurnaceExtractEvent::getFurnace)
            .register();
        reg.newEventValue(FurnaceExtractEvent.class, Player.class)
            .converter(FurnaceExtractEvent::getPlayer)
            .register();
        reg.newEventValue(FurnaceExtractEvent.class, ItemStack.class)
            .converter(FurnaceExtractEvent::getItemStack)
            .changer(Changer.ChangeMode.SET, FurnaceExtractEvent::setItemStack)
            .register();
        reg.newEventValue(FurnaceExtractEvent.class, ItemType.class)
            .converter(from -> new ItemType(from.getItemStack()))
            .changer(Changer.ChangeMode.SET, (event, value) -> {
                if (value == null) {
                    event.setItemStack(null);
                } else {
                    event.setItemStack(value.getRandom());
                }
            })
            .register();

        // FUEL BURN EVENT
        reg.newEvent(EvtVirtualFurnace.class, FurnaceFuelBurnEvent.class,
                "virtual furnace fuel burn[ing] [of %-itemtypes%]")
            .name("VirtualFurnace - Fuel Burn")
            .description("Called when a virtual furnace burns its fuel.")
            .examples("on virtual furnace fuel burn:",
                "\tif event-item is coal:",
                "\t\tset event-item to charcoal")
            .since("3.15.0")
            .register();

        reg.newEventValue(FurnaceFuelBurnEvent.class, Machine.class)
            .converter(FurnaceFuelBurnEvent::getFurnace)
            .register();
        reg.newEventValue(FurnaceFuelBurnEvent.class, Fuel.class)
            .converter(FurnaceFuelBurnEvent::getFuel)
            .register();
        reg.newEventValue(FurnaceFuelBurnEvent.class, ItemStack.class)
            .converter(FurnaceFuelBurnEvent::getFuelItem)
            .register();
        reg.newEventValue(FurnaceFuelBurnEvent.class, ItemType.class)
            .converter(new Converter<>() {
                @Override
                public @NonNull ItemType convert(FurnaceFuelBurnEvent event) {
                    return new ItemType(event.getFuelItem());
                }
            })
            .register();
        reg.newEventValue(FurnaceFuelBurnEvent.class, Number.class)
            .converter(FurnaceFuelBurnEvent::getBurnTime)
            .changer(Changer.ChangeMode.SET, (event, value) -> {
                if (value == null) return;
                event.setBurnTime(value.intValue());
            })
            .register();
    }

    private @Nullable Literal<ItemType> types;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?>[] exprs, int matchedPattern, ParseResult parseResult) {
        if (exprs[0] != null) {
            this.types = (Literal<ItemType>) exprs[0];
        }
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (this.types == null) return true;

        ItemType itemType;

        switch (event) {
            case FurnaceCookEvent cookEvent -> itemType = new ItemType(cookEvent.getSource());
            case FurnaceExtractEvent extractEvent -> itemType = new ItemType(extractEvent.getItemStack());
            case FurnaceFuelBurnEvent fuelBurnEvent -> itemType = new ItemType(fuelBurnEvent.getFuelItem());
            case null, default -> {
                return false;
            }
        }

        return this.types.check(event, item -> item.isSupertypeOf(itemType));
    }

    @Override
    public boolean canExecuteAsynchronously() {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String result = switch (event) {
            case FurnaceCookEvent ignored -> "smelt";
            case FurnaceExtractEvent ignored -> "extract";
            case FurnaceFuelBurnEvent ignored -> "fuel burn";
            case null, default -> throw new IllegalStateException("Unexpected event: " + event);
        };
        return result + " of " + Classes.toString(this.types);
    }

}
