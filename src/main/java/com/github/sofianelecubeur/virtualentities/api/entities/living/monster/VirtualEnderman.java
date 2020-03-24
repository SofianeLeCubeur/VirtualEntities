package com.github.sofianelecubeur.virtualentities.api.entities.living.monster;

import com.github.sofianelecubeur.virtualentities.api.VirtualEntity;
import com.github.sofianelecubeur.virtualentities.api.entities.living.VirtualEntityMonster;
import org.bukkit.block.Block;

/**
 * Created by Sofiane on 28/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualEnderman extends VirtualEntityMonster {

    Block getCarriedBlock();
    void setCarriedBlock(Block carriedBlock);
    boolean isScreaming();
    void setScreaming(boolean screaming, VirtualEntity target);
}