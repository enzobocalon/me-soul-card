package com.mesoulcard.mixins.upgradeable;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.UpgradeableMenu;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import com.mesoulcard.helper.AccelerationMenuHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UpgradeableMenu.class)
public abstract class UpgradeableMenuMixin extends AEBaseMenu implements IAccelerationReceiver {

    @Unique
    private int meSoulCard$clientMultiplier = 1;

    @Unique
    private boolean meSoulCard$clientLock = false;

    public UpgradeableMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "broadcastChanges", at = @At("TAIL"))
    private void broadcastChanges(CallbackInfo ci) {
        if (!this.getPlayer().level().isClientSide) {
            if (this.getPlayer() instanceof ServerPlayer serverPlayer) {
                this.meSoulCard$syncClientState(serverPlayer);
            }
        }
    }

    /*
     * Receive from client in server
     */
    @Override
    @Unique
    public void meSoulCard$receiveStates(int multiplier) {
        AccelerationMenuHelper.setMultiplier(null, this.getActionHost(), multiplier);
    }

    @Override
    @Unique
    public void meSoulCard$syncClientState(ServerPlayer player) {
        AccelerationMenuHelper.syncClientState(player, null, this.getActionHost());
    }

    /*
     * Receive from server in client
     */
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
