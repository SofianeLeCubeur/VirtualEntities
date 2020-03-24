package com.github.sofianelecubeur.virtualentities.api.entities.living;

import com.github.sofianelecubeur.virtualentities.api.entities.VirtualEntityLiving;
import org.bukkit.util.EulerAngle;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public interface VirtualArmorStand extends VirtualEntityLiving {

    boolean isSmall();
    void setSmall(boolean small);
    EulerAngle getHeadPose();
    void setHeadPose(EulerAngle headPose);
    boolean hasArms();
    void setArms(boolean arms);
    boolean hasGravity();
    void setGravity(boolean gravity);
    boolean hasBasePlate();
    void setBasePlate(boolean showBasePlate);
    boolean hasMarker();
    void setMarker(boolean marker);

}