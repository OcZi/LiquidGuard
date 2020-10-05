package me.oczi.util;

import me.oczi.listener.CallableListener;
import me.oczi.logger.CancelDetector;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Listeners {

    /**
     * Create a new {@link RegisteredListener} with
     * a {@link CancelDetector} as {@link EventExecutor}.
     * @param listener Listener to use.
     * @param plugin Plugin to use.
     * @param <T> Type.
     * @return RegisteredListener with a internal logger.
     */
    static <T extends Event> RegisteredListener newLoggerListener(CallableListener<T> listener,
                                                                  JavaPlugin plugin) {
        return new RegisteredListener(
            listener,
            new CancelDetector<>(listener),
            EventPriority.HIGHEST,
            plugin,
            true);
    }

    /**
     * Inject the listener in every {@link HandlerList} that exist.
     * @param listener Listener to register.
     */
    static void justInject(RegisteredListener listener) {
        for (HandlerList handlerList : HandlerList.getHandlerLists()) {
            handlerList.register(listener);
        }
    }

    /**
     * Inject the listener only in {@link HandlerList} that have
     * WorldGuard registered.
     * @param listener Listener to register.
     */
    static void injectListener(RegisteredListener listener) {
        for (HandlerList handlerList : HandlerList.getHandlerLists()) {
            Plugin plugin = getPluginOfHandlerList(handlerList);
            if (plugin == null ||
                plugin.getName().contains("WorldGuard")) {
                handlerList.register(listener);
            }
        }
    }

    /**
     * Inject the listener only in {@link HandlerList} that have
     * WorldGuard registered.
     * @param listener Listener to register.
     */
    @Deprecated
    static void injectListener(Listener listener) {
        Plugin plugin = null;
        for (HandlerList handlerList : HandlerList.getHandlerLists()) {
            for (RegisteredListener registeredListener : handlerList.getRegisteredListeners()) {
                if (registeredListener.getPlugin().getName().equals("WorldGuard")) {
                    plugin = registeredListener.getPlugin();
                    break;
                }
            }
        }
        if (plugin != null) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

    /**
     * Inject listener directly into
     * the {@link HandlerList} of a {@link Event} class.
     * @param clazz Event class to inject.
     * @param listener Listener to register.
     * @param <C> Class type.
     */
    // Throws NullPointerException in method.invoke
    static <C extends Event> void injectHandlerListOf(Class<C> clazz,
                                                      RegisteredListener listener) {
        try {
            Method method = clazz.getDeclaredMethod("getHandlers");
            method.setAccessible(true);
            HandlerList handlerList = (HandlerList) method.invoke(null);
            handlerList.register(listener);
        } catch (NoSuchMethodException |
            InvocationTargetException |
            IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the plugin of the first {@link RegisteredListener} in the {@link HandlerList}.
     * @param list HandlerList to get plugin.
     * @return The plugin class.
     */
    @Nullable
    static Plugin getPluginOfHandlerList(HandlerList list) {
        @NotNull RegisteredListener[] array = list.getRegisteredListeners();
        return array.length > 0
            ? list.getRegisteredListeners()[0].getPlugin()
            : null;
    }

    /**
     * Check if HandlerList is handle a Listener with a specific name.
     * @param handlerList HandlerList to check.
     * @param listenerName Listener name to check.
     * @return Is handle by this HandlerList.
     */
    static boolean isHandle(HandlerList handlerList, String listenerName) {
        for (RegisteredListener registeredListener : handlerList.getRegisteredListeners()) {
            String simpleName = registeredListener
                .getListener()
                .getClass()
                .getSimpleName();
            if (simpleName.equalsIgnoreCase(listenerName)) {
                return true;
            }
        }
        return false;
    }
}
