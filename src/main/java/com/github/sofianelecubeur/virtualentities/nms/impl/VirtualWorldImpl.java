package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.*;
import com.github.sofianelecubeur.virtualentities.api.entities.VirtualDroppedItem;
import com.github.sofianelecubeur.virtualentities.api.entities.VirtualEntityObject;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import com.github.sofianelecubeur.virtualentities.nms.event.PlayerInteractVirtualEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public class VirtualWorldImpl implements VirtualWorld {

    public static final Reflection.MethodInvoker getHandleWorldMethod = Reflection.getMethod("{obc}.CraftWorld", "getHandle", World.class);

    private final String name;
    private final World referer;
    protected Object handle;
    protected final VirtualApi owner;

    private Map<UUID, Player> viewers;
    private EntityController entityController;

    public VirtualWorldImpl(String name, World handle, VirtualApi owner) {
        this.name = name;
        this.referer = handle;
        this.owner = owner;
        this.viewers = new HashMap<>();
        this.entityController = new EntityController(this);
        this.worldInit();
    }

    private void worldInit(){
        this.handle = getHandleWorldMethod.invoke(this.referer);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addViewer(Player player) {
        viewers.put(player.getUniqueId(), player);
        this.entityController.sendEntitiesFor(player);
    }

    @Override
    public void removeViewer(Player player) {
        viewers.remove(player.getUniqueId());
        entityController.despawnAllFor(Collections.singleton(player));
    }

    @Override
    public boolean hasViewer(Player viewer) {
        return viewers.containsKey(viewer.getUniqueId());
    }

    @Override
    public <T extends VirtualEntity> T spawn(Class<T> virtualEntityInterfaceClass, Location location) {
        VirtualEnumEntities vEntityInfo = VirtualEnumEntities.of(virtualEntityInterfaceClass);
        if(vEntityInfo == null)  throw new RuntimeException("The specified Entity is unknown: " + virtualEntityInterfaceClass.getName());
        Class<?> virtualEntityClass = vEntityInfo.getEntityClass();
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(virtualEntityClass, VirtualWorldImpl.class);
        T instance = (T) constructor.invoke(this);
        this.entityController.spawnEntity((VirtualEntityImpl) instance, vEntityInfo, location, null);
        return instance;
    }

    public <T extends VirtualEntityObject> T spawnObject(Class<T> virtualEntityInterfaceClass, int objectData, Location location){
        VirtualEnumEntities vEntityInfo = VirtualEnumEntities.of(virtualEntityInterfaceClass);
        if(vEntityInfo == null)  throw new RuntimeException("The specified Entity is unknown: " + virtualEntityInterfaceClass.getName());
        Class<?> virtualEntityClass = vEntityInfo.getEntityClass();
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(virtualEntityClass, VirtualWorldImpl.class);
        T instance = (T) constructor.invoke(this);
        this.entityController.spawnObject((VirtualEntityObjectImpl) instance, vEntityInfo, objectData, location);
        return instance;
    }

    protected VirtualEntityImpl getEntityMirror(int entityId){
        return (VirtualEntityImpl) this.entityController.getVEntity(entityId);
    }

    public <T extends VirtualEntity> T getEntityMirror(Entity entity) {
        if (getEntityMirror(entity.getEntityId()) != null) {
            return (T) getEntityMirror(entity.getEntityId());
        }
        VirtualEnumEntities vEntityInfo = VirtualEnumEntities.of(entity);
        if (vEntityInfo == null)
            throw new RuntimeException("Unable to find the entity image for " + entity.getClass().getSimpleName() + " entity class");
        Class<?> virtualEntityClass = vEntityInfo.getEntityClass();
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(virtualEntityClass, VirtualWorldImpl.class);
        T instance = (T) constructor.invoke(this);
        this.entityController.spawnEntity((VirtualEntityImpl) instance, vEntityInfo, null, entity);
        return instance;
    }

    @Override
    public void despawn(VirtualEntity virtualEntity) {
        this.entityController.despawn(virtualEntity);
    }

    @Override
    public World getRealReferer() {
        return this.referer;
    }

    @Override
    public Collection<Player> getViewers() {
        return viewers.values();
    }

    @Override
    public void destroy() {
        this.entityController.despawnAll();
    }

    protected void notifySendPacket(Object... packets) {
        this.owner.protocol.sendPacket(this.getViewers(), packets);
    }

    protected void notifyDataUpdated(VirtualEntityImpl vEntity) {
        this.entityController.updateVEntity(vEntity, this.getViewers());
    }

    protected void notifyEquipmentUpdated(VirtualEntity vEntity, VirtualEnumEquipment equipmentSlot, Object itemStack){
        this.entityController.updateEquipment(vEntity, equipmentSlot, itemStack, this.getViewers());
    }

    protected void notifyItemCollected(VirtualDroppedItem virtualItem, Player collector){
        this.entityController.collectItem(virtualItem, collector);
    }

    protected void notifyLocationUpdated(VirtualEntityImpl vEntity){
        this.entityController.teleport(vEntity, getViewers());
    }

    protected void notifyStatusUpdated(VirtualEntityImpl vEntity, int status){
        this.entityController.updateStatus(vEntity, status, getViewers());
    }

    protected void applyEntityVelocity(VirtualEntityImpl vEntity, Vector velocity){
        this.entityController.applyVelocity(vEntity, velocity, getViewers());
    }

    protected void notifyEntityMounted(VirtualEntityImpl vehicle, VirtualEntityImpl target){
        this.entityController.updateVehicles(vehicle, target, getViewers());
    }

    protected void notifyEntityUsed(Player user, int used, String action){
        VirtualEntity usedEntity = entityController.getVEntity(used);
        if(usedEntity != null) {
            Bukkit.getPluginManager().callEvent(new PlayerInteractVirtualEntityEvent(user, usedEntity, PlayerInteractVirtualEntityEvent.EntityUseType.valueOf(
                    PlayerInteractVirtualEntityEvent.EntityUseType.class, action)));
        }
    }
}