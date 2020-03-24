package com.direwolf20.mininggadgets.common.items.upgrade;

import com.direwolf20.mininggadgets.common.Config;

import java.util.Arrays;
import java.util.Optional;

/**
 * Holds the different battery levels.
 *
 * @implNote the existence of this class shows a need for a more robust implementation
 *           of the current enum based Upgrade system.
 */
public enum UpgradeBatteryLevels {
    BATTERY(0, Config.MININGGADGET_MAXPOWER.get()),
    BATTERY_1(1, Config.UPGRADECOST_BATTERY1.get()),
    BATTERY_2(2, Config.UPGRADECOST_BATTERY2.get()),
    BATTERY_3(3, Config.UPGRADECOST_BATTERY3.get());

    private int level;
    private int power;

    UpgradeBatteryLevels(int level, int power) {
        this.level = level;
        this.power = power;
    }

    private int getLevel() {
        return level;
    }

    public int getPower() {
        return power;
    }

    /**
     * Quick any dirty way to get the amount of power required by each battery upgrade level
     */
    public static Optional<UpgradeBatteryLevels> getBatteryByLevel(int level) {
        return Arrays.stream(UpgradeBatteryLevels.values()).filter(e -> e.getLevel() == level).findFirst();
    }
}
