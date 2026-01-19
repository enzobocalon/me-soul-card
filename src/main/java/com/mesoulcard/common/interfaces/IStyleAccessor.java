package com.mesoulcard.common.interfaces;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.WidgetStyle;

import java.util.Map;

public interface IStyleAccessor {

    Map<String, Blitter> meSoulCard$getImages();

    Map<String, WidgetStyle> meSoulCard$getWidgets();

}
