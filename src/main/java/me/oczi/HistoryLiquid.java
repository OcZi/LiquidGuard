package me.oczi;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.oczi.api.LiquidType;
import me.oczi.api.node.block.LiquidNode;
import me.oczi.api.region.Region;
import me.oczi.util.BukkitParser;
import me.oczi.util.Configs;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class HistoryLiquid {
    private static final Cache<Location, LiquidEntry> historyCache;

    static {
        historyCache = CacheBuilder.newBuilder()
            .expireAfterWrite(
                Configs.getCacheLiquidTimeOut(),
                TimeUnit.MINUTES)
            .build();
    }

    public static List<LiquidEntry> getInRegion(Region region) {
        historyCache.cleanUp();
        List<LiquidEntry> list = new ArrayList<>();
        for (Block block : region.getBlocks()) {
            LiquidEntry entry = historyCache.getIfPresent(
                block.getLocation());
            if (entry == null) continue;
            list.add(entry);
        }
        return list;
    }

    public static void put(Location location) {
        put(null, location);
    }

    public static void put(@Nullable Player player, Location location) {
        put(player, location.getBlock(), location);
    }

    public static void put(Block block) {
        put(null, block);
    }

    public static void put(@Nullable Player player, Block block) {
        put(player, block, block.getLocation());
    }

    private static void put(@Nullable Player player, Block block, Location location) {
        historyCache.cleanUp();
        LiquidType liquidType = BukkitParser
            .uncheckedAsLiquid(block);
        if (!LiquidType.isValid(liquidType)) return;
        LiquidNode node = LiquidNode
            .newNode(liquidType, block);
        getHistoryMap().putIfAbsent(location,
            LiquidEntry.of(player, node));
    }

    public static boolean hasHistoryOf(Location location) {
        historyCache.cleanUp();
        return getHistoryMap().containsKey(location);
    }

    public static void remove(Block block) {
        remove(block.getLocation());
    }

    public static void remove(Location location) {
        LiquidEntry entry = historyCache.getIfPresent(location);
        if (entry != null) entry.lock();
        historyCache.invalidate(location);
        historyCache.cleanUp();
    }

    public static boolean isEmpty() {
        return getHistoryMap().isEmpty();
    }

    public static Cache<Location, LiquidEntry> getHistoryCache() {
        return historyCache;
    }

    public static ConcurrentMap<Location, LiquidEntry> getHistoryMap() {
        return historyCache.asMap();
    }
}
