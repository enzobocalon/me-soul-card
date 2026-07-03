package com.mesoulcard.helper;

import appeng.api.networking.IManagedGridNode;
import appeng.helpers.InterfaceLogicHost;
import appeng.parts.AEBasePart;
import com.mesoulcard.common.SoulService;
import com.mesoulcard.common.interfaces.ISoulDistributor;
import com.mesoulcard.common.interfaces.ISoulDistributorAccessor;
import com.mesoulcard.network.payloads.SyncAccelerationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public final class AccelerationMenuHelper {
    private AccelerationMenuHelper() {}

    public static int getMultiplier(@Nullable Object logic, @Nullable Object host) {
        var distributor = resolveDistributor(logic, host);
        return distributor != null ? distributor.getAccelerationMultiplier() : 1;
    }

    public static boolean isLocked(@Nullable Object logic, @Nullable Object host) {
        var distributor = resolveDistributor(logic, host);
        return distributor != null && distributor.isLocked();
    }

    public static void setMultiplier(@Nullable Object logic, @Nullable Object host, int multiplier) {
        var distributor = resolveDistributor(logic, host);
        if (distributor != null) {
            distributor.setAccelerationMultiplier(multiplier);
        }
    }

    public static void syncClientState(ServerPlayer player, @Nullable Object logic, @Nullable Object host) {
        PacketDistributor.sendToPlayer(player,
                new SyncAccelerationPacket(getMultiplier(logic, host), isLocked(logic, host)));
    }

    @Nullable
    private static ISoulDistributor resolveDistributor(@Nullable Object logic, @Nullable Object host) {
        var distributor = fromAccessor(logic);
        if (distributor != null) {
            return distributor;
        }

        distributor = fromAccessor(host);
        if (distributor != null) {
            return distributor;
        }

        if (host instanceof InterfaceLogicHost interfaceHost) {
            distributor = fromAccessor(interfaceHost.getInterfaceLogic());
            if (distributor != null) {
                return distributor;
            }
        }

        if (host instanceof AEBasePart part) {
            return fromGrid(part.getMainNode());
        }

        return null;
    }

    @Nullable
    private static ISoulDistributor fromAccessor(@Nullable Object target) {
        if (target instanceof ISoulDistributorAccessor accessor) {
            return accessor.meSoulCard$getDistributor();
        }
        return null;
    }

    @Nullable
    private static ISoulDistributor fromGrid(IManagedGridNode mainNode) {
        if (!mainNode.isActive()) {
            return null;
        }

        var grid = mainNode.getGrid();
        if (grid == null) {
            return null;
        }

        var service = grid.getService(SoulService.class);
        if (service == null) {
            return null;
        }

        return service.getDistributor(mainNode.getNode());
    }
}
