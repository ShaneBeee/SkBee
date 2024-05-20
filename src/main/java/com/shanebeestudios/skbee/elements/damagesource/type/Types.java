package com.shanebeestudios.skbee.elements.damagesource.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.wrapper.RegistryWrapper;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;

public class Types {

    static {
        if (Skript.classExists("org.bukkit.damage.DamageSource")) {
            if (Classes.getExactClassInfo(DamageType.class) == null) {
                RegistryWrapper<DamageType> DAMAGE_TYPE_REGISTRY = RegistryWrapper.wrap(DamageType.class);
                Classes.registerClass(new ClassInfo<>(DamageType.class, "damagetype")
                    .user("damage ?types?")
                    .name("Damage Type")
                    .description("Represents a type of damage.",
                        "See [**DamageType**](https://minecraft.wiki/w/Damage_type) on McWiki for more details.",
                        "Requires MC 1.20.4+",
                        "NOTE: These are auto-generated and may differ between server versions.")
                    .usage(DAMAGE_TYPE_REGISTRY.getNames())
                    .after("itemtype", "visualeffect")
                    .since("3.3.0")
                    .parser(DAMAGE_TYPE_REGISTRY.getParser()));
            }

            if (Classes.getExactClassInfo(DamageSource.class) == null) {
                Classes.registerClass(new ClassInfo<>(DamageSource.class, "damagesource")
                    .user("damage ?sources?")
                    .name("Damage Source")
                    .description("Represents a source of damage. Requires MC 1.20.4+")
                    .since("3.3.0")
                    .parser(SkriptUtils.getDefaultParser()));
            }
        }
    }

}
