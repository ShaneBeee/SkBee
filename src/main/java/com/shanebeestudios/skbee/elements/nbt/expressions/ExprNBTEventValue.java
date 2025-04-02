package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.expressions.base.EventValueExpression;
import de.tr7zw.changeme.nbtapi.NBTCompound;

@NoDoc
public class ExprNBTEventValue extends EventValueExpression<NBTCompound> {

    static {
        register(ExprNBTEventValue.class, NBTCompound.class, "nbt");
    }

    public ExprNBTEventValue() {
        super(NBTCompound.class);
    }

}
