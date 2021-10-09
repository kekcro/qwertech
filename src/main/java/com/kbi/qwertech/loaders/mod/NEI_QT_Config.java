package com.kbi.qwertech.loaders.mod;

import com.kbi.qwertech.QwerTech;
import com.kbi.qwertech.api.data.QTConfigs;
import com.kbi.qwertech.client.gui.GuiQTCraftingTableClient;
import com.kbi.qwertech.tileentities.CraftingTableT3;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import gregapi.NEI_RecipeMap;
import gregapi.data.MD;
import gregapi.data.RM;

import static gregapi.data.CS.CODE_CLIENT;
import static gregapi.data.CS.GAPI;

@InterfaceList(value = {
		@Interface(iface = "codechicken.nei.api.IConfigureNEI", modid = "NotEnoughItems")
		})
public class NEI_QT_Config implements codechicken.nei.api.IConfigureNEI
{
	public static boolean sIsAdded = true;
	
	@Override
	public void loadConfig()
	{
	  sIsAdded = false;
	  new NEI_Tool_Handler();
	  new NEI_Hammer_Handler();
	  new NEI_Wood_Handler();
	  new NEI_3D_Handler();
	  if (QTConfigs.enableNEIBumbleBreeding) new NEI_Bumble_Handler();
	  NEI_RecipeMap chisel = new NEI_RecipeMap(RM.Chisel);
	  FMLInterModComms.sendRuntimeMessage(GAPI, "NEIPlugins", "register-crafting-handler", MD.GAPI.mID+"@"+chisel.getRecipeName()+"@"+chisel.getOverlayIdentifier());
	  codechicken.nei.recipe.GuiCraftingRecipe.craftinghandlers.add(chisel);
	  codechicken.nei.recipe.GuiUsageRecipe.usagehandlers.add(chisel);

	  if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
		  int oX = 55, oY = 11;
		  //codechicken.nei.api.API.registerGuiOverlay(GuiQTCraftingTableClient.class, "crafting", oX, oY);
		  //codechicken.nei.api.API.registerGuiOverlayHandler(GuiQTCraftingTableClient.class, new codechicken.nei.recipe.DefaultOverlayHandler(oX, oY), "crafting");

	  }
	  sIsAdded = true;
	}
	
	@Override
	public String getName()
	{
	  return QwerTech.MODNAME + " NEI Plugin";
	}
	
	@Override
	public String getVersion()
	{
	  return "1.1.0";
	}
}