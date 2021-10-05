package com.kbi.qwertech.loaders.mod;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.kbi.qwertech.QwerTech;
import com.kbi.qwertech.api.data.QTConfigs;
import cpw.mods.fml.common.event.FMLInterModComms;
import gregapi.data.CS;
import gregapi.data.MD;
import gregapi.lang.LanguageHandler;
import gregapi.util.ST;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static codechicken.lib.render.FontUtils.drawCenteredString;
import static com.kbi.qwertech.loaders.RegisterBumbles.*;

public class NEI_Bumble_Handler extends TemplateRecipeHandler {

    public NEI_Bumble_Handler() {
        if (!NEI_QT_Config.sIsAdded) {
            System.out.println("Creating Bumble NEI handler");
            FMLInterModComms.sendRuntimeMessage(QwerTech.instance, "NEIPlugins", "register-crafting-handler", QwerTech.MODID + "@" + getRecipeName() + "@" + getOverlayIdentifier());
            GuiCraftingRecipe.craftinghandlers.add(this);
            GuiUsageRecipe.usagehandlers.add(this);
        }
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new NEI_Bumble_Handler();
    }

    @Override
    public String getGuiTexture() {
        return "qwertech:textures/gui/nei/breeding.png";
    }

    @Override
    public String getRecipeName() {
        return "Bumble Breeding";
    }

    @Override
    public String getOverlayIdentifier() {
        return "bumble_breeding";
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (CachedMutationRecipe recipe : getMutations()) {
            if (recipe.getResult().contains(result)) {
                arecipes.add(recipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (CachedMutationRecipe recipe : getMutations()) {
            if (recipe.contains(recipe.getIngredients(), ingredient)) {
                arecipes.add(recipe);
            }
        }
    }

    @Override
    public void drawExtras(int recipe) {
        CachedMutationRecipe rec = (CachedMutationRecipe) arecipes.get(recipe);
        rec.child.drawLabel();
        rec.parent1.drawLabel();
        rec.parent2.drawLabel();
        drawCenteredString((rec.chance / 10 + "%").replace(".0", ""), 105, 26, 0x151515);
    }

    private List<CachedMutationRecipe> getMutations() {
        List<CachedMutationRecipe> list = new ArrayList<>();
        Item gtBumble = ST.item(MD.GT, CS.ItemsGT.BUMBLEBEES.getUnlocalizedName());
        Item qtBumble = ST.item(MD.QT, CUSTOM_BUMBLES.getUnlocalizedName());
        int[] baseSpecies = new int[]{0, 100, 200, 300, 400, 500, 600, 700, 800, 900, 10000, 10100, 10200, 10300, 10400, 10500, 20000, 20100, 20200, 20300};
        for (int p1 : baseSpecies) {
            for (int i = 0; i < 40; i += 10) {
                for (int p2 : baseSpecies) {
                    for (int k = 0; k < 40; k += 10) {
                        int parent1 = p1 + i;
                        int parent2 = p2 + k;
                        int child;
                        if (parent1 == parent2 && i < 30)
                            child = parent1 + 10;
                        else
                            child = sneed(parent1, parent2);
                        if (child == parent1 || child == parent2) continue;

                        CachedMutationRecipe recipe = new CachedMutationRecipe(
                                toLS(new ParentData(gtBumble, (short) parent1), 1, 21, 14),
                                toLS(new ParentData(gtBumble, (short) parent2), 0, 75, 14),
                                toLS(new ParentData(gtBumble, (short) child), 2, 129, 14),
                                mutationChance(child)
                        );
                        if (!list.contains(recipe)) {
                            list.add(recipe);
                        }
                    }
                }
            }
        }
        if (QTConfigs.customBumbles) {
            for (Map.Entry<Short, BumbleData> e : CUSTOM_BUMBLE_DATA.entrySet()) {
                BumbleData data = e.getValue();
                ParentData parent1;
                ParentData parent2;
                short child = e.getKey();
                if (data.parents != null) {
                    parent1 = data.parents[0];
                    parent2 = data.parents[1];
                } else if ((child / 10) % 10 > 0) {
                    parent1 = new ParentData(qtBumble, (short) (child - 10));
                    parent2 = parent1;
                } else continue;

                CachedMutationRecipe recipe = new CachedMutationRecipe(
                        toLS(parent1, 1, 21, 14),
                        toLS(parent2, 0, 75, 14),
                        toLS(new ParentData(qtBumble, child), 2, 129, 14),
                        mutationChance(child)
                );
                if (!list.contains(recipe)) {
                    list.add(recipe);
                }
            }
        }
        return list;
    }

    private List<ItemStack> toBumbles(ParentData data, int... types) {
        List<ItemStack> list = new ArrayList<>();
        for (int type : types) {
            list.add(ST.make(data.item, 1, data.species + type));
        }
        return list;
    }

    private LabeledStack toLS(ParentData data, int display, int x, int y) {
        ItemStack bumble = ST.make(data.item, 1, data.species + display);
        return new LabeledStack(toBumbles(data, 0, 1, 2, 5, 6, 7), x, y, LanguageHandler.translate(bumble.getUnlocalizedName()), 19);
    }

    private int sneed(int parent1, int parent2) {
        switch (parent1 / 10) {
            case 3:
                switch (parent2 / 10) {
                    case 13:
                        return 10100;
                    case 53:
                        return 10000;
                    case 93:
                        return 10200;
                }
                break;
            case 13:
                switch (parent2 / 10) {
                    case 3:
                        return 10100;
                    case 53:
                        return 10400;
                }
                break;
            case 33:
                switch (parent2 / 10) {
                    case 43:
                        return 10300;
                    case 1053:
                        return 20000;
                }
                break;
            case 43:
                switch (parent2 / 10) {
                    case 33:
                        return 10300;
                    case 1053:
                        return 20200;
                }
                break;
            case 53:
                switch (parent2 / 10) {
                    case 3:
                        return 10000;
                    case 13:
                        return 10400;
                    case 1053:
                        return 20300;
                }
                break;
            case 63:
                switch (parent2 / 10) {
                    case 93:
                        return 10500;
                }
                break;
            case 73:
                switch (parent2 / 10) {
                    case 1053:
                        return 20100;
                }
                break;
            case 93:
                switch (parent2 / 10) {
                    case 3:
                        return 10200;
                    case 63:
                        return 10500;
                }
                break;
            case 1053:
                switch (parent2 / 10) {
                    case 33:
                        return 20000;
                    case 43:
                        return 20200;
                    case 53:
                        return 20300;
                    case 73:
                        return 20100;
                }
                break;
        }
        return (parent1 / 10) * 10;
    }

    private int mutationChance(int species) {
        switch ((species / 10) % 10) {
            case 0:
            case 1:
                return 500;
            case 2:
                return 250;
            case 3:
                return 25;
            default:
                return 0;
        }
    }

    public class CachedMutationRecipe extends CachedRecipe {
        LabeledStack parent1, parent2, child;
        int chance;

        public CachedMutationRecipe(LabeledStack parent1, LabeledStack parent2, LabeledStack child, int chance) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.child = child;
            this.chance = chance;
        }

        @Override
        public PositionedStack getResult() {
            return child;
        }

        @Override
        public ArrayList<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> list = new ArrayList<>();
            list.add(parent1);
            list.add(parent2);
            return list;
        }
    }

    public static class LabeledStack extends PositionedStack {
        private final String label;
        private final int yOff;

        public LabeledStack(Object stack, int x, int y, String label, int yOff) {
            super(stack, x, y);
            this.label = label;
            this.yOff = yOff;
        }

        public void drawLabel() {
            if (label.contains(" ")) {
                String[] parts = label.split(" ");
                for (int i = 0; i < parts.length; i++) {
                    drawCenteredString(parts[i], relx + 8, rely + 8 + yOff + 9 * i, 0x151515);
                }
            } else {
                drawCenteredString(label, relx + 8, rely + 8 + yOff, 0x151515);
            }
        }
    }
}
