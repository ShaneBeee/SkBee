package com.shanebeestudios.skbee.elements.other.conditions;

import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.PropertyCondition;
import net.kyori.adventure.resource.ResourcePackStatus;

public class CondResourcePackStatusIntermediate extends PropertyCondition<ResourcePackStatus> {

    public static void register(Registration reg) {
        reg.newPropertyCondition(CondResourcePackStatusIntermediate.class,
            "intermediate", "resourcepackstatus")
            .name("ResourcePack - Status - Intermediate")
            .description("Whether, after receiving this status, further status events might occur.")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public boolean check(ResourcePackStatus value) {
        return value.intermediate();
    }

    @Override
    protected String getPropertyName() {
        return "intermediate";
    }

}
