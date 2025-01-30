package studio.resonos.nano.core.commands.arena;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.ArenaType;
import studio.resonos.nano.core.util.CC;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenasCommand {

    @Command(names = {"arenas", "arena list"}, playerOnly = true, permission = "nano.arena")
    public void Command(Player player) {
        player.sendMessage(CC.BLUE + "Arenas:");

        if (Arena.getArenas().isEmpty()) {
            player.sendMessage(CC.GRAY + "There are no arenas.");
            return;
        }

        for (Arena arena : Arena.getArenas()) {
            if (arena.getType() != ArenaType.DUPLICATE) {
                String statusText = arena.isSetup() ? CC.GREEN : CC.RED;
                TextComponent builder = new TextComponent(
                        CC.GRAY + "- " + statusText + arena.getName() +
                                CC.GRAY + "(" + arena.getType().name() + ") "
                );

                TextComponent status = new TextComponent(CC.GRAY + "[STATUS]");
                status.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.BLUE + "Click to view this arena's status.").create()));
                status.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arena status " + arena.getName()));

                builder.addExtra(" ");
                builder.addExtra(status);

                player.spigot().sendMessage(builder);
            }
        }
    }
}