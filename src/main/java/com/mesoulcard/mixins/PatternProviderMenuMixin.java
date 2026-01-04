package com.mesoulcard.mixins;

import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.PatternProviderMenu;
import appeng.parts.AEBasePart;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
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

@Mixin({PatternProviderMenu.class})
public class PatternProviderMenuMixin extends AEBaseMenu implements IAccelerationReceiver {

    @Unique
    private int clientMultiplier = 1;

    public PatternProviderMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, PatternProviderLogicHost host) {
        super(menuType, id, playerInventory, host);
    }

    @Final
    @Shadow
    protected PatternProviderLogic logic;

    @Inject(method = "broadcastChanges", at = @At("TAIL"))
    private void broadcastChanges(CallbackInfo ci) {
        if (!this.getPlayer().level().isClientSide()) {
            var host = this.getActionHost();
            if (host instanceof AEBasePart part) {
                var mainNode = part.getMainNode();
                if (mainNode.isActive()) {
                    var grid = mainNode.getGrid();
                    if (grid != null) {
                        var service = grid.getService(SoulService.class);
                        if (service != null) {
                            var dist = service.getDistributor(mainNode.getNode());
                            if (dist != null) {
                                int currentMultiplier = dist.getAccelerationMultiplier();
                                if (this.getPlayer() instanceof ServerPlayer serverPlayer) {
                                    PacketDistributor.sendToPlayer(serverPlayer,
                                            new SyncAccelerationPacket(currentMultiplier));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @Unique
    public void receiveStates(int multiplier) {
        var host = this.getActionHost();
        if (!(host instanceof AEBasePart part)) {
            return;
        }

        var mainNode = part.getMainNode();
        if (!mainNode.isActive()) {
            return;
        }

        var grid = mainNode.getGrid();
        if (grid == null) {
            return;
        }

        var service = grid.getService(SoulService.class);
        if (service == null) {
            return;
        }

        var gridNode = mainNode.getNode();
        var dist = service.getDistributor(gridNode);

        if (dist == null) {
            return;
        }

        dist.setAccelerationMultiplier(multiplier);
    }

    @Override
    public void receiveClientSync(int multiplier) {
        this.clientMultiplier = multiplier;
    }

    @Override
    public int getClientMultiplier() {
        return this.clientMultiplier;
    }

}
