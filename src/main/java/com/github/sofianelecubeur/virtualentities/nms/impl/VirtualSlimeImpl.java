package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.VirtualSlime;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;

public class VirtualSlimeImpl extends VirtualEntityLivingImpl implements VirtualSlime  {

    private static final Class<?> entitySlimeClass = Reflection.getMinecraftClass("EntitySlime");

    private Reflection.MethodInvoker setSize;

    public VirtualSlimeImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = this.createEntity("EntitySlime");
        super.entityInit();

        try {
            setSize = Reflection.getMethod(entitySlimeClass, "setSize", int.class, boolean.class);
        } catch (Exception e){
            System.out.println("Unable to find setSize() method");
        }
    }

    // Bounding box, size is multiplied by 0.51000005F in NMS
    @Override
    public void setSize(int size) {
        if(setSize != null){
            try {
                setSize.invoke(this.handle, size, false);
            } catch(Exception e){
                System.out.println("Failed to setSize");
            }
            this.vworld.notifyDataUpdated(this);
        }
    }

    @Override
    public int getSize() {
        return (int) invokeGetter("getSize");
    }

}
