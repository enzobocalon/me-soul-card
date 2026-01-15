package com.mesoulcard.mixins.compat.pprovider.advancedae;

import com.mesoulcard.helper.PatternProviderMixinHelper;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ AdvPatternProviderPart.class })
public class AdvPatternProviderPartMixin {
    @Inject(method = "saveChanges", at = @At("HEAD"))
    private void onSaveChanges(CallbackInfo ci) {
        var self = (AdvPatternProviderPart) (Object) this;
        PatternProviderMixinHelper.updateSoulDistributor(self.getMainNode());
    }
}
