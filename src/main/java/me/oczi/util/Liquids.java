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

    /**
     * Disable the flow of the block.
     * If it's a waterlogged block it will be disabled
     * otherwise it will be deleted.
     * @param goal Goal block.
     */
    static void disableFlow(Block goal) {
        if (goal.isLiquid()) {
            goal.setType(Material.AIR);
            return;
        }
        Liquids.disableWaterlogged(goal);
    }

    /**
     * Get {@link LiquidFlag} equivalent of {@link LiquidType}.
     * @param type Liquid type.
     * @return LiquidFlag equivalent.
     */
    static LiquidFlag switchFlagByLiquid(LiquidType type) {
        return type == LiquidType.WATER
            ? LiquidFlag.WATER_ENTER
            : LiquidFlag.LAVA_ENTER;
    }

    /**
     * Check if liquid type of {@link LiquidFlag} is deny in the location.
     * @param location Location to check.
     * @param flag Liquid Flag to get Liquid type.
     * @return Is deny or not.
     */
    static boolean isLiquidDeny(Location location, LiquidFlag flag) {
        return GuardRegion.isStateDeny(location, flag.stateFlag());
    }

    /**
     * Disable {@link Waterlogged} block.
     *
     * Redundant methods are used to ensure
     * that it is disabled with the same type.
     * @param block Waterlogged block.
     */
    static void disableWaterlogged(Block block) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Waterlogged)) return;
        Waterlogged logged = (Waterlogged) data;
        if (logged.isWaterlogged()) {
            logged.setWaterlogged(false);
            setWaterloggedWithSameType(logged);
            block.setType(logged.getMaterial());
            block.setBlockData(logged);
        }
    }

    /**
     * Set waterlogged {@link Slab}
     * with the same {@link Slab.Type}.
     * @param waterlogged Waterlogged block data.
     */
    static void setWaterloggedWithSameType(Waterlogged waterlogged) {
        if (waterlogged instanceof Slab) {
            Slab slab = (Slab) waterlogged;
            slab.setType(slab.getType());
        }
    }
}
