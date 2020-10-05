package me.oczi.util;

import com.sk89q.worldguard.protection.flags.Flags;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;

import static me.oczi.util.Guards.isStateDeny;

public interface Liquids {

    /**
     * Check if bucket contains a liquid.
     * Milk will be ignored.
     *
     * @param bucket Bucket to check.
     * @return Is liquid or not.
     */
    static boolean isBucketLiquid(Material bucket) {
        return bucket == Material.WATER_BUCKET ||
               bucket == Material.LAVA_BUCKET;
    }

    /**
     * Check if bucket contains a liquid.
     * Milk will be ignored.
     *
     * @param bucket Bucket to check.
     * @return Is liquid or not.
     */
    static Material bucketLiquidToLiquid(Material bucket) {
        return bucket == Material.WATER_BUCKET
            ? Material.WATER
            : Material.LAVA;
    }

    static boolean isLiquidDeny(Block block) {
        Location location = block.getLocation();
        return isWaterDeny(location) && isLavaDeny(location);
    }

    static boolean switchLiquidDeny(Location location,
                                    Material material) {
        return material == Material.WATER
            ? isWaterDeny(location)
            : isLavaDeny(location);
    }

    static void switchWaterlogged(Block block) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Waterlogged)) {
            return;
        }

        Waterlogged logged = (Waterlogged) data;
        logged.setWaterlogged(!logged.isWaterlogged());
        block.setBlockData(logged);
    }

    static boolean isWaterDeny(Location location) {
        return isStateDeny(location, Flags.WATER_FLOW);
    }

    static boolean isLavaDeny(Location location) {
        return isStateDeny(location, Flags.LAVA_FLOW);
    }
}
