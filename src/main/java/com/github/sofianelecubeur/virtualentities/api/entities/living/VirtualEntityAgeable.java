package com.github.sofianelecubeur.virtualentities.api.entities.living;

import com.github.sofianelecubeur.virtualentities.api.entities.VirtualEntityLiving;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualEntityAgeable extends VirtualEntityLiving {

    boolean isBaby();
    void setBaby(boolean baby);
}