package me.oczi;

import me.oczi.listener.HistoryLiquidListener;
import me.oczi.listener.LiquidGuardListener;
import me.oczi.util.Guards;
import me.oczi.util.LiquidFlag;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LiquidGuardPlugin extends JavaPlugin {
    private static LiquidGuardPlugin instance;

    public LiquidGuardPlugin() {
        instance = this;
    }

    @Override
    public void onLoad() {
        Guards.registerFlags(LiquidFlag.getFlags());
    }

    @Override
    public void onEnable() {
        super.saveDefaultConfig();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(
            new HistoryLiquidListener(), this);

        pluginManager.registerEvents(
            new LiquidGuardListener(), this);
    }

    public static BukkitTask runTaskLater(BukkitRunnable runnable,
                                          long delay) {
        return runnable.runTaskLater(
            instance,
            delay);
    }

    public static BukkitTask runTaskLater(Runnable runnable,
                                          long delay) {
        return Bukkit.getScheduler()
            .runTaskLater(
                instance,
                runnable,
                delay);
    }

    public static BukkitTask runTaskTimer(BukkitRunnable runnable,
                                          long delay,
                                          int period) {
        return runnable.runTaskTimer(
            instance,
            delay,
            period);
    }

    public static BukkitTask runTask(BukkitRunnable runnable) {
        return runnable.runTask(
            instance);
    }

    public static BukkitTask runTask(Runnable runnable) {
        return Bukkit.getScheduler()
            .runTask(
                instance, runnable);
    }

    public static void cancelTask(int id) {
        Bukkit.getScheduler().cancelTask(id);
    }

    public static LiquidGuardPlugin getInstance() {
        return instance;
    }
}
