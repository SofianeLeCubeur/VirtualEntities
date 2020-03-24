package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.VirtualEntity;
import com.github.sofianelecubeur.virtualentities.api.VirtualEnumEntities;
import com.github.sofianelecubeur.virtualentities.api.VirtualEnumEquipment;
import com.github.sofianelecubeur.virtualentities.api.entities.VirtualDroppedItem;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
class EntityController {

    private static final Class<?> entityClass = Reflection.getMinecraftClass("Entity");
    private static final Class<?> dataWatcherClass = Reflection.getMinecraftClass("DataWatcher");
    private static final Reflection.MethodInvoker getDataWatcherMethod = Reflection.getMethod(entityClass, "getDataWatcher");

    private final VirtualWorldImpl world;
    private Map<Integer, VirtualEntity> entities;

    public EntityController(VirtualWorldImpl world) {
        this.world = world;
        this.entities = new HashMap<>();
        if(world.owner == null){
            throw new IllegalStateException("Can't use an world with unknown owner");
        }
    }

    public void sendEntitiesFor(Player player){
        Collection<Player> viewer = Collections.singleton(player);
        for(VirtualEntity entity : getEntities()){
            VirtualEntityImpl vEntity = (VirtualEntityImpl) entity;
            if(vEntity instanceof VirtualEntityObjectImpl){
                createVEntityObject(vEntity, VirtualEnumEntities.of(vEntity).getId(), ((VirtualEntityObjectImpl) vEntity).getObjectData(), viewer);
            } else createVEntity(vEntity, viewer);
            updateVEntity(vEntity, viewer);
            updateRotation(vEntity, viewer);
        }
    }

    public void spawnEntity(VirtualEntityImpl entity, VirtualEnumEntities vEntityInfo, Location location, Entity realReferer){
        if(entity == null) throw new NullPointerException("Entity can't be null");
        entity.location = location;
        if(realReferer != null){
            entity.setRealReferer(realReferer);
            entity.importParams(realReferer);
            entity.entityInit();
            return;
        }
        entity.entityInit();
        entity.setLocation(location);
        if(vEntityInfo.getType().equals(VirtualEnumEntities.EntityType.ENTITY)){
            createVEntity(entity, world.getViewers());
            updateRotation(entity, world.getViewers());
        } else if(vEntityInfo.getType().equals(VirtualEnumEntities.EntityType.OBJECT)){
            throw new IllegalArgumentException("For spawning objects, you need to call spawnObject");
        }
        this.entities.put(entity.getId(), entity);
    }

    public void spawnObject(VirtualEntityObjectImpl entity, VirtualEnumEntities vEntityInfo, int objectData, Location location){
        if(entity == null) throw new NullPointerException("Entity can't be null");
        entity.location = location;
        entity.entityInit();
        entity.setLocation(location);
        entity.storeObjectData(objectData);
        createVEntityObject(entity, vEntityInfo.getId(), objectData, world.getViewers());
        updateRotation(entity, world.getViewers());
        this.entities.put(entity.getId(), entity);
    }

    public Collection<VirtualEntity> getEntities() {
        return entities.values();
    }

    public VirtualEntity getVEntity(int entityId){
        return this.entities.get(entityId);
    }

    public boolean despawn(VirtualEntity entity){
        if(entities.containsValue(entity)){
            this.entities.remove(entity.getId());
            this.despawnVEntity(entity);
            return true;
        }
        return false;
    }

    public void despawnAllFor(Collection<Player> viewers){
        this.despawnVEntities(this.entities.values(), viewers);
    }

    public void despawnAll(){
        this.despawnAllFor(world.getViewers());
    }

    /* NMS */
    private void createVEntity(VirtualEntityImpl entity, Collection<Player> viewers){
        Class<?> packetClass = getClass("PacketPlayOutSpawnEntityLiving");
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetClass, getClass("EntityLiving"));
        this.world.owner.protocol.sendPacket(viewers, constructor.invoke(entity.handle));
    }

    private void createVEntityObject(VirtualEntityImpl entityObject, int entityTypeId, int objectData, Collection<Player> viewers){
        Class<?> packetClass = getClass("PacketPlayOutSpawnEntity");
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetClass, getClass("Entity"), int.class, int.class);
        Object packet = constructor.invoke(entityObject.handle, entityTypeId, objectData);
        this.world.owner.protocol.sendPacket(viewers, packet);
    }

    public void updateVEntity(VirtualEntityImpl vEntity, Collection<Player> viewers) {
        Class<?> packetMetadataClass = getClass("PacketPlayOutEntityMetadata");
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetMetadataClass, int.class, dataWatcherClass, boolean.class);
        Object packet = constructor.invoke(vEntity.getId(), getDataWatcherMethod.invoke(vEntity.handle), true);
        this.world.owner.protocol.sendPacket(viewers, packet);
    }

    public void updateEquipment(VirtualEntity vEntity, VirtualEnumEquipment equipmentSlot, Object itemStack, Collection<Player> viewers) {
        Class<?> packetEquipmentClass = getClass("PacketPlayOutEntityEquipment");
        try {
            Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetEquipmentClass, int.class, int.class, itemStack.getClass());
            Object packet = constructor.invoke(vEntity.getId(), equipmentSlot.getSlotId(), itemStack);
            this.world.owner.protocol.sendPacket(viewers, packet);
        } catch (Exception ignored) { // Minecraft Version >= 1.9
            try {
                Class<?> enumItemSlot = Reflection.getMinecraftClass("EnumItemSlot");
                Reflection.MethodInvoker aMethod = Reflection.getMethod(enumItemSlot, "a", String.class);
                Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetEquipmentClass, int.class, enumItemSlot, itemStack.getClass());
                Object packet = constructor.invoke(vEntity.getId(), aMethod.invoke(null, equipmentSlot.getName()), itemStack);
                this.world.owner.protocol.sendPacket(viewers, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void collectItem(VirtualDroppedItem virtualItem, Player collector) {
        Class<?> packetCollectClass = getClass("PacketPlayOutCollect");
        try {
            Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetCollectClass, int.class, int.class);
            this.world.owner.protocol.sendPacket(world.getViewers(), constructor.invoke(virtualItem.getId(), collector.getEntityId()));
        } catch (Exception ignored) { // Minecraft Version >= 1.9
            try {
                Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetCollectClass, int.class, int.class, int.class);
                this.world.owner.protocol.sendPacket(world.getViewers(), constructor.invoke(virtualItem.getId(), collector.getEntityId(),
                        virtualItem.getItem().getAmount()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void teleport(VirtualEntityImpl vEntity, Collection<Player> viewers){
        Class<?> packetTeleportClass = getClass("PacketPlayOutEntityTeleport");
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetTeleportClass, entityClass);
        this.world.owner.protocol.sendPacket(viewers, constructor.invoke(vEntity.handle));
    }

    public void updateRotation(VirtualEntityImpl vEntity, Collection<Player> viewers){
        Class<?> packetTeleportClass = getClass("PacketPlayOutEntity$PacketPlayOutEntityLook");
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetTeleportClass, int.class, byte.class, byte.class, boolean.class);
        Location loc = vEntity.getLocation();
        Object packet = constructor.invoke(vEntity.getId(), toPackedByte(loc.getYaw()), toPackedByte(loc.getPitch()), false);
        this.world.owner.protocol.sendPacket(viewers, packet);
    }

    public void updateStatus(VirtualEntityImpl vEntity, int status, Collection<Player> viewers){
        Class<?> packetTeleportClass = getClass("PacketPlayOutEntityStatus");
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetTeleportClass, entityClass, byte.class);
        this.world.owner.protocol.sendPacket(viewers, constructor.invoke(vEntity, status));
    }

    public void applyVelocity(VirtualEntityImpl vEntity, Vector velocity, Collection<Player> viewers){
        Class<?> packetVelocityClass = getClass("PacketPlayOutEntityVelocity");
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetVelocityClass, int.class, double.class, double.class, double.class);
        this.world.owner.protocol.sendPacket(viewers, constructor.invoke(vEntity.getId(), velocity.getX(), velocity.getY(), velocity.getZ()));
    }

    public void updateVehicles(VirtualEntityImpl vehicle, VirtualEntityImpl target, Collection<Player> viewers){
        try {
            Class<?> packetClass = getClass("PacketPlayOutMount");
            Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetClass, entityClass);
            this.world.owner.protocol.sendPacket(viewers, constructor.invoke(vehicle.handle));
        } catch(Exception ignored){
            try {
                Class<?> packetClass = getClass("PacketPlayOutAttachEntity");
                Reflection.ConstructorInvoker constructor = Reflection.getConstructor(packetClass, int.class, entityClass, entityClass);
                this.world.owner.protocol.sendPacket(viewers, constructor.invoke(0, vehicle.handle, target != null ? target.handle : null));
            } catch (Exception ignored2){
                System.err.println("[VirtualEntities] Unable to update vehicles of a virtual entity in VWorld #" + vehicle.vworld.getName());
            }
        }
    }

    private void despawnVEntity(VirtualEntity entity){
        this.despawnVEntities(Collections.singletonList(entity), world.getViewers());
    }

    private void despawnVEntities(Collection<VirtualEntity> entities, Collection<Player> viewers){
        int[] ids = new int[entities.size()];
        int i = 0;
        for (VirtualEntity vEntity : entities) {
            ids[i++] = vEntity.getId();
        }
        this.world.owner.protocol.sendPacket(viewers, buildDestroyPacket(ids));
    }

    private Object buildDestroyPacket(int... eIds){
        Class<?> packetDestroyEntityClass = getClass("PacketPlayOutEntityDestroy");
        Reflection.ConstructorInvoker packetDestroyEntityConstructor = Reflection.getConstructor(packetDestroyEntityClass, int[].class);
        return packetDestroyEntityConstructor.invoke(eIds);
    }

    private Class<?> getClass(String arg0){ return Reflection.getMinecraftClass(arg0); }

    public static byte toPackedByte(float f) {
        return (byte)(int)(f * 256.0F / 360.0F);
    }

}