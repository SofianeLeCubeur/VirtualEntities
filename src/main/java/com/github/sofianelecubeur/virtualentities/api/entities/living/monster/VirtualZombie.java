package com.github.sofianelecubeur.virtualentities.api.entities.living.monster;

import com.github.sofianelecubeur.virtualentities.api.entities.living.VirtualEntityAgeable;
import com.github.sofianelecubeur.virtualentities.api.entities.living.VirtualEntityMonster;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualZombie extends VirtualEntityMonster, VirtualEntityAgeable {

    boolean isVillager();
    void setVillager(boolean villager);
    // Only 1.9+
    void setVillagerType(int type);
    int getVillagerType();

}