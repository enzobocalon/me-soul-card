package com.mesoulcard.mixins;

import com.buuz135.industrialforegoingsouls.block.tile.SoulSurgeBlockEntity;
import com.mesoulcard.common.SoulAccelerationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ SoulSurgeBlockEntity.class })
public class SoulSurgeBlockEntityMixin {
    @Inject(method = "serverTick*", at = @At("HEAD"), cancellable = true)
    private static void onServerTick(Level level, BlockPos pos, BlockState state, SoulSurgeBlockEntity blockEntity,
            CallbackInfo ci) {
        if (state.hasProperty(com.hrznstudio.titanium.block.RotatableBlock.FACING_ALL)) {
            Direction facing = state
                    .getValue(com.hrznstudio.titanium.block.RotatableBlock.FACING_ALL);
            BlockPos targetPos = pos.relative(facing.getOpposite());

            if (SoulAccelerationManager.isLocked(level, targetPos)) {
                ci.cancel();
            }
        }
    }
}
