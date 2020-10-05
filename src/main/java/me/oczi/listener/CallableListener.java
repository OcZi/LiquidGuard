package me.oczi.listener;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * A {@link Listener} that only be called
 * manually and can be cancelled.
 * @param <T> Event's type.
 */
public interface CallableListener<T extends Event>
    extends Listener {

    /**
     * Call the event.
     * @param event Event to call.
     */
    void call(T event);

    /**
     * Get the Listener's type event.
     * @return Class type event of Listener.
     */
    Class<T> getType();
}
