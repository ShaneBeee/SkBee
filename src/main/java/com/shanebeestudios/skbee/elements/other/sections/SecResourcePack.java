package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.resource.ResourcePackStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("UnstableApiUsage")
public class SecResourcePack extends Section {

    public static class ResourcePackPacksEvent extends Event {

        private final List<ResourcePackInfo> infos = new ArrayList<>();

        public void addPack(String url, UUID id, String hash) {
            ResourcePackInfo info = ResourcePackInfo.resourcePackInfo(id, URI.create(url), hash);
            this.infos.add(info);
        }

        public List<ResourcePackInfo> getInfos() {
            return this.infos;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("This event should never be called!");
        }

    }

    public static class ResourcePackReceiveEvent extends Event {

        private final UUID uuid;
        private final ResourcePackStatus status;
        private final Audience audience;

        public ResourcePackReceiveEvent(UUID uuid, ResourcePackStatus status, Audience audience) {
            this.uuid = uuid;
            this.status = status;
            this.audience = audience;
        }

        public UUID getUuid() {
            return uuid;
        }

        public ResourcePackStatus getStatus() {
            return status;
        }

        public Audience getAudience() {
            return audience;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("This event should never be called!");
        }
    }

    private static EntryValidator VALIDATOR;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void register(Registration reg) {
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        Class[] classes = {String.class, TextComponent.class, ComponentWrapper.class};
        builder.addRequiredSection("packs");
        builder.addOptionalEntry("prompt", classes);
        builder.addOptionalEntry("force", Boolean.class);
        builder.addRequiredSection("received");

        VALIDATOR = builder.build();
        reg.newSection(SecResourcePack.class, VALIDATOR,
                "send [a] resource pack[s] to %audience/playerconnection%")
            .name("ResourcePack - Send Request")
            .description("Send a resource pack request to an audience or player connection.",
                "This is similar to the effect but includes a callback section to handle what the client does.",
                "This can be used in the 'Async Player Connection Configure' event as well.",
                "",
                "**Entries**:",
                " - `packs` = A section where you can apply multiple packs to send at once (required).",
                "   - Use the 'ResourcePack - Apply Pack' effect to add packs in this section.",
                " - `prompt` = The prompt to send to the player. Accepts String/TextComponent/TextComp. [optional]",
                " - `force` = Whether or not to force the player to accept the resource pack [optional boolean].",
                " - `received` = A callback section where you can run code when the resource pack is received.",
                "   - This section may be called multiple times for different stages of the request.",
                "",
                "**Event Values in Received Section**:",
                " - `event-uuid` = The id of the resource pack.",
                " - `event-status` = The status (ResourcePackStatus) of the resource pack request.",
                " - `event-audience` = The audience of the resource pack request.")
            .examples("send resource packs to player:",
                "\tprompt: \"Please accept our packs\"",
                "\tforce: true",
                "\tpacks:",
                "\t\tapply pack with url \"some.url\" with id (random uuid)",
                "\t\tapply pack with url \"anoter.url\" with id (random uuid)",
                "\treceived:",
                "\t\tif event-status = successfully_loaded:",
                "\t\t\tsend \"YAY\" to event-audience")
            .since("INSERT VERSION")
            .register();

        reg.newEventValue(ResourcePackReceiveEvent.class, UUID.class)
            .converter(ResourcePackReceiveEvent::getUuid)
            .patterns("id")
            .register();
        reg.newEventValue(ResourcePackReceiveEvent.class, ResourcePackStatus.class)
            .converter(ResourcePackReceiveEvent::getStatus)
            .patterns("status")
            .register();
        reg.newEventValue(ResourcePackReceiveEvent.class, Audience.class)
            .converter(ResourcePackReceiveEvent::getAudience)
            .register();
    }

    private Expression<?> receiver;
    private Expression<?> prompt;
    private Expression<Boolean> force;
    private Trigger packs;
    private Trigger received;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed,
                        ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.receiver = expressions[0];
        this.prompt = (Expression<?>) container.getOptional("prompt", false);
        this.force = (Expression<Boolean>) container.getOptional("force", false);

        SectionNode packsNode = (SectionNode) container.getOptional("packs", false);
        if (packsNode != null) {
            this.packs = loadCode(packsNode, "packs", ResourcePackPacksEvent.class);
        }

        SectionNode successNode = (SectionNode) container.getOptional("received", false);
        if (successNode != null) {
            this.received = loadCode(successNode, "received", ResourcePackReceiveEvent.class);
        }

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        Component prompt = Component.text("");
        if (this.prompt != null) {
            Object o = this.prompt.getSingle(event);
            if (o instanceof TextComponent textComp) prompt = textComp;
            else if (o instanceof ComponentWrapper wrapper) prompt = wrapper.getComponent();
            else if (o instanceof String string) prompt = Component.text(string);
        }

        boolean force = this.force != null && Boolean.TRUE.equals(this.force.getSingle(event));

        AtomicReference<Object> variables = new AtomicReference<>();
        variables.set(Variables.copyLocalVariables(event));

        // Apply packs
        ResourcePackPacksEvent resourcePackPacksEvent = new ResourcePackPacksEvent();
        Variables.setLocalVariables(resourcePackPacksEvent, variables);
        TriggerItem.walk(this.packs, resourcePackPacksEvent);
        variables.set(Variables.removeLocals(resourcePackPacksEvent));

        // Setup request
        ResourcePackRequest.Builder builder = ResourcePackRequest.resourcePackRequest();
        builder.packs(resourcePackPacksEvent.getInfos());
        builder.prompt(prompt);
        builder.required(force);

        // Create callback
        builder.callback((uuid, status, audience) -> {
            ResourcePackReceiveEvent resourcePackReceiveEvent = new ResourcePackReceiveEvent(uuid, status, audience);
            Variables.setLocalVariables(resourcePackReceiveEvent, variables.get());
            TriggerItem.walk(SecResourcePack.this.received, resourcePackReceiveEvent);
            variables.set(Variables.removeLocals(resourcePackReceiveEvent));
        });

        // Send request
        ResourcePackRequest request = builder.build();
        Object receiver = this.receiver.getSingle(event);
        if (receiver instanceof Audience audience) {
            audience.sendResourcePacks(request);
        } else if (receiver instanceof PlayerConfigurationConnection connection) {
            connection.getAudience().sendResourcePacks(request);
        }

        return next;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "send resource packs to " + this.receiver.toString(event, debug);
    }

}
