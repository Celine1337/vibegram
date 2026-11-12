package com.exteragram.messenger.pillstack.core;

import java.util.ArrayList;

public final class PillStackConfig {
    private PillStackConfig() {
    }

    public static ArrayList<Integer> getActivePills() {
        app.vibegram.pillstack.PillStackConfig.loadConfig(false);
        return app.vibegram.pillstack.PillStackConfig.getActivePills();
    }

    public static ArrayList<Integer> getHiddenPills() {
        app.vibegram.pillstack.PillStackConfig.loadConfig(false);
        return app.vibegram.pillstack.PillStackConfig.getHiddenPills();
    }

    public static int getLastActivePillId() {
        app.vibegram.pillstack.PillStackConfig.loadConfig(false);
        return app.vibegram.pillstack.PillStackConfig.lastActivePillId.Int();
    }
}
