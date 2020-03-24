package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.VirtualCreeper;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;

/**
 * Created by Sofiane on 28/07/2018.
 *
 * @author Sofiane
 */
public class VirtualCreeperImpl extends VirtualEntityLivingImpl implements VirtualCreeper {

    private static final Class<?> entityCreeperClass = Reflection.getMinecraftClass("EntityCreeper");
    private static final Reflection.MethodInvoker setPoweredMethod = Reflection.getMethod(entityCreeperClass, "setPowered", boolean.class);
    private static Reflection.MethodInvoker setIgnitedMethod, isIgnitedMethod, setStateMethod, getStateMethod;

    public VirtualCreeperImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = this.createEntity("EntityCreeper");
        super.entityInit();

        String version = Reflection.getServerProtocolVersion();
        for (CREEPER_FIELDS cv : CREEPER_FIELDS.values()) {
            if (cv.name().toLowerCase().startsWith(version.toLowerCase())) {
               setIgnitedMethod = Reflection.getMethod(entityCreeperClass, cv.getSetIgnitedMethod());
               isIgnitedMethod = Reflection.getTypedMethod(entityCreeperClass, cv.getIsIgnitedMethod(), boolean.class);
               setStateMethod = Reflection.getMethod(entityCreeperClass, cv.getSetStateMethod(), int.class);
               getStateMethod = Reflection.getTypedMethod(entityCreeperClass, cv.getGetStateMethod(), int.class);
               break;
            }
        }
    }

    @Override
    public int getState() {
        return getStateMethod != null ? (int) getStateMethod.invoke(this.handle) : 0;
    }

    @Override
    public void setState(int state) {
        if(setStateMethod != null){
            setStateMethod.invoke(this.handle, state);
            this.vworld.notifyDataUpdated(this);
        }
    }

    @Override
    public boolean isPowered() {
        return (boolean) invokeGetter("isPowered");
    }

    @Override
    public void setPowered(boolean powered) {
        setPoweredMethod.invoke(this.handle, powered);
        this.vworld.notifyDataUpdated(this);
    }

    @Override
    public boolean isIgnited() {
        return isIgnitedMethod != null && (boolean) isIgnitedMethod.invoke(this.handle);
    }

    @Override
    public void setIgnited() {
        if(setIgnitedMethod != null){
            setIgnitedMethod.invoke(this.handle);
            this.vworld.notifyDataUpdated(this);
        }
    }

    private enum CREEPER_FIELDS {

        v1_12("do_", "isIgnited", "dm", "a"),
        v1_11("ck", "isIgnited", "di", "a"),
        v1_10("dh", "isIgnited", "df", "a"),
        v1_9_R2("dd", "isIgnited", "db", "a"), v1_9_R1("dc", "isIgnited", "da", "a"),
        v1_8_R3("co", "cn", "cm", "a"), v1_8_R2("co", "cn", "cm", "a"), v1_8_R1("cm", "cl", "ck", "a");

        private final String setIgnitedMethod, isIgnitedMethod, getStateMethod, setStateMethod;

        CREEPER_FIELDS(String setIgnitedMethod, String isIgnitedMethod, String getStateMethod, String setStateMethod) {
            this.setIgnitedMethod = setIgnitedMethod;
            this.isIgnitedMethod = isIgnitedMethod;
            this.getStateMethod = getStateMethod;
            this.setStateMethod = setStateMethod;
        }

        public String getSetIgnitedMethod() {
            return this.setIgnitedMethod;
        }

        public String getIsIgnitedMethod() {
            return isIgnitedMethod;
        }

        public String getGetStateMethod() {
            return getStateMethod;
        }

        public String getSetStateMethod() {
            return setStateMethod;
        }
    }
}