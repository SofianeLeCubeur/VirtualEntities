package com.github.sofianelecubeur.virtualentities.api.entities.hanging;

import com.github.sofianelecubeur.virtualentities.api.entities.VirtualEntityObject;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualItemFrame extends VirtualEntityObject {

    int getRotation();
    void setRotation(int rotation);
    ItemStack getItem();
    void setItem(ItemStack item);

    public static enum Facing {
        SOUTH, WEST, NORTH, EAST;

        public int toObjectData(){
            return this.ordinal();
        }

        public static Facing fromBlockFace(BlockFace blockFace){
            final String faceName = blockFace.name().toUpperCase();
            if(faceName.startsWith("SOUTH")) {
                return SOUTH;
            } else if(faceName.startsWith("WEST")) {
                return WEST;
            } else if(faceName.startsWith("NORTH")) {
                return NORTH;
            } else if(faceName.startsWith("EAST")) {
                return EAST;
            } else return null;
        }
        // Facing: 0 = South | 1 = West | 2 = North | 3 = East
    }

}