package com.kbi.qwertech.items;

import com.kbi.qwertech.loaders.RegisterBumbles.BumbleData;
import com.kbi.qwertech.loaders.RegisterBumbles.ParentData;
import cpw.mods.fml.common.registry.GameRegistry;
import gregapi.data.IL;
import gregapi.data.MD;
import gregapi.util.ST;
import gregapi.util.UT;
import gregapi.util.WD;
import gregtech.items.MultiItemBumbles;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.kbi.qwertech.loaders.RegisterBumbles.CUSTOM_BUMBLE_DATA;
import static gregapi.data.CS.*;

public class CustomItemBumbles extends MultiItemBumbles {

    public CustomItemBumbles(String aModID, String aUnlocalized) {
        super(aModID, aUnlocalized);
    }

    @Override
    public void addItems() {
        for (Map.Entry<Short, BumbleData> e : CUSTOM_BUMBLE_DATA.entrySet()) {
            make(e.getKey(), e.getValue().bumbleName, e.getValue().bumbleTooltip);
        }
    }

    @Override
    public ItemStack bumbleProductStack(ItemStack aBumbleBee, short aMetaData, long aStacksize, int aProductIndex) {
        if (matches(aMetaData)) {
            BumbleData data = getData(aMetaData);
            return toComb(data.combMeta);
        }
        return IL.Comb_Honey.get(aStacksize);
    }

    @Override
    public ChunkCoordinates bumbleCanProduce(World aWorld, int aX, int aY, int aZ, ItemStack aBumbleBee, short aMetaData, int aDistance) {
        boolean temp = T;
        for (byte tSide : ALL_SIDES_VALID)
            if (WD.oxygen(aWorld, aX + OFFX[tSide], aY + OFFY[tSide], aZ + OFFZ[tSide])) {
                temp = F;
                break;
            }
        if (temp) return null;

        aDistance = Math.abs(aDistance);
        int[] tOrderX = RNGSUS.nextBoolean() ? aDistance < SCANS_POS.length ? SCANS_POS[aDistance] : SCANS_POS[SCANS_POS.length - 1] : aDistance < SCANS_NEG.length ? SCANS_NEG[aDistance] : SCANS_NEG[SCANS_NEG.length - 1];
        int[] tOrderY = RNGSUS.nextBoolean() ? aDistance < SCANS_POS.length ? SCANS_POS[aDistance] : SCANS_POS[SCANS_POS.length - 1] : aDistance < SCANS_NEG.length ? SCANS_NEG[aDistance] : SCANS_NEG[SCANS_NEG.length - 1];
        int[] tOrderZ = RNGSUS.nextBoolean() ? aDistance < SCANS_POS.length ? SCANS_POS[aDistance] : SCANS_POS[SCANS_POS.length - 1] : aDistance < SCANS_NEG.length ? SCANS_NEG[aDistance] : SCANS_NEG[SCANS_NEG.length - 1];
        if (matches(aMetaData)) {
            BumbleData data = getData(aMetaData);
            if (data == null || data.flowers == null || data.flowers.length == 0) {
                if (RNGSUS.nextBoolean()) {
                    for (int j : tOrderY)
                        for (int i : tOrderX)
                            for (int k : tOrderZ)
                                if (checkFlowers(aWorld, aX + i, aY + j, aZ + k))
                                    return new ChunkCoordinates(aX + i, aY + j, aZ + k);
                } else {
                    for (int j : tOrderY)
                        for (int k : tOrderZ)
                            for (int i : tOrderX)
                                if (checkFlowers(aWorld, aX + i, aY + j, aZ + k))
                                    return new ChunkCoordinates(aX + i, aY + j, aZ + k);
                }
            } else {
                List<Block> flowers = composeFlowers(data.flowers);
                for (int j : tOrderY)
                    for (int i : tOrderX)
                        for (int k : tOrderZ) {
                            Block tBlock = WD.block(aWorld, aX + i, aY + j, aZ + k, F);
                            if (flowers.contains(tBlock)) return new ChunkCoordinates(aX + i, aY + j, aZ + k);
                        }
            }
        }
        return null;
    }

    @Override
    public String getFlowerTooltip(short aMetaData) {
        if (matches(aMetaData)) {
            return getData(aMetaData).flowerTooltip;
        }
        return "Flowers (even potted ones work)";
    }

    @Override
    public ItemStack bumbleCombine(ItemStack aBumbleBeeA, short aMetaDataA, ItemStack aBumbleBeeB, short aMetaDataB, byte aBumbleType, Random aRandom) {
        for (Map.Entry<Short, BumbleData> e : CUSTOM_BUMBLE_DATA.entrySet()) {
            ParentData[] parents = e.getValue().parents;
            if (parents == null) break;
            else if (matches(aBumbleBeeA, aMetaDataA, aBumbleBeeB, aMetaDataB, parents)) {
                return ST.make(this, 1, e.getKey() + aBumbleType);
            } else break;
        }
        return ST.make(this, 1, trunc(aMetaDataA) + aBumbleType);
    }

    @Override
    public int bumbleProductCount(ItemStack aBumbleBee, short aMetaData) {
        BumbleData data = getData(aMetaData);
        return Math.max(data.minCombAmount, RNGSUS.nextInt(data.maxCombAmount) + 1);
    }

    private List<Block> composeFlowers(String[] flowers) {
        List<Block> blocks = new ArrayList<>();
        for (String flower : flowers) {
            String[] split = flower.split(":");
            if (split[0].equals("ore")) {
                for (ItemStack item : OreDictionary.getOres(split[1])) {
                    blocks.add(ST.block(item));
                }
            } else {
                blocks.add(GameRegistry.findBlock(split[0], split[1]));
            }
        }
        return blocks;
    }

    private short trunc(short aMetaData) {
        if (aMetaData < 10) return 0;
        else return (short) ((aMetaData / 10) * 10);
    }

    private boolean matches(short aMetaData) {
        return CUSTOM_BUMBLE_DATA.containsKey(trunc(aMetaData));
    }

    private BumbleData getData(short aMetaData) {
        return CUSTOM_BUMBLE_DATA.get(trunc(aMetaData));
    }

    private boolean matches(ItemStack aBumbleBeeA, short aMetaDataA, ItemStack aBumbleBeeB, short aMetaDataB, ParentData[] parents) {
        short metaA = parents[0].species, metaB = parents[1].species;
        if (metaA == metaB) return false;
        aMetaDataA = trunc(aMetaDataA);
        aMetaDataB = trunc(aMetaDataB);
        return/**/ (aBumbleBeeA.getItem() == parents[0].item && aMetaDataA == metaA && aBumbleBeeB.getItem() == parents[1].item && aMetaDataB == metaB)
                || (aBumbleBeeB.getItem() == parents[0].item && aMetaDataB == metaA && aBumbleBeeA.getItem() == parents[1].item && aMetaDataA == metaB);
    }

    public static ItemStack toComb(short meta) {
        return ST.make(ST.item(MD.QT, "qwertech.comb"), 1, meta);
    }

    public IIcon BASE, OVERLAY;

    @Override
    public void registerIcons(IIconRegister aIconRegister) {
        BASE = aIconRegister.registerIcon(mModID + ":" + getUnlocalizedName() + "/bumblebee");
        OVERLAY = aIconRegister.registerIcon(mModID + ":" + getUnlocalizedName() + "/bumblebee_overlay");
        PRINCESS = aIconRegister.registerIcon(MD.GT.mID + ":gt.multiitem.bumblebee/overlay_princess");
        QUEEN = aIconRegister.registerIcon(MD.GT.mID + ":gt.multiitem.bumblebee/overlay_queen");
        SCANNED = aIconRegister.registerIcon(MD.GT.mID + ":gt.multiitem.bumblebee/overlay_scanned");
        DEAD = aIconRegister.registerIcon(MD.GT.mID + ":gt.multiitem.bumblebee/overlay_dead");
    }

    @Override
    public IIcon getIconIndex(ItemStack aStack) {
        return BASE;
    }

    @Override
    public IIcon getIconFromDamage(int aMetaData) {
        return BASE;
    }

    @Override
    public IIcon getIcon(ItemStack aStack, int aRenderPass, EntityPlayer aPlayer, ItemStack aUsedStack, int aUseRemaining) {return getIcon(aStack, aRenderPass);}

    @Override
    public IIcon getIcon(ItemStack aStack, int aRenderPass) {return getIconFromDamageForRenderPass(ST.meta_(aStack), aRenderPass);}

    @Override
    public boolean requiresMultipleRenderPasses() {return T;}

    @Override
    public int getRenderPasses(int aMetaData) {
        switch (aMetaData % 10) {
            case 6:
            case 7:
            case 8:
            case 9:
                return 4;
            case 1:
            case 2:
            case 4:
            case 5:
                return 3;
            default:
                return 2;
        }
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int aMetaData, int aRenderPass) {
        switch (aRenderPass) {
            case 0:
                return BASE;
            case 1:
                return OVERLAY;
            case 2:
                switch (aMetaData % 10) {
                    case 1:
                    case 6:
                        return PRINCESS;
                    case 2:
                    case 7:
                        return QUEEN;
                    case 3:
                    case 8:
                    case 4:
                    case 9:
                        return DEAD;
                }
        }
        return SCANNED;
    }

    @Override
    public int getColorFromItemStack(ItemStack bumble, int renderPass) {
        if (renderPass == 0) {
            short aMeta = trunc(ST.meta(bumble));
            if (CUSTOM_BUMBLE_DATA.containsKey(aMeta)) {
                return UT.Code.getRGBInt(CUSTOM_BUMBLE_DATA.get(aMeta).rgb);
            }
        }
        return 16777215;
    }
}
