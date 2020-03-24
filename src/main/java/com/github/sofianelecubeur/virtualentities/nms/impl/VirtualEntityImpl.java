package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.VirtualEntity;
import com.github.sofianelecubeur.virtualentities.api.VirtualWorld;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public abstract class VirtualEntityImpl implements VirtualEntity {

    protected static final Class<?> worldClass = Reflection.getMinecraftClass("World");
    protected static final Class<?> entityClass = Reflection.getMinecraftClass("Entity");
    protected static final Class<?> itemStackClass = Reflection.getMinecraftClass("ItemStack");
    protected static final Class<?> craftItemStackClass = Reflection.getClass("{obc}.inventory.CraftItemStack");
    protected static final Class<?> blockPositionClass = Reflection.getMinecraftClass("BlockPosition");
    protected static final Reflection.MethodInvoker asNMSCopyMethod = Reflection.getMethod(craftItemStackClass, "asNMSCopy", ItemStack.class);
    protected static final Reflection.MethodInvoker asBukkitCopyMethod = Reflection.getMethod(craftItemStackClass, "asBukkitCopy", itemStackClass);

    private static final Reflection.MethodInvoker setPositionMethod = Reflection.getMethod(entityClass, "setLocation",
            Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE);
    private static final Reflection.MethodInvoker setCustomNameMethod = Reflection.getMethod(entityClass, "setCustomName", String.class);
    private static final Reflection.MethodInvoker setCustomNameVisibleMethod = Reflection.getMethod(entityClass, "setCustomNameVisible", Boolean.TYPE);
    private static final Reflection.MethodInvoker setAirTicksMethod = Reflection.getMethod(entityClass, "setAirTicks", Integer.TYPE);
    private static final Reflection.MethodInvoker setInvisibleMethod = Reflection.getMethod(entityClass, "setInvisible", Boolean.TYPE);
    private static final Reflection.FieldAccessor<Boolean> onGroundField = Reflection.getField(entityClass, "onGround", boolean.class);

    protected final VirtualWorldImpl vworld;
    protected Object handle;
    private int entityId = -1;
    private Entity realReferer;

    protected Location location;
    protected Location prevLocation;

    private  Reflection.MethodInvoker mountMethod;

    public VirtualEntityImpl(VirtualWorldImpl vworld) {
        this.vworld = vworld;
    }

    /**
     * This method have to be overrided
     */
    protected void entityInit(){
        this.entityId = (int) this.invokeGetter("getId");
        this.location = new Location(vworld.getRealReferer(), 0, 0, 0);

        try {
            Reflection.MethodInvoker setInvulnerable = Reflection.getMethod(entityClass, "setInvulnerable", Boolean.TYPE);
            setInvulnerable.invoke(this.handle, true);
        } catch (Exception ignored){
        }

        try {
            mountMethod = Reflection.getTypedMethod(entityClass, "a", boolean.class, entityClass, boolean.class);
            System.out.println("Using startRiding() alternate force 'a' method for riding");
        } catch (Exception ignored){
            try {
                mountMethod = Reflection.getTypedMethod(entityClass, "mount", entityClass);
                System.out.println("Using mount() method for riding");
            } catch(Exception ignored2){
                System.err.println("Unable to find attach/mount vehicle method.");
            }
        }
    }

    protected void importParams(Entity referer){
        this.handle = this.invokeGetter(this.getRealReferer(), "getHandle");
    }

    @Override
    public UUID getUniqueId() {
        return (UUID) this.invokeGetter("getUniqueID");
    }

    @Override
    public boolean isDead() {
        return !((boolean) this.invokeGetter("isAlive"));
    }

    @Override
    public boolean onGround() {
        return onGroundField.get(this.handle);
    }

    @Override
    public int getAirTicks() {
        return (int) this.invokeGetter("getAirTicks");
    }

    @Override
    public void setAirTicks(int air) {
        setAirTicksMethod.invoke(this.handle, air);
    }

    @Override
    public String getCustomName() {
        return (String) this.invokeGetter("getCustomName");
    }

    protected void setRealReferer(Entity realReferer) {
        this.realReferer = realReferer;
    }

    public Entity getRealReferer() {
        return realReferer;
    }

    @Override
    public void setCustomName(String customName) {
        setCustomNameMethod.invoke(this.handle, customName);
        this.vworld.notifyDataUpdated(this);
    }

    @Override
    public boolean isCustomNameVisible() {
        return (boolean) this.invokeGetter("getCustomNameVisible");
    }

    @Override
    public void setCustomNameVisible(boolean visible) {
        setCustomNameVisibleMethod.invoke(this.handle, visible);
        this.vworld.notifyDataUpdated(this);
    }

    @Override
    public boolean isInvisible() {
        return (boolean) this.invokeGetter("isInvisible");
    }

    @Override
    public void setInvisible(boolean invisible) {
        setInvisibleMethod.invoke(this.handle, invisible);
    }

    @Override
    public VirtualWorld getWorld() {
        return this.vworld;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void teleport(Location location) {
        this.setLocation(location);
        this.vworld.notifyLocationUpdated(this);
    }

    @Override
    public void setVelocity(Vector velocity) {
        this.vworld.applyEntityVelocity(this, velocity);
    }

    @Override
    public int getId() {
        return this.entityId;
    }

    @Override
    public void remove() {
        this.vworld.despawn(this);
    }

    @Override
    public void startRiding(VirtualEntity baseEntity){
        if(mountMethod != null){
            Object result;
            try {
                result = mountMethod.invoke(this.handle, ((VirtualEntityImpl) baseEntity).handle, true);
            } catch (Exception e){
                result = mountMethod.invoke(this.handle, ((VirtualEntityImpl) baseEntity).handle);
            }
            System.out.println("Result: " + result);
            this.vworld.notifyEntityMounted((VirtualEntityImpl) baseEntity, this);
        }
    }

    protected void setLocation(Location location) {
        this.prevLocation = this.location.clone();
        this.location = location;
        setPositionMethod.invoke(this.handle, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    protected final Object createEntity(String entityClass){
        Reflection.ConstructorInvoker entityConstructor = Reflection.getConstructor(Reflection.getMinecraftClass(entityClass), worldClass);
        return entityConstructor.invoke(this.vworld.handle);
    }

    protected final Object invokeGetter(String name){
        return Reflection.getMethod(this.handle.getClass(), name).invoke(this.handle);
    }

    protected final Object invokeGetter(Object obj, String name){
        return Reflection.getMethod(obj.getClass(), name).invoke(obj);
    }

    protected final String buildExceptionMessage(String method, String clazz, Throwable error){
        return "Could not find the " + method + " method in the " + clazz + " Class (Protocol: " + Reflection.getServerProtocolVersion() + ")" +
                (error != null ? ": " + error.getMessage() : "");
    }

    protected final Object toBlockPosition(Location location){
        Reflection.ConstructorInvoker blockPositionConstructor = Reflection.getConstructor(blockPositionClass, double.class, double.class, double.class);
        return blockPositionConstructor.invoke(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + entityId + ",uuid=" + getUniqueId() + getCustomName() + "]";
    }
}