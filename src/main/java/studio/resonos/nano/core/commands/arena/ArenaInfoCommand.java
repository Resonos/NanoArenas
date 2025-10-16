package studio.resonos.nano.core.commands.arena;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;

import java.io.IOException;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaInfoCommand {

    @Command(names = {"arena info"}, permission = "nano.arena", playerOnly = true)
    public void Command(Player sender, @Param(name = "arena") Arena arena)  {
        if (arena != null) {

           sender.sendMessage(CC.CHAT_BAR);
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &b&lArena Info &7(" + (arena.isSetup() ? "&a" : "&c") + arena.getName() + "&7)"));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bName: &e" + arena.getName()));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bWorld: &e" + (arena.getWorld() == null ? "✗" : arena.getWorld().getName())));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bSize: &e" + getVolume(arena.getLowerCorner(), arena.getUpperCorner()) + " blocks"));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bDimensions: &e" + (arena.getLowerCorner() == null || arena.getUpperCorner() == null ? "✗" : (getDimensions(arena.getLowerCorner(), arena.getUpperCorner())[0] + " x " + getDimensions(arena.getLowerCorner(), arena.getUpperCorner())[1] + " x " + getDimensions(arena.getLowerCorner(), arena.getUpperCorner())[2]))));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bLower Corner: &e" + (arena.getLowerCorner() == null ? "✗" : ("x: " + arena.getLowerCorner().getBlockX() + " y: " + arena.getLowerCorner().getBlockY() + " z: " + arena.getLowerCorner().getBlockZ()))));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bUpper Corner: &e" + (arena.getUpperCorner() == null ? "✗" : ("x: " + arena.getUpperCorner().getBlockX() + " y: " + arena.getUpperCorner().getBlockY() + " z: " + arena.getUpperCorner().getBlockZ()))));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bSpawn Location: &e" + (arena.getSpawn() == null ? "✗" : ("x: " + arena.getSpawn().getBlockX() + " y: " + arena.getSpawn().getBlockY() + " z: " + arena.getSpawn().getBlockZ()))));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bReset Time: &e" + arena.getResetTime() + " seconds"));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bSetup Complete: &e" + (arena.isSetup() ? "✔" : "✗")));
           sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &bStatus: &f" + (!arena.isAutoResetPaused() ? "&aAuto-Resetting" : "&cPaused")));
           sender.sendMessage(CC.CHAT_BAR);

        } else {
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &cAn arena with that name does not exist."));

        }
    }

    /**
     * Returns dimensions {width, height, depth} for the cuboid defined by two corners.
     * Uses block coordinates and counts inclusively (e.g. same-corner => {1,1,1}).
     */
    public static int[] getDimensions(Location a, Location b) {
        if (a == null || b == null) throw new IllegalArgumentException("Locations must not be null");
        int dx = Math.abs(a.getBlockX() - b.getBlockX()) + 1;
        int dy = Math.abs(a.getBlockY() - b.getBlockY()) + 1;
        int dz = Math.abs(a.getBlockZ() - b.getBlockZ()) + 1;
        return new int[] { dx, dy, dz };
    }

    /** Returns the inclusive volume (number of blocks) for the cuboid. */
    public static int getVolume(Location a, Location b) {
        int[] d = getDimensions(a, b);
        return d[0] * d[1] * d[2];
    }
}
