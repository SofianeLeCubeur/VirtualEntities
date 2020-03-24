package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.living.golem.VirtualIronGolem;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;

/**
 * Created by Sofiane on 25/07/2018.
 *
 * @author Sofiane
 */
public class VirtualIronGolemImpl extends VirtualEntityLivingImpl implements VirtualIronGolem {

    private static final Class<?> entityIronGolemClass = Reflection.getMinecraftClass("EntityIronGolem");
    private static final Reflection.MethodInvoker setPlayerCreatedMethod = Reflection.getMethod(entityIronGolemClass, "setPlayerCreated", boolean.class);

    public VirtualIronGolemImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = createEntity("EntityIronGolem");
        super.entityInit();
    }

    @Override
    public boolean isPlayerCreated() {
        return (boolean) this.invokeGetter("isPlayerCreated");
    }

    @Override
    public void setPlayerCreated(boolean playerCreated) {
        setPlayerCreatedMethod.invoke(this.handle, playerCreated);
    }

    @Override
    public void throwUpArms() {
        this.vworld.notifyStatusUpdated(this, 4);
    }

    /**
     * Only with Version >= 1.9
     */
    @Override
    public void setHoldingPoppy(){
        this.vworld.notifyStatusUpdated(this, 11);
    }

    /**
     * Only with Version >= 1.10
     */
    @Override
    public void removePoppy(){
        this.vworld.notifyStatusUpdated(this, 34);
    }
}