package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.VirtualZombie;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public class VirtualZombieImpl extends VirtualEntityAgeableImpl implements VirtualZombie {

    private static final Class<?> entityZombieClass = Reflection.getMinecraftClass("EntityZombie");

    private static Reflection.MethodInvoker getVillagerTypeMethod, setVillager, setVillagerType;

    public VirtualZombieImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = createEntity("EntityZombie");
        super.entityInit();
        try {
            setVillagerType = Reflection.getMethod(entityZombieClass, "setVillagerType", int.class);
            getVillagerTypeMethod = Reflection.getMethod(entityZombieClass, "getVillagerType");
        } catch (Exception ignored) {}
        try {
             setVillager = Reflection.getMethod(entityZombieClass, "setVillager", Boolean.TYPE);
        } catch (Exception ignored) {}
    }

    @Override
    public boolean isVillager() {
        return (boolean) invokeGetter("isVillager");
    }

    @Override
    public void setVillager(boolean villager) {
        if(setVillager != null){
            setVillager.invoke(this.handle, villager);
        }
    }

    @Override
    public void setVillagerType(int type) {
        if(setVillagerType != null){
            setVillagerType.invoke(this.handle, type);
        }
    }

    @Override
    public int getVillagerType() {
        if(getVillagerTypeMethod != null){
            return (int) getVillagerTypeMethod.invoke(this.handle);
        }
        return 0;
    }
}