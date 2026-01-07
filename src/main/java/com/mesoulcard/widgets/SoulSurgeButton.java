package com.mesoulcard.widgets;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.ITooltip;
import appeng.client.gui.widgets.IconButton;
import com.mesoulcard.network.payloads.AccelerationPacket;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SoulSurgeButton extends IconButton implements ITooltip {
    int currentMultiplier = 1;
    private List<Component> tooltip = Collections.emptyList();

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

    public void setTooltip(List<Component> tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return tooltip;
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return super.isTooltipAreaVisible() && !getTooltipMessage().isEmpty();
    }

    @Override
    protected Icon getIcon() {
        return null;
    }
}
