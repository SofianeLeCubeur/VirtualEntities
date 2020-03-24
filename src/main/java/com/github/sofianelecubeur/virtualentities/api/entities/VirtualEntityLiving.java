package com.github.sofianelecubeur.virtualentities.api.entities;

import com.github.sofianelecubeur.virtualentities.api.VirtualEntity;
import com.github.sofianelecubeur.virtualentities.api.VirtualEnumEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualEntityLiving extends VirtualEntity {

    float getHealth();
    void setHealth(float health);
    int getArrowsCount();
    void setArrows(int arrowsInEntity);
    boolean isPotionEffectAmbient();
    void setPotionEffectAmbient(boolean ambient);
    void setEquipment(VirtualEnumEquipment slot, ItemStack stack);
}