package tk.shanebee.bee.elements.text.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import net.md_5.bungee.api.chat.BaseComponent;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(BaseComponent.class, "basecomponent")
                .user("base ?components?")
                .name("Text Component - Base Component")
                .description("Text components used for hover/click events.")
                .examples("set {_t} to text component from \"CLICK FOR OUR DISCORD\"",
                        "set hover event of {_t} to a new hover event showing \"Clicky Clicky!\"",
                        "set click event of {_t} to a new click event to open url \"https://OurDiscord.com\"",
                        "send component {_t} to player")
                .since("1.5.0"));
    }

}
