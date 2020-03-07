package tk.shanebee.bee.elements.structure.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shynixn.structureblocklib.bukkit.api.StructureBlockApi;
import com.github.shynixn.structureblocklib.bukkit.api.business.service.PersistenceStructureService;
import com.github.shynixn.structureblocklib.bukkit.api.persistence.entity.StructureSaveConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;

@Name("Structure Block - Save")
@Description("Save structure block structures. 1.9.4+ ONLY")
@Examples("save structure between {loc1} and {loc2} as \"house\"")
@Since("1.0.0")
public class EffSaveStructure extends Effect {

    static {
        if (Skript.isRunningMinecraft(1, 9, 4)) {
            Skript.registerEffect(EffSaveStructure.class, "save [structure] between %location% and %location% as %string%");
        }
    }

    @SuppressWarnings("null")
    private Expression<Location> loc1;
    private Expression<Location> loc2;
    private Expression<String> name;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
       loc1 = (Expression<Location>) exprs[0];
       loc2 = (Expression<Location>) exprs[1];
       name = (Expression<String>) exprs[2];
       return true;
    }

    @Override
    protected void execute(Event event) {
        PersistenceStructureService service = StructureBlockApi.INSTANCE.getStructurePersistenceService();
        String world = Bukkit.getServer().getWorlds().get(0).getName();
        final StructureSaveConfiguration saveConfig = service.createSaveConfiguration("minecraft", name.getSingle(event), world);
        saveConfig.setIgnoreEntities(false);
        service.save(saveConfig, loc1.getSingle(event), loc2.getSingle(event));
    }

    @Override
    public String toString(Event e, boolean d) {
        return "save structure between " + loc1.toString(e, d) + " and " + loc2.toString(e, d) + " as " + name.toString(e, d);
    }

}
