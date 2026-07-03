package com.mesoulcard.mixins.compat.pprovider.advancedae;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.parts.AEBasePart;
import com.mesoulcard.common.interfaces.IAccelerationReceiver;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotHost;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotMenu;
import com.mesoulcard.common.interfaces.IUpgradableMenu;
import com.mesoulcard.core.ModConstants;
import com.mesoulcard.core.Registration;
import com.mesoulcard.helper.AccelerationMenuHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
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
    private Slot meSoulCard$soulUpgradeSlot; // slot UI layer

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
        if (!(host instanceof AEBasePart)) {
            return;
        }

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
        AccelerationMenuHelper.setMultiplier(this.logic, this.getActionHost(), multiplier);
    }

    @Override
    @Unique
    public void meSoulCard$syncClientState(ServerPlayer player) {
        AccelerationMenuHelper.syncClientState(player, this.logic, this.getActionHost());
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
    public boolean meSoulCard$hasSoulSlot() {
        return this.meSoulCard$soulUpgradeSlot != null && this.meSoulCard$getSoulUpgradeInventory() != null;
    }

    @Override
    public ToolboxMenu getToolbox() {
        return this.meSoulCard$toolbox;
    }

    @Override
    public IUpgradeInventory meSoulCard$getUpgrades() {
        return ((IUpgradeableObject) this.logic).getUpgrades();
    }

    @Override
    public boolean meSoulCard$hasUpgrade(ItemLike upgradeCard) {
        var soulInventory = this.meSoulCard$getSoulUpgradeInventory();
        return ((IUpgradeableObject) this.logic).getUpgrades().isInstalled(upgradeCard)
                || soulInventory != null && soulInventory.isInstalled(upgradeCard);
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
