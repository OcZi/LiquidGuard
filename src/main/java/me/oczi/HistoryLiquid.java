package me.oczi;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.oczi.api.LiquidType;
import me.oczi.api.node.block.LiquidNode;
import me.oczi.api.region.Region;
import me.oczi.util.BukkitParser;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class HistoryLiquid {
    private static final Cache<Location, LiquidNode> historyCache;

    static {
        historyCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    }

    public static List<LiquidEntry> getInRegion(Region region) {
        historyCache.cleanUp();
        if (isEmpty()) {
            return Collections.emptyList();
        }
        List<LiquidEntry> result = new ArrayList<>();
        for (Block block : region.getBlocks()) {
            Location location = block.getLocation();
            if (hasHistoryOf(location)) {
                result.add(
                    new LiquidEntry(
                        getHistoryMap().get(location)));
            }
        }
        return result;
    }

    public static void put(Location location) {
        put(location.getBlock(), location);
    }

    public static void put(Block block) {
        put(block, block.getLocation());
    }

    private static void put(Block block, Location location) {
        historyCache.cleanUp();
        LiquidType liquidType = BukkitParser.checkedAsLiquid(
            block,
            "Block is not a liquid type.");
        LiquidNode node = LiquidNode
            .newNode(liquidType, block);
        historyCache.put(location, node);
    }

    public static boolean hasHistoryOf(Location location) {
        historyCache.cleanUp();
        return getHistoryMap().containsKey(location);
    }

    public static void remove(Block block) {
        remove(block.getLocation());
    }

    public static void remove(Location location) {
        historyCache.invalidate(location);
        historyCache.cleanUp();
    }

    public static boolean isEmpty() {
        return getHistoryMap().isEmpty();
    }

    public static Cache<Location, LiquidNode> getHistoryCache() {
        return historyCache;
    }

    public static ConcurrentMap<Location, LiquidNode> getHistoryMap() {
        return historyCache.asMap();
    }
}
