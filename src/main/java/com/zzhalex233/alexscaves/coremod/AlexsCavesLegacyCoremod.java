package com.zzhalex233.alexscaves.coremod;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("AlexsCavesLegacyCoremod")
@IFMLLoadingPlugin.TransformerExclusions("com.zzhalex233.alexscaves.coremod")
public final class AlexsCavesLegacyCoremod implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
            "com.zzhalex233.alexscaves.coremod.RenderItemOverlayStateTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
