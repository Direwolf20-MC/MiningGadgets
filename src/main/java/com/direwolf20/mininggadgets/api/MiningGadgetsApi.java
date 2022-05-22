package com.direwolf20.mininggadgets.api;

import com.direwolf20.mininggadgets.api.upgrades.UpgradesRegistry;

public class MiningGadgetsApi {
    public static final String MOD_ID = "mininggadgets";
    private static MiningGadgetsApi instance;

    private final UpgradesRegistry upgradesRegistry = new UpgradesRegistry();

    public static MiningGadgetsApi get() {
        if (instance == null) {
            instance = new MiningGadgetsApi();
        }

        return instance;
    }

    public UpgradesRegistry upgradesRegistry() {
        return upgradesRegistry;
    }
}
