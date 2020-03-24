package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.living.VirtualEntityAgeable;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public abstract class VirtualEntityAgeableImpl extends VirtualEntityLivingImpl implements VirtualEntityAgeable {

    public VirtualEntityAgeableImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    public boolean isBaby() {
        return (boolean) invokeGetter("isBaby");
    }

    @Override
    public void setBaby(boolean baby) {
        Reflection.MethodInvoker setBabyMethod = Reflection.getMethod(this.handle.getClass(), "setBaby", Boolean.TYPE);
        setBabyMethod.invoke(this.handle, baby);
    }
}