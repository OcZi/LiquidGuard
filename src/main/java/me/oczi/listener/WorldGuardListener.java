package me.oczi.listener;

import me.oczi.HistoryLiquid;
import me.oczi.LiquidConnector;
import me.oczi.LiquidEntry;
import me.oczi.LiquidGuardPlugin;
import me.oczi.api.LiquidType;
import me.oczi.api.TaskState;
import me.oczi.api.node.block.ALiquidNode;
import me.oczi.api.region.Region;
import me.oczi.api.region.Regions;
import me.oczi.util.BukkitParser;
import me.oczi.util.Liquids;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;

public class WorldGuardListener
    implements CallableListener<BlockFromToEvent> {

    @Override
    public void call(BlockFromToEvent event) {
        final Block from = event.getBlock();
        final Block to = event.getToBlock();
        // Check material is a liquid type or an active waterlogged
        LiquidType liquidType = BukkitParser.asLiquid(from.getType());
        if (liquidType == LiquidType.NONE) {
            return;
        }
        boolean denied = Liquids.switchLiquidDeny(
            to.getLocation(), liquidType.getMaterial());
        if (!denied) {
            return;
        }

        /*if (HistoryLiquid.isEmpty()) {
            HistoryLiquid.put(
                new Location(
                    from.getWorld(),
                    203, 72, 202));
        }*/
        Region cuboid = Regions.newCuboidWithRadius(from, 20);
        List<LiquidEntry> inRegion = HistoryLiquid
            .getInRegion(cuboid);
        if (inRegion.isEmpty()) {
            return;
        }
        BlockFace ignore = from.getFace(to);
        for (LiquidEntry liquidEntry : inRegion) {
            Set<Block> levelBlocks = cuboid
                .getBlocksBetweenLevel(
                    to.getY(), from.getY());
            Block goal = liquidEntry.getNode().getBlock();
            LiquidConnector connector = new LiquidConnector(
                from,
                goal,
                levelBlocks,
                ignore);
            connector.run(
                node -> isSuccess(
                    connector, goal, to),
                Throwable::printStackTrace);
        }
    }

    private void isSuccess(LiquidConnector connector,
                           Block goal,
                           Block cooldownBlock) {
        if (connector.getState() != TaskState.SUCCESSFULLY) {
            return;
        }

        System.out.println("done!!!");
        LiquidGuardPlugin.runTask(
            new BukkitRunnable() {
                @Override
                public void run() {
                    disableFlow(goal);
                    HistoryLiquid.remove(goal.getLocation());
                    runCooldown(cooldownBlock);
                }
            });
        Set<ALiquidNode> blackSet = connector.iterator()
            .getCheckedSet()
            .getBlackSet();

        blackSet.forEach(n ->
            LiquidGuardPlugin.runTask(() -> {
                n.getBlock().setType
                    (Material.GRANITE);
                LiquidGuardPlugin.runTaskLater(
                    () -> n.getBlock().setType(Material.AIR),
                    10L);
            }));
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
                    if (cooldown > 10) {
                        disableFlow(cooldownBlock);
                        this.cancel();
                    }
                }
            },
            100,
            12);
    }

    private void disableFlow(Block goal) {
        // Disabling waterlogged in some cases not work has intended.
        // but, if is set with the same material without data,
        // will stop the flow.
        goal.setType(!goal.isLiquid()
            ? goal.getType()
            : Material.AIR);
    }

    public boolean checkWater(Block block) {
        if (!block.isLiquid()) {
            return false;
        }
        Levelled levelled = (Levelled) block.getBlockData();
        int level = levelled.getLevel();
        return level == 0 || level > 1;
    }

    @Override
    public Class<BlockFromToEvent> getType() {
        return BlockFromToEvent.class;
    }
}
