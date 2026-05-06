package com.mesoulcard.mixins.compat.pprovider.screens;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Tooltip;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.menu.SlotSemantics;
import com.mesoulcard.common.interfaces.IPatternProviderSoulSlotMenu;
import com.mesoulcard.common.interfaces.IUpgradableMenu;
import com.mesoulcard.helper.PatternProviderSoulSlotHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(UpgradesPanel.class)
public abstract class UpgradesPanelMixin {
    @Shadow
    @Final
    private List<Slot> slots;

    @Final
    private static final int meSoulCard$slotOffset = 16;

    @Final
    private static final int meSoulCard$borderSize = 1;

    @Inject(method = "getTooltip", at = @At("HEAD"), cancellable = true)
    private void meSoulCard$getTooltip(int mouseX, int mouseY, CallbackInfoReturnable<Tooltip> cir) {
        Minecraft gameInstance = Minecraft.getInstance();
        if (!(gameInstance.screen instanceof AEBaseScreen<?> screen)) {
            return;
        }

        if (!(screen.getMenu() instanceof IPatternProviderSoulSlotMenu soulMenu)) {
            return;
        }

        if (!soulMenu.meSoulCard$hasSoulSlot()) {
            return;
        }

        Slot hoveredUpgradeSlot = null;
        for (var slot : this.slots) {
            if (!slot.isActive()) {
                continue;
            }

            if (mouseX >= slot.x - meSoulCard$borderSize
                    && mouseX < slot.x + meSoulCard$slotOffset + meSoulCard$borderSize
                    && mouseY >= slot.y - meSoulCard$borderSize
                    && mouseY < slot.y + meSoulCard$slotOffset + meSoulCard$borderSize) {
                hoveredUpgradeSlot = slot;
                break;
            }
        }

        if (hoveredUpgradeSlot == null) {
            return;
        }

        if (soulMenu.meSoulCard$isSoulSlot(hoveredUpgradeSlot)) {
            cir.setReturnValue(new Tooltip(PatternProviderSoulSlotHelper.getSoulSlotTooltip()));
            return;
        }

        if (screen.getMenu().getSlotSemantic(hoveredUpgradeSlot) != SlotSemantics.UPGRADE) {
            return;
        }

        if (screen.getMenu() instanceof IUpgradableMenu upgradesMenu) {
            cir.setReturnValue(new Tooltip(
                    PatternProviderSoulSlotHelper.getRegularUpgradeTooltip(upgradesMenu.meSoulCard$getUpgrades().getUpgradableItem())));
        }
    }
}
