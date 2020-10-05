package me.oczi.logger;

import me.oczi.listener.CallableListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public class CancelDetector<T extends Event> implements EventExecutor {
    private final CallableListener<T> callableListener;

    public CancelDetector(CallableListener<T> callableListener) {
        this.callableListener = callableListener;
    }

    public void execute(Listener listener,
                        @NotNull Event event) {
        execute(event);
    }

    @SuppressWarnings("unchecked")
    public void execute(Event event) {
        // Java 8 doesn't support Enhanced instanceof...
        if (!callableListener.getType()
            .getSimpleName()
            .equals(event.getEventName())) {
            return;
        }
        callableListener.call((T) event);
    }

    /*
    public RegisteredListener getListenerOf(Event event, String pluginName) {
        for (RegisteredListener registeredListener :
            event.getHandlers().getRegisteredListeners()) {
            if (registeredListener.getPlugin().getName().equals(pluginName)) {
                return registeredListener;
            }
        }
        return null;
    }
     */
}
