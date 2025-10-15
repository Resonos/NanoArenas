package studio.resonos.nano;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.resonos.nano.api.command.CommandHandler;
import studio.resonos.nano.api.gui.SpiGUI;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.core.util.Config;
import studio.resonos.nano.core.util.file.type.BasicConfigurationFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.generator
 * Created on: 12/16/2023
 */

@Getter
@Setter
public class NanoArenas extends JavaPlugin {

    public static SpiGUI spiGUI;
    private static NanoArenas nanoArenas;
    @Getter
    private BasicConfigurationFile arenasConfig;
    public Config mainConfig;
    private final Map<String, Integer> arenaResetTaskIds = new HashMap<>();

    public static NanoArenas get() {
        return nanoArenas;
    }

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &b&lNano Arenas"));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" "));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &aSuccessfully authenticated with the License Server!"));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &aThank you for purchasing Nano Arenas!"));
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        nanoArenas = this;
        arenasConfig = new BasicConfigurationFile(this, "arenas");
        spiGUI = new SpiGUI(this);
        Arena.init();
        registerProcessors();
        registerCommands();
        new BukkitRunnable() {
            @Override
            public void run() {
                NanoArenas.get().getLogger().info("Started Reset timer Task");
                scheduleArenaResets();
            }
        }.runTaskLater(this, 10 * 20L); // delay to allow arenas to load first
    }


    @Override
    public void onDisable() {
        // cancel any scheduled tasks to avoid leaks
        for (Integer taskId : arenaResetTaskIds.values()) {
            if (taskId != null) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }
        arenaResetTaskIds.clear();

        Arena.getArenas().forEach(Arena::reset);
        Arena.getArenas().forEach(Arena::save);
    }

    private void registerProcessors() {
        CommandHandler.registerProcessors("studio.resonos.nano.api.command.processors", this);
    }

    private void registerCommands() {
        CommandHandler.registerCommands("studio.resonos.nano.core.commands.arena", this);
        CommandHandler.registerCommands("studio.resonos.nano.core.commands.dev", this);
        System.out.println("Registered Commands");
    }

    private void scheduleArenaResets() {
        // cancel previous tasks
        for (Integer taskId : arenaResetTaskIds.values()) {
            if (taskId != null) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }
        arenaResetTaskIds.clear();

        // schedule new tasks per arena using the helper so single arenas can be scheduled later
        for (Arena arena : Arena.getArenas()) {
            scheduleResetFor(arena);
        }
    }

    /**
     * Schedule repeating reset for a single arena.
     * Call this after a new arena is added at runtime: NanoArenas.get().scheduleResetFor(addedArena)
     */
    public void scheduleResetFor(Arena arena) {
        if (arena == null) return;

        // cancel existing task for this arena if present
        Integer existing = arenaResetTaskIds.remove(arena.getName());
        if (existing != null) {
            Bukkit.getScheduler().cancelTask(existing);
        }

        int delaySeconds = arena.getResetTime();
        if (delaySeconds <= 0) {
            getLogger().info("Auto-reset disabled for arena " + arena.getName() + " (resetTime=" + delaySeconds + ")");
            return;
        }

        long ticks = Math.max(1L, delaySeconds * 20L);

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    arena.reset();
                } catch (Exception e) {
                    getLogger().severe("Failed to reset arena " + arena.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(this, ticks, ticks).getTaskId();

        arenaResetTaskIds.put(arena.getName(), taskId);
    }

    /**
     * Cancel scheduled reset for a single arena.
     * Call this when an arena is removed at runtime.
     */
    public void cancelResetFor(Arena arena) {
        if (arena == null) return;
        Integer taskId = arenaResetTaskIds.remove(arena.getName());
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

}
