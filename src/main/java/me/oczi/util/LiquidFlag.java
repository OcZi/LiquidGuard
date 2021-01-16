package me.oczi.util;

import com.sk89q.worldguard.protection.flags.StateFlag;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WorldGuard's {@link StateFlag} for the plugin.
 */
public enum LiquidFlag {
    WATER_ENTER("water-enter", true),
    LAVA_ENTER("lava-enter", true);

    private final StateFlag flag;

    LiquidFlag(String flagName, boolean def) {
        this.flag = new StateFlag(flagName, def);
    }

    private static List<StateFlag> flags =
        Arrays.stream(LiquidFlag.values())
            .map(LiquidFlag::stateFlag)
            .collect(Collectors.toList());

    public static List<StateFlag> getFlags() {
        return flags;
    }

    public StateFlag stateFlag() {
        return flag;
    }
}
