package me.oczi.util;

import me.oczi.LiquidGuardPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public interface Configs {
    FileConfiguration config =
        LiquidGuardPlugin.getInstance()
            .getConfig();

    static boolean isConnectorAsync() {
        return config
            .getBoolean("liquid-related.async-pathfinding", false);
    }

    static int getCacheLiquidTimeOut() {
        return config
            .getInt("liquid-related.cache-liquid-timeout", 5);
    }

    static int getRadiusOfRegions() {
        return config
            .getInt("region-related.region-radius", 10);
    }

    static int getCheckLiquidDelay() {
        return config
            .getInt("liquid-related.check-liquid-delay", 100);
    }

    static int getCheckLiquidCooldown() {
        return config
            .getInt("liquid-related.check-liquid-cooldown", 12);
    }

    static int getCheckLiquidTries() {
        return config
            .getInt("liquid-related.check-liquid-tries", 12);
    }
}
