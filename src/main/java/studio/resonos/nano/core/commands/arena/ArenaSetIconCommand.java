package studio.resonos.nano.core.commands.arena;

import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaSetIconCommand {

    @Command(names = {"arena seticon", "arena icon"}, permission = "nano.kit", playerOnly = true)
    public void Command(Player player, @Param(name = "name") Arena arena) {
        if (arena != null) {
            if (player.getItemInHand() != null) {
                arena.setIcon(player.getItemInUse());
                arena.save();
                player.sendMessage(CC.translate("&8[&bNanoArenas&8] &aYou have set icon of arena " + arena.getName()));
            } else {
                player.sendMessage(CC.translate("&8[&bNanoArenas&8] &cInvalid Item"));
            }
        }
    }
}
