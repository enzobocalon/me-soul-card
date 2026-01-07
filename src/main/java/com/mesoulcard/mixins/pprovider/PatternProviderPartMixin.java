package com.mesoulcard.mixins.pprovider;

import appeng.parts.crafting.PatternProviderPart;
import com.mesoulcard.helper.PatternProviderMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PatternProviderPart.class})
public class PatternProviderPartMixin {

    @Inject(
            method = "saveChanges",
            at = @At("HEAD")
    )
    private void onSaveChanges(CallbackInfo ci) {
        var self = (PatternProviderPart) (Object) this;
        PatternProviderMixinHelper.updateSoulDistributor(self.getMainNode());
    }
}
