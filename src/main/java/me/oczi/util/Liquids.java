package me.oczi.util;

import me.oczi.api.LiquidType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Slab;

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

    static void disableFlow(Block goal) {
        if (goal.isLiquid()) {
            goal.setType(Material.AIR);
            return;
        }
        Liquids.switchWaterlogged(goal);
    }

    static LiquidFlag switchFlagByLiquid(LiquidType material) {
        return material == LiquidType.WATER
            ? LiquidFlag.WATER_ENTER
            : LiquidFlag.LAVA_ENTER;
    }

    static boolean switchLiquidDeny(Location location, LiquidFlag flag) {
        return GuardRegion.isStateDeny(location, flag.stateFlag());
    }

    static void switchWaterlogged(Block block) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Waterlogged)) return;
        Waterlogged logged = (Waterlogged) data;
        if (logged.isWaterlogged()) {
            logged.setWaterlogged(false);
            setWithSameType(logged);
            block.setType(logged.getMaterial());
            block.setBlockData(logged);
        }
    }

    static void setWithSameType(Waterlogged waterlogged) {
        if (waterlogged instanceof Slab) {
            Slab slab = (Slab) waterlogged;
            slab.setType(slab.getType());
        }
    }
}
