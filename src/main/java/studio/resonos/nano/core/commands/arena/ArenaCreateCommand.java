package studio.resonos.nano.core.commands.arena;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.impl.StandaloneArena;
import studio.resonos.nano.core.arena.selection.Selection;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.core.util.VectorUtil;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaCreateCommand {

    @Command(names = {"arena create"}, permission = "nano.arena", playerOnly = true)
    public void Command(Player player, @Param(name = "name") String arenaName) {

        if (Arena.getByName(arenaName) == null) {
            Region region = Selection.createOrGetSelection(player);

            if (region != null) {
                StandaloneArena arena = new StandaloneArena(arenaName,
                        VectorUtil.vectorToLocation(BukkitAdapter.adapt(region.getWorld()), region.getMinimumPoint()),
                        VectorUtil.vectorToLocation(BukkitAdapter.adapt(region.getWorld()), region.getMaximumPoint()));
                Arena.getArenaNames().add(arena.getName());
                Arena.getArenas().add(arena);
                arena.save();
                player.sendMessage(CC.translate("&bCreated new Arena&f " + arenaName));
                player.sendMessage(CC.translate("&eMake sure to set the spawn using &a/arena setspawn " + arenaName + "&ein order to finalize the arena."));
            } else {
                player.sendMessage(CC.RED + "Your region is incomplete.");
            }
        } else {
            player.sendMessage(CC.RED + "An arena with that name already exists.");
        }
    }


}
