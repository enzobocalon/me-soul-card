package com.mesoulcard.mixins.compat.pprovider.advancedae;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.parts.AEBasePart;
import com.mesoulcard.common.SoulService;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotHost;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotMenu;
import com.mesoulcard.common.interfaces.IUpgradableMenu;
import com.mesoulcard.common.interfaces.ISoulDistributorAccessor;
import com.mesoulcard.core.ModConstants;
import com.mesoulcard.core.Registration;
import com.mesoulcard.network.payloads.SyncAccelerationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({ AdvPatternProviderMenu.class })
public class AdvPatternProviderMenuMixin extends AEBaseMenu
        implements IAccelerationReceiver, IPatternProviderSoulSlotMenu, IUpgradableMenu {
    @Unique
    private int meSoulCard$clientMultiplier = 1;

    @Unique
    private boolean meSoulCard$clientLock = false;

    @Unique
    private Slot meSoulCard$soulUpgradeSlot;

    @Unique
    private ToolboxMenu meSoulCard$toolbox;

    @Final
    @Shadow
    protected AdvPatternProviderLogic logic;

    public AdvPatternProviderMenuMixin(MenuType<? extends AdvPatternProviderMenu> menuType, int id, Inventory playerInventory, AdvPatternProviderLogicHost host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogicHost;)V", at = @At("TAIL"))
    private void meSoulCard$addSoulSlot(MenuType<?> menuType, int id, Inventory playerInventory, AdvPatternProviderLogicHost host,
            CallbackInfo ci) {
        this.meSoulCard$toolbox = new ToolboxMenu(this);
        var soulUpgrades = this.meSoulCard$getSoulUpgradeInventory();
        var slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, soulUpgrades, 0) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Registration.SOUL_CARD.get()) && super.mayPlace(stack);
            }
        };
        slot.setStackLimit(ModConstants.SOUL_CARD_PATTERN_PROVIDER_UPGRADE_LIMIT);
        slot.setNotDraggable();
        this.meSoulCard$soulUpgradeSlot = this.addSlot(slot, SlotSemantics.UPGRADE);
    }

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
    private boolean meSoulCard$isLocked() {
        if (this.logic instanceof ISoulDistributorAccessor accessor) {
            var distributor = accessor.meSoulCard$getDistributor();
            if (distributor != null) {
                return distributor.isLocked();
            }
        }

        // Fallback to grid service
        var host = this.getActionHost();
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

    @Override
    public IUpgradeInventory meSoulCard$getSoulUpgradeInventory() {
        return ((IPatternProviderSoulSlotHost) this.logic).meSoulCard$getSoulUpgradeInventory();
    }

    @Override
    public ToolboxMenu getToolbox() {
        return this.meSoulCard$toolbox;
    }

    @Override
    public IUpgradeInventory meSoulCard$getUpgrades() {
        return this.meSoulCard$getSoulUpgradeInventory();
    }

    @Override
    public boolean meSoulCard$hasUpgrade(ItemLike upgradeCard) {
        return ((IUpgradeableObject) this.logic).getUpgrades().isInstalled(upgradeCard)
                || this.meSoulCard$getSoulUpgradeInventory().isInstalled(upgradeCard);
    }

    @Override
    public boolean meSoulCard$isSoulSlot(Slot slot) {
        return slot == this.meSoulCard$soulUpgradeSlot;
    }

    @Override
    public boolean isValidForSlot(Slot slot, ItemStack stack) {
        if (slot == this.meSoulCard$soulUpgradeSlot) {
            return stack.is(Registration.SOUL_CARD.get());
        }

        if (stack.is(Registration.SOUL_CARD.get()) && this.getSlotSemantic(slot) == SlotSemantics.UPGRADE) {
            return false;
        }

        return super.isValidForSlot(slot, stack);
    }

    @Override
    protected List<Slot> getQuickMoveDestinationSlots(ItemStack stackToMove, boolean fromPlayerSide) {
        var slots = super.getQuickMoveDestinationSlots(stackToMove, fromPlayerSide);

        if (!fromPlayerSide || this.meSoulCard$soulUpgradeSlot == null || !slots.remove(this.meSoulCard$soulUpgradeSlot)) {
            return slots;
        }

        if (stackToMove.is(Registration.SOUL_CARD.get())) {
            slots.add(0, this.meSoulCard$soulUpgradeSlot);
        } else {
            slots.add(this.meSoulCard$soulUpgradeSlot);
        }

        return slots;
    }

}
