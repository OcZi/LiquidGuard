package me.oczi.listener;

import me.oczi.HistoryLiquid;
import me.oczi.LiquidEntry;
import me.oczi.LiquidGuardPlugin;
import me.oczi.api.LiquidConnection;
import me.oczi.api.LiquidType;
import me.oczi.api.Pathfinding;
import me.oczi.api.TaskState;
import me.oczi.api.node.block.LiquidNode;
import me.oczi.api.region.Region;
import me.oczi.api.region.Regions;
import me.oczi.util.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.function.Consumer;

public class LiquidGuardListener
    implements Listener {

    @EventHandler
    public void onLiquidEnter(BlockFromToEvent event) {
        final Block from = event.getBlock();
        final Block to = event.getToBlock();
        // Check material is a liquid type or an active waterlogged
        LiquidType liquidType = BukkitParser
            .uncheckedAsLiquid(from);
        if (liquidType == LiquidType.NONE) return;
        LiquidFlag flag = Liquids.switchFlagByLiquid(liquidType);
        if (!Liquids.switchLiquidDeny(
            to.getLocation(), flag)) return;
        if (Liquids.switchLiquidDeny(
            from.getLocation(), flag)) return;

        Region cuboid = Regions.newCuboidWithRadius(
            from, Configs.getRadiusOfRegions());
        BlockFace ignore = from.getFace(to);
        boolean cancel = true;
        for (LiquidEntry entry : HistoryLiquid.getInRegion(cuboid)) {
            if (!entry.tryLock()) return;
            if (entry.getLiquidType() != liquidType ||
                entry.getPlayer() != null &&
                    !GuardRegion.isDenyFor(entry.getPlayer(),
                        to.getLocation(),
                        flag.stateFlag())) {
                // Set to false to admit the liquid flow to the region.
                // can be cancelled by next entries.
                cancel = false;
                continue;
            }
            // Set true again in case that is false by a previous entry.
            cancel = true;

            entry.lock();
            Block goal = entry.getNode().getBlock();
            Set<Block> levelBlocks = cuboid
                .getBlocksBetweenLevel(
                    to.getY(), goal.getY());
            LiquidConnection connector = Pathfinding
                .liquidConnector(
                    from,
                    goal,
                    levelBlocks,
                    ignore,
                    AsyncThread.getExecutorService());
            runByConfig(connector,
                node -> isSuccess(
                    entry, connector, from),
                t -> {
                    entry.unlock();
                    t.printStackTrace();
                    HistoryLiquid.remove(goal);
                });
        }
        event.setCancelled(cancel);
    }

    private void runByConfig(LiquidConnection connector,
                             Consumer<LiquidNode> success,
                             Consumer<Throwable> failure) {
        if (Configs.isConnectorAsync()) {
            connector.runAsync(success, failure);
        } else {
            connector.run(success, failure);
        }
    }

    private void isSuccess(LiquidEntry entry,
                           LiquidConnection connector,
                           Block cooldownBlock) {
        if (connector.getState() != TaskState.SUCCESSFULLY) {
            entry.unlock();
            return;
        }
        LiquidGuardPlugin.runTask(
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        Block goal = entry.getNode().getBlock();
                        Liquids.disableFlow(goal);
                        HistoryLiquid.remove(goal);
                        runCooldown(cooldownBlock);
                    } finally {
                        entry.unlock();
                    }
                }
            });
    }

    private void runCooldown(Block cooldownBlock) {
        LiquidGuardPlugin.runTaskTimer(
            new BukkitRunnable() {
                private int cooldown;

                @Override
                public void run() {
                    if (!checkWater(cooldownBlock)) {
                        this.cancel();
                    }
                    cooldown++;
                    if (cooldown > Configs.getCheckLiquidTries()) {
                        Liquids.disableFlow(cooldownBlock);
                        this.cancel();
                    }
                }
            },
            Configs.getCheckLiquidDelay(),
            Configs.getCheckLiquidCooldown());
    }

    public boolean checkWater(Block block) {
        if (!block.isLiquid()) return false;
        Levelled levelled = (Levelled) block.getBlockData();
        int level = levelled.getLevel();
        return level == 0 || level > 1;
    }
}
