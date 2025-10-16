
package studio.resonos.nano.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import studio.resonos.nano.core.arena.Arena;

public class ArenaResetEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    private boolean cancelled = false;

    public ArenaResetEvent(Arena arena) {
        this.arena = arena;
    }

    public Arena getArena() {
        return arena;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}