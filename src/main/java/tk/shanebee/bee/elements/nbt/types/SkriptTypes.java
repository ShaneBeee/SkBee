package tk.shanebee.bee.elements.nbt.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

@SuppressWarnings("unused")
public class SkriptTypes {

    static {
        Classes.registerClass(new ClassInfo<>(NBTCompound.class, "nbtcompound")
                .user("nbt ?compound")
                .name("NBT - Compound")
                .description("Represents the NBT compound of an entity/block/item.")
                .usage("{id:\"minecraft:netherite_axe\",tag:{Damage:0,Enchantments:[{id:\"minecraft:unbreaking\",lvl:2s}]},Count:1b}")
                .examples("set {_a} to nbt compound of player")
                .since("INSERT VERSION")
                .parser(new Parser<NBTCompound>() {

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(@NotNull NBTCompound nbt, int flags) {
                        return nbt.toString();
                    }

                    @Override
                    public @NotNull String toVariableNameString(@NotNull NBTCompound nbt) {
                        return "nbt:" + nbt.toString();
                    }

                    @Override
                    public @NotNull String getVariableNamePattern() {
                        return "nbt:.+";
                    }
                })
                .serializer(new Serializer<NBTCompound>() {
                    @Override
                    public @NotNull Fields serialize(@NotNull NBTCompound nbt) {
                        Fields fields = new Fields();
                        fields.putObject("nbt", nbt.toString());
                        return fields;
                    }

                    @Override
                    public void deserialize(@NotNull NBTCompound o, @NotNull Fields f) {
                        assert false;
                    }

                    @Override
                    protected @NotNull NBTCompound deserialize(@NotNull Fields fields) throws StreamCorruptedException {
                        String nbt = fields.getObject("nbt", String.class);
                        assert nbt != null;
                        try {
                            return new NBTContainer(nbt);
                        } catch (IllegalArgumentException ex) {
                            throw new StreamCorruptedException("Invalid nbt data: " + nbt);
                        }
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return true;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                }));
    }

}
