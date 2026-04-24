package com.mesoulcard.mixins.compat.pprovider;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.PatternProviderMenu;
import appeng.menu.slot.RestrictedInputSlot;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotHost;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotMenu;
import com.mesoulcard.common.interfaces.IUpgradableMenu;
import com.mesoulcard.core.ModConstants;
import com.mesoulcard.core.Registration;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/*
* Handles Soul Slot in PP Menu
*  */

@Mixin(value = PatternProviderMenu.class, remap = false)
public abstract class PatternProviderCompatMenuMixin extends AEBaseMenu
        implements IPatternProviderSoulSlotMenu, IUpgradableMenu {
    @Shadow
    @Final
    protected PatternProviderLogic logic;

    @Unique
    private Slot meSoulCard$soulUpgradeSlot;

    @Unique
    private ToolboxMenu meSoulCard$toolbox;

    protected PatternProviderCompatMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V", at = @At("TAIL"))
    private void meSoulCard$addSoulSlot(MenuType<?> menuType, int id, Inventory playerInventory, PatternProviderLogicHost host,
            CallbackInfo ci) {
        this.meSoulCard$toolbox = new ToolboxMenu(this);
        var soulInv = this.meSoulCard$getSoulUpgradeInventory();
        var slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, soulInv, 0) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Registration.SOUL_CARD.get()) && super.mayPlace(stack);
            }
        };
        slot.setStackLimit(ModConstants.SOUL_CARD_PATTERN_PROVIDER_UPGRADE_LIMIT);
        slot.setNotDraggable();
        this.meSoulCard$soulUpgradeSlot = this.addSlot(slot, SlotSemantics.UPGRADE);
    }

    @Override
    public ToolboxMenu getToolbox() {
        return this.meSoulCard$toolbox;
    }

    @Override
    public IUpgradeInventory meSoulCard$getSoulUpgradeInventory() {
        return ((IPatternProviderSoulSlotHost) this.logic).meSoulCard$getSoulUpgradeInventory();
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
