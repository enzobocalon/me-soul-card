package com.mesoulcard;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_ACCELERATION_LOCK = BUILDER
            .comment("Enable acceleration lock to prevent multiple AE2 machines from accelerating the same block simultaneously.")
            .comment("When enabled, only one machine can apply acceleration to a block at a time.")
            .comment("Default: true")
            .define("enableAccelerationLock", true);

    static final ModConfigSpec SPEC = BUILDER.build();

}
