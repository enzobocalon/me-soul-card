package com.mesoulcard.mixins.pprovider;

import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.PatternProviderMenu;
import appeng.parts.AEBasePart;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import com.mesoulcard.common.interfaces.ISoulDistributorAccessor;
import com.mesoulcard.common.SoulService;
import com.mesoulcard.network.payloads.SyncAccelerationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ PatternProviderMenu.class })
public class PatternProviderMenuMixin extends AEBaseMenu implements IAccelerationReceiver {

    @Unique
    private int meSoulCard$clientMultiplier = 1;

    @Unique
    private boolean meSoulCard$clientLock = false;

    public PatternProviderMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory,
                                    PatternProviderLogicHost host) {
        super(menuType, id, playerInventory, host);
    }

    @Final
    @Shadow
    protected PatternProviderLogic logic;

    @Inject(method = "broadcastChanges", at = @At("TAIL"))
    private void broadcastChanges(CallbackInfo ci) {
        if (!this.getPlayer().level().isClientSide()) {
            int currentMultiplier = meSoulCard$getMultiplier();
            boolean currentLocked = meSoulCard$isLocked();

            if (this.getPlayer() instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer,
                        new SyncAccelerationPacket(currentMultiplier, currentLocked));
            }
        }
    }


    @Unique
    private int meSoulCard$getMultiplier() {
        if (this.logic instanceof ISoulDistributorAccessor accessor) {
            var distributor = accessor.meSoulCard$getDistributor();
            if (distributor != null) {
                return distributor.getAccelerationMultiplier();
            }
        }

        // Fallback to grid service
        var host = this.getActionHost();
        if (!(host instanceof AEBasePart part)) return 1;

        var mainNode = part.getMainNode();
        if (!mainNode.isActive()) return 1;

        var grid = mainNode.getGrid();
        if (grid == null) return 1;

        var service = grid.getService(SoulService.class);
        if (service == null) return 1;

        var distributor = service.getDistributor(mainNode.getNode());
        if (distributor == null)  return 1;

        return distributor.getAccelerationMultiplier();
    }

    @Unique
    private boolean meSoulCard$isLocked() {
        if (this.logic instanceof ISoulDistributorAccessor accessor) {
            var distributor = accessor.meSoulCard$getDistributor();
            if (distributor != null) {
                return distributor.isLocked();
            }
        }

        // Fallback to grid service
        var host = this.getActionHost();
        if (!(host instanceof AEBasePart part)) return false;

        var mainNode = part.getMainNode();
        if (!mainNode.isActive()) return false;

        var grid = mainNode.getGrid();
        if (grid == null) return false;

        var service = grid.getService(SoulService.class);
        if (service == null) return false;

        var distributor = service.getDistributor(mainNode.getNode());
        if (distributor == null) return false;

        return distributor.isLocked();
    }

    /*
     * Receive from client in server
     * */
    @Override
    @Unique
    public void meSoulCard$receiveStates(int multiplier) {
        if (this.logic instanceof ISoulDistributorAccessor accessor) {
            var distributor = accessor.meSoulCard$getDistributor();
            if (distributor != null) {
                distributor.setAccelerationMultiplier(multiplier);
                return;
            }
        }

        // Fallback to grid service
        var host = this.getActionHost();
        if (!(host instanceof AEBasePart part)) return;

        var mainNode = part.getMainNode();
        if (!mainNode.isActive()) return;

        var grid = mainNode.getGrid();
        if (grid == null) return;

        var service = grid.getService(SoulService.class);
        if (service == null) return;

        var distributor = service.getDistributor(mainNode.getNode());
        if (distributor == null) return;

        distributor.setAccelerationMultiplier(multiplier);
    }


    /*
     * Receive from server in client
     * */
    @Override
    public void meSoulCard$receiveClientSync(int multiplier, boolean locked) {
        this.meSoulCard$clientMultiplier = multiplier;
        this.meSoulCard$clientLock = locked;
    }

    @Override
    public int meSoulCard$getClientMultiplier() {
        return this.meSoulCard$clientMultiplier;
    }

    @Override
    public boolean meSoulCard$getClientLockStatus() {
        return this.meSoulCard$clientLock;
    }

}