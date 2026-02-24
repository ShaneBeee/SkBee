package com.shanebeestudios.skbee;

import com.shanebeestudios.skbee.api.registration.Registration;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class SkBeeAddonModule implements AddonModule {

    private final Registration registration;

    public SkBeeAddonModule(Registration registration) {
        this.registration = registration;
    }

    @Override
    public void init(SkriptAddon addon) {
        this.registration.registerInit();
    }

    @Override
    public void load(SkriptAddon addon) {
        this.registration.registerLoad();
    }

    @Override
    public String name() {
        return "SkBee";
    }

}
