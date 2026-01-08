package com.shanebeestudios.skbee.elements.virtualfurnace.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventConverter;
import ch.njol.skript.registrations.EventValues;
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

    static {
        // SMELT EVENT
        Skript.registerEvent("VirtualFurnace - Smelt", EvtVirtualFurnace.class, FurnaceCookEvent.class,
                "virtual furnace (smelt|cook)[ed|ing] [of %-itemtypes%]")
            .description("Called when a virtual furnace smelts an item in its input slot.")
            .examples("on virtual furnace smelt:",
                "\tif event-item is a diamond:",
                "\t\tcancel event")
            .since("INSERT VERSION");

        EventValues.registerEventValue(FurnaceCookEvent.class, Machine.class, FurnaceCookEvent::getFurnace);
        EventValues.registerEventValue(FurnaceCookEvent.class, ItemStack.class, FurnaceCookEvent::getSource);
        EventValues.registerEventValue(FurnaceCookEvent.class, ItemType.class, from -> new ItemType(from.getSource()));

        // EXTRACT EVENT
        Skript.registerEvent("VirtualFurnace - Extract", EvtVirtualFurnace.class, FurnaceExtractEvent.class,
                "virtual furnace [item] extract[ion] [of %-itemtypes%]")
            .description("Called when a player extracts an item from a virtual furnace.")
            .examples("on virtual furnace extract:",
                "\tif event-item is an iron nugget:",
                "\t\tsend \"Congrats\" to player")
            .since("INSERT VERSION");

        EventValues.registerEventValue(FurnaceExtractEvent.class, Machine.class, FurnaceExtractEvent::getFurnace);
        EventValues.registerEventValue(FurnaceExtractEvent.class, Player.class, FurnaceExtractEvent::getPlayer);
        EventValues.registerEventValue(FurnaceExtractEvent.class, ItemStack.class, new EventConverter<>() {
            @Override
            public void set(FurnaceExtractEvent event, @Nullable ItemStack value) {
                event.setItemStack(value);
            }

            @Override
            public @Nullable ItemStack convert(FurnaceExtractEvent event) {
                return event.getItemStack();
            }
        });
        EventValues.registerEventValue(FurnaceExtractEvent.class, ItemType.class, new EventConverter<>() {
            @Override
            public void set(FurnaceExtractEvent event, @Nullable ItemType value) {
                if (value == null) {
                    event.setItemStack(null);
                } else {
                    event.setItemStack(value.getRandom());
                }
            }

            @Override
            public ItemType convert(FurnaceExtractEvent event) {
                return new ItemType(event.getItemStack());
            }
        });

        // FUEL BURN EVENT
        Skript.registerEvent("VirtualFurnace - Fuel Burn", EvtVirtualFurnace.class, FurnaceFuelBurnEvent.class,
                "virtual furnace fuel burn[ing] [of %-itemtypes%]")
            .description("Called when a virtual furnace burns its fuel.")
            .examples("on virtual furnace fuel burn:",
                "\tif event-item is coal:",
                "\t\tset event-item to charcoal")
            .since("INSERT VERSION");

        EventValues.registerEventValue(FurnaceFuelBurnEvent.class, Machine.class, FurnaceFuelBurnEvent::getFurnace);
        EventValues.registerEventValue(FurnaceFuelBurnEvent.class, Fuel.class, FurnaceFuelBurnEvent::getFuel);
        EventValues.registerEventValue(FurnaceFuelBurnEvent.class, ItemStack.class, FurnaceFuelBurnEvent::getFuelItem);
        EventValues.registerEventValue(FurnaceFuelBurnEvent.class, ItemType.class, new Converter<>() {
            @Override
            public @NonNull ItemType convert(FurnaceFuelBurnEvent event) {
                return new ItemType(event.getFuelItem());
            }
        });
        EventValues.registerEventValue(FurnaceFuelBurnEvent.class, Number.class, new EventConverter<>() {
            @Override
            public void set(FurnaceFuelBurnEvent event, @Nullable Number value) {
                int burnTime = value != null ? value.intValue() : 0;
                event.setBurnTime(burnTime);
            }

            @Override
            public @NonNull Number convert(FurnaceFuelBurnEvent event) {
                return event.getBurnTime();
            }
        });
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
