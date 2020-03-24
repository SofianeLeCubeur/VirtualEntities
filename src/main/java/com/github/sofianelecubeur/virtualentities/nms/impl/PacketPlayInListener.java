package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.VirtualWorld;
import com.github.sofianelecubeur.virtualentities.nms.PacketManager;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

/**
 * Created by Sofiane on 28/07/2018.
 *
 * @author Sofiane
 */
public class PacketPlayInListener implements PacketManager.PacketListener {

    private static final Class<?> packetUseEntityClass = Reflection.getMinecraftClass("PacketPlayInUseEntity");
    private static final Class<?> enumEntityUseClass = Reflection.getMinecraftClass("PacketPlayInUseEntity$EnumEntityUseAction");
    private static final Reflection.FieldAccessor<Integer> entityIdField = Reflection.getField(packetUseEntityClass, "a", int.class);

    private final VirtualWorldImpl vworld;

    public PacketPlayInListener(VirtualWorldImpl vworld) {
        this.vworld = vworld;
    }

    @Override
    public boolean onPacketInAsync(Player sender, Object packet) {
        if(sender == null) return false;
        if(packet != null && packet.getClass().isAssignableFrom(packetUseEntityClass)){
            int entityId = entityIdField.get(packet);
            Object action = Reflection.getField(packetUseEntityClass, "action", enumEntityUseClass).get(packet);
            // Can be INTERACT, ATTACK or INTERACT_AT
            vworld.notifyEntityUsed(sender, entityId, action.toString());
        }
        return false;
    }

    @Override
    public boolean onPacketOutAsync(Player receiver, Object packet) {
        return false;
    }
}