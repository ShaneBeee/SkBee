package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.nbt.NBTCustomType;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

@Name("NBT - Tag")
@Description({"Get/set/delete the value of the specified tag of an NBT compound. Also supports getting nested tags using a semi colon as a delimiter.",
    "If the return value is a list, you can use it as a list, as it will automatically split it for ya.",
    "**NOTES**:",
    "- `uuid tag` will set an int array tag (This is how MC stores uuids). On return it'll convert back to a uuid.",
    "- Entities/blocks can not natively hold custom NBT tags. SkBee allows you to put custom nbt",
    "data in the \"custom\" compound tag of a block/entity's NBT compound. Due to Minecraft not supporting this, I had to use some hacky methods to make this happen.",
    "That said, this system is a tad convoluted, see the SkBee WIKI for more details.",
    "- 1.20.5+: All custom data on items must be stored in the \"minecraft:custom_data\" compound " +
        "(see examples and [**McWiki**](https://minecraft.wiki/w/Data_component_format#custom_data)).",
    "- For more info regarding 1.20.5+ components please see [**Data Component Format**](https://minecraft.wiki/w/Data_component_format) on McWiki.",
    "- GET FROM LIST BY POSITION: You can get the element of a list by adding `[%number%]` to the end of a tag",
    "(This only works for getters, not set/add/remove/delete) see examples.",
    "",
    "**CHANGERS**:",
    "- ADD: You can add numbers to number type tags, you can also add numbers/strings/compounds to lists type tags.",
    "- REMOVE: You can remove numbers from number type tags, you can also remove numbers/strings from lists type tags." +
        "(You can NOT remove compounds from lists type tags)"})
@Examples({"# Getting the value of a simple tag",
    "set {_damage} to int tag \"minecraft:damage\" of nbt of player's tool",
    "set {_sheared} to byte tag \"Sheared\" of nbt of target entity",
    "set {_pass::*} to compound list tag \"Passengers\" of nbt of last spawned entity",
    "set {_pos::*} to double list tag \"Pos\" of nbt of {_sheep}",
    "set {_burntime} to short tag \"BurnTime\" of nbt of target block",
    "set {_items::*} to compound list tag \"Items\" of nbt of target block",
    "",
    "# Getting the value of a nested tag",
    "set {_maybuild} to byte tag \"abilities;mayBuild\" of nbt of player",
    "set {_x} to int tag \"Leash;X\" of nbt of target entity",
    "set {_pos::*} to int array tag \"LastDeathLocation;pos\" of nbt of player",
    "",
    "# Setting the value of a simple tag",
    "set int tag \"Damage\" of nbt of player's tool to 100",
    "set byte tag \"Sheared\" of nbt of last spawned entity to 1",
    "set double list tag \"Pos\" of nbt of {_sheep} to 1,100,1",
    "set short tag \"BurnTime\" of nbt of target block to 150",
    "",
    "# Setting the value of a nested tag",
    "set byte tag \"abilities;mayBuild\" of nbt of player to 1",
    "set int array tag \"LastDeathLocation;pos\" of nbt of player to 1,64,1",
    "",
    "# Setting the value of a custom tag of an entity/block",
    "set int tag \"custom;points\" of nbt of player to 55",
    "set short tag \"custom;bloop\" of nbt of last spawned entity to 10",
    "set uuid tag of \"custom;owner\" of nbt of target block to uuid of player",
    "",
    "# Adding to the value of a tag",
    "add 10 to int tag \"Score\" of nbt of player",
    "add 50 to int tag \"custom;points\" of nbt of player",
    "",
    "# Removeing from a value of a tag",
    "remove 10 from int tag \"Score\" of nbt of player",
    "remove 25 from int tag \"custom;points\" of nbt of player",
    "",
    "# Getting elements from lists",
    "set {_c} to compound tag \"Inventory[0]\" of nbt of player",
    "set {_x} to double tag \"Pos[0]\" of nbt of player",
    "set {_y} to double tag \"Pos[1]\" of nbt of player",
    "set {_z} to double tag \"Pos[2]\" of nbt of player",
    "set {_s} to string tag \"some_strings[5]\" of {_nbt}",
    "set {_i} to int tag \"intlist[1]\" of {_nbt}",
    "set {_b} to byte tag \"someBytes[10]\" of {_nbt}",
    "",
    "# Minecraft 1.20.5+ item component stuff",
    "set int tag \"minecraft:max_damage\" of nbt of player's tool to 500",
    "set int tag \"minecraft:max_stack_size\" of nbt of player's tool to 25",
    "set byte tag \"minecraft:enchantment_glint_override\" of nbt of player's tool to 1",
    "set compound tag \"minecraft:fire_resistant\" of nbt of player's tool to nbt from \"{}\"",
    "set string tag \"minecraft:rarity\" of nbt of player's tool to \"epic\"",
    "",
    "# All custom data must be within the \"minecraft:custom_data\" compound",
    "# See NBT Compound expression for futher details on the `custom nbt` expression",
    "set byte tag \"Points\" of custom nbt of player's tool to 10",
    "set int tag \"MyBloop\" of custom nbt of player's tool to 152",
    "# These examples will do the same as above",
    "set byte tag \"minecraft:custom_data;Points\" of nbt of player's tool to 10",
    "set int tag \"minecraft:custom_data;MyBloop\" of nbt of player's tool to 152"})
@Since("1.0.0")
public class ExprTagOfNBT extends SimpleExpression<Object> {

    private static final boolean ALLOW_UNSAFE_OPERATIONS = SkBee.getPlugin().getPluginConfig().NBT_ALLOW_UNSAFE_OPERATIONS;


    static {
        Skript.registerExpression(ExprTagOfNBT.class, Object.class, ExpressionType.COMBINED,
            "%nbttype% %string% of %nbtcompound%");
    }

    @Nullable
    private Literal<NBTCustomType> nbtTypeLit;
    private Expression<NBTCustomType> nbtType;
    private Expression<String> tag;
    private Expression<NBTCompound> nbt;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        if (exprs[0] instanceof Literal<?>) {
            this.nbtTypeLit = (Literal<NBTCustomType>) exprs[0];
        }
        this.nbtType = (Expression<NBTCustomType>) exprs[0];
        this.tag = (Expression<String>) exprs[1];
        this.nbt = (Expression<NBTCompound>) exprs[2];
        return true;
    }

    @Override
    protected Object @Nullable [] get(@NotNull Event event) {
        NBTCustomType type = this.nbtType.getSingle(event);
        String tag = this.tag.getSingle(event);
        NBTCompound nbt = this.nbt.getSingle(event);

        if (type == null || tag == null || nbt == null) {
            return null;
        }

        if (nbt instanceof NBTEntity && !Bukkit.isPrimaryThread() && !ALLOW_UNSAFE_OPERATIONS) {
            error("NBT of an entity cannot be retrieved off the main thread");
            return null;
        }
        if (nbt instanceof NBTTileEntity && !Bukkit.isPrimaryThread() && !ALLOW_UNSAFE_OPERATIONS) {
            error("NBT of a block cannot be retrieved off the main thread");
            return null;
        }

        Object object = NBTApi.getTag(tag, nbt, type);
        if (object instanceof ArrayList<?> arrayList) {
            return arrayList.toArray();
        }
        if (object instanceof NBTCompound compound) return new NBTCompound[]{compound};
        else if (object instanceof String string) return new String[]{string};
        else if (object instanceof Number number) return new Number[]{number};
        else if (object instanceof Boolean bool) return new Boolean[]{bool};
        else if (object instanceof UUID uuid) return new UUID[]{uuid};
        return new Object[]{object};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            if (this.nbtTypeLit == null) {
                Skript.error("NBT TYPE must be a literal, variables are not accepted!");
                return null;
            } else {
                NBTCustomType nbtType = this.nbtTypeLit.getSingle();
                if (nbtType == NBTCustomType.NBTTagCompoundList && mode == ChangeMode.REMOVE) {
                    Skript.error("NBT compounds cannot be removed from an NBT compound list!");
                    return null;
                }
                if (nbtType.getTypeClass().isArray() || Number.class.isAssignableFrom(nbtType.getTypeClass())) {
                    return CollectionUtils.array(nbtType.getTypeClass());
                }
            }
        } else if (mode == ChangeMode.SET) {
            if (this.nbtTypeLit != null) {
                NBTCustomType nbtType = this.nbtTypeLit.getSingle();
                if (nbtType != NBTCustomType.NBTTagUUID) {
                    return CollectionUtils.array(nbtType.getTypeClass());
                }
            }
            return CollectionUtils.array(Object[].class);
        } else if (mode == ChangeMode.DELETE) {
            return CollectionUtils.array();
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(@NotNull Event event, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        NBTCustomType type = this.nbtType.getSingle(event);
        String tag = this.tag.getSingle(event);
        NBTCompound nbt = this.nbt.getSingle(event);

        if (type == null || tag == null || nbt == null) {
            return;
        }

        if (nbt instanceof NBTEntity && !Bukkit.isPrimaryThread() && !ALLOW_UNSAFE_OPERATIONS) {
            error("NBT of an entity cannot be modified off the main thread");
            return;
        }
        if (nbt instanceof NBTTileEntity && !Bukkit.isPrimaryThread() && !ALLOW_UNSAFE_OPERATIONS) {
            error("NBT of a block cannot be modified off the main thread");
            return;
        }

        if (mode == ChangeMode.DELETE) {
            NBTApi.deleteTag(tag, nbt);
            return;
        }
        if (delta == null) return;

        try {
            if (mode == ChangeMode.SET) {
                NBTApi.setTag(tag, nbt, delta, type);
            } else if (mode == ChangeMode.ADD) {
                NBTApi.addToTag(tag, nbt, delta, type);
            } else if (mode == ChangeMode.REMOVE) {
                NBTApi.removeFromTag(tag, nbt, delta, type);
            }
        } catch (NbtApiException ignore) {
            // Errors may occur when sub compounds don't exist
        }
    }

    @Override
    public boolean isSingle() {
        if (this.nbtTypeLit != null) return !this.nbtTypeLit.getSingle().getTypeClass().isArray();
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        if (this.nbtTypeLit != null) {
            Class<?> nbtTypeClass = this.nbtTypeLit.getSingle().getTypeClass();
            if (nbtTypeClass.getComponentType() != null) return nbtTypeClass.getComponentType();
            return nbtTypeClass;
        }
        return Object.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String type = this.nbtType.toString(e, d);
        String tag = this.tag.toString(e, d);
        String nbt = this.nbt.toString(e, d);
        return String.format("%s %s of %s", type, tag, nbt);
    }

}
