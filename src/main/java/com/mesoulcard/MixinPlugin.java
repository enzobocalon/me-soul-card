package com.mesoulcard;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private static final Map<String, List<String>> COMPAT_MIXINS = Map.ofEntries(
            extendedAe("compat.aeinterface.extendedae.GuiExInterfaceMixin"),
            extendedAe("compat.io.extendedae.GuiExIOBusMixin"),
            extendedAe("compat.pprovider.PartExPatternProviderMixin"),

            advancedAe("compat.io.advancedae.ImportExportBusScreenMixin"),
            advancedAe("compat.io.advancedae.StockExportBusScreenMixin"),
            advancedAe("compat.pprovider.advancedae.AdvPatternProviderLogicHostMixin"),
            advancedAe("compat.pprovider.advancedae.AdvPatternProviderLogicMixin"),
            advancedAe("compat.pprovider.advancedae.AdvPatternProviderMenuMixin"),
            advancedAe("compat.pprovider.advancedae.AdvPatternProviderPartMixin"),
            advancedAe("compat.pprovider.advancedae.AdvPatternProviderScreenMixin"),
            advancedAe("compat.pprovider.advancedae.SmallAdvPatternProviderScreenMixin"),

            compat("compat.pprovider.screens.AppFluxPatternProviderScreenMixin", "appflux"),
            compat("compat.pprovider.screens.AppFluxAdvPatternProviderScreenMixin", "advanced_ae", "appflux"),
            compat("compat.pprovider.screens.AppFluxSmallAdvPatternProviderScreenMixin", "advanced_ae", "appflux"),
            compat("compat.pprovider.screens.ExpandedAEPatternProviderScreenMixin", "expandedae")
    );

    private static Map.Entry<String, List<String>> extendedAe(String mixinClassName) {
        return compat(mixinClassName, "extendedae");
    }

    private static Map.Entry<String, List<String>> advancedAe(String mixinClassName) {
        return compat(mixinClassName, "advanced_ae");
    }

    private static Map.Entry<String, List<String>> compat(String mixinClassName, String... requiredMods) {
        return Map.entry("com.mesoulcard.mixins." + mixinClassName, List.of(requiredMods));
    }

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
        if (COMPAT_MIXINS.containsKey(mixinClassName)) {
            return COMPAT_MIXINS.get(mixinClassName).stream().allMatch(this::isModLoaded);
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
