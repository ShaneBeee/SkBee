package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.region.TaskUtils;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.SkBeeMetrics;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.ResolvableProfile.SkinPatchBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.profile.PlayerTextures.SkinModel;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class SecProfileComponent extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("name", String.class)
            .addOptionalEntry("id", UUID.class)
            .addOptionalEntry("texture-value", String.class)
            .addOptionalEntry("texture-signature", String.class)
            .addOptionalEntry("texture", String.class)
            .addOptionalEntry("cape", String.class)
            .addOptionalEntry("elytra", String.class)
            .addOptionalEntry("model", String.class)
            .build();

        reg.newSection(SecProfileComponent.class, VALIDATOR, "apply profile [component] to %itemstacks/itemtypes/slots/blocks/entities%")
            .name("ItemComponent - Profile Component Apply")
            .description("Apply a profile component to an item/entity/block.",
                "This is generally used for player heads and mannequins.",
                "It can also be used on players but please use with caution.",
                "See [**Profile Component**](https://minecraft.wiki/w/Data_component_format#profile) on McWiki for more details.",
                "",
                "**Entries**:",
                " - `name` = A username with a maximum length of 16, and only consisting of username-allowed characters [optional].",
                " - `id` = A UUID. If no other profile fields are specified, this is used to dynamically request the profile of a player " +
                    "with that UUID from Mojang's servers. Once received, that profile's properties (such as its skin, cape, and elytra textures) " +
                    "can be used for rendering. If the profile does not exist, a random default skin is provided [optional].",
                " - `texture-value` = A base64 encoded string representing the texture data for the profile. [optional].",
                " - `texture-signature` = A base64 encoded string representing the signature of the texture data. [optional].",
                " - `model` = The model of the skin, either `classic` or `slim`. Defaults to `classic` if not specified. [optional].",
                " - `texture` = Namespaced path to a player skin texture, relative to the `textures` folder in a resource pack [optional].",
                " - `cape` = Namespaced path to a cape texture, relative to the `textures` folder in a resource pack. [optional].",
                " - `elytra` = Namespaced path to an elytra texture, relative to the `textures` folder in a resource pack. [optional].")
            .examples("# Hamburger Head",
                "set {_i} to 1 of player head",
                "apply profile to {_i}:",
                "\ttexture-value: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmJlNjAyN2NhM2MwZTE3MDJlOGU0ODE4ZjlkYjk0NDc1NTU4MDVjZDE1NjFlYTUyZWNhODRjOTkyNDk1NTRlMyJ9fX0=\"",
                "give {_i} to player",
                "",
                "# Yours Truly's Head",
                "set {_i} to 1 of player head",
                "apply profile to {_i}:",
                "\tname: \"ShaneBee\"",
                "give {_i} to player",
                "",
                "# Batman",
                "spawn a mannequin above target block of player:",
                "\tapply profile to entity:",
                "\t\tname: \"Batman\"")
            .since("3.18.0")
            .register();
    }

    private Expression<?> objects;
    private Expression<String> name;
    private Expression<UUID> id;
    private Expression<String> textureValue;
    private Expression<String> textureSignature;
    private Expression<String> texture;
    private Expression<String> cape;
    private Expression<String> elytra;
    private Expression<String> model;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                        ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        SkBeeMetrics.Features.ITEM_COMPONENTS.used();
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.objects = exprs[0];
        this.name = (Expression<String>) container.getOptional("name", false);
        this.id = (Expression<UUID>) container.getOptional("id", false);
        this.textureValue = (Expression<String>) container.getOptional("texture-value", false);
        this.textureSignature = (Expression<String>) container.getOptional("texture-signature", false);
        this.texture = (Expression<String>) container.getOptional("texture", false);
        this.cape = (Expression<String>) container.getOptional("cape", false);
        this.elytra = (Expression<String>) container.getOptional("elytra", false);
        this.model = (Expression<String>) container.getOptional("model", false);
        return true;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        ResolvableProfile.Builder builder = ResolvableProfile.resolvableProfile();

        if (this.name != null) {
            String name = this.name.getSingle(event);
            if (name != null) builder.name(name);
        }

        if (this.id != null) {
            UUID id = this.id.getSingle(event);
            if (id != null) builder.uuid(id);
        }

        if (this.textureValue != null) {
            String value = this.textureValue.getSingle(event);
            String signature = null;
            if (this.textureSignature != null) {
                signature = this.textureSignature.getSingle(event);
            }
            if (value != null) {
                ProfileProperty textures = new ProfileProperty("textures", value, signature);
                builder.addProperty(textures);
            }
        }

        builder.skinPatch(skin(event).build());
        ResolvableProfile profile = builder.build();

        for (Object object : this.objects.getArray(event)) {
            if (object instanceof Entity entity) {
                if (entity instanceof Mannequin mannequin) {
                    mannequin.setProfile(profile);
                } else if (entity instanceof Player player) {
                    profile.resolve().thenAcceptAsync(player::setPlayerProfile,
                        runnable ->
                            TaskUtils.getEntityScheduler(player).runTask(runnable));
                }
            } else if (object instanceof Block block) {
                if (block.getState() instanceof Skull skull) {
                    skull.setProfile(profile);
                    skull.update();
                }
            } else {
                ItemComponentUtils.modifyComponent(new Object[]{object}, ChangeMode.SET,
                    DataComponentTypes.PROFILE, profile);
            }
        }

        return super.walk(event, false);
    }

    private SkinPatchBuilder skin(Event event) {
        SkinPatchBuilder builder = ResolvableProfile.SkinPatch.skinPatch();
        if (this.texture != null) {
            String single = this.texture.getSingle(event);
            if (single != null) {
                NamespacedKey namespacedKey = Util.getNamespacedKey(single, false);
                if (namespacedKey != null) {
                    builder.body(namespacedKey.key());
                }
            }
        }

        if (this.cape != null) {
            String single = this.cape.getSingle(event);
            if (single != null) {
                NamespacedKey namespacedKey = Util.getNamespacedKey(single, false);
                if (namespacedKey != null) {
                    builder.cape(namespacedKey.key());
                }
            }
        }

        if (this.elytra != null) {
            String single = this.elytra.getSingle(event);
            if (single != null) {
                NamespacedKey namespacedKey = Util.getNamespacedKey(single, false);
                if (namespacedKey != null) {
                    builder.elytra(namespacedKey.key());
                }
            }
        }

        if (this.model != null) {
            String single = this.model.getSingle(event);
            if (single != null) {
                SkinModel m = single.equalsIgnoreCase("slim") ? SkinModel.SLIM : SkinModel.CLASSIC;
                builder.model(m);
            }
        }

        return builder;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "apply profile component to " + this.objects.toString(event, debug);
    }

}
