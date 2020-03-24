package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.hanging.VirtualItemFrame;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import com.github.sofianelecubeur.virtualentities.nms.impl.VirtualEntityObjectImpl;
import com.github.sofianelecubeur.virtualentities.nms.impl.VirtualWorldImpl;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public class VirtualItemFrameImpl extends VirtualEntityObjectImpl implements VirtualItemFrame {

    private static final Class<?> entityItemFrameClass = Reflection.getMinecraftClass("EntityItemFrame");
    private static final Reflection.MethodInvoker setItemMethod = Reflection.getMethod(entityItemFrameClass, "setItem", itemStackClass);
    private static final Reflection.MethodInvoker setRotationMethod = Reflection.getMethod(entityItemFrameClass, "setRotation", int.class);

    private int facing;

    public VirtualItemFrameImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = createEntity("EntityItemFrame");
        super.entityInit();
    }

    @Override
    void storeObjectData(int objectData) {
        this.facing = objectData;
    }

    @Override
    int getObjectData() {
        return facing;
    }

    @Override
    public int getRotation() {
        return (int) this.invokeGetter("getRotation");
    }

    @Override
    public void setRotation(int rotation) {
        setRotationMethod.invoke(this.handle, rotation);
    }

    @Override
    public ItemStack getItem() {
        return (ItemStack) asBukkitCopyMethod.invoke(null, this.invokeGetter("getItem"));
    }

    @Override
    public void setItem(ItemStack item) {
        try {
            setItemMethod.invoke(this.handle, asNMSCopyMethod.invoke(null, item));
        } catch (Exception ignored) {
            try {
                setItemMethod.invoke(this.handle, asNMSCopyMethod.invoke(null, item), false);
            } catch (Exception e) {
                System.err.println("Could not setItem for Dropped Item (" + getId() + ")");
            }
        }
        this.vworld.notifyDataUpdated(this);
    }
}