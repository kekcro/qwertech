package com.kbi.qwertech.tileentities;

import com.kbi.qwertech.QwerTech;
import com.kbi.qwertech.api.data.QTI;
import com.kbi.qwertech.api.data.QTTextures;
import com.kbi.qwertech.api.recipe.RepairRecipe;
import com.kbi.qwertech.api.recipe.managers.CraftingManagerHammer;
import com.kbi.qwertech.api.tileentities.IDamageableCraftingTable;
import com.kbi.qwertech.client.gui.GuiQTCraftingTableClient;
import com.kbi.qwertech.gui.GuiQTCraftingTable;
import com.kbi.qwertech.network.packets.PacketInventorySync;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.IBlockToolable;
import gregapi.block.multitileentity.IMultiTileEntity;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetLightOpacity;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetSubItems;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_OnBlockClicked;
import gregapi.block.multitileentity.MultiTileEntityBlockInternal;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.code.ArrayListNoNulls;
import gregapi.data.CS;
import gregapi.data.LH.Chat;
import gregapi.data.OP;
import gregapi.item.multiitem.MultiItemTool;
import gregapi.network.INetworkHandler;
import gregapi.network.packets.data.PacketSyncDataLong;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.ITileEntityAnvil;
import gregapi.util.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.item.multiitem.behaviors.Behavior_Tool._7_GRAND_DAD_;

public class CraftingTableT1 extends TileEntityBase09FacingSingle implements ITileEntityAnvil, IMTE_GetSubItems, IMTE_OnBlockClicked, IMTE_GetLightOpacity, IMultiTileEntity.IMTE_SyncDataLong, IDamageableCraftingTable {

    public String mGUITexture = "qwertech:textures/gui/craftingTable.png";
    public boolean mUpdatedGrid = T;
    public long mDamage = 0;
    public int pitch = -1;

    @Override
    public void onTick2(long aTimer, boolean aIsServerSide) {
        super.onTick2(aTimer, aIsServerSide);
        if (aIsServerSide) {
            if (mUpdatedGrid) {
                getCraftingOutput();
                sendDisplays();
                //updateInventory();
                //updateClientData();
                mUpdatedGrid = F;
            } else if (aTimer % (200 + (xCoord % 10) + (zCoord % 10)) == 0) {
                sendDisplays();
            }
        }
    }

    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey("qt.damage")) setDamage(aNBT.getLong("qt.damage"));
    }

    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        UT.NBT.setNumber(aNBT, "qt.damage", getDamage());
    }

    @Override
    public NBTTagCompound writeItemNBT2(NBTTagCompound aNBT) {
        super.writeItemNBT2(aNBT);
        UT.NBT.setNumber(aNBT, "qt.damage", getDamage());
        return aNBT;
    }

    @Override
    public boolean receiveDataLong(long aData, INetworkHandler aNetworkHandler) {
        setDamage(aData);
        return true;
    }

    @Override
    public ArrayListNoNulls<ItemStack> getDrops(int aFortune, boolean aSilkTouch) {
        ArrayListNoNulls<ItemStack> rList = new ArrayListNoNulls<>();
        if (getDamage() == getMaxDamage()) {
            int amt = 18;
            if (aFortune > 0) {
                amt = this.getRandomNumber(3) * this.getRandomNumber(aFortune);
            }
            rList.add(OP.scrapGt.mat(OreDictMaterial.get(mMaterial.mNameInternal.replace("Wood", "")), amt));
        } else {
            rList = super.getDrops(aFortune, aSilkTouch);
            ItemStack hrm = rList.get(0);
            if (hrm != null) {
                NBTTagCompound setter = UT.NBT.make();
                this.writeItemNBT(setter);
                UT.NBT.set(hrm, setter);
                rList.set(0, hrm);
            }
        }
        return rList;
    }

    @Override
    public int getLightOpacity() {
        return CS.LIGHT_OPACITY_WATER;
    }

    @Override
    public boolean allowInteraction(Entity aEntity) {
        return true;
    }

    @Override
    public boolean onPlaced(ItemStack aStack, EntityPlayer aPlayer, MultiTileEntityContainer aMTEContainer, World aWorld, int aX, int aY, int aZ, byte aSide, float aHitX, float aHitY, float aHitZ) {
        Block aBlock = aWorld.getBlock(aX, aY + 1, aZ);
        if (aBlock.canBeReplacedByLeaves(aWorld, aX, aY + 1, aZ)) {
            WD.set(aWorld, aX, aY + 1, aZ, QwerTech.machines.getItem(400));
        }
        return super.onPlaced(aStack, aPlayer, aMTEContainer, aWorld, aX, aY, aZ, aSide, aHitX, aHitY, aHitZ);
    }

    public void sendDisplays() {
        for (int q = 0; q < this.invsize(); q++) {
            QTI.NW_API.sendToAllPlayersInRange(new PacketInventorySync(slot(q), this.xCoord, this.yCoord, this.zCoord, q), this.worldObj, this.xCoord, this.zCoord);
        }
        getNetworkHandler().sendToAllPlayersInRange(new PacketSyncDataLong(getCoords(), getDamage()), worldObj, getCoords());
    }

    @Override
    public void setInventorySlotContents(int aSlot, ItemStack aStack) {
        if (aSlot >= 0 && aSlot < 9 && !ST.equal(aStack, slot(aSlot), F)) mUpdatedGrid = T;
        super.setInventorySlotContents(aSlot, aStack);
    }

    @Override
    public void setInventorySlotContentsGUI(int aSlot, ItemStack aStack) {
        if (aSlot >= 0 && aSlot < 9 && !ST.equal(aStack, slot(aSlot), F)) mUpdatedGrid = T;
        super.setInventorySlotContentsGUI(aSlot, aStack);
    }

    @Override
    public ItemStack decrStackSize(int aSlot, int aDecrement) {
        if (aSlot >= 0 && aSlot < 9 && aDecrement > 0) mUpdatedGrid = T;
        return super.decrStackSize(aSlot, aDecrement);
    }

    @Override
    public ItemStack decrStackSizeGUI(int aSlot, int aDecrement) {
        if (aSlot >= 0 && aSlot < 9 && aDecrement > 0) mUpdatedGrid = T;
        return super.decrStackSizeGUI(aSlot, aDecrement);
    }

    @Override
    public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
        if (aTool.equals(CS.TOOL_hammer)) {
            if (isClientSide()) {
                if (canDoHammerOutput()) celebrate();
                return super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
            }
            long returnit = UT.Code.units(this.handleHammer(aPlayer, aStack), 10000, 400, true);
            this.causeBlockUpdate();
            return returnit;
        }
        if (aSide != CS.SIDE_UP) {
            return super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
        }
        return 0;
    }

    public void celebrate() {
        this.worldObj.spawnParticle("fireworksSpark", this.getX() + 0.6, this.getY() + 1, this.getZ() + 0.6, CS.RANDOM.nextDouble() * 0.1D, 0.1D, CS.RANDOM.nextDouble() * 0.1D);
        this.worldObj.spawnParticle("fireworksSpark", this.getX() + 0.6, this.getY() + 1, this.getZ() + 0.4, CS.RANDOM.nextDouble() * -0.1D, 0.1D, CS.RANDOM.nextDouble() * -0.1D);
    }

    public long processHammer(MultiItemTool tool, EntityPlayer aPlayer, IInventory aPlayerInventory, ItemStack aStack) {
        long tDamage = IBlockToolable.Util.onToolClick(worldObj.getBlock(xCoord, yCoord, zCoord), TOOL_hammer, Long.MAX_VALUE, tool.getHarvestLevel(aStack, TOOL_hammer), aPlayer, null, aPlayerInventory, aPlayer.isSneaking(), aStack, worldObj, SIDE_UP, xCoord, yCoord, zCoord, 0f, 0f, 0f);
        if (tDamage > 0) {
            if (!worldObj.isRemote) UT.Sounds.send(worldObj, "random.anvil_land", 1.0F, _7_GRAND_DAD_[pitch = ((pitch + 1) % _7_GRAND_DAD_.length)], xCoord, yCoord, zCoord);
        }
        return tDamage;
    }

    public List<AxisAlignedBB> currentBoxes;

    @SideOnly(Side.CLIENT)
    public void drawBoundingBox(EntityPlayer entity, AxisAlignedBB[] boxes, float partialTick, RenderGlobal context, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        float f1 = 0.002F;

        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTick;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTick;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTick;
        for (int q = 0; q < boxes.length; q++) {
            AxisAlignedBB box2 = boxes[q].expand(f1, f1, f1).getOffsetBoundingBox(-d0, -d1, -d2);
            //System.out.println("Drawing " + box2.minX + "x to " + box2.maxX + "x, " + box2.minY + "y to " + box2.maxY + ", " + box2.minZ + "z to " + box2.maxZ);
            RenderGlobal.drawOutlinedBoundingBox(box2, color);
        }


        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public boolean addDefaultCollisionBoxToList() {
        return true;
    }

    public void generateBoundingBoxes() {
        List<AxisAlignedBB> aList = new ArrayList();
        AxisAlignedBB newBox = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
        boolean sideways = false;
        double[] dists = new double[]{0.2, 0.4, 0.4, 0.6, 0.6, 0.8};
        if (this.mFacing == CS.SIDE_Y_POS || this.mFacing == CS.SIDE_X_POS || this.mFacing == CS.SIDE_X_NEG) {
            sideways = true;
        }

        if (this.mFacing == CS.SIDE_Y_NEG || this.mFacing == CS.SIDE_X_NEG || this.mFacing == CS.SIDE_Z_POS) {
            dists[0] = 0.6;
            dists[1] = 0.8;
            //dists[2] = 0.4;
            //dists[3] = 0.6;
            dists[4] = 0.2;
            dists[5] = 0.4;
        }
        newBox.setBounds(sideways ? xCoord + 0.4 : xCoord + dists[0], yCoord + 1.05, sideways ? zCoord + dists[0] : zCoord + 0.4, sideways ? xCoord + 0.6 : xCoord + dists[1], yCoord + 1.25, sideways ? zCoord + dists[1] : zCoord + 0.6);
        aList.add(newBox.copy());
        newBox.setBounds(sideways ? xCoord + 0.4 : xCoord + dists[2], yCoord + 1.05, sideways ? zCoord + dists[2] : zCoord + 0.4, sideways ? xCoord + 0.6 : xCoord + dists[3], yCoord + 1.25, sideways ? zCoord + dists[3] : zCoord + 0.6);
        aList.add(newBox.copy());
        newBox.setBounds(sideways ? xCoord + 0.4 : xCoord + dists[4], yCoord + 1.05, sideways ? zCoord + dists[4] : zCoord + 0.4, sideways ? xCoord + 0.6 : xCoord + dists[5], yCoord + 1.25, sideways ? zCoord + dists[5] : zCoord + 0.6);
        aList.add(newBox.copy());

        newBox.setBounds(sideways ? xCoord + 0.4 : xCoord + dists[0], yCoord + 1.25, sideways ? zCoord + dists[0] : zCoord + 0.4, sideways ? xCoord + 0.6 : xCoord + dists[1], yCoord + 1.45, sideways ? zCoord + dists[1] : zCoord + 0.6);
        aList.add(newBox.copy());
        newBox.setBounds(sideways ? xCoord + 0.4 : xCoord + dists[2], yCoord + 1.25, sideways ? zCoord + dists[2] : zCoord + 0.4, sideways ? xCoord + 0.6 : xCoord + dists[3], yCoord + 1.45, sideways ? zCoord + dists[3] : zCoord + 0.6);
        aList.add(newBox.copy());
        newBox.setBounds(sideways ? xCoord + 0.4 : xCoord + dists[4], yCoord + 1.25, sideways ? zCoord + dists[4] : zCoord + 0.4, sideways ? xCoord + 0.6 : xCoord + dists[5], yCoord + 1.45, sideways ? zCoord + dists[5] : zCoord + 0.6);
        aList.add(newBox.copy());

        newBox.setBounds(sideways ? xCoord + 0.4 : xCoord + dists[0], yCoord + 1.45, sideways ? zCoord + dists[0] : zCoord + 0.4, sideways ? xCoord + 0.6 : xCoord + dists[1], yCoord + 1.65, sideways ? zCoord + dists[1] : zCoord + 0.6);
        aList.add(newBox.copy());
        newBox.setBounds(sideways ? xCoord + 0.4 : xCoord + dists[2], yCoord + 1.45, sideways ? zCoord + dists[2] : zCoord + 0.4, sideways ? xCoord + 0.6 : xCoord + dists[3], yCoord + 1.65, sideways ? zCoord + dists[3] : zCoord + 0.6);
        aList.add(newBox.copy());
        newBox.setBounds(sideways ? xCoord + 0.4 : xCoord + dists[4], yCoord + 1.45, sideways ? zCoord + dists[4] : zCoord + 0.4, sideways ? xCoord + 0.6 : xCoord + dists[5], yCoord + 1.65, sideways ? zCoord + dists[5] : zCoord + 0.6);
        aList.add(newBox.copy());
        currentBoxes = new ArrayList(9);
        for (int q = 0; q < aList.size(); q++) {
            currentBoxes.add(aList.get(aList.size() - 1 - q));
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return getCollisionBoundingBoxFromPool();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool() {
        return this.box(new float[]{0, 0, 0, 1, 1, 1});
        //return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord);
    }

    @Override
    public boolean checkObstruction(EntityPlayer player, byte aSide, float aHitX, float aHitY, float aHitZ) {
        return false;
    }

    @Override
    public boolean getOpacity(int aX, int aY, int aZ) {
        return false;
    }

    public int getIntersectedBox(Vec3 startPos, Vec3 endPos, boolean sneaky) {
        if (currentBoxes == null || currentBoxes.size() <= 0) {
            generateBoundingBoxes();
        }

        Vec3 dif = Vec3.createVectorHelper(endPos.xCoord - startPos.xCoord, endPos.yCoord - startPos.yCoord, endPos.zCoord - startPos.zCoord);
        while (Math.abs(dif.xCoord) + Math.abs(dif.yCoord) + Math.abs(dif.zCoord) < 5) {
            dif = Vec3.createVectorHelper(dif.xCoord * 2, dif.yCoord * 2, dif.zCoord * 2);
        }
        endPos = endPos.addVector(dif.xCoord, dif.yCoord, dif.zCoord);

        if (currentBoxes != null && currentBoxes.size() > 0) {
            AxisAlignedBB primaryBox = null;
            Vec3 theCenter = null;
            boolean isSlot = false;
            int returnit = -1;
            for (int q = 0; q < currentBoxes.size(); q++) {
                AxisAlignedBB checkBox = currentBoxes.get(q);
                if (checkBox.calculateIntercept(startPos, endPos) != null) {
                    if (primaryBox != null) {
                        Vec3 center = Vec3.createVectorHelper(checkBox.maxX - 0.1, checkBox.maxY - 0.1, checkBox.maxZ - 0.1);
                        if (startPos.distanceTo(center) < startPos.distanceTo(theCenter)) {
                            if (slotHas(q)) {
                                primaryBox = checkBox;
                                theCenter = center;
                                isSlot = true;
                                returnit = q;
                            } else if (!sneaky && isSlot) {
                                primaryBox = checkBox;
                                theCenter = center;
                                isSlot = false;
                                returnit = q;
                            }
                        } else if (!isSlot && sneaky) {
                            primaryBox = checkBox;
                            theCenter = center;
                            isSlot = true;
                            returnit = q;
                        }
                    } else {
                        primaryBox = currentBoxes.get(q);
                        theCenter = Vec3.createVectorHelper(primaryBox.maxX - 0.1, primaryBox.maxY - 0.1, primaryBox.maxZ - 0.1);
                        isSlot = slotHas(q);
                        returnit = q;
                    }
                }
            }
            return returnit;
        }
        return -1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onDrawBlockHighlight2(DrawBlockHighlightEvent event) {
        if (event.player.getHeldItem() != null && OreDictManager.isItemStackInstanceOf(event.player.getHeldItem(), "craftingToolHardHammer")) {
            return false;
        }
        Vec3 startPos = event.player.getPosition(event.player.isSneaking() ? 0.75F : 1F);
        Vec3 endPos = event.target.hitVec;
        if (currentBoxes != null && currentBoxes.size() > 0) {
            AxisAlignedBB[] allOfThem = new AxisAlignedBB[currentBoxes.size()];
            for (int q = 0; q < currentBoxes.size(); q++) {
                allOfThem[q] = currentBoxes.get(q);
            }
            drawBoundingBox(event.player, allOfThem, event.partialTicks, event.context, -1);
        }
        int spot = getIntersectedBox(startPos, endPos, event.player.isSneaking());
        if (spot > -1) {
            AxisAlignedBB box = currentBoxes.get(spot);
            drawBoundingBox(event.player, new AxisAlignedBB[]{box}, event.partialTicks, event.context, 39168);
            event.setCanceled(true);
        }
        return true;
    }

    @Override
    public void addCollisionBoxesToList2(AxisAlignedBB aAABB, List bList, Entity aEntity) {
        //aAABB.setBounds(aAABB.minX, aAABB.minY, aAABB.minZ, aAABB.maxX, aAABB.maxY + 1, aAABB.maxZ);
        //bList.add(aAABB);
    }

    public IRecipe sLastRecipe = null;
    public boolean isRepair = false;

    public ItemStack getHammeringOutput(World world, ItemStack... aRecipe) {
        List<IRecipe> list = CraftingManagerHammer.getInstance().getRecipeList();
        InventoryCrafting aCrafting = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer var1) {return F;}
        }, 3, 3);
        for (int i = 0; i < 9 && i < aRecipe.length; i++) aCrafting.setInventorySlotContents(i, aRecipe[i]);

        ItemStack repairReturn = null;

        for (int i = 0; i < list.size(); i++) {
            IRecipe check = list.get(i);
            if (check.matches(aCrafting, worldObj)) {
                isRepair = check instanceof RepairRecipe;
                if (isRepair) {
                    repairReturn = check.getCraftingResult(aCrafting);
                } else {
                    return check.getCraftingResult(aCrafting);
                }
            }
        }
        return repairReturn;
    }

    public ItemStack getCraftingOutput() {
        ItemStack mainReturnable = CR.getany(worldObj, slot(0), slot(1), slot(2), slot(3), slot(4), slot(5), slot(6), slot(7), slot(8));
        ItemStack returnable = getHammeringOutput(worldObj, slot(0), slot(1), slot(2), slot(3), slot(4), slot(5), slot(6), slot(7), slot(8));

        slot(10, returnable);
        //if (!ST.equal(slot(10), returnable, F)) mUpdatedGrid = T;
        return slot(9, mainReturnable);
        //return slot(9, CR.getany(worldObj, slot(0), slot(1), slot(2), slot(3), slot(4), slot(5), slot(6), slot(7), slot(8)));
    }

    @Override
    public void addToolTips(List aList, ItemStack aStack, boolean aF3_H) {
        addHammerToolTip(aList, aStack);
        super.addToolTips(aList, aStack, aF3_H);
    }

    public void addHammerToolTip(List aList, ItemStack aStack) {
        aList.add(Chat.BLUE + "Max Uses: " + Chat.YELLOW + getMaxDamage());
    }

    public String[] tooltipColor = new String[]{
            Chat.GREEN, Chat.DGREEN, Chat.YELLOW, Chat.ORANGE, Chat.DRED, Chat.RED
    };

    @Override
    public boolean getSubItems(MultiTileEntityBlockInternal aBlock, Item aItem,
                               CreativeTabs aTab, List aList, short aID) {
        return SHOW_HIDDEN_MATERIALS || !mMaterial.mHidden;
    }


    public IIconContainer[] primary;
    public IIconContainer[] overlay;

    @Override
    public boolean allowCovers(byte side) {
        return side >= 2;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
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
                this.primary = QTTextures.cubeWood;
                this.overlay = QTTextures.cubeWoodOverlay;
            } else if (OP.ingot.canGenerateItem(this.mMaterial)) {
                this.primary = QTTextures.cubeMetal;
                this.overlay = QTTextures.cubeMetalOverlay;
            } else if (OP.gem.canGenerateItem(this.mMaterial)) {
                this.primary = QTTextures.cubeGem;
                this.overlay = QTTextures.cubeGemOverlay;
            } else {
                this.primary = QTTextures.cubeStone;
                this.overlay = QTTextures.cubeStoneOverlay;
            }
        }
        return BlockTextureMulti.get(BlockTextureDefault.get(primary[aIndex], mRGBa), BlockTextureDefault.get(overlay[aIndex]));
    }

    @Override
    public boolean canDrop(int aSlot) {
        if (aSlot < 9) return T;
        return F;
    }

    @Override
    public String getTileEntityName() {
        // TODO Auto-generated method stub
        return "qt.crafting.tier1";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Object getGUIClient2(int aGUIID, EntityPlayer aPlayer) { return new GuiQTCraftingTableClient(aPlayer.inventory, this, isPainted() ? UT.Code.getRGBaArray(getPaint()) : mMaterial.mRGBaSolid); }

    @Override
    public Object getGUIServer2(int aGUIID, EntityPlayer aPlayer) {
        return new GuiQTCraftingTable(aPlayer.inventory, this);
    }

    //@Override public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {return new ItemStack[aNBT.getInteger(CS.NBT_INV_SIZE)];}
    public boolean interpretClick(EntityPlayer aPlayer, boolean isSneaking, boolean isRightclick) {
        Vec3 eyeSpot = Vec3.createVectorHelper(aPlayer.posX, aPlayer.posY + (aPlayer.isSneaking() ? aPlayer.getEyeHeight() * 0.9F : aPlayer.getEyeHeight()), aPlayer.posZ);
        Vec3 lookVec = aPlayer.getLookVec();
        Vec3 endsUpAt = eyeSpot.addVector(lookVec.xCoord * 2, lookVec.yCoord * 2, lookVec.zCoord * 2);
        //System.out.println("Trying to go from " + eyeSpot.xCoord + "x " + eyeSpot.yCoord + "y " + eyeSpot.zCoord + "z to " + endsUpAt.xCoord + "x " + endsUpAt.yCoord + "y " + endsUpAt.zCoord + "z");
        int leSlot = getIntersectedBox(eyeSpot, endsUpAt, aPlayer.isSneaking());
        if (leSlot > -1) {
            AxisAlignedBB leBox = currentBoxes.get(leSlot);
            ItemStack inHand = aPlayer.getHeldItem();
            ItemStack inTable = slot(leSlot);
            if (isRightclick) {
                if (isSneaking) {
                    setInventorySlotContents(leSlot, inHand);
                    QTI.NW_API.sendToAllPlayersInRange(new PacketInventorySync(inHand, this.xCoord, this.yCoord, this.zCoord, leSlot), this.worldObj, this.xCoord, this.zCoord);
                    aPlayer.setCurrentItemOrArmor(0, inTable);
                } else {
                    if (inTable == null) {
                        setInventorySlotContents(leSlot, ST.amount(1, inHand));
                        QTI.NW_API.sendToAllPlayersInRange(new PacketInventorySync(ST.amount(1, inHand), this.xCoord, this.yCoord, this.zCoord, leSlot), this.worldObj, this.xCoord, this.zCoord);
                        inHand.stackSize = inHand.stackSize - 1;
                        if (inHand.stackSize < 1) {
                            inHand = null;
                        }
                        aPlayer.setCurrentItemOrArmor(0, inHand);
                    } else if (inHand == null) {
                        aPlayer.setCurrentItemOrArmor(0, ST.amount(1, inTable));
                        inTable.stackSize = inTable.stackSize - 1;
                        if (inTable.stackSize < 1) {
                            inTable = null;
                        }
                        setInventorySlotContents(leSlot, inTable);
                        QTI.NW_API.sendToAllPlayersInRange(new PacketInventorySync(inTable, this.xCoord, this.yCoord, this.zCoord, leSlot), this.worldObj, this.xCoord, this.zCoord);
                    } else if (ST.equal(inHand, inTable) && inTable.stackSize < inTable.getItem().getItemStackLimit(inTable)) {
                        inTable.stackSize = inTable.stackSize + 1;
                        inHand.stackSize = inHand.stackSize - 1;
                        if (inHand.stackSize < 1) {
                            inHand = null;
                        }
                        setInventorySlotContents(leSlot, inTable);
                        QTI.NW_API.sendToAllPlayersInRange(new PacketInventorySync(inTable, this.xCoord, this.yCoord, this.zCoord, leSlot), this.worldObj, this.xCoord, this.zCoord);
                        aPlayer.setCurrentItemOrArmor(0, inHand);
                    }
                }
            } else {
                if (isSneaking) {
                    ItemStack choice = getCraftingOutput();
                    int stacker = inHand == null ? 0 : inHand.stackSize;
                    if (inHand == null || ST.equal(inHand, choice)) {
                        for (int j = 0; getCraftingOutput() != null && j < (getCraftingOutput().getMaxStackSize() / getCraftingOutput().stackSize) - stacker; j++) {
                            inHand = consumeMaterials(aPlayer, inHand, j != 0);
                        }
                    }
                    aPlayer.setCurrentItemOrArmor(0, inHand);
                } else {
                    if (inHand == null) {
                        ItemStack returnable = consumeMaterials(aPlayer, aPlayer.getHeldItem(), false);
                        if (returnable != null) {
                            aPlayer.setCurrentItemOrArmor(0, returnable);
                        }
                    } else if (ST.equal(inHand, getCraftingOutput()) && inHand.stackSize + getCraftingOutput().stackSize <= inHand.getItem().getItemStackLimit(inHand)) {
                        ItemStack returnable = consumeMaterials(aPlayer, aPlayer.getHeldItem(), false);
                        aPlayer.setCurrentItemOrArmor(0, returnable);
                    }
                }
            }
            return true;
        } else {
            return isRightclick && openGUI(aPlayer, 0);
        }
    }


    @Override
    public boolean onBlockActivated3(EntityPlayer aPlayer, byte aSide, float aHitX, float aHitY, float aHitZ) {
        if (!isServerSide()) return true;
        if (!(this.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof CraftingHelper)) {
            Block aBlock = worldObj.getBlock(xCoord, yCoord + 1, zCoord);
            if (aBlock.canBeReplacedByLeaves(worldObj, xCoord, yCoord + 1, zCoord)) {
                WD.set(worldObj, xCoord, yCoord + 1, zCoord, QwerTech.machines.getItem(400));
            } else {
                return false;
            }
        }
        //System.out.println(aHitX +", " + aHitY + ", " + aHitZ);
        return interpretClick(aPlayer, aPlayer.isSneaking(), true);
    }

    public boolean canDoCraftingOutput() {
        if (!slotHas(9)) return F;
        return T;
    }

    public boolean canDoHammerOutput() {
        if (!slotHas(10)) return F;
        return T;
    }

    public ItemStack consumeMaterials(EntityPlayer aPlayer, ItemStack aHoldStack, boolean aSubsequentClick) {
        if (!slotHas(9)) return aHoldStack;

        if (aHoldStack != null) {
            if (!ST.equal(aHoldStack, slot(9))) {
                if (!aSubsequentClick) {
                    UT.Sounds.play(SFX.MC_HMM, 50, 1.0F, 1.0F, getCoords());
                }
                return aHoldStack;
            }
            if (aHoldStack.stackSize + slot(9).stackSize > aHoldStack.getMaxStackSize()) return aHoldStack;
            for (int i = 0; i < 9; i++)
                if (OM.is("gt:autocrafterinfinite", slot(i))) {
                    if (!aSubsequentClick) {
                        UT.Sounds.play(SFX.MC_HMM, 50, 1.0F, 1.0F, getCoords());
                    }
                    return aHoldStack;
                }
        }

        MultiItemTool.LAST_TOOL_COORDS_BEFORE_DAMAGE = getCoords();

        try {
            FMLCommonHandler.instance().firePlayerCraftingEvent(aPlayer, ST.copy(slot(9)), new InventoryCrafting(null, 3, 3));
        } catch (Throwable e) {e.printStackTrace(ERR);}

        boolean tOldToolSounds = TOOL_SOUNDS;

        for (int i = 0; i < 9; i++)
            if (slotHas(i)) {
                boolean tNeeds = T;
                TOOL_SOUNDS = isClientSide() && tOldToolSounds && !aSubsequentClick;
                ItemStack tContainer = ST.container(slot(i), F);
                TOOL_SOUNDS = F;
                // Contains itself, so it's an infinite use Container Item anyways.
                if (ST.equal(slot(i), tContainer, F)) continue;

                if (tNeeds) for (int j = 0; j < 9; j++)
                    if (j == i) {
                        if (ST.equalTools(slot(i), slot(j), F) && slot(j).stackSize > 0) {
                            tNeeds = F;
                            TOOL_SOUNDS = F;
                            ItemStack tContainer2 = ST.container(slot(j), F);
                            TOOL_SOUNDS = F;
                            // Consume the Item.
                            if (tContainer2 == null || (tContainer2.isItemStackDamageable() && tContainer2.getItemDamage() >= tContainer2.getMaxDamage())) {
                                decrStackSize(j, 1);
                            } else if (slot(j).stackSize == 1) {
                                slot(j, tContainer2);
                            } else {
                                decrStackSize(j, 1);
                            }
                            break;
                        }
                    }
            }

        if (aHoldStack == null) aHoldStack = ST.copy(slot(9));
        else aHoldStack.stackSize += slot(9).stackSize;

        aHoldStack.onCrafting(worldObj, aPlayer, slot(9).stackSize);

        handleAchievements(aPlayer, aHoldStack);

        MultiItemTool.LAST_TOOL_COORDS_BEFORE_DAMAGE = null;

        TOOL_SOUNDS = tOldToolSounds;

        return aHoldStack;
    }

    public ItemStack consumeMaterialsHammer(EntityPlayer aPlayer, boolean aSubsequentClick) {
        if (!slotHas(10)) return null;

        MultiItemTool.LAST_TOOL_COORDS_BEFORE_DAMAGE = getCoords();

        ItemStack aHoldStack;

        try {
            FMLCommonHandler.instance().firePlayerCraftingEvent(aPlayer, ST.copy(slot(10)), new InventoryCrafting(null, 3, 3));
        } catch (Throwable e) {e.printStackTrace(ERR);}

        boolean tOldToolSounds = TOOL_SOUNDS;

        for (int i = 0; i < 9; i++)
            if (slotHas(i)) {
                boolean tNeeds = T;
                TOOL_SOUNDS = isClientSide() && tOldToolSounds && !aSubsequentClick;
                ItemStack tContainer = ST.container(slot(i), F);
                TOOL_SOUNDS = F;
                // Contains itself, so it's an infinite use Container Item anyways.
                if (ST.equal(slot(i), tContainer, F)) continue;

                if (isRepair) {
                    if (OM.data(slot(i)) == null || (OM.data(slot(i)).mPrefix != OP.plate && OM.data(slot(i)).mPrefix != OP.plateTiny)) {
                        decrStackSize(i, 1);
                    }
                }

                if (tNeeds) for (int j = 0; j < 9; j++)
                    if (j == i) {
                        if (ST.equalTools(slot(i), slot(j), F) && slot(j).stackSize > 0) {
                            tNeeds = F;
                            TOOL_SOUNDS = F;
                            ItemStack tContainer2 = ST.container(slot(j), F);
                            TOOL_SOUNDS = F;
                            // Consume the Item.
                            if (tContainer2 == null || (tContainer2.isItemStackDamageable() && tContainer2.getItemDamage() >= tContainer2.getMaxDamage())) {
                                decrStackSize(j, 1);
                            } else if (slot(j).stackSize == 1 && !isRepair) {
                                slot(j, tContainer2);
                            } else {
                                decrStackSize(j, 1);
                            }
                            break;
                        }
                    }
            }

        aHoldStack = ST.copy(slot(10));

        if (aPlayer != null) {
            aHoldStack.onCrafting(worldObj, aPlayer, slot(10).stackSize);

            handleAchievements(aPlayer, aHoldStack);
        }

        MultiItemTool.LAST_TOOL_COORDS_BEFORE_DAMAGE = null;

        TOOL_SOUNDS = tOldToolSounds;

        return aHoldStack;
    }

    public long checkDamage(long unit) {
        if (getDamage() >= getMaxDamage()) {
            this.mUpdatedGrid = true;
            if (!this.worldObj.isRemote) {
                this.worldObj.func_147480_a(this.xCoord, this.yCoord, this.zCoord, true);
            }
            return 12500;
        }
        return unit;
    }

    public long handleHammer(Entity player, ItemStack stack) {
        if (this.slotHas(10)) {

            if (canBeDamaged()) applyDamage();
            ItemStack droppable;
            if (player instanceof EntityPlayer) {
                droppable = this.consumeMaterialsHammer((EntityPlayer) player, F);
            } else {
                droppable = this.consumeMaterialsHammer(null, F);
            }
            this.mUpdatedGrid = true;
				/*EntityItem dropped = ST.drop(worldObj, getCoords(), droppable);
				dropped.posY = this.yCoord + 1.1;
				dropped.motionX = 0;
				dropped.motionY = 0;
				dropped.motionZ = 0;*/

            if (player instanceof EntityPlayer) {
                EntityPlayer playa = (EntityPlayer) player;
                if (playa.openContainer instanceof GuiQTCraftingTable) {
                    playa.inventory.addItemStackToInventory(droppable);
                    droppable = null;
                }
            }

            DelegatorTileEntity<TileEntity> table = this.getAdjacentTileEntity(CS.SIDE_RIGHT);
            if (!table.exists() || !(table.mTileEntity instanceof CuttingBoardTileEntity)) {
                table = this.getAdjacentTileEntity(CS.SIDE_LEFT);
                if (!table.exists() || !(table.mTileEntity instanceof CuttingBoardTileEntity)) {
                    table = this.getAdjacentTileEntity(CS.SIDE_BACK);
                    if (!table.exists() || !(table.mTileEntity instanceof CuttingBoardTileEntity)) {
                        table = this.getAdjacentTileEntity(CS.SIDE_FRONT);
                    }
                }
            }
            if (droppable != null && table.exists() && table.mTileEntity instanceof CuttingBoardTileEntity) {
                for (int q = 0; q < 8; q++) {
                    ItemStack stacky = ((CuttingBoardTileEntity) table.mTileEntity).getStackInSlot(q);
                    if (!ST.valid(stacky)) {
                        ((CuttingBoardTileEntity) table.mTileEntity).setInventorySlotContents(q, droppable);
                        droppable = null;
                        break;
                    } else if (ST.equal(stacky, droppable) && stacky.stackSize + droppable.stackSize < droppable.getMaxStackSize()) {
                        stacky.stackSize = stacky.stackSize + droppable.stackSize;
                        ((CuttingBoardTileEntity) table.mTileEntity).setInventorySlotContents(q, stacky);
                        droppable = null;
                        break;
                    }
                }
            }
            if (droppable != null && !worldObj.isRemote) {
                worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, droppable));
            }
            return checkDamage(10000);
        }
        return 0;
    }

    public static void handleAchievements(EntityPlayer aPlayer, ItemStack aHoldStack) {
        if (aHoldStack.getItem() == Item.getItemFromBlock(Blocks.crafting_table))
            aPlayer.addStat(AchievementList.buildWorkBench, 1);
        else if (aHoldStack.getItem() == Item.getItemFromBlock(Blocks.furnace))
            aPlayer.addStat(AchievementList.buildFurnace, 1);
        else if (aHoldStack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table))
            aPlayer.addStat(AchievementList.enchantments, 1);
        else if (aHoldStack.getItem() == Item.getItemFromBlock(Blocks.bookshelf))
            aPlayer.addStat(AchievementList.bookcase, 1);
        else if (aHoldStack.getItem() == Items.bread) aPlayer.addStat(AchievementList.makeBread, 1);
        else if (aHoldStack.getItem() == Items.cake) aPlayer.addStat(AchievementList.bakeCake, 1);
        else if (aHoldStack.getItem() instanceof ItemHoe) aPlayer.addStat(AchievementList.buildHoe, 1);
        else if (aHoldStack.getItem() instanceof ItemSword) aPlayer.addStat(AchievementList.buildSword, 1);
        else if (aHoldStack.getItem() instanceof ItemPickaxe) {
            aPlayer.addStat(AchievementList.buildPickaxe, 1);
            if (aHoldStack.getItem() != Items.wooden_pickaxe) aPlayer.addStat(AchievementList.buildBetterPickaxe, 1);
        }
    }

    @Override
    public boolean isAnvil(byte aSide) {
        return true;
    }

    @Override
    public void onBlockClicked(EntityPlayer aPlayer) {
        interpretClick(aPlayer, aPlayer.isSneaking(), false);
    }

    @Override
    public long getDamage() {
        return this.mDamage;
    }

    @Override
    public long getMaxDamage() {
        return 1;
    }

    @Override
    public boolean canBeDamaged() {
        return true;
    }

    @Override
    public void applyDamage() {
        mDamage++;
    }

    @Override
    public void setDamage(long amt) {
        this.mDamage = amt;
    }

}
