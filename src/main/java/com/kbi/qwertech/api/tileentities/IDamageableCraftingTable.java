package com.kbi.qwertech.api.tileentities;

public interface IDamageableCraftingTable {
    long getDamage();

    long getMaxDamage();

    boolean canBeDamaged();

    void applyDamage();

    void setDamage(long amt);
}
