package studio.resonos.nano.core.commands.arena;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;

import java.util.List;

public class ArenasCommand {

    private static final int PER_PAGE = 6;
    private static final String PREFIX = CC.translate("&8[&bNanoArenas&8] ");

    @Command(names = {"arenas"}, permission = "nano.arena")
    public void Command(CommandSender player, @Param(name = "page", required = false) Integer page) {
        List<Arena> arenas = Arena.getArenas();

        if (arenas.isEmpty()) {
            player.sendMessage(PREFIX + CC.translate("&9Arenas:"));
            player.sendMessage(PREFIX + CC.translate("&7There are no arenas."));
            return;
        }

        int total = arenas.size();
        int pages = Math.max(1, (total + PER_PAGE - 1) / PER_PAGE);
        int p = (page == null || page < 1) ? 1 : Math.min(page, pages);

        player.sendMessage(PREFIX + CC.translate("&9Arenas: &7(Page " + p + "/" + pages + ")"));

        int start = (p - 1) * PER_PAGE;
        int end = Math.min(total, start + PER_PAGE);

        for (int i = start; i < end; i++) {
            Arena arena = arenas.get(i);

            String line = PREFIX + CC.translate("&7- ") + arena.getDisplayName();
            TextComponent tc = new TextComponent(line);

            // Build hover using ArenaInfoCommand-like data
            StringBuilder hoverSb = new StringBuilder();
            hoverSb.append("&bName: &7").append(arena.getName());
            hoverSb.append("\n&bReset: &7").append(arena.getResetTime());
            hoverSb.append("\n&bPaused: &7").append(arena.isAutoResetPaused() ? "yes" : "no");

            // Dimensions & volume (use lower/upper corners like ArenaInfoCommand)
            Location lower = arena.getLowerCorner();
            Location upper = arena.getUpperCorner();
            if (lower != null && upper != null) {
                int[] dims = getDimensions(lower, upper);
                int width = dims[0], height = dims[1], depth = dims[2];
                int volume = getVolume(lower, upper);
                hoverSb.append("\n&bDimensions: &7").append(width).append(" x ").append(height).append(" x ").append(depth);
                hoverSb.append("\n&bSize: &7").append(volume).append(" blocks");
            }

            // World & simplified spawn coords (use spawn like ArenaInfoCommand)
            Location spawn = arena.getSpawn();
            if (spawn != null) {
                String worldName = spawn.getWorld() != null ? spawn.getWorld().getName() : "unknown";
                hoverSb.append("\n&bWorld: &7").append(worldName);
                hoverSb.append("\n&bSpawn: &7")
                        .append(spawn.getBlockX()).append(", ")
                        .append(spawn.getBlockY()).append(", ")
                        .append(spawn.getBlockZ());
            }
            hoverSb.append("\n\n&7Click to teleport to this arena.");

            String hover = CC.translate(hoverSb.toString());
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arena teleport " + arena.getName()));

            player.spigot().sendMessage(tc);
        }

        // Navigation
        if (pages > 1) {
            TextComponent prev = new TextComponent(PREFIX + CC.translate("&a< Prev "));
            TextComponent spacer = new TextComponent(CC.translate("&7 | "));
            TextComponent next = new TextComponent(CC.translate(" " + "&aNext >"));

            if (p > 1) {
                prev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arenas " + (p - 1)));
                prev.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(CC.translate("&7Go to page " + (p - 1))).create()));
            } else {
                prev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arenas " + p));
                prev.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(CC.translate("&7Already on first page")).create()));
            }

            if (p < pages) {
                next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arenas " + (p + 1)));
                next.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(CC.translate("&7Go to page " + (p + 1))).create()));
            } else {
                next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arenas " + p));
                next.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(CC.translate("&7Already on last page")).create()));
            }

            player.spigot().sendMessage(prev, spacer, next);
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
        return new int[]{dx, dy, dz};
    }

    /** Returns the inclusive volume (number of blocks) for the cuboid. */
    public static int getVolume(Location a, Location b) {
        int[] d = getDimensions(a, b);
        return d[0] * d[1] * d[2];
    }
}