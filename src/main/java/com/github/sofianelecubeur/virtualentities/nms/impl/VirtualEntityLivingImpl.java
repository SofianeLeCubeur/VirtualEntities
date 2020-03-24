package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.VirtualEnumEquipment;
import com.github.sofianelecubeur.virtualentities.api.entities.VirtualEntityLiving;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public class VirtualEntityLivingImpl extends VirtualEntityImpl implements VirtualEntityLiving {

    protected static Class<?> entityLivingClass = Reflection.getMinecraftClass("EntityLiving");
    protected static Reflection.MethodInvoker setHealthMethod = Reflection.getMethod(entityLivingClass, "setHealth", Float.TYPE);

    private int arrowsInEntity;
    private boolean potionEffectAmbient;

    public VirtualEntityLivingImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    public float getHealth() {
        return (float) this.invokeGetter("getHealth");
    }

    @Override
    public void setHealth(float health) {
        setHealthMethod.invoke(this.handle, health);
    }

    @Override
    public int getArrowsCount() {
        return this.arrowsInEntity;
    }

    @Override
    public void setArrows(int arrowsInEntity) {
        this.arrowsInEntity = arrowsInEntity;
    }

    @Override
    public boolean isPotionEffectAmbient() {
        return potionEffectAmbient;
    }

    @Override
    public void setPotionEffectAmbient(boolean ambient) {
        this.potionEffectAmbient = ambient;
    }

    @Override
    public void setEquipment(VirtualEnumEquipment slot, ItemStack stack) {
        this.vworld.notifyEquipmentUpdated(this, slot, asNMSCopyMethod.invoke(null, stack));
    }
}