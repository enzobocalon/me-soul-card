package com.mesoulcard.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record TargetInfo(Level level, BlockPos pos, BlockState state) {}
