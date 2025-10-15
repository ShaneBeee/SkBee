package com.shanebeestudios.skbee.api.util;

import io.papermc.paper.dialog.Dialog;
import net.kyori.adventure.text.event.ClickEvent;

// TODO remove after lowest support is 1.21.6+
public class DialogUtil {

    public static ClickEvent showDialog(Dialog dialog) {
        return ClickEvent.showDialog(dialog);
    }

}
