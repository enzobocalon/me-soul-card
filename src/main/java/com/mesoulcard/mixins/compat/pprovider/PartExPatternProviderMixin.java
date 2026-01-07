package com.mesoulcard.mixins.compat.pprovider;

import com.glodblock.github.extendedae.common.parts.PartExPatternProvider;
import com.mesoulcard.helper.PatternProviderMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PartExPatternProvider.class})
public class PartExPatternProviderMixin {
    @Inject(
            method = "saveChanges",
            at = @At("HEAD")
    )
    private void onSaveChanges(CallbackInfo ci) {
        var self = (PartExPatternProvider) (Object) this;
        PatternProviderMixinHelper.updateSoulDistributor(self.getMainNode());
    }
}
