package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.registry.KeyUtils;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import net.kyori.adventure.key.Key;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

import static ch.njol.skript.classes.Changer.ChangeMode.SET;

@SuppressWarnings("UnstableApiUsage")
public class SecPiercingWeapon extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        if (Util.IS_RUNNING_MC_1_21_11) {
            VALIDATOR = SimpleEntryValidator.builder()
                .addOptionalEntry("deals_knockback", Boolean.class)
                .addOptionalEntry("dismounts", Boolean.class)
                .addOptionalEntry("sound", String.class)
                .addOptionalEntry("hit_sound", String.class)
                .build();
            reg.newSection(SecPiercingWeapon.class,
                    "apply piercing weapon component to %itemstacks/itemtypes/slots%")
                .name("ItemComponent - Piercing Weapon Component Apply")
                .description("Melee attacks using this item damage multiple entities along a ray, " +
                        "instead of only a single entity. Also prevents this item from being used to mine blocks.",
                    "See [**Piercing Weapon Component**](https://minecraft.wiki/w/Data_component_format#piercing_weapon) on McWiki for more details.",
                    "Requires Minecraft 1.21.11+",
                    "",
                    "**ENTRIES**:",
                    "All entries are optional and will use their defaults when omitted.",
                    "- `deals_knockback` = Boolean, whether the attack deals knockback. Defaults to true.",
                    "- `dismounts` = Boolean, whether the attack dismounts the target. Defaults to false.",
                    "- `sound` = String, sound key to play when a player attacks with the weapon.",
                    "- `hit_sound` = String, sound key to play when the weapon hits an entity.")
                .examples("set {_i} to 1 of stick",
                    "apply piercing weapon component to {_i}:",
                    "\tdeals_knockback: true",
                    "\tdismounts: true",
                    "\tsound: \"minecraft:item.spear.attack\"",
                    "\thit_sound: \"minecraft:item.spear.hit\"",
                    "",
                    "give player 1 of {_i}")
                .since("3.16.0")
                .register();
        }
    }

    private Expression<?> items;
    private Expression<Boolean> dealsKnockback;
    private Expression<Boolean> dismounts;
    private Expression<String> sound;
    private Expression<String> hitSound;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (sectionNode == null) return false;
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }
        this.items = exprs[0];

        this.dealsKnockback = (Expression<Boolean>) validate.getOptional("deals_knockback", false);
        this.dismounts = (Expression<Boolean>) validate.getOptional("dismounts", false);
        this.sound = (Expression<String>) validate.getOptional("sound", false);
        this.hitSound = (Expression<String>) validate.getOptional("hit_sound", false);

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon();

        if (this.dealsKnockback != null) {
            Boolean bool = this.dealsKnockback.getSingle(event);
            if (bool != null) builder.dealsKnockback(bool);
        }
        if (this.dismounts != null) {
            Boolean bool = this.dismounts.getSingle(event);
            if (bool != null) builder.dismounts(bool);
        }
        if (this.sound != null) {
            String soundString = this.sound.getSingle(event);
            Key key = KeyUtils.getKey(soundString);
            if (key != null) builder.sound(key);
        }
        if (this.hitSound != null) {
            String hitSoundString = this.hitSound.getSingle(event);
            Key key = KeyUtils.getKey(hitSoundString);
            if (key != null) builder.hitSound(key);
        }

        ItemComponentUtils.modifyComponent(this.items.getArray(event), SET,
            DataComponentTypes.PIERCING_WEAPON, builder.build());
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "apply piercing weapon component to " + this.items.toString(e, d);
    }
}
