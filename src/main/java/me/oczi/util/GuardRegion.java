package me.oczi.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import static me.oczi.util.Guards.asLocalPlayer;
import static me.oczi.util.Guards.getWorldEditLocation;

public interface GuardRegion {
    RegionContainer container = Guards.guard
        .getPlatform()
        .getRegionContainer();

    static boolean isStateDeny(Location location, StateFlag flag) {
        return container.createQuery()
            .queryState(
                BukkitAdapter.adapt(location),
                null,
                flag) == StateFlag.State.DENY;
    }

    static boolean isDenyFor(Player player,
                             Location location,
                             StateFlag flag) {
        return container.createQuery()
            .queryState(
                BukkitAdapter.adapt(location),
                asLocalPlayer(player),
                flag) == StateFlag.State.DENY;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean isInARegion(Block block) {
        RegionQuery regionManager = container.createQuery();
        if (regionManager == null) {
            return false;
        }
        ApplicableRegionSet applicableRegions =
            regionManager.getApplicableRegions(getWorldEditLocation(block));
        return !applicableRegions.getRegions().isEmpty();
    }
}
