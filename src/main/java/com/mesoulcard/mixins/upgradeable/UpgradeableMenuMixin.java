package com.mesoulcard.mixins.upgradeable;

import appeng.helpers.InterfaceLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.parts.AEBasePart;
import com.mesoulcard.common.SoulService;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import com.mesoulcard.common.interfaces.ISoulDistributorAccessor;
import com.mesoulcard.network.payloads.SyncAccelerationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UpgradeableMenu.class)
public abstract class UpgradeableMenuMixin extends AEBaseMenu implements IAccelerationReceiver {

    @Unique
    private int clientMultiplier = 1;

    @Unique
    private boolean clientLock = false;

    public UpgradeableMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "broadcastChanges", at = @At("TAIL"))
    private void broadcastChanges(CallbackInfo ci) {
        if (!this.getPlayer().level().isClientSide) {
            int currentMultiplier = getMultiplier();
            boolean currentLocked = isLocked();

            if (this.getPlayer() instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer,
                        new SyncAccelerationPacket(currentMultiplier, currentLocked));
            }
        }
    }

    @Unique
    private int getMultiplier() {
        var host = this.getActionHost();
        if (host instanceof ISoulDistributorAccessor accessor) {
            var distributor = accessor.getDistributor();
            if (distributor != null) {
                return distributor.getAccelerationMultiplier();
            }
        }

        // Try InterfaceLogicHost -> InterfaceLogic
        if (host instanceof InterfaceLogicHost interfaceHost) {
            var logic = interfaceHost.getInterfaceLogic();
            if (logic instanceof ISoulDistributorAccessor accessor) {
                var distributor = accessor.getDistributor();
                if (distributor != null) {
                    return distributor.getAccelerationMultiplier();
                }
            }
        }

        // Fallback to grid service
        if (!(host instanceof AEBasePart part))
            return 1;

        var mainNode = part.getMainNode();
        if (!mainNode.isActive())
            return 1;

        var grid = mainNode.getGrid();
        if (grid == null)
            return 1;

        var service = grid.getService(SoulService.class);
        if (service == null)
            return 1;

        var distributor = service.getDistributor(mainNode.getNode());
        if (distributor == null)
            return 1;

        return distributor.getAccelerationMultiplier();
    }

    @Unique
    private boolean isLocked() {
        var host = this.getActionHost();
        if (host instanceof ISoulDistributorAccessor accessor) {
            var distributor = accessor.getDistributor();
            if (distributor != null) {
                return distributor.isLocked();
            }
        }

        // Try InterfaceLogicHost -> InterfaceLogic
        if (host instanceof InterfaceLogicHost interfaceHost) {
            var logic = interfaceHost.getInterfaceLogic();
            if (logic instanceof ISoulDistributorAccessor accessor) {
                var distributor = accessor.getDistributor();
                if (distributor != null) {
                    return distributor.isLocked();
                }
            }
        }

        // Fallback to grid service
        if (!(host instanceof AEBasePart part))
            return false;

        var mainNode = part.getMainNode();
        if (!mainNode.isActive())
            return false;

        var grid = mainNode.getGrid();
        if (grid == null)
            return false;

        var service = grid.getService(SoulService.class);
        if (service == null)
            return false;

        var distributor = service.getDistributor(mainNode.getNode());
        if (distributor == null)
            return false;

        return distributor.isLocked();
    }

    /*
     * Receive from client in server
     */
    @Override
    @Unique
    public void receiveStates(int multiplier) {
        var host = this.getActionHost();
        if (host instanceof ISoulDistributorAccessor accessor) {
            var distributor = accessor.getDistributor();
            if (distributor != null) {
                distributor.setAccelerationMultiplier(multiplier);
                return;
            }
        }

        // Fallback to grid service
        if (!(host instanceof AEBasePart part))
            return;

        var mainNode = part.getMainNode();
        if (!mainNode.isActive())
            return;

        var grid = mainNode.getGrid();
        if (grid == null)
            return;

        var service = grid.getService(SoulService.class);
        if (service == null)
            return;

        var distributor = service.getDistributor(mainNode.getNode());
        if (distributor == null)
            return;

        distributor.setAccelerationMultiplier(multiplier);
    }

    /*
     * Receive from server in client
     */
    @Override
    public void receiveClientSync(int multiplier, boolean locked) {
        this.clientMultiplier = multiplier;
        this.clientLock = locked;
    }

    @Override
    public int getClientMultiplier() {
        return this.clientMultiplier;
    }

    @Override
    public boolean getClientLockStatus() {
        return this.clientLock;
    }
}
