package com.mesoulcard.common;

import appeng.api.networking.*;
import com.mesoulcard.MESoulCard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class SoulService implements IGridService, IGridServiceProvider {
    private final Map<IGridNode, ISoulDistributor> distributors = new IdentityHashMap<>();
    private final Set<ISoulDistributor> active = Collections.newSetFromMap(new IdentityHashMap<>());
    private long tickCount = 0;

    public SoulService() {}

    @Override
    public void onServerStartTick() {
        tickCount++;
    }

    @Override
    public void onLevelEndTick(Level level) {
        for (var dis : this.active) {
            if (dis.isActive()) {
                dis.accelerate();
            }
        }
    }

    @Override
    public void addNode(IGridNode gridNode, @Nullable CompoundTag savedData) {
        ISoulDistributor dist = gridNode.getService(ISoulDistributor.class);
        if (dist != null) {
            distributors.put(gridNode, dist);
            dist.setServiceHost(this);
        }
    }

    @Override
    public void removeNode(IGridNode gridNode) {
        var node = distributors.get(gridNode);
        if (node != null) {
            node.setServiceHost(null);
            active.remove(node);
            this.distributors.remove(gridNode);
        }
    }

    public void wake(ISoulDistributor node) {
        this.active.add(node);
    }

    public void sleep(ISoulDistributor node) {
        this.active.remove(node);
    }
}
