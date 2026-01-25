package com.mesoulcard.helper;

import com.buuz135.industrialforegoingsouls.config.ConfigSoulSurge;
import com.buuz135.industrialforegoingsouls.tag.SoulTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public class SoulAccelerationHelper {
    public static boolean accelerate(Level level, BlockPos pos, BlockState state, int ACCELERATION_MULTIPLIER) {
        if (!level.isLoaded(pos))
            return false;

        if (!state.is(Blocks.AIR) && !state.is(SoulTags.Blocks.CANT_ACCELERATE)
                && !state.is(SoulTags.Blocks.FORGE_CANT_ACCELERATE)) {
            BlockEntity targetingTile = level.getBlockEntity(pos);

            if (targetingTile != null) {
                BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) state.getTicker(level, targetingTile.getType());

                if (ticker != null) {
                    for (int i = 0; i < ((ACCELERATION_MULTIPLIER * ConfigSoulSurge.ACCELERATION_TICK) + 1); i++) {
                        ticker.tick(level, pos, state, targetingTile);
                    }
                    return true;
                }
            } else if (level instanceof ServerLevel serverLevel) {
                if (serverLevel.random.nextDouble() < ConfigSoulSurge.RANDOM_TICK_ACCELERATION_CHANCE) {
                    for (int i = 0; i < ACCELERATION_MULTIPLIER; i++) {
                        state.randomTick(serverLevel, pos, serverLevel.random);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
