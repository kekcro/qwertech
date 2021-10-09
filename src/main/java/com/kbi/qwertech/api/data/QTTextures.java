package com.kbi.qwertech.api.data;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;

public class QTTextures {
    public static String ctDir = "qwertech:craftingtables/";
    public static String ctCubeDir = ctDir + "cube/";
    public static String ctAnvilDir = ctDir + "anvil/";

    public static IIconContainer[] cubeWood = new IIconContainer[]{
            of(ctCubeDir, "colored/bottom"),
            of(ctCubeDir, "colored/top"),
            of(ctCubeDir, "colored/front"),
            of(ctCubeDir, "colored/back"),
            of(ctCubeDir, "colored/side"),
            of(ctCubeDir, "colored/top2")
    }, cubeWoodOverlay = new IIconContainer[]{
            of(ctCubeDir, "overlay/bottom"),
            of(ctCubeDir, "overlay/top"),
            of(ctCubeDir, "overlay/front"),
            of(ctCubeDir, "overlay/back"),
            of(ctCubeDir, "overlay/side"),
            of(ctCubeDir, "overlay/top")
    }, cubeMetal = new IIconContainer[]{
            of(ctCubeDir, "metal/bottom"),
            of(ctCubeDir, "metal/top"),
            of(ctCubeDir, "metal/front"),
            of(ctCubeDir, "metal/back"),
            of(ctCubeDir, "metal/side"),
            of(ctCubeDir, "metal/top2")
    }, cubeMetalOverlay = new IIconContainer[]{
            of(ctCubeDir, "overmetal/bottom"),
            of(ctCubeDir, "overmetal/top"),
            of(ctCubeDir, "overmetal/front"),
            of(ctCubeDir, "overmetal/back"),
            of(ctCubeDir, "overmetal/side"),
            of(ctCubeDir, "overmetal/top")
    }, cubeGem = new IIconContainer[]{
            of(ctCubeDir, "gem/bottom"),
            of(ctCubeDir, "gem/top"),
            of(ctCubeDir, "gem/front"),
            of(ctCubeDir, "gem/back"),
            of(ctCubeDir, "gem/side"),
            of(ctCubeDir, "gem/top2")
    }, cubeGemOverlay = new IIconContainer[]{
            of(ctCubeDir, "overgem/bottom"),
            of(ctCubeDir, "overgem/top"),
            of(ctCubeDir, "overgem/front"),
            of(ctCubeDir, "overgem/back"),
            of(ctCubeDir, "overgem/side"),
            of(ctCubeDir, "overgem/top")
    }, cubeStone = new IIconContainer[]{
            of(ctCubeDir, "stone/bottom"),
            of(ctCubeDir, "stone/top"),
            of(ctCubeDir, "stone/front"),
            of(ctCubeDir, "stone/back"),
            of(ctCubeDir, "stone/side"),
            of(ctCubeDir, "stone/top2")
    }, cubeStoneOverlay = new IIconContainer[]{
            of(ctCubeDir, "overstone/bottom"),
            of(ctCubeDir, "overstone/top"),
            of(ctCubeDir, "overstone/front"),
            of(ctCubeDir, "overstone/back"),
            of(ctCubeDir, "overstone/side"),
            of(ctCubeDir, "overstone/top")
    };

    public static IIconContainer[] cracks = new IIconContainer[]{
            of(ctCubeDir, "overlay/top"),
            of(ctCubeDir, "overlay/top"),
            of(ctCubeDir, "overlay/top"),
            of(ctDir, "destroy_stage_0"),
            of(ctDir, "destroy_stage_0"),
            of(ctDir, "destroy_stage_1"),
            of(ctDir, "destroy_stage_2"),
            of(ctDir, "destroy_stage_3"),
            of(ctDir, "destroy_stage_4"),
            of(ctDir, "destroy_stage_5"),
            of(ctDir, "destroy_stage_6"),
            of(ctDir, "destroy_stage_6"),
            of(ctDir, "destroy_stage_7"),
            of(ctDir, "destroy_stage_7"),
            of(ctDir, "destroy_stage_8"),
            of(ctDir, "destroy_stage_8"),
            of(ctDir, "destroy_stage_9")
    };

    public static IIconContainer[] anvilWood = new IIconContainer[]{
            of(ctAnvilDir, "colored/bottom"),
            of(ctAnvilDir, "colored/top"),
            of(ctAnvilDir, "colored/front"),
            of(ctAnvilDir, "colored/back"),
            of(ctAnvilDir, "colored/side"),
            of(ctAnvilDir, "colored/top2")
    }, anvilWoodOverlay = new IIconContainer[]{
            of(ctAnvilDir, "overlay/bottom"),
            of(ctAnvilDir, "overlay/top"),
            of(ctAnvilDir, "overlay/front"),
            of(ctAnvilDir, "overlay/back"),
            of(ctAnvilDir, "overlay/side"),
            of(ctAnvilDir, "overlay/top")
    }, anvilMetal = new IIconContainer[]{
            of(ctAnvilDir, "metal/bottom"),
            of(ctAnvilDir, "metal/top"),
            of(ctAnvilDir, "metal/front"),
            of(ctAnvilDir, "metal/back"),
            of(ctAnvilDir, "metal/side"),
            of(ctAnvilDir, "metal/top2")
    }, anvilMetalOverlay = new IIconContainer[]{
            of(ctAnvilDir, "overmetal/bottom"),
            of(ctAnvilDir, "overmetal/top"),
            of(ctAnvilDir, "overmetal/front"),
            of(ctAnvilDir, "overmetal/back"),
            of(ctAnvilDir, "overmetal/side"),
            of(ctAnvilDir, "overmetal/top")
    }, anvilGem = new IIconContainer[]{
            of(ctAnvilDir, "gem/bottom"),
            of(ctAnvilDir, "gem/top"),
            of(ctAnvilDir, "gem/front"),
            of(ctAnvilDir, "gem/back"),
            of(ctAnvilDir, "gem/side"),
            of(ctAnvilDir, "gem/top2")
    }, anvilGemOverlay = new IIconContainer[]{
            of(ctAnvilDir, "overgem/bottom"),
            of(ctAnvilDir, "overgem/top"),
            of(ctAnvilDir, "overgem/front"),
            of(ctAnvilDir, "overgem/back"),
            of(ctAnvilDir, "overgem/side"),
            of(ctAnvilDir, "overgem/top")
    }, anvilStone = new IIconContainer[]{
            of(ctAnvilDir, "stone/bottom"),
            of(ctAnvilDir, "stone/top"),
            of(ctAnvilDir, "stone/front"),
            of(ctAnvilDir, "stone/back"),
            of(ctAnvilDir, "stone/side"),
            of(ctAnvilDir, "stone/top2")
    }, anvilStoneOverlay = new IIconContainer[]{
            of(ctAnvilDir, "overstone/bottom"),
            of(ctAnvilDir, "overstone/top"),
            of(ctAnvilDir, "overstone/front"),
            of(ctAnvilDir, "overstone/back"),
            of(ctAnvilDir, "overstone/side"),
            of(ctAnvilDir, "overstone/top")
    };

    public static IIconContainer[] anvilCracks = new IIconContainer[]{
            of(ctAnvilDir, "overlay/top"),
            of(ctAnvilDir, "overlay/top"),
            of(ctCubeDir, "overlay/top"),
            of(ctDir, "destroy_stage_0"),
            of(ctDir, "destroy_stage_0"),
            of(ctDir, "destroy_stage_1"),
            of(ctDir, "destroy_stage_2"),
            of(ctDir, "destroy_stage_3"),
            of(ctDir, "destroy_stage_4"),
            of(ctDir, "destroy_stage_5"),
            of(ctDir, "destroy_stage_6"),
            of(ctDir, "destroy_stage_6"),
            of(ctDir, "destroy_stage_7"),
            of(ctDir, "destroy_stage_7"),
            of(ctDir, "destroy_stage_8"),
            of(ctDir, "destroy_stage_8"),
            of(ctDir, "destroy_stage_9")
    };
    
    public static IIconContainer of(String master, String slave) {
        return new Textures.BlockIcons.CustomIcon(master + slave);
    }

    public static IIconContainer getCracksForDamage(long damage, long maxDamage, IIconContainer[] type) {
        int ordinal = (int) Math.min(((damage * type.length) / maxDamage), type.length);
        return type[ordinal];
    }
}
