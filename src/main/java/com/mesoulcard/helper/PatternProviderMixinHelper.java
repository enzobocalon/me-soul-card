package com.mesoulcard.helper;

import appeng.api.networking.IManagedGridNode;
import com.mesoulcard.common.SoulService;

public class PatternProviderMixinHelper {
    public static void updateSoulDistributor(IManagedGridNode managedNode) {
        if (managedNode == null || managedNode.getNode() == null || !managedNode.isReady()) {
            return;
        }

        var grid = managedNode.getNode().getGrid();
        if (grid == null) return;

        var service = grid.getService(SoulService.class);
        if (service == null) return;

        var distributor = service.getDistributor(managedNode.getNode());
        if (distributor == null) return;

        distributor.updateSleep();
    }
}
