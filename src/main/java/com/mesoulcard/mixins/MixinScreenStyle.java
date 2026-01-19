package com.mesoulcard.mixins;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import com.mesoulcard.common.interfaces.IStyleAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin({ScreenStyle.class})
public class MixinScreenStyle implements IStyleAccessor {
    @Final
    @Shadow(remap = false)
    private Map<String, Blitter> images;
    @Final
    @Shadow(remap = false)
    private Map<String, WidgetStyle> widgets;

    @Override
    public Map<String, Blitter> meSoulCard$getImages() {
        return this.images;
    }

    @Override
    public Map<String, WidgetStyle> meSoulCard$getWidgets() {
        return this.widgets;
    }
}
