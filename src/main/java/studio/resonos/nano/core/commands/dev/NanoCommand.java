package studio.resonos.nano.core.commands.dev;

import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.core.util.CC;

/**
 * @Author: Athulsib
 * Package: studio.resonos.arenas.core.commands.arena
 * Created on: 12/25/2023
 */
public class NanoCommand {

    @Command(names = {"arena"})
    public void Command(Player sender) {
        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(CC.translate(" &b&lNano Arenas"));
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate(" &fThis server is running &bNano. A lightning fast Arena system"));
        sender.sendMessage(CC.translate(" &fmade for Large scale maps and servers."));
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate(" &fDeveloped by &bResonos Studios &f[&bdsc.gg/resonos&f]"));
        sender.sendMessage(CC.CHAT_BAR);
    }
}
