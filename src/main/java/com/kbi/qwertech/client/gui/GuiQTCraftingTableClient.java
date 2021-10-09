package com.kbi.qwertech.client.gui;

import com.kbi.qwertech.QwerTech;
import com.kbi.qwertech.gui.GuiQTCraftingTable;
import com.kbi.qwertech.tileentities.CraftingTableT1;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.CS;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.gui.ContainerClient;
import gregapi.gui.Slot_Holo;
import gregapi.render.RenderHelper;
import gregapi.util.OM;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiQTCraftingTableClient extends ContainerClient {
    public ItemStack hammerStack;
    public ItemStack defaultHammer = CS.ToolsGT.sMetaTool.getToolWithStats(12, MT.Steel, MT.Wood);
    public ItemStack mTable;
    public int counter = 0;
    public boolean upCounting = true;
    public float[] mRGBa;
    public ResourceLocation mQT = new ResourceLocation(QwerTech.MODID, "textures/gui/qt.png");
    public boolean tTip = false;

    public GuiQTCraftingTableClient(InventoryPlayer aInventoryPlayer, CraftingTableT1 aTileEntity, short[] mRGBa) {
        super(new GuiQTCraftingTable(aInventoryPlayer, aTileEntity), aTileEntity.mGUITexture);
        this.mTable = aTileEntity.getPickBlock(null);
        this.mRGBa = new float[]{mRGBa[0] / 255F, mRGBa[1] / 255F, mRGBa[2] / 255F};
    }

    @Override
    protected void mouseClicked(int x, int y, int mB) {
        super.mouseClicked(x, y, mB);
        updateCache();
    }

    @Override
    public void drawGuiContainerForegroundLayer(int p1, int p2) {
        super.drawGuiContainerForegroundLayer(p1, p2);
        this.drawCenteredString(this.fontRendererObj, "Hammer output", 41, 15, 16777215);
        this.displayHammerable(p1, p2);
        this.drawTable();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(mBackground);
        GL11.glColor4f(mRGBa[0], mRGBa[1], mRGBa[2], 1.0F);
        super.drawGuiContainerBackgroundLayer2(par1, par2, par3);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        mc.renderEngine.bindTexture(mQT);
        zLevel += 1F;
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        func_152125_a(x + 150, y + 5, 0, 0, 22, 22, 22, 22, 22, 22);
    }

    public void displayHammerable(int mX, int mY) {
        if (this.mContainer.mInventoryPlayer.inventoryChanged) {
            updateCache();
        }
        if (upCounting) {
            counter = counter + 1;
        } else {
            counter = counter - 1;
        }
        if (counter < 0) {
            upCounting = true;
        } else if (counter > 60) {
            upCounting = false;
        }
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColorMask(true, true, true, false);
        if (this.inventorySlots.getSlot(10).getStack() != null) {
            this.drawGradientRect(61, 34, 79, 52, new Color(90 + (counter * 2), 120 + (counter * 2), 90 + (counter * 2)).getRGB(), new Color(90 + (counter * 2), 120 + (counter * 2), 90 + (counter * 2)).getRGB());
            if (hammerStack != null) {
                mouseOverTooltip(12, mX, mY, "Use Hammer", LH.Chat.GREEN);
                this.drawGradientRect(62, 35, 78, 51, new Color(10 + counter * 3, 255, 10 + counter, 150).getRGB(), new Color(3 + (counter * 3), 200, 1 + counter, 255).getRGB());
                itemRender.renderItemIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), hammerStack, 62, 35, true);
            } else {
                mouseOverTooltip(12, mX, mY, "Hammer Needed", LH.Chat.DRED);
                this.drawGradientRect(62, 35, 78, 51, new Color(255, 10 + counter * 3, 10 + counter, 150).getRGB(), new Color(200, 3 + counter * 3, 1 + counter, 255).getRGB());
                itemRender.renderItemIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), defaultHammer, 62, 35, true);
            }
        } else {
            this.drawGradientRect(61, 34, 79, 52, new Color(60 + counter, 60 + counter, 60 + counter).getRGB(), new Color(60 + counter, 60 + counter, 60 + counter).getRGB());
            mouseOverTooltip(12, mX, mY, "No hammer recipe", LH.Chat.DGRAY);
            itemRender.renderItemIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), defaultHammer, 62, 35, true);
            this.drawGradientRect(62, 35, 78, 51, new Color(100 + counter, 100 + counter, 100 + counter, 150).getRGB(), new Color(110 + counter, 110 + counter, 110 + counter, 255).getRGB());
        }
        mouseOverTable(mX, mY);
        GL11.glColorMask(true, true, true, true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void mouseOverTooltip(int ordinal, int mX, int mY, String aTooltip, String aFormatting) {
        if (mouseOver(ordinal, mX, mY)) {
            if (!tTip) {
                getHolo(ordinal).setTooltip(aTooltip, aFormatting);
                tTip = true;
            }
        } else tTip = false;
    }

    private void mouseOverTable(int mX, int mY) {
        if (mouseOver(13, mX, mY)) {
            if (!tTip) {
                MovingObjectPosition hit = mc.objectMouseOver;
                if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    TileEntity tile = mc.theWorld.getTileEntity(hit.blockX, hit.blockY, hit.blockZ);
                    if (tile instanceof CraftingTableT1) {
                        CraftingTableT1 table = (CraftingTableT1) tile;
                        List tooltips = mTable.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
                        if (table.getDamage() > 0)
                            tooltips.add(2, LH.Chat.BLUE + "Uses Left: " + table.tooltipColor[(int) ((table.getDamage() * table.tooltipColor.length) / table.getMaxDamage())] + (table.getMaxDamage() - table.getDamage()));
                        getHolo(13).setTooltips((String[]) tooltips.toArray(new String[0]), new String[0]);
                        tTip = true;
                    }
                } else {
                    List tooltips = mTable.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
                    getHolo(13).setTooltips((String[]) tooltips.toArray(new String[0]), new String[0]);
                    tTip = true;
                }
            }
        } else tTip = false;
    }

    private boolean mouseOver(int ordinal, int mX, int mY) {
        return isMouseOverSlot(this.mContainer.getSlot(ordinal), mX, mY);
    }

    private Slot_Holo getHolo(int ordinal) {
        return (Slot_Holo) this.mContainer.getSlot(ordinal);
    }

    public void drawTable() {
        RenderHelper.renderItemIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), mTable, 44, 53, false);
    }

    public void updateCache() {
        ItemStack[] stacks = this.mContainer.mInventoryPlayer.mainInventory;
        boolean found = false;
        for (ItemStack stack : stacks) {
            if (OM.is("craftingToolHardHammer", stack)) {
                hammerStack = stack;
                found = true;
                break;
            }
        }
        if (!found) {
            hammerStack = null;
        }
    }
}