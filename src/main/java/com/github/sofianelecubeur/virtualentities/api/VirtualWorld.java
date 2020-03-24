package com.github.sofianelecubeur.virtualentities.api;

import com.github.sofianelecubeur.virtualentities.api.entities.VirtualEntityObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualWorld {

    String getName();
    void addViewer(Player player);
    void removeViewer(Player player);
    boolean hasViewer(Player viewer);
    Collection<Player> getViewers();

    <T extends VirtualEntity> T spawn(Class<T> virtualEntityInterfaceClass, Location location);
    <T extends VirtualEntityObject> T spawnObject(Class<T> virtualEntityInterfaceClass, int objectData, Location location);
    <T extends VirtualEntity> T getEntityMirror(Entity entity);
    void despawn(VirtualEntity virtualEntity);
    World getRealReferer();

    void destroy();

}