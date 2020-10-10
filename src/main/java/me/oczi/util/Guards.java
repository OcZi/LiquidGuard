package me.oczi.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface Guards {
    WorldGuardPlugin guardPlugin = WorldGuardPlugin.inst();
    WorldGuard guard = WorldGuard.getInstance();

    @SuppressWarnings("unchecked")
    static void registerFlags(Collection<? extends Flag<?>> flags) {
        WorldGuard.getInstance().getFlagRegistry()
            .registerAll((Collection<Flag<?>>) flags);
    }

    static com.sk89q.worldedit.util.Location getWorldEditLocation(Block block) {
        return BukkitAdapter.adapt(block.getLocation());
    }

    static LocalPlayer asLocalPlayer(Player player) {
        return guardPlugin.wrapPlayer(player);
    }
}
