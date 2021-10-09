package com.kbi.qwertech.gui;

import com.kbi.qwertech.api.data.QTConfigs;
import com.kbi.qwertech.tileentities.CraftingTableT1;
import gregapi.data.LH;
import gregapi.gui.ContainerCommon;
import gregapi.gui.Slot_Holo;
import gregapi.gui.Slot_Normal;
import gregapi.item.multiitem.MultiItem;
import gregapi.item.multiitem.MultiItemTool;
import gregapi.item.multiitem.behaviors.IBehavior;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

import static gregapi.data.CS.*;

public class GuiQTCraftingTable extends ContainerCommon {

    public GuiQTCraftingTable(InventoryPlayer aInventoryPlayer, CraftingTableT1 aTileEntity) {
        super(aInventoryPlayer, aTileEntity);
    }

    @Override
    public int addSlots(InventoryPlayer aInventoryPlayer) {
        int i = mOffset;
        addSlotToContainer(new Slot_Normal(mTileEntity, i++, 80, 17));
        addSlotToContainer(new Slot_Normal(mTileEntity, i++, 98, 17));
        addSlotToContainer(new Slot_Normal(mTileEntity, i++, 116, 17));
        addSlotToContainer(new Slot_Normal(mTileEntity, i++, 80, 35));
        addSlotToContainer(new Slot_Normal(mTileEntity, i++, 98, 35));
        addSlotToContainer(new Slot_Normal(mTileEntity, i++, 116, 35));
        addSlotToContainer(new Slot_Normal(mTileEntity, i++, 80, 53));
        addSlotToContainer(new Slot_Normal(mTileEntity, i++, 98, 53));
        addSlotToContainer(new Slot_Normal(mTileEntity, i++, 116, 53));

        /*9 output*/
        addSlotToContainer(new Slot_Holo(mTileEntity, i++, 152, 35, F, F, 1));
        /*10 hammer output*/
        addSlotToContainer(new Slot_Holo(mTileEntity, i++, 44, 35, F, F, 1));
        /*11 dump*/
        addSlotToContainer(new Slot_Holo(mTileEntity, i++, 134, 35, F, F, 1).setTooltip("Dump to Inventory", LH.Chat.WHITE));
        /*12 use hammer from inventory*/
        Slot_Holo hammer = new Slot_Holo(mTileEntity, i++, 62, 35, F, F, 1);
        addSlotToContainer(hammer.setTooltip("Use Hammer", LH.Chat.WHITE));
        /*13 tier*/
        addSlotToContainer(new Slot_Holo(mTileEntity, i, 44, 53, F, F, 1));

        return 84;
    }

    @Override
    public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer aPlayer) {
        if (aSlotIndex == 11) {
            for (int q = 0; q < 9; q++) {
                ItemStack stack = mTileEntity.getStackInSlotGUI(q);
                if (aPlayer.inventory.addItemStackToInventory(stack) || !ST.valid(stack)) {
                    mTileEntity.setInventorySlotContentsGUI(q, null);
                }

            }
            return null;
        } else if (aSlotIndex == 12) {
            CraftingTableT1 ct = (CraftingTableT1) this.mTileEntity;
            int slot = getHammer();
            if (slot != -1 && ct.canDoHammerOutput()) {
                EntityPlayer player = this.mInventoryPlayer.player;
                ItemStack ourHammer = ((Slot) this.inventorySlots.get(slot)).getStack();

                player.swingItem();
                if (ct.isServerSide()) {
                    if (ourHammer.getItem() instanceof MultiItemTool) {
                        MultiItemTool tool = ToolsGT.sMetaTool;
                        ArrayList<IBehavior<MultiItem>> tList = tool.mItemBehaviors.get(ToolsGT.HARDHAMMER);
                        if (tList != null) for (IBehavior<MultiItem> tBehavior : tList)
                            try {
                                if (tBehavior.onItemUseFirst(tool, ourHammer, player, ct.getWorld(), ct.xCoord, ct.yCoord, ct.zCoord, SIDE_TOP, 0f, 0f, 0f)) {
                                    detectAndSendChanges();
                                    break;
                                }
                            } catch (Throwable e) {
                                QTConfigs.L.error(e);
                            }
                        if (tool != ourHammer.getItem()) {
                            QTConfigs.L.warn("It would appear that your [{}] isn't actually a hammer.", LH.get(ourHammer.getUnlocalizedName()));
                        }
                    } else if (ourHammer.isItemStackDamageable()) {
                        ourHammer.damageItem(1, this.mInventoryPlayer.player);
                        ct.handleHammer(player, ourHammer);
                    }
                } else {
                    ct.celebrate();
                }
            }
            return null;

        } else if (aSlotIndex == 13) {
            //todo send it
            return null;
        } else try {
            Slot tSlot = ((Slot) inventorySlots.get(aSlotIndex));
            ItemStack tStack = tSlot.getStack();
            if (tStack != null && tStack.stackSize <= 0) {
                tSlot.putStack(null);
                if (aSlotIndex < 9) {
                    ItemStack toReturn = super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
                    detectAndSendChanges();
                    return toReturn;
                }
                return null;
            }
            if (aSlotIndex == 9) {
                ItemStack tCraftedStack = ((CraftingTableT1) mTileEntity).getCraftingOutput();
                if (tCraftedStack != null) {
                    if (aShifthold == 1) {
                        if (aMouseclick == 0) {
                            // SHIFT LEFTCLICK
                            for (int i = 0; i < aPlayer.inventory.mainInventory.length; i++) {
                                if (aPlayer.inventory.mainInventory[i] == null || ST.equal(aPlayer.inventory.mainInventory[i], tCraftedStack, false)) {
                                    if (aPlayer.inventory.mainInventory[i] == null || tCraftedStack.stackSize + aPlayer.inventory.mainInventory[i].stackSize <= aPlayer.inventory.mainInventory[i].getMaxStackSize()) {
                                        for (int j = 0; j < tCraftedStack.getMaxStackSize() / tCraftedStack.stackSize && ((CraftingTableT1) mTileEntity).canDoCraftingOutput(); j++) {
                                            if (!ST.equal(tStack = ((CraftingTableT1) mTileEntity).getCraftingOutput(), tCraftedStack) || tStack.stackSize != tCraftedStack.stackSize) {
                                                detectAndSendChanges();
                                                return aPlayer.inventory.getItemStack();
                                            }
                                            aPlayer.inventory.mainInventory[i] = (((CraftingTableT1) mTileEntity).consumeMaterials(aPlayer, aPlayer.inventory.mainInventory[i], i != 0 || j != 0));
                                        }
                                    }
                                    if (aPlayer.inventory.mainInventory[i].stackSize < aPlayer.inventory.mainInventory[i].getMaxStackSize()) {
                                        for (int q = i; q < aPlayer.inventory.mainInventory.length; q++) {
                                            if (aPlayer.inventory.mainInventory[q] == null || (ST.equal(tCraftedStack, aPlayer.inventory.mainInventory[q]) && tCraftedStack.stackSize + aPlayer.inventory.mainInventory[q].stackSize + aPlayer.inventory.mainInventory[i].stackSize <= aPlayer.inventory.mainInventory[i].getMaxStackSize() * 2)) {
                                                if (!ST.equal(tStack = ((CraftingTableT1) mTileEntity).getCraftingOutput(), tCraftedStack) || tStack.stackSize != tCraftedStack.stackSize) {
                                                    detectAndSendChanges();
                                                    return aPlayer.inventory.getItemStack();
                                                }
                                                ItemStack splittable = (((CraftingTableT1) mTileEntity).consumeMaterials(aPlayer, null, i != 0));
                                                this.mergeItemStack(splittable, 0, aPlayer.inventory.mainInventory.length, true);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            return aPlayer.inventory.getItemStack();
                        }
                        // SHIFT RIGHTCLICK
                        for (int i = 0; i < aPlayer.inventory.mainInventory.length; i++) {
                            if (aPlayer.inventory.mainInventory[i] == null || tCraftedStack.stackSize + aPlayer.inventory.mainInventory[i].stackSize <= aPlayer.inventory.mainInventory[i].getMaxStackSize()) {
                                boolean temp = F;
                                for (int j = 0; j < tCraftedStack.getMaxStackSize() / tCraftedStack.stackSize && ((CraftingTableT1) mTileEntity).canDoCraftingOutput(); j++) {
                                    if (!ST.equal(tStack = ((CraftingTableT1) mTileEntity).getCraftingOutput(), tCraftedStack) || tStack.stackSize != tCraftedStack.stackSize) {
                                        detectAndSendChanges();
                                        return aPlayer.inventory.getItemStack();
                                    }
                                    ItemStack splittable = (((CraftingTableT1) mTileEntity).consumeMaterials(aPlayer, null, i != 0 || j != 0));
                                    this.mergeItemStack(splittable, 0, aPlayer.inventory.mainInventory.length, true);
                                    temp = T;
                                }
                                if (temp) return aPlayer.inventory.getItemStack();
                            }
                        }
                        return aPlayer.inventory.getItemStack();
                    }
                    if (aMouseclick == 0) {
                        // LEFTCLICK
                        if (((CraftingTableT1) mTileEntity).canDoCraftingOutput())
                            aPlayer.inventory.setItemStack(((CraftingTableT1) mTileEntity).consumeMaterials(aPlayer, aPlayer.inventory.getItemStack(), F));
                        detectAndSendChanges();
                        return aPlayer.inventory.getItemStack();
                    }
                    // RIGHTCLICK
                    for (int i = 0; i < tCraftedStack.getMaxStackSize() / tCraftedStack.stackSize && ((CraftingTableT1) mTileEntity).canDoCraftingOutput(); i++) {
                        if (!ST.equal(tStack = ((CraftingTableT1) mTileEntity).getCraftingOutput(), tCraftedStack) || tStack.stackSize != tCraftedStack.stackSize) {
                            detectAndSendChanges();
                            return aPlayer.inventory.getItemStack();
                        }
                        aPlayer.inventory.setItemStack(((CraftingTableT1) mTileEntity).consumeMaterials(aPlayer, aPlayer.inventory.getItemStack(), i != 0));
                    }
                    detectAndSendChanges();
                    return aPlayer.inventory.getItemStack();
                }
                detectAndSendChanges();
                return null;
            }
        } catch (Throwable e) {
            e.printStackTrace(ERR);
        }
        return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
    }

    @Override
    public int getSlotCount() {
        return 9;
    }

    @Override
    public int getShiftClickSlotCount() {
        return 9;
    }

    public int getHammer() {
        /*
        for (int q = 0; q < this.mInventoryPlayer.getSizeInventory(); q++) {
            ItemStack check = this.mInventoryPlayer.getStackInSlot(q);
            if (OM.is("craftingToolHardHammer", check)) {
                return q;
            }
        }
        return -1;
         */
        for (int q = 14; q < this.inventorySlots.size(); q++) {
            ItemStack check = ((Slot) this.inventorySlots.get(q)).getStack();
            if (OM.is("craftingToolHardHammer", check)) {
                return q;
            }
        }
        return -1;
    }
}
