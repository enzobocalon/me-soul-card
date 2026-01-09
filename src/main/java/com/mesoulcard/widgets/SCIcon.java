package com.mesoulcard.widgets;

import appeng.client.gui.style.Blitter;
import com.mesoulcard.MESoulCard;
import net.minecraft.resources.ResourceLocation;

public enum SCIcon {
    ONE(0, 0),
    TWO(16, 0);

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public static final ResourceLocation TEXTURE = MESoulCard.makeId("textures/guis/states.png");
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;

    SCIcon(int x, int y) {
        this(x, y, 16, 16);
    }

    SCIcon(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Blitter getBlitter() {
        return Blitter.texture(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT).src(x, y, width, height);
    }
}
