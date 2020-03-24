package com.github.sofianelecubeur.virtualentities.api.entities.living.monster;

import com.github.sofianelecubeur.virtualentities.api.entities.living.VirtualEntityMonster;

/**
 * Created by Sofiane on 28/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualCreeper extends VirtualEntityMonster {

    int getState();
    void setState(int state);
    boolean isPowered();
    void setPowered(boolean powered);
    boolean isIgnited();
    void setIgnited();

}