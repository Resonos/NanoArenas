package studio.resonos.nano.core.arena.schedule;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scheduler that manages per-arena countdowns and resets.
 */
public class ArenaResetScheduler {

    private final JavaPlugin plugin;
    private final Map<String, Integer> taskIds = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> remainingSeconds = new ConcurrentHashMap<>();

    public ArenaResetScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void scheduleAll() {
        cancelAll();
        // Check if reset system is enabled
        if (!NanoArenas.get().getConfigManager().isResetEnabled()) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &eAuto-reset system is disabled in configuration."));
            return;
        }
        
        for (Arena arena : Arena.getArenas()) {
            schedule(arena);
        }
    }

    public void schedule(Arena arena) {
        if (arena == null) return;

        // cancel existing task if present
        Integer existing = taskIds.remove(arena.getName());
        if (existing != null) {
            plugin.getServer().getScheduler().cancelTask(existing);
            remainingSeconds.remove(arena.getName());
        }

        int resetSeconds = arena.getResetTime();
        if (resetSeconds <= 0) {
            if (NanoArenas.get().getConfigManager().isDebugMode()) {
                Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &fAuto-reset disabled for arena &b" + arena.getName() + "&f (resetTime=" + resetSeconds + ")"));
            }
            return;
        }

        // Capture the configured seconds once to avoid mutations caused by arena.reset()
        final int configuredSeconds = Math.max(1, resetSeconds);

        AtomicInteger remaining = new AtomicInteger(configuredSeconds);
        remainingSeconds.put(arena.getName(), remaining);

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    AtomicInteger rem = remainingSeconds.get(arena.getName());
                    if (rem == null) return; // cancelled meanwhile

                    // If the arena has auto-reset paused, freeze the countdown (do not decrement)
                    if (arena.isAutoResetPaused()) {
                        return;
                    }

                    int value = rem.decrementAndGet();
                    if (value <= 0) {
                        try {
                            if (NanoArenas.get().getConfigManager().isDebugMode()) {
                                Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &fAuto-resetting arena &b" + arena.getName() + "&f now."));
                            }
                            // Arena.reset() handles firing events and measuring duration.
                            arena.reset();
                            // reload the captured configured seconds
                            rem.set(configuredSeconds);
                        } catch (Exception e) {
                            Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &cFailed to reset arena &b" + arena.getName() + "&c. See console for details."));
                            e.printStackTrace();
                            rem.set(configuredSeconds);
                        }
                    }
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &cError in arena countdown for arena &b" + arena.getName() + ". See console for details."));
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 
            NanoArenas.get().getConfigManager().getResetIntervalTicks(), 
            NanoArenas.get().getConfigManager().getResetIntervalTicks()).getTaskId();

        taskIds.put(arena.getName(), taskId);
    }

    public void cancel(Arena arena) {
        if (arena == null) return;
        Integer id = taskIds.remove(arena.getName());
        if (id != null) {
            plugin.getServer().getScheduler().cancelTask(id);
        }
        remainingSeconds.remove(arena.getName());
    }

    public void cancelAll() {
        for (Integer id : taskIds.values()) {
            if (id != null) {
                plugin.getServer().getScheduler().cancelTask(id);
            }
        }
        taskIds.clear();
        remainingSeconds.clear();
    }

    /**
     * Returns remaining seconds until next reset for the given arena, or -1 if no countdown scheduled.
     */
    public int getRemainingSeconds(Arena arena) {
        if (arena == null) return -1;
        AtomicInteger a = remainingSeconds.get(arena.getName());
        return a == null ? -1 : a.get();
    }
}
