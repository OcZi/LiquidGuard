package me.oczi.listener;

import me.oczi.HistoryLiquid;
import me.oczi.LiquidGuardPlugin;
import me.oczi.util.GuardRegion;
import me.oczi.util.Liquids;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

public class HistoryLiquidListener implements Listener {

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Material bucket = event.getBucket();
        if (Liquids.isBucketLiquid(bucket)) {
            Block liquidPlaced = event.getBlock();
            if (!GuardRegion.isInARegion(liquidPlaced)) {
                LiquidGuardPlugin.runTask(
                    () -> HistoryLiquid.put(
                        event.getPlayer(), liquidPlaced));
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block blockClicked = event.getBlockClicked();
        HistoryLiquid.remove(blockClicked);
    }

    @EventHandler
    public void onDispenser(BlockDispenseEvent event) {
        ItemStack item = event.getItem();
        Block block = event.getBlock();
        // Redundant code for each condition
        // to avoid an unnecessary query of region
        // on every dispenser event.
        if (Liquids.isBucketLiquid(item.getType())) {
            if (!GuardRegion.isInARegion(block)) {
                LiquidGuardPlugin.runTask(
                    () -> HistoryLiquid
                        .put(getRelativeLiquid(block)));
            }
        } else if (item.getType() == Material.BUCKET) {
            if (!GuardRegion.isInARegion(block)) {
                HistoryLiquid.remove(getRelativeLiquid(block));
            }
        }
    }

    private Block getRelativeLiquid(Block block) {
        Dispenser data = (Dispenser)
            block.getState().getBlockData();
        return block.getRelative(data.getFacing());
    }
}
