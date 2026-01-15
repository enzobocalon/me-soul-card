package com.mesoulcard;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    // Disabled when B mods are loaded
    public static final Object2ObjectMap<String, List<String>> mixinMapDisableUpgradeablePPMixins = new Object2ObjectOpenHashMap<>(
            new String[] {
                    "com.mesoulcard.mixins.upgradeable.pprovider.UpgradeablePatternProviderLogicMixin",
                    "com.mesoulcard.mixins.upgradeable.pprovider.UpgradeablePatternProviderScreenMixin",
                    "com.mesoulcard.mixins.upgradeable.pprovider.UpgradeablePatternProviderMenuMixin",

                    "com.mesoulcard.mixins.upgradeable.advancedae.pprovider.UpgradeableAdvPatternProviderLogicMixin",
                    "com.mesoulcard.mixins.upgradeable.advancedae.pprovider.UpgradeableAdvPatternProviderScreenMixin",
                    "com.mesoulcard.mixins.upgradeable.advancedae.pprovider.UpgradeableAdvPatternProviderMenuMixin",
                    "com.mesoulcard.mixins.upgradeable.advancedae.pprovider.UpgradeableSmallAdvPatternProviderScreenMixin"
            },
            new List[] {
                    List.of("expandedae", "appflux"),
                    List.of("expandedae", "appflux"),
                    List.of("expandedae", "appflux"),
                    List.of("appflux"),
                    List.of("appflux"),
                    List.of("appflux"),
                    List.of("appflux")
            });

    // Enabled when B mods are loaded
    public static final Object2ObjectMap<String, List<String>> mixinMapEnableCompat = new Object2ObjectOpenHashMap<>(
            new String[] {
                    "com.mesoulcard.mixins.compat.pprovider.screens.AppFluxPatternProviderScreenMixin",
                    "com.mesoulcard.mixins.compat.pprovider.screens.ExpandedAEPatternProviderScreenMixin",

                    "com.mesoulcard.mixins.compat.io.extendedae.GuiExIOBusMixin",

                    "com.mesoulcard.mixins.compat.pprovider.advancedae.AdvPatternProviderLogicHostMixin",
                    "com.mesoulcard.mixins.compat.pprovider.advancedae.AdvPatternProviderScreenMixin",
                    "com.mesoulcard.mixins.compat.pprovider.advancedae.AdvPatternProviderMenuMixin",
                    "com.mesoulcard.mixins.compat.pprovider.advancedae.AdvPatternProviderLogicMixin",
                    "com.mesoulcard.mixins.compat.pprovider.advancedae.AdvPatternProviderPartMixin",

                    "com.mesoulcard.mixins.compat.pprovider.screens.AppFluxAdvPatternProviderScreenMixin",
                    "com.mesoulcard.mixins.compat.pprovider.screens.AppFluxSmallAdvPatternProviderScreenMixin"
            },
            new List[] {
                    List.of("appflux"),
                    List.of("expandedae"),
                    List.of("extendedae"),
                    List.of("advanced_ae"),
                    List.of("advanced_ae"),
                    List.of("advanced_ae"),
                    List.of("advanced_ae"),
                    List.of("advanced_ae"),
                    List.of("advanced_ae", "appflux"),
                    List.of("advanced_ae", "appflux")
            });

    private boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream()
                    .map(ModInfo::getModId)
                    .anyMatch(modId::equals);
        } else {
            return ModList.get().isLoaded(modId);
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinMapDisableUpgradeablePPMixins.containsKey(mixinClassName)) {
            return mixinMapDisableUpgradeablePPMixins.get(mixinClassName).stream().noneMatch(this::isModLoaded);
        }

        if (mixinMapEnableCompat.containsKey(mixinClassName)) {
            return mixinMapEnableCompat.get(mixinClassName).stream().allMatch(this::isModLoaded);
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
