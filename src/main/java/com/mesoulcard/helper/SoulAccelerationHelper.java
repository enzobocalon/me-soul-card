package com.mesoulcard.helper;

import appeng.api.networking.IInWorldGridNodeHost;
import appeng.blockentity.AEBaseBlockEntity;
import com.buuz135.industrialforegoingsouls.config.ConfigSoulSurge;
import com.buuz135.industrialforegoingsouls.tag.SoulTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class SoulAccelerationHelper {
    private static final Set<String> AE_NETWORK_MOD_IDS = Set.of(
            "ae2",
            "advanced_ae",
            "appflux",
            "extendedae",
            "expandedae"
    );

    public static boolean accelerate(Level level, BlockPos pos, BlockState state, int ACCELERATION_MULTIPLIER) {
        if (!level.isLoaded(pos))
            return false;

        if (!state.is(Blocks.AIR) && !state.is(SoulTags.Blocks.CANT_ACCELERATE)
                && !state.is(SoulTags.Blocks.FORGE_CANT_ACCELERATE)) {
            BlockEntity targetingTile = level.getBlockEntity(pos);

            if (targetingTile != null) {
                if (isAENetworkTarget(targetingTile, state)) {
                    return false;
                }

                BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) state.getTicker(level, targetingTile.getType());

                if (ticker != null) {
                    for (int i = 0; i < (ACCELERATION_MULTIPLIER * ConfigSoulSurge.ACCELERATION_TICK); i++) {
                        ticker.tick(level, pos, state, targetingTile);
                    }
                    return true;
                }
            } else if (level instanceof ServerLevel serverLevel) {
                if (serverLevel.random.nextDouble() < ConfigSoulSurge.RANDOM_TICK_ACCELERATION_CHANCE) {
                    for (int i = 0; i < (ACCELERATION_MULTIPLIER * ConfigSoulSurge.ACCELERATION_TICK); i++) {
                        state.randomTick(serverLevel, pos, serverLevel.random);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isAENetworkTarget(BlockEntity blockEntity, BlockState state) {
        if (blockEntity instanceof AEBaseBlockEntity || blockEntity instanceof IInWorldGridNodeHost) {
            return true;
        }

        var blockEntityTypeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
        if (blockEntityTypeId != null && AE_NETWORK_MOD_IDS.contains(blockEntityTypeId.getNamespace())) {
            return true;
        }

        var blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return blockId != null && AE_NETWORK_MOD_IDS.contains(blockId.getNamespace());
    }
}
