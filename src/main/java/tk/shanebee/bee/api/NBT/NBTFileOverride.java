package tk.shanebee.bee.api.NBT;

import de.tr7zw.changeme.nbtapi.NBTFile;

import java.io.File;
import java.io.IOException;

/**
 * Overrides {@link NBTFile} to allow for setting compounds
 * <br>
 * {@link NBTFile#setCompound(Object)} is protected, so we're just making it public
 */
public class NBTFileOverride extends NBTFile {
    /**
     * Creates a NBTFile that uses @param file to store it's data. If this file
     * exists, the data will be loaded.
     *
     * @param file File to create compound from
     * @throws IOException Exception
     */
    public NBTFileOverride(File file) throws IOException {
        super(file);
    }

    @Override
    public void setCompound(Object compound) {
        super.setCompound(compound);
    }

}
