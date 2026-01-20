package com.mesoulcard.common;

import com.mesoulcard.Config;
import com.mesoulcard.MESoulCard;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoulAccelerationManager {

  private static final String NBT_KEY = "MESoulCard_Lock";

  // In-memory cache: targetBlockPos -> distributorId
  private static final Map<BlockPos, String> lockCache = new ConcurrentHashMap<>();

  public static boolean tryAcquire(Level level, BlockPos targetPos, String distributorId) {
    if (!Config.ENABLE_ACCELERATION_LOCK.get()) {
      return true;
    }

    if (level == null || level.isClientSide())
      return false;

    // Fast path: check cache first
    String currentOwner = lockCache.get(targetPos);

    if (currentOwner == null) {
      // No lock exists - acquire it
      lockCache.put(targetPos, distributorId);
      writeToNBT(level, targetPos, distributorId);

      if (MESoulCard.ENABLE_DEBUG_LOGS) {
        MESoulCard.LOGGER.debug("[SoulAccelerationManager] {} acquired lock on {}",
            distributorId, targetPos.toShortString());
      }
      return true;
    }

    if (currentOwner.equals(distributorId)) {
      // Already own the lock - continue accelerating
      return true;
    }

    // Someone else owns the lock
    if (MESoulCard.ENABLE_DEBUG_LOGS) {
      MESoulCard.LOGGER.debug("[SoulAccelerationManager] {} blocked on {} - owned by {}",
          distributorId, targetPos.toShortString(), currentOwner);
    }
    return false;
  }

  /*
   * Releases an acceleration lock on a target block.
   * Only releases if the caller is the current owner.
   */
  public static void release(Level level, BlockPos targetPos, String distributorId) {
    if (level == null || level.isClientSide())
      return;
    if (targetPos == null)
      return;

    String currentOwner = lockCache.get(targetPos);

    if (distributorId.equals(currentOwner)) {
      lockCache.remove(targetPos);
      removeFromNBT(level, targetPos);

      if (MESoulCard.ENABLE_DEBUG_LOGS) {
        MESoulCard.LOGGER.debug("[SoulAccelerationManager] {} released lock on {}",
            distributorId, targetPos.toShortString());
      }
    }
  }

  /*
   * Checks if a block position is currently locked by any accelerator.
   * This method is designed for use by Soul Surge Mixin to check if it should
   * skip acceleration.
   */
  public static boolean isLocked(BlockPos targetPos) {
    return lockCache.containsKey(targetPos);
  }

  /*
   * Gets the distributor ID that currently owns the lock on a position.
   */
  @Nullable
  public static String getLockOwner(BlockPos targetPos) {
    return lockCache.get(targetPos);
  }

  /*
   * Loads lock data from a BlockEntity's NBT into the cache.
   * Should be called when a chunk loads or a BlockEntity is created.
   */
  public static void loadFromNBT(BlockEntity blockEntity) {
    if (blockEntity == null)
      return;

    CompoundTag nbt = blockEntity.getPersistentData();
    if (nbt.contains(NBT_KEY)) {
      String owner = nbt.getString(NBT_KEY);
      if (!owner.isEmpty()) {
        BlockPos pos = blockEntity.getBlockPos();
        lockCache.put(pos, owner);

        if (MESoulCard.ENABLE_DEBUG_LOGS) {
          MESoulCard.LOGGER.debug("[SoulAccelerationManager] Loaded lock from NBT: {} -> {}",
              pos.toShortString(), owner);
        }
      }
    }
  }

  /*
   * Clears all locks within a specific chunk.
   * Should be called when a chunk unloads.
   */
  public static void clearChunk(ChunkPos chunkPos) {
    int minX = chunkPos.getMinBlockX();
    int maxX = chunkPos.getMaxBlockX();
    int minZ = chunkPos.getMinBlockZ();
    int maxZ = chunkPos.getMaxBlockZ();

    Iterator<Map.Entry<BlockPos, String>> iterator = lockCache.entrySet().iterator();
    int cleared = 0;

    while (iterator.hasNext()) {
      BlockPos pos = iterator.next().getKey();
      if (pos.getX() >= minX && pos.getX() <= maxX &&
          pos.getZ() >= minZ && pos.getZ() <= maxZ) {
        iterator.remove();
        cleared++;
      }
    }

    if (cleared > 0 && MESoulCard.ENABLE_DEBUG_LOGS) {
      MESoulCard.LOGGER.debug("[SoulAccelerationManager] Cleared {} locks in chunk {}",
          cleared, chunkPos);
    }
  }

  /*
   * Clears all locks from the cache.
   * Should be called when the server stops.
   */
  public static void clearAll() {
    int size = lockCache.size();
    lockCache.clear();

    MESoulCard.LOGGER.info("[SoulAccelerationManager] Cleared all {} locks from cache", size);
  }

  // ========== NBT helpers ==========

  private static void writeToNBT(Level level, BlockPos targetPos, String distributorId) {
    BlockEntity be = level.getBlockEntity(targetPos);
    if (be != null) {
      CompoundTag nbt = be.getPersistentData();
      nbt.putString(NBT_KEY, distributorId);
      be.setChanged();
    }
  }

  private static void removeFromNBT(Level level, BlockPos targetPos) {
    BlockEntity be = level.getBlockEntity(targetPos);
    if (be != null) {
      CompoundTag nbt = be.getPersistentData();
      if (nbt.contains(NBT_KEY)) {
        nbt.remove(NBT_KEY);
        be.setChanged();
      }
    }
  }
}
