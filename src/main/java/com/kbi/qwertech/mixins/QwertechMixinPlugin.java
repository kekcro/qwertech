package com.kbi.qwertech.mixins;

import com.kbi.qwertech.api.data.QTConfigs;
import com.kbi.qwertech.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import ru.timeconqueror.spongemixins.MinecraftURLClassPath;

import java.io.File;
import java.util.*;

public class QwertechMixinPlugin implements IMixinConfigPlugin {
    public static final Logger L = LogManager.getLogger("QwerTech");

    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        Config.init();
        List<String> mixins = new ArrayList<>();
        List<String> loadedMods = new ArrayList<>();
        if (QTConfigs.patchTechgunsAutoFeeder) load(mixins, loadedMods, "Techguns", "techguns.SlotFoodMixin", "techguns.TechgunsMixin", "techguns.TechgunsTickHandlerMixin");
        if (QTConfigs.patchTechgunsCrash) load(mixins, loadedMods, "Techguns", "techguns.TechgunsEventhandlerMixin");
        if (QTConfigs.patchImmibisAutoFeeder) load(mixins, loadedMods, "autofood", "autofood.AutoFeederTileMixin");
        //if (QTConfigs.customBumblesMixins) { load(mixins, loadedMods, "gregtech", "gregtech.MultiItemBumblesMixin"); }

        return mixins;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    private void load(List<String> list, List<String> loadedMods, String mod, String... mixins) {
        try {
            if (!loadedMods.contains(mod) && canLoad(mod)) {
                loadedMods.add(mod);
            }
            list.addAll(Arrays.asList(mixins));
        } catch (Exception e) {
            L.error(e.getStackTrace());
        }
    }

    private boolean canLoad(String modname) throws Exception {
        String[] names = new String[]{modname, modname.toLowerCase(Locale.ROOT), modname.toUpperCase(Locale.ROOT)};
        for (String s : names) {
            File jar = MinecraftURLClassPath.getJarInModPath(s);
            if (jar != null && jar.exists()) {
                L.info("Applying mixins to {}...", modname);
                MinecraftURLClassPath.addJar(jar);
                return true;
            }
        }
        return false;
    }
}
