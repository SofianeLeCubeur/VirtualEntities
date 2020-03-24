package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.living.golem.VirtualSnowman;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;

/**
 * Created by Sofiane on 25/07/2018.
 *
 * @author Sofiane
 */
public class VirtualSnowmanImpl extends VirtualEntityLivingImpl implements VirtualSnowman {

    private static final Class<?> entitySnowmanClass = Reflection.getMinecraftClass("EntitySnowman");
    private static Reflection.MethodInvoker setPumpkinHatMethod, hasPumpkinHatMethod;

    public VirtualSnowmanImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = createEntity("EntitySnowman");
        super.entityInit();

        try {
            if(Reflection.getServerProtocolVersion().startsWith("v1_9")){
                setPumpkinHatMethod = Reflection.getTypedMethod(entitySnowmanClass, "a", void.class, boolean.class);
                hasPumpkinHatMethod = Reflection.getTypedMethod(entitySnowmanClass, "o", boolean.class);
            } else if(Reflection.getServerProtocolVersion().startsWith("v1_10")){
                setPumpkinHatMethod = Reflection.getMethod(entitySnowmanClass, "setDerp", void.class, boolean.class);
                hasPumpkinHatMethod = Reflection.getMethod(entitySnowmanClass, "isDerp");
            } else {
                setPumpkinHatMethod = Reflection.getMethod(entitySnowmanClass, "setHasPumpkin", boolean.class);
                hasPumpkinHatMethod = Reflection.getMethod(entitySnowmanClass, "hasPumpkin");
            }
        } catch (Exception e) {
            System.err.println(buildExceptionMessage("hasPumpkin", "Snowman", null));
        }
    }

    /**
     * Only after 1.9
     */
    @Override
    public boolean hasPumpkinHat() {
        return hasPumpkinHatMethod != null && (boolean) hasPumpkinHatMethod.invoke(this.handle);
    }

    /**
     * Only after 1.9
     */
    @Override
    public void setPumpkinHat(boolean pumpkinHat) {
        if(setPumpkinHatMethod != null){
            setPumpkinHatMethod.invoke(this.handle, pumpkinHat);
        }
    }
}