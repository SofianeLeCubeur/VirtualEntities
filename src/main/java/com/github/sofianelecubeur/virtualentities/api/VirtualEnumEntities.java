package com.github.sofianelecubeur.virtualentities.api;

import com.github.sofianelecubeur.virtualentities.api.entities.VirtualDroppedItem;
import com.github.sofianelecubeur.virtualentities.api.entities.VirtualEntityLiving;
import com.github.sofianelecubeur.virtualentities.api.entities.hanging.VirtualItemFrame;
import com.github.sofianelecubeur.virtualentities.api.entities.living.*;
import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.*;
import com.github.sofianelecubeur.virtualentities.api.entities.living.player.VirtualPlayer;
import com.github.sofianelecubeur.virtualentities.nms.impl.*;
import com.github.sofianelecubeur.virtualentities.nms.impl.VirtualDroppedItemImpl;
import com.github.sofianelecubeur.virtualentities.nms.impl.VirtualItemFrameImpl;
import org.bukkit.entity.*;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public enum VirtualEnumEntities {
    GIANT(EntityType.ENTITY, Giant.class, "Giant Zombie", 27, VirtualEntityLiving.class, VirtualEntityLivingImpl.class), // TODO: Custom implementation
    ARMOR_STAND(EntityType.ENTITY, ArmorStand.class, "Armor Stand", 30, VirtualArmorStand.class, VirtualArmorStandImpl.class),
    CREEPER(EntityType.ENTITY, Creeper.class, "Creeper", 50, VirtualCreeper.class, VirtualCreeperImpl.class),
    PIG_ZOMBIE(EntityType.ENTITY, PigZombie.class, "Pig Zombie", 53, VirtualEntityLiving.class, VirtualEntityLivingImpl.class), // TODO: Custom implementation
    ZOMBIE(EntityType.ENTITY, Zombie.class, "Zombie", 54, VirtualZombie.class, VirtualZombieImpl.class),
    PIG(EntityType.ENTITY, Pig.class, "Pig", 51, VirtualEntityLiving.class, VirtualEntityLivingImpl.class), // TODO: Custom implementation
    RABBIT(EntityType.ENTITY, Rabbit.class, "PIG", 56, VirtualEntityLiving.class, VirtualEntityLivingImpl.class), // TODO: Custom implementation
    ENDERMAN(EntityType.ENTITY, Enderman.class, "Enderman", 18, VirtualEnderman.class, VirtualEndermanImpl.class),
    SLIME(EntityType.ENTITY, Slime.class, "Slime", 64, VirtualSlime.class, VirtualSlimeImpl.class),
    GUARDIAN(EntityType.ENTITY, Guardian.class, "Guardian", 68, VirtualGuardian.class, VirtualGuardianImpl.class),

    ITEM_FRAME(EntityType.OBJECT, ItemFrame.class, "Item Frame", 71, VirtualItemFrame.class, VirtualItemFrameImpl.class),
    DROPPED_ITEM(EntityType.OBJECT, Item.class, "Dropped Item", 32, VirtualDroppedItem.class, VirtualDroppedItemImpl.class),

    PLAYER(EntityType.PLAYER, Player.class, "Player", 91, VirtualPlayer.class, VirtualPlayerImpl.class);

    private final EntityType type;
    private Class<?> bukkitEntity;
    private final String name;
    private final int id;
    private final Class<? extends VirtualEntity> interfaceClass, entityClass;

    VirtualEnumEntities(EntityType type, Class<?> bukkitEntity, String name, int id, Class<? extends VirtualEntity> interfaceClass, Class<? extends VirtualEntityImpl> entityClass) {
        this.type = type;
        this.bukkitEntity = bukkitEntity;
        this.name = name;
        this.id = id;
        this.interfaceClass = interfaceClass;
        this.entityClass = entityClass;
    }

    public EntityType getType() {
        return type;
    }

    public Class<?> getBukkitEntityClass() {
        return bukkitEntity;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Class<? extends VirtualEntity> getEntityClass() {
        return entityClass;
    }

    public static VirtualEnumEntities of(Class<? extends VirtualEntity> vEntityInterfaceClass){
        for (VirtualEnumEntities entry : VirtualEnumEntities.values()) {
            if(entry.interfaceClass.getName().equals(vEntityInterfaceClass.getName())) return entry;
        }
        return null;
    }

    public static VirtualEnumEntities of(VirtualEntity vEntity){
        for (VirtualEnumEntities entry : VirtualEnumEntities.values()) {
            if(entry.entityClass.getName().equals(vEntity.getClass().getName())) return entry;
        }
        return null;
    }

    public static VirtualEnumEntities of(Entity entity){
        for (VirtualEnumEntities entry : VirtualEnumEntities.values()) {
            if(entry.bukkitEntity.isAssignableFrom(entity.getClass())) return entry;
        }
        return null;
    }

    public static enum EntityType {
        ENTITY, PLAYER, OBJECT, PAINTING, EXPERIENCE_ORB
    }
}