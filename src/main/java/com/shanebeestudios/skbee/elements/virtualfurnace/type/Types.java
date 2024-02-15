package com.shanebeestudios.skbee.elements.virtualfurnace.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.vf.api.FurnaceManager;
import com.shanebeestudios.vf.api.machine.Furnace;
import com.shanebeestudios.vf.api.machine.Machine;
import com.shanebeestudios.vf.api.property.Properties;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;
import java.util.UUID;

public class Types {

    public static final FurnaceManager FURNACE_MANAGER = SkBee.getPlugin().getVirtualFurnaceAPI().getFurnaceManager();

    static {
        Classes.registerClass(new ClassInfo<>(Machine.class, "machine")
                .user("machines?")
                .name("VirtualFurnace - Machine")
                .description("Represents a virtual machine. These machines tick on their own like a regular",
                        "vanilla Minecraft furnace except they can be loaded from anywhere and don't rely on chunks.")
                .since("3.3.0")
                .parser(SkriptUtils.getDefaultParser())
                .serializer(new Serializer<>() {
                    @Override
                    public @NotNull Fields serialize(Machine machine) {
                        Fields fields = new Fields();

                        // Might add new machines in the future
                        if (machine instanceof Furnace furnace) {
                            fields.putObject("type", "furnace");
                        }
                        fields.putObject("id", machine.getUniqueID().toString());
                        return fields;
                    }

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public void deserialize(Machine o, Fields f) {
                    }

                    @SuppressWarnings("NullableProblems")
                    @Override
                    protected Machine deserialize(Fields fields) throws StreamCorruptedException {
                        String type = fields.getObject("type", String.class);
                        assert type != null;
                        if (type.equals("furnace")) {
                            String uuid = fields.getObject("id", String.class);
                            assert uuid != null;
                            Furnace furnace = FURNACE_MANAGER.getByID(UUID.fromString(uuid));
                            if (furnace != null) return furnace;
                            throw new StreamCorruptedException("Invalid machine with id: " + uuid);
                        }
                        throw new StreamCorruptedException("Invalid machine type: " + type);
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return false;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                }));

        Classes.registerClass(new ClassInfo<>(Properties.class, "machineproperty")
                .user("machine ?propert(y|ies)")
                .name("VirtualFurnace - Machine Properties")
                .description("Represents the machine properties of a virtual machine.")
                .since("3.3.0")
                .parser(SkriptUtils.getDefaultParser())
        );
    }

}
