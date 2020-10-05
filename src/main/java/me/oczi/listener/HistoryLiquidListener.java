package me.oczi.listener;

import me.oczi.HistoryLiquid;
import me.oczi.LiquidGuardPlugin;
import me.oczi.util.Liquids;
import org.bukkit.Location;
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
            Location location = liquidPlaced.getLocation();
            boolean isDeny = Liquids
                .switchLiquidDeny(location, bucket);
            if (!isDeny) {
                LiquidGuardPlugin.runTaskLater(
                    () -> HistoryLiquid.put(location),
                    1L);
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block blockClicked = event.getBlockClicked();
        System.out.println("history liquid fill called");
        if (blockClicked.isLiquid()) {
            HistoryLiquid.remove(blockClicked);
            System.out.println("history liquid fill success");
        }
    }

    @EventHandler
    public void onDispenser(BlockDispenseEvent event) {
        ItemStack item = event.getItem();
        System.out.println("dispenser called");
        Block block = event.getBlock();
        if (Liquids.isBucketLiquid(item.getType())) {
            LiquidGuardPlugin.runTask(
                () -> HistoryLiquid
                    .put(getRelativeLiquid(block)));
            System.out.println("dispenser success");
        } else if (item.getType() == Material.BUCKET) {
            HistoryLiquid.remove(getRelativeLiquid(block));
        }
    }

    private Block getRelativeLiquid(Block block) {
        Dispenser data = (Dispenser)
            block.getState().getBlockData();
        return block.getRelative(data.getFacing());
    }
}
