package com.kbi.qwertech.tileentities;

import com.kbi.qwertech.api.data.QTTextures;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.CS;
import gregapi.data.OP;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import net.minecraft.block.Block;

import static gregapi.data.CS.*;

public class CraftingTableT3 extends CraftingTableT2 {

    @Override
    public String getTileEntityName() {
        return "qt.crafting.tier3";
    }

    @Override
    public boolean isSurfaceOpaque2(byte aSide) {return aSide == CS.SIDE_UP || aSide == CS.SIDE_DOWN;}

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses2(Block aBlock, boolean[] aShouldSideBeRendered) {
        return 5;
    }

    @Override
    public boolean shouldSideBeRendered(byte side) {
        return true;
    }

    @Override
    public boolean allowCovers(byte side) {
        return false;
    }

    @Override
    public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (!aShouldSideBeRendered[aSide]) return null;
        int aIndex = aSide < 2 ? aSide : aSide == mFacing ? 2 : aSide == OPPOSITES[mFacing] ? 3 : 4;
        if (aSide == CS.SIDE_Y_POS && (mFacing == CS.SIDE_Y_POS || mFacing == CS.SIDE_Z_NEG || mFacing == CS.SIDE_Z_POS)) {
            aIndex = 5;
        }
        if (this.primary == null) {
            if (this.mMaterial.mNameInternal.startsWith("Wood")) {
                this.primary = QTTextures.anvilWood;
                this.overlay = QTTextures.anvilWoodOverlay;
            } else if (OP.ingot.canGenerateItem(this.mMaterial)) {
                this.primary = QTTextures.anvilMetal;
                this.overlay = QTTextures.anvilMetalOverlay;
            } else if (OP.gem.canGenerateItem(this.mMaterial)) {
                this.primary = QTTextures.anvilGem;
                this.overlay = QTTextures.anvilGemOverlay;
            } else {
                this.primary = QTTextures.anvilStone;
                this.overlay = QTTextures.anvilStoneOverlay;
            }
        }
        BlockTextureMulti base = BlockTextureMulti.get(BlockTextureDefault.get(primary[aIndex], mRGBa), BlockTextureDefault.get(overlay[aIndex]));
        return BlockTextureMulti.get(base, BlockTextureDefault.get(QTTextures.getCracksForDamage(getDamage(), getMaxDamage(), QTTextures.anvilCracks), mRGBa));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean setBlockBounds2(Block block, int aRenderPass, boolean[] aShouldSideBeRendered) {
        if (this.getFacing() == CS.SIDE_Y_POS || this.getFacing() == CS.SIDE_Z_NEG || this.getFacing() == CS.SIDE_Z_POS) {
            switch (aRenderPass) {
                case 0:
                    block.setBlockBounds(PX_P[0], PX_P[0], PX_P[2], PX_P[16], PX_P[3], PX_P[14]);
                    return T;
                case 1:
                    block.setBlockBounds(PX_P[1], PX_P[1], PX_P[3], PX_P[15], PX_P[4], PX_P[13]);
                    return T;
                case 2:
                    block.setBlockBounds(PX_P[2], PX_P[2], PX_P[4], PX_P[14], PX_P[5], PX_P[12]);
                    return T;
                case 3:
                    block.setBlockBounds(PX_P[4], PX_P[3], PX_P[6], PX_P[12], PX_P[10], PX_P[10]);
                    return T;
                case 4:
                    block.setBlockBounds(PX_P[0], PX_P[10], PX_P[1], PX_P[16], PX_P[16], PX_P[15]);
                    return T;
            }
        }
        if (this.getFacing() == CS.SIDE_Y_NEG || this.getFacing() == CS.SIDE_X_NEG || this.getFacing() == CS.SIDE_X_POS) {
            switch (aRenderPass) {
                case 0:
                    block.setBlockBounds(PX_P[2], PX_P[0], PX_P[0], PX_P[14], PX_P[3], PX_P[16]);
                    return T;
                case 1:
                    block.setBlockBounds(PX_P[3], PX_P[1], PX_P[1], PX_P[13], PX_P[4], PX_P[15]);
                    return T;
                case 2:
                    block.setBlockBounds(PX_P[4], PX_P[2], PX_P[2], PX_P[12], PX_P[5], PX_P[14]);
                    return T;
                case 3:
                    block.setBlockBounds(PX_P[6], PX_P[3], PX_P[4], PX_P[10], PX_P[10], PX_P[12]);
                    return T;
                case 4:
                    block.setBlockBounds(PX_P[1], PX_P[10], PX_P[0], PX_P[15], PX_P[16], PX_P[16]);
                    return T;
            }
        }
        return true;
    }

    @Override
    public boolean canBeDamaged() {
        return true;
    }
}
