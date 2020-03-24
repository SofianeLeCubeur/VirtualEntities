package com.github.sofianelecubeur.virtualentities.api.entities.living.golem;

import com.github.sofianelecubeur.virtualentities.api.entities.living.VirtualEntityGolem;

/**
 * Created by Sofiane on 25/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualIronGolem extends VirtualEntityGolem {

    boolean isPlayerCreated();
    void setPlayerCreated(boolean playerCreated);
    void throwUpArms();
    void setHoldingPoppy();
    void removePoppy();

}