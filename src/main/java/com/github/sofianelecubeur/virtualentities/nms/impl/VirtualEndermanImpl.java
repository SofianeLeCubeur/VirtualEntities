package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.VirtualEntity;
import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.VirtualEnderman;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Created by Sofiane on 28/07/2018.
 *
 * @author Sofiane
 */
public class VirtualEndermanImpl extends VirtualEntityLivingImpl implements VirtualEnderman {

    private static final Class<?> entityEndermanClass = Reflection.getMinecraftClass("EntityEnderman");
    private static final Class<?> iBlockDataClass = Reflection.getMinecraftClass("IBlockData");
    private static final Reflection.MethodInvoker getBlockMethod = Reflection.getMethod(iBlockDataClass, "getBlock");

    private static final Reflection.MethodInvoker setCarriedMethod = Reflection.getMethod(entityEndermanClass, "setCarried", iBlockDataClass);
    private static final Reflection.MethodInvoker getCarriedMethod = Reflection.getTypedMethod(entityEndermanClass, "getCarried", iBlockDataClass);
    private static final Reflection.MethodInvoker getType = Reflection.getTypedMethod(worldClass, "getType", iBlockDataClass, blockPositionClass);
    private static Reflection.MethodInvoker setScreamingMethod, isScreamingMethod;

    public VirtualEndermanImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = createEntity("EntityEnderman");
        super.entityInit();

        String version = Reflection.getServerProtocolVersion();
        for (VirtualEndermanImpl.ENDERMAN_FIELDS cv : VirtualEndermanImpl.ENDERMAN_FIELDS.values()) {
            if (cv.name().toLowerCase().startsWith(version.toLowerCase())) {
               isScreamingMethod = Reflection.getTypedMethod(entityEndermanClass, cv.getIsScreamingMethod(), boolean.class);

               try {
                   setScreamingMethod = Reflection.getTypedMethod(entityEndermanClass, cv.getSetScreamingMethod(), void.class, boolean.class);
               } catch (Exception ignored) {
                   try {
                       setScreamingMethod = Reflection.getTypedMethod(entityEndermanClass, cv.getSetScreamingMethod(), boolean.class, entityLivingClass,
                               EntityTargetEvent.TargetReason.class, boolean.class);
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
               break;
            }
        }
    }

    @Override
    public Block getCarriedBlock() {
        Object iBlockData = getCarriedMethod.invoke(this.handle);
        Object block = getBlockMethod.invoke(iBlockData);

         // TODO
        return null;
    }

    @Override
    public void setCarriedBlock(Block carriedBlock) {
        Object iBlockData = getType.invoke(this.handle, toBlockPosition(carriedBlock.getLocation()));
        setCarriedMethod.invoke(this.handle, iBlockData);
    }

    @Override
    public boolean isScreaming() {
        return isScreamingMethod != null && (boolean) isScreamingMethod.invoke(this.handle);
    }

    @Override
    public void setScreaming(boolean screaming, VirtualEntity target) {
        if(screaming && target == null) throw new IllegalArgumentException("The target entity must exist.");
        if(setScreamingMethod != null){
            try {
                setScreamingMethod.invoke(this.handle, screaming);
            } catch (Exception ignored) {
                try {
                    setScreamingMethod.invoke(this.handle,
                            screaming ? ((VirtualEntityImpl) target).handle : null,
                            EntityTargetEvent.TargetReason.CUSTOM, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.vworld.notifyDataUpdated(this);
        }
    }
    
    private enum ENDERMAN_FIELDS {
        v1_12_R1("do_", "setGoalTarget"),
        v1_11("dk", "setGoalTarget"),
        v1_10("dh", "setGoalTarget"),
        v1_9_R2("dd", "setGoalTarget"), v1_9_R1("dc", "setGoalTarget"),
        v1_8_R3("co", "a"), v1_8_R2("co", "a"), v1_8_R1("cm", "a");

        private final String isScreamingMethod, setScreamingMethod;

        ENDERMAN_FIELDS(String isScreamingMethod, String setScreamingMethod) {
            this.isScreamingMethod = isScreamingMethod;
            this.setScreamingMethod = setScreamingMethod;
        }

        public String getIsScreamingMethod() {
            return isScreamingMethod;
        }

        public String getSetScreamingMethod() {
            return setScreamingMethod;
        }
    }
}