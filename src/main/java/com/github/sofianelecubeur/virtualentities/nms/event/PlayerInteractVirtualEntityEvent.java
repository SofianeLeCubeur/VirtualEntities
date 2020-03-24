package com.github.sofianelecubeur.virtualentities.nms.event;

import com.github.sofianelecubeur.virtualentities.api.VirtualEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by Sofiane on 28/07/2018.
 *
 * @author Sofiane
 */
public class PlayerInteractVirtualEntityEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final VirtualEntity usedEntity;
    private final EntityUseType useType;

    public PlayerInteractVirtualEntityEvent(Player who, VirtualEntity usedEntity, EntityUseType useType) {
        super(who);
        this.usedEntity = usedEntity;
        this.useType = useType;
    }

    public VirtualEntity getUsedEntity() {
        return usedEntity;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public EntityUseType getUseType() {
        return useType;
    }

    public enum EntityUseType { INTERACT, INTERACT_AT, ATTACK }

}