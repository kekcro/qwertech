package com.kbi.qwertech.loaders;

import com.google.gson.*;
import com.kbi.qwertech.items.CustomItemBumbles;
import cpw.mods.fml.common.registry.GameRegistry;
import gregapi.data.MD;
import net.minecraft.item.Item;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.kbi.qwertech.mixins.QwertechMixinPlugin.L;

public class RegisterBumbles {

    public static Map<Short, BumbleData> CUSTOM_BUMBLE_DATA;
    public static Map<Short, String[]> PARENT_DATA;
    public static CustomItemBumbles CUSTOM_BUMBLES;

    public static boolean init() {
        Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        Path dir = new File(".", "config/gregtech/CustomBumbles.json").toPath();
        try {
            CUSTOM_BUMBLE_DATA = new HashMap<>();
            PARENT_DATA = new HashMap<>();
            boolean changed = false;
            File file = dir.toFile();
            if (Files.notExists(dir) && !file.createNewFile()) {
                throw new IOException("Can't create CustomBumbles config file! Using defaults.");
            }
            FileInputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            JsonObject json = GSON.fromJson(br, JsonObject.class);
            if (json == null) {
                changed = true;
                json = new JsonObject();
            }

            if (changed) {
                FileWriter fw = new FileWriter(file, false);
                fw.write(GSON.toJson(json));
                fw.close();
            }

            populateMap(json);
            return !CUSTOM_BUMBLE_DATA.isEmpty();

        } catch (IOException e) {
            L.error(e.getMessage());
            return false;
        }
    }

    private static void populateMap(JsonObject o) {
        Map<String, Short> COMB_NAME_TO_META = new HashMap<>();
        short bumbleMeta = 0;
        short lastBumbleMeta;
        short combMeta = 0;
        for (Map.Entry<String, JsonElement> pair : o.entrySet()) {
            try {
                String lastCombName = "";
                String material = pair.getKey();
                JsonArray entries = pair.getValue().getAsJsonArray();
                if (entries.size() != 4) {
                    L.error("Every Bumble material should have 4 entries! Skipping {}...", material);
                    continue;
                }

                for (int i = 0; i < 4; i++) {
                    JsonObject data = entries.get(i).getAsJsonObject();
                    BumbleData bData;
                    String bumbleName = data.getAsJsonPrimitive("bumbleName").getAsString();
                    String bumbleTooltip = data.has("bumbleTooltip") ? data.getAsJsonPrimitive("bumbleTooltip").getAsString() : "";
                    String combName = "";
                    short minCombAmount = 1, maxCombAmount = 1;
                    String[] parents = null;
                    String[] flowers = null;
                    short[] rgb = new short[]{255, 255, 255};
                    String flowerTooltip = "Flowers (even potted ones work)";
                    if (data.has("combName")) {
                        String s = data.getAsJsonPrimitive("combName").getAsString();
                        if (COMB_NAME_TO_META.containsKey(s)) {
                            combMeta = COMB_NAME_TO_META.get(s);
                        } else {
                            COMB_NAME_TO_META.put(s, combMeta);
                        }
                        combName = s;
                        lastCombName = combName;
                    }

                    if (data.has("rgb")) {
                        JsonArray rgba = data.getAsJsonArray("rgb");
                        for (int k = 0; k < 3; k++) {
                            rgb[k] = rgba.get(k).getAsShort();
                        }
                    }

                    if (data.has("maxCombAmount")) {
                        maxCombAmount = data.getAsJsonPrimitive("maxCombAmount").getAsShort();
                    }
                    if (data.has("minCombAmount")) {
                        minCombAmount = data.getAsJsonPrimitive("minCombAmount").getAsShort();
                        if (minCombAmount > maxCombAmount) maxCombAmount = minCombAmount;
                    }

                    if (data.has("parents")) {
                        JsonArray a = data.getAsJsonArray("parents");
                        if (!a.get(0).getAsString().equals(a.get(1).getAsString())) {
                            parents = new String[2];
                            for (int k = 0; k < 2; k++) {
                                parents[k] = a.get(k).getAsString();
                            }
                        }
                    }
                    if (data.has("flowers") && data.has("flowerTooltip")) {
                        JsonArray a = data.getAsJsonArray("flowers");
                        flowers = new String[a.size()];
                        for (int k = 0; k < a.size(); k++) {
                            flowers[k] = a.get(k).getAsString();
                        }
                        flowerTooltip = data.get("flowerTooltip").getAsString();
                    }
                    if (combName.equals("")) {
                        if (lastCombName.equals("")) {
                            L.error("No Comb Name specified for the first entry of \"{}\". Skipping...", material);
                            break;
                        }
                        bData = new BumbleData(bumbleName, bumbleTooltip, lastCombName, COMB_NAME_TO_META.get(lastCombName), minCombAmount, maxCombAmount, flowers, flowerTooltip, rgb);
                    } else {
                        bData = new BumbleData(bumbleName, bumbleTooltip, combName, combMeta, minCombAmount, maxCombAmount, flowers, flowerTooltip, rgb);
                        combMeta++;
                    }
                    lastBumbleMeta = (short) (bumbleMeta + (i * 10));
                    CUSTOM_BUMBLE_DATA.put(lastBumbleMeta, bData);
                    PARENT_DATA.put(lastBumbleMeta, parents);
                }
                bumbleMeta += 100;
            } catch (IllegalStateException e) {
                L.error(e.getMessage());
            }
        }
    }

    public static void bakeParentData() {
        for (Map.Entry<Short, BumbleData> data : CUSTOM_BUMBLE_DATA.entrySet()) {
            if (PARENT_DATA.containsKey(data.getKey())) {
                String[] strings = PARENT_DATA.get(data.getKey());
                if (strings == null) continue;
                ParentData[] parents = new ParentData[2];
                for (int i = 0; i < 2; i++) {
                    String mID = "qwertech", unloc = "qwertech.bumblebee";
                    short species;
                    String[] split = strings[i].split(":");
                    switch (split.length) {
                        case 1:
                            species = Short.parseShort(split[0]);
                            break;
                        case 2:
                            if (split[0].equals("gt")) {
                                mID = MD.GT.mID;
                                unloc = "gt.multiitem.bumblebee";
                            }
                            species = Short.parseShort(split[1]);
                            break;
                        case 3:
                            mID = split[0];
                            unloc = split[1];
                            species = Short.parseShort(split[2]);
                            break;
                        default:
                            species = 0;
                            break;
                    }
                    Item item = GameRegistry.findItem(mID, unloc);
                    if (item != null) {
                        parents[i] = new ParentData(item, species);
                    }
                }
                if (parents[0] != null && parents[1] != null) {
                    data.getValue().parents = parents;
                }
            }
        }

        PARENT_DATA = null;
    }

    public static class BumbleData {
        public final String bumbleName, bumbleTooltip, combName, flowerTooltip;
        public final short combMeta, minCombAmount, maxCombAmount;
        public final short[] rgb;
        public ParentData[] parents;
        public final String[] flowers;

        public BumbleData(String bumbleName, String bumbleTooltip, String combName, short combMeta, short minCombAmount, short maxCombAmount, String[] flowers, String flowerTooltip, short[] rgb) {
            this.bumbleName = bumbleName;
            this.bumbleTooltip = bumbleTooltip;
            this.combName = combName;
            this.combMeta = combMeta;
            this.minCombAmount = minCombAmount;
            this.maxCombAmount = maxCombAmount;
            this.rgb = rgb;
            this.parents = null;
            this.flowers = flowers;
            this.flowerTooltip = flowerTooltip;
        }
    }

    public static class ParentData {
        public final Item item;
        public final short species;

        public ParentData(Item item, short species) {
            this.item = item;
            this.species = species;
        }
    }
}
