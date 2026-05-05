package com.mesoulcard.common;

import com.mesoulcard.Config;
import com.mesoulcard.MESoulCard;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoulAccelerationManager {

  private record LockKey(ResourceKey<Level> dimension, BlockPos pos) {
  }

  // In-memory cache: target dimension + block pos -> distributorId
  private static final Map<LockKey, String> lockCache = new ConcurrentHashMap<>();

  public static boolean tryAcquire(Level level, BlockPos targetPos, String distributorId) {
    if (!Config.ENABLE_ACCELERATION_LOCK.get()) {
      return true;
    }

    if (level == null || level.isClientSide())
      return false;

    var key = new LockKey(level.dimension(), targetPos.immutable());

    // Fast path: check cache first
    String currentOwner = lockCache.get(key);

    if (currentOwner == null) {
      // No lock exists - acquire it
      lockCache.put(key, distributorId);

      if (MESoulCard.isDebugLogEnabled()) {
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
    if (MESoulCard.isDebugLogEnabled()) {
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

    var key = new LockKey(level.dimension(), targetPos);
    String currentOwner = lockCache.get(key);

    if (distributorId.equals(currentOwner)) {
      lockCache.remove(key);

      if (MESoulCard.isDebugLogEnabled()) {
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
  public static boolean isLocked(Level level, BlockPos targetPos) {
    if (level == null || targetPos == null) {
      return false;
    }
    return lockCache.containsKey(new LockKey(level.dimension(), targetPos));
  }

  /*
   * Clears all locks within a specific chunk.
   * Should be called when a chunk unloads.
   */
  public static void clearChunk(Level level, ChunkPos chunkPos) {
    if (level == null) {
      return;
    }

    int minX = chunkPos.getMinBlockX();
    int maxX = chunkPos.getMaxBlockX();
    int minZ = chunkPos.getMinBlockZ();
    int maxZ = chunkPos.getMaxBlockZ();

    Iterator<Map.Entry<LockKey, String>> iterator = lockCache.entrySet().iterator();
    int cleared = 0;

    while (iterator.hasNext()) {
      LockKey key = iterator.next().getKey();
      BlockPos pos = key.pos();
      if (pos.getX() >= minX && pos.getX() <= maxX &&
          pos.getZ() >= minZ && pos.getZ() <= maxZ &&
          key.dimension().equals(level.dimension())) {
        iterator.remove();
        cleared++;
      }
    }

    if (cleared > 0 && MESoulCard.isDebugLogEnabled()) {
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
    if (MESoulCard.isDebugLogEnabled()) {
      MESoulCard.LOGGER.info("[SoulAccelerationManager] Cleared all {} locks from cache", size);
    }
  }
}
