package com.github.sofianelecubeur.virtualentities.api.entities.living.monster;

import com.github.sofianelecubeur.virtualentities.api.VirtualEntity;
import com.github.sofianelecubeur.virtualentities.api.entities.living.VirtualEntityMonster;
import org.bukkit.entity.Entity;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualGuardian extends VirtualEntityMonster {

    boolean isElder();
    void setElder(boolean elder);
    void setLaserTarget(Entity ent);
    void setLaserTarget(VirtualEntity ent);
    boolean hasLaser();
    boolean isSpikesRetracted();
    void setRetractingSpikes(boolean retractingSpikes);

}