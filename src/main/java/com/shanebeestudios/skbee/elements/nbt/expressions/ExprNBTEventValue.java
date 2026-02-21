package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.expressions.base.EventValueExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import de.tr7zw.changeme.nbtapi.NBTCompound;

@NoDoc
public class ExprNBTEventValue extends EventValueExpression<NBTCompound> {

    public static void register(Registration reg) {
        reg.newEventExpression(ExprNBTEventValue.class, NBTCompound.class, "nbt")
            .noDoc()
            .register();
    }

    public ExprNBTEventValue() {
        super(NBTCompound.class);
    }

}
