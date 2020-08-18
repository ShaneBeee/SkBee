package tk.shanebee.bee.api.NBT;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.entity.Entity;

/**
 * Overrides {@link NBTEntity} to allow for setting compounds
 * <br>
 * {@link NBTEntity#setCompound(Object)} is protected, so we're just making it public
 */
public class NBTEntityOverride extends NBTEntity {

    /**
     * @param entity Any valid Bukkit Entity
     */
    public NBTEntityOverride(Entity entity) {
        super(entity);
    }

    @Override
    public void setCompound(Object compound) {
        super.setCompound(compound);
    }

}
