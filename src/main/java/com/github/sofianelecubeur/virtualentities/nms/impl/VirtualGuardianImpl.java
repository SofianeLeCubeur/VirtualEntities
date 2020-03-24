package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.VirtualEntity;
import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.VirtualGuardian;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import org.bukkit.entity.Entity;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public class VirtualGuardianImpl extends VirtualEntityLivingImpl implements VirtualGuardian {

    private static final Class<?> entityGuardianClass = Reflection.getMinecraftClass("EntityGuardian");
    private static Reflection.MethodInvoker setElderMethod, setTargetEntityMethod, setRetractingSpikesMethod, isSpikesRetractedMethod;
    private static String hasLaserMethod;

    public VirtualGuardianImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = createEntity("EntityGuardian");
        super.entityInit();

        try {
            setElderMethod = Reflection.getMethod(entityGuardianClass, "setElder", boolean.class);
        } catch (Exception e) {
            System.err.println(this.buildExceptionMessage("setElder", "Guardian", null));
        }

        boolean methodState = false;
        try {
            if(Reflection.getServerProtocolVersion().startsWith("v1_8")){
                setTargetEntityMethod = Reflection.getTypedMethod(entityGuardianClass, "b", void.class, int.class);
                hasLaserMethod = "cp";
                methodState = true;
                setRetractingSpikesMethod = Reflection.getTypedMethod(entityGuardianClass, "l", void.class, boolean.class);
                isSpikesRetractedMethod = Reflection.getTypedMethod(entityGuardianClass, "n", boolean.class);
            } else if(Reflection.getServerProtocolVersion().startsWith("v1_9")){
                setTargetEntityMethod = Reflection.getTypedMethod(entityGuardianClass, "b", void.class, int.class);
                hasLaserMethod = "dd";
                methodState = true;
                setRetractingSpikesMethod = Reflection.getTypedMethod(entityGuardianClass, "o", void.class, boolean.class);
                isSpikesRetractedMethod = Reflection.getTypedMethod(entityGuardianClass, "o", boolean.class);
            } else if(Reflection.getServerProtocolVersion().startsWith("v1_10")){
                setTargetEntityMethod = Reflection.getTypedMethod(entityGuardianClass, "b", void.class, int.class);
                hasLaserMethod = "di";
                methodState = true;
                setRetractingSpikesMethod = Reflection.getTypedMethod(entityGuardianClass, "p", void.class, boolean.class);
                isSpikesRetractedMethod = Reflection.getTypedMethod(entityGuardianClass, "o", boolean.class);
            } else if(Reflection.getServerProtocolVersion().startsWith("v1_11")) {
                setTargetEntityMethod = Reflection.getTypedMethod(entityGuardianClass, "a", void.class, int.class);
                hasLaserMethod = "dl";
                methodState = true;
                setRetractingSpikesMethod = Reflection.getTypedMethod(entityGuardianClass, "a", void.class, boolean.class);
                isSpikesRetractedMethod = Reflection.getTypedMethod(entityGuardianClass, "dk", boolean.class);
            } else {
                setTargetEntityMethod = Reflection.getTypedMethod(entityGuardianClass, "a", void.class, int.class);
                hasLaserMethod = "dp";
                methodState = true;
                setRetractingSpikesMethod = Reflection.getTypedMethod(entityGuardianClass, "a", void.class, boolean.class);
                isSpikesRetractedMethod = Reflection.getTypedMethod(entityGuardianClass, "do", boolean.class);
            }
        } catch (Exception e) {
            System.err.println(this.buildExceptionMessage(methodState ? "setRetractingSpikes" : "setTarget", "Guardian", e));
        }
    }

    @Override
    public boolean isElder() {
        return (boolean) invokeGetter("isElder");
    }

    @Override
    public void setElder(boolean elder) {
        if(setElderMethod != null) {
            setElderMethod.invoke(this.handle, elder);
            this.vworld.notifyDataUpdated(this);
        }
    }

    @Override
    public void setLaserTarget(Entity ent) {
        this.setLaserTarget(ent.getEntityId());
    }

    @Override
    public void setLaserTarget(VirtualEntity ent) {
        this.setLaserTarget(ent.getId());
    }

    public void setLaserTarget(int entityId){
        if(setTargetEntityMethod != null){
            setTargetEntityMethod.invoke(this.handle, entityId);
            this.vworld.notifyDataUpdated(this);
        }
    }

    @Override
    public boolean hasLaser() {
        return (boolean) invokeGetter(hasLaserMethod);
    }

    @Override
    public boolean isSpikesRetracted() {
        return (boolean) isSpikesRetractedMethod.invoke(this.handle);
    }

    @Override
    public void setRetractingSpikes(boolean retractingSpikes) {
        if(setRetractingSpikesMethod != null){
            setRetractingSpikesMethod.invoke(this.handle, retractingSpikes);
            this.vworld.notifyDataUpdated(this);
        }
    }
}