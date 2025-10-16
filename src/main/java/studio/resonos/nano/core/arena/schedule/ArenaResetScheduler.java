
package studio.resonos.nano.core.arena.schedule;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.resonos.nano.api.event.ArenaResetEvent;
import studio.resonos.nano.core.arena.Arena;


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
            plugin.getLogger().info("Auto-reset disabled for arena " + arena.getName() + " (resetTime=" + resetSeconds + ")");
            return;
        }

        AtomicInteger remaining = new AtomicInteger(resetSeconds);
        remainingSeconds.put(arena.getName(), remaining);

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    AtomicInteger rem = remainingSeconds.get(arena.getName());
                    if (rem == null) return; // cancelled meanwhile
                    int value = rem.decrementAndGet();
                    if (value <= 0) {
                        try {
                            // fire cancellable event BEFORE performing the reset
                            ArenaResetEvent event = new ArenaResetEvent(arena);
                            Bukkit.getServer().getPluginManager().callEvent(event);

                            if (event.isCancelled()) {
                                plugin.getLogger().info("Auto-reset cancelled for arena " + arena.getName());
                                int configured = Math.max(1, arena.getResetTime());
                                rem.set(configured);
                            } else {
                                plugin.getLogger().info("Auto-resetting arena " + arena.getName());
                                arena.reset();
                                int configured = Math.max(1, arena.getResetTime());
                                rem.set(configured);
                            }
                        } catch (Exception e) {
                            plugin.getLogger().severe("Failed to reset arena " + arena.getName() + ": " + e.getMessage());
                            e.printStackTrace();
                            int configured = Math.max(1, arena.getResetTime());
                            rem.set(configured);
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Error in arena countdown for " + arena.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L).getTaskId(); // tick every second

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