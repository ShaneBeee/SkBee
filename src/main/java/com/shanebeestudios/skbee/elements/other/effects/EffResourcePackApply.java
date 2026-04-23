package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.elements.other.sections.SecResourcePack.ResourcePackPacksEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EffResourcePackApply extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffResourcePackApply.class,
                "apply [resource] pack with url %string% [and] with (uuid|id) %uuid% [and with hash %-string%]")
            .name("ResourcePack - Apply Pack")
            .description("Apply a resource pack in the `packs` section of a resouce pack send section.",
                "The ID is used to remove the pack later, if you dont plan on removing a pack you can use a `random uuid`.")
            .examples("send resource packs to player:",
                "\tpacks:",
                "\t\tapply pack with url \"some.url\" with id (random uuid)",
                "\t\tapply pack with url \"anoter.url\" with id (random uuid)")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<String> url;
    private Expression<UUID> uuid;
    private Expression<String> hash;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(ResourcePackPacksEvent.class)) {
            Skript.error("The 'apply pack' effect can only be used in a 'packs' section!");
            return false;
        }
        this.url = (Expression<String>) expressions[0];
        this.uuid = (Expression<UUID>) expressions[1];
        this.hash = (Expression<String>) expressions[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (!(event instanceof ResourcePackPacksEvent packEvent)) {
            return;
        }

        String url = this.url.getSingle(event);
        UUID uuid = this.uuid.getSingle(event);
        String hash = "";

        if (this.hash != null) {
            hash = this.hash.getSingle(event);
        }

        packEvent.addPack(url, uuid, hash);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String hash = this.hash != null ? " with hash " + this.hash.toString(event, debug) : "";
        return "apply pack with url " + this.url.toString(event, debug) + " with id " + this.uuid.toString(event, debug) + hash;
    }

}
