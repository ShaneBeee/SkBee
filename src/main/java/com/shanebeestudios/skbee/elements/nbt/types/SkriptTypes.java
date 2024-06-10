package com.shanebeestudios.skbee.elements.nbt.types;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.api.nbt.NBTCustomType;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;

@SuppressWarnings({"unused", "NullableProblems"})
public class SkriptTypes {

    public static final Changer<NBTCompound> NBT_COMPOUND_CHANGER = new Changer<>() {
        @Nullable
        @Override
        public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
            return switch (mode) {
                case ADD -> CollectionUtils.array(NBTCompound.class);
                case DELETE, RESET -> CollectionUtils.array();
                default -> null;
            };
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        public void change(NBTCompound[] what, @Nullable Object[] delta, ChangeMode mode) {
            if (mode == ChangeMode.ADD && delta[0] instanceof NBTCompound changer) {
                for (NBTCompound nbtCompound : what) {
                    nbtCompound.mergeCompound(changer);
                }
            } else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
                for (NBTCompound nbtCompound : what) {
                    nbtCompound.clearNBT();
                    if (nbtCompound instanceof NBTFile nbtFile && mode == ChangeMode.DELETE) {
                        nbtFile.getFile().delete();
                    }
                }
            }
        }
    };

    static {
        Classes.registerClass(new ClassInfo<>(NBTCustomType.class, "nbttype")
                .user("nbt ?types?")
                .name("NBT - Tag Type")
                .description("Represents a type of NBT tag.",
                        "You can read more about NBT types:",
                        "\nMcWiki [**NBT Data Types**](https://minecraft.wiki/w/NBT_format#Data_types)",
                        "\nSkBee Wiki [**NBT Data Types**](https://github.com/ShaneBeee/SkBee/wiki/NBT-Intro#datatypes-in-nbt)")
                .usage(NBTCustomType.getNames())
                .examples("set byte tag \"points\" of {_nbt} to 1",
                        "set compound tag \"tool\" of {_nbt} to nbt compound of player's tool")
                .since("1.10.0")
                .parser(new Parser<NBTCustomType>() {

                    @Nullable
                    @Override
                    public NBTCustomType parse(String s, ParseContext context) {
                        return NBTCustomType.fromName(s);
                    }

                    @Override
                    public String toString(NBTCustomType nbtCustomType, int i) {
                        return nbtCustomType.getName();
                    }

                    @Override
                    public String toVariableNameString(NBTCustomType nbtCustomType) {
                        return toString(nbtCustomType, 0);
                    }

                    public String getVariableNamePattern() {
                        return "\\S";
                    }
                }));
        Classes.registerClass(new ClassInfo<>(NBTCompound.class, "nbtcompound")
                .user("nbt ?compound")
                .name("NBT - Compound")
                .description("Represents the NBT compound of an entity/block/item/file/string.",
                        "NBT compounds can be merged by adding together, see examples.")
                .usage("{id:\"minecraft:netherite_axe\",tag:{Damage:0,Enchantments:[{id:\"minecraft:unbreaking\",lvl:2s}]},Count:1b}")
                .examples("set {_a} to nbt compound of player",
                        "set {_nbt} to nbt compound of last spawned entity",
                        "set {_n} to nbt compound from \"{Invisible:1b}\"",
                        "add {_n} to nbt of target entity")
                .since("1.6.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(@NotNull NBTCompound nbt, int flags) {
                        return getNBTString(nbt);
                    }

                    @Override
                    public String toVariableNameString(@NotNull NBTCompound nbt) {
                        return "nbt:" + toString(nbt, 0);
                    }
                })
                .serializer(new Serializer<>() {
                    @Override
                    public @NotNull Fields serialize(@NotNull NBTCompound nbt) {
                        Fields fields = new Fields();
                        fields.putObject("nbt", getNBTString(nbt));
                        return fields;
                    }

                    @Override
                    public void deserialize(@NotNull NBTCompound o, @NotNull Fields f) {
                        assert false;
                    }

                    @Override
                    protected NBTCompound deserialize(@NotNull Fields fields) throws StreamCorruptedException {
                        String nbt = fields.getObject("nbt", String.class);
                        if (nbt == null) {
                            throw new StreamCorruptedException("NBT string is null");
                        }
                        try {
                            return new NBTContainer(nbt);
                        } catch (Exception ex) {
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
                })
                .changer(NBT_COMPOUND_CHANGER));
    }

    private static final String NBT_EMPTY = new NBTContainer("{}").toString();

    private static String getNBTString(@NotNull NBTCompound nbtCompound) {
        try {
            return nbtCompound.toString();
        } catch (NbtApiException ignore) {
            // Error can occur when a parent compound is deleted
            return NBT_EMPTY;
        }
    }

}
