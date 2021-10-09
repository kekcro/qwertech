package com.kbi.qwertech.tileentities;

import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import net.minecraft.block.Block;

import static com.kbi.qwertech.api.data.QTTextures.cracks;
import static com.kbi.qwertech.api.data.QTTextures.getCracksForDamage;

public class CraftingTableT2 extends CraftingTableT1 {

    @Override
    public String getTileEntityName() {
        return "qt.crafting.tier2";
    }

    @Override
    public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        return BlockTextureMulti.get(super.getTexture2(aBlock, aRenderPass, aSide, aShouldSideBeRendered), BlockTextureDefault.get(getCracksForDamage(getDamage(), getMaxDamage(), cracks), mRGBa));
    }

    @Override
    public long getMaxDamage() {
        return mMaterial.mToolDurability;
    }
}
