package com.github.sofianelecubeur.virtualentities.api.entities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualDroppedItem extends VirtualEntityObject {

    ItemStack getItem();
    void setItem(ItemStack item);

    /**
     * Warning: This method removes the entity client-side
     * @param player the collector
     */
    void collect(Player player);
}