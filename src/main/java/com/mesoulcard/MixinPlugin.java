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

    public static final Object2ObjectMap<String, List<String>> mixinMapDisableUpgradeablePPMixins = new Object2ObjectOpenHashMap<>(
            new String[]{
                    "com.mesoulcard.mixins.patternprovider.UpgradeablePatternProviderLogicMixin",
                    "com.mesoulcard.mixins.patternprovider.UpgradeablePatternProviderScreenMixin",
                    "com.mesoulcard.mixins.patternprovider.UpgradeablePatternProviderMenuMixin"
            },
            new List[]{
                    List.of("expandedae", "appflux", "pccard"),
                    List.of("expandedae", "appflux", "pccard"),
                    List.of("expandedae", "appflux", "pccard")
            }
    );

    public static final Object2ObjectMap<String, List<String>> mixinMap = new Object2ObjectOpenHashMap<>(
            new String[]{
                    "com.mesoulcard.mixins.PatternProviderLogicMixin",
            },
            new List[]{
                    List.of("expandedae", "appflux", "pccard"),
            }
    );

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

        if (mixinMap.containsKey(mixinClassName)) {
            return mixinMap.get(mixinClassName).stream().anyMatch(this::isModLoaded);
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
