package com.mesoulcard.common.interfaces;

import com.mesoulcard.common.SoulDistributor;
import org.jetbrains.annotations.Nullable;

public interface ISoulDistributorAccessor {
  @Nullable
  SoulDistributor meSoulCard$getDistributor();
}
