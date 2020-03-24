package com.github.sofianelecubeur.virtualentities.api;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualEntity {

    // Properties
    UUID getUniqueId();
    boolean isDead();
    boolean onGround();
    int getAirTicks();
    void setAirTicks(int air);
    String getCustomName();
    void setCustomName(String customName);
    boolean isCustomNameVisible();
    void setCustomNameVisible(boolean visible);
    boolean isInvisible();
    void setInvisible(boolean invisible);
    VirtualWorld getWorld();
    Location getLocation();
    void teleport(Location location);
    void setVelocity(Vector velocity);
    void startRiding(VirtualEntity otherEntity);

    int getId();
    void remove();
}