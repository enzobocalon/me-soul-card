package com.mesoulcard.widgets;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import com.mesoulcard.network.payloads.AccelerationPacket;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class SoulSurgeButton extends IconButton {
    int currentMultiplier = 1;

    public SoulSurgeButton(OnPress onPress) {
        super(onPress);
    }

    @Override
    public void onPress() {
        currentMultiplier++;
        if (currentMultiplier > 6) {
            currentMultiplier = 1;
        }

        PacketDistributor.sendToServer(new AccelerationPacket(currentMultiplier));
        super.onPress();
    }

    public void setMultiplier(int multiplier) {
        this.currentMultiplier = multiplier;
    }

    public int getMultiplier() {
        return this.currentMultiplier;
    }

    @Override
    protected Icon getIcon() {
        return null;
    }
}
