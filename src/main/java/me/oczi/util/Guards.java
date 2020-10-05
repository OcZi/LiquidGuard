package me.oczi.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;

public interface Guards {
    RegionContainer container = WorldGuard.getInstance()
        .getPlatform()
        .getRegionContainer();

    static boolean isStateDeny(Location location, StateFlag flag) {
        StateFlag.State state = container.createQuery()
            .queryState(
                BukkitAdapter.adapt(location),
                null,
                flag);
        return state == StateFlag.State.DENY;
    }

    static BlockVector3 getVector(Location location) {
        return BukkitAdapter.adapt(location)
            .toVector()
            .toBlockPoint();
    }
}
