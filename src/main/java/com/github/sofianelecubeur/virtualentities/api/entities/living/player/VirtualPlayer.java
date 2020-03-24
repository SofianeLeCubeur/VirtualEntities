package com.github.sofianelecubeur.virtualentities.api.entities.living.player;

import com.github.sofianelecubeur.virtualentities.api.entities.VirtualEntityLiving;
import org.bukkit.GameMode;
import org.bukkit.Location;

public interface VirtualPlayer extends VirtualEntityLiving {

    void setSleepingAt(Location bedLocation);
    void setNameTagVisibility(boolean isVisible);
    void setDisplayName(String displayName);
    String getDisplayName();
    void setTablistVisibility(boolean isVisible);
    void setSneaking(boolean isSneaking);
    boolean isSneaking();
    void setVirtualGameMode(GameMode gameMode);
    int getPing();
}
