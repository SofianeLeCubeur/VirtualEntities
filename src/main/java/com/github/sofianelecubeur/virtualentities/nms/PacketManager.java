package com.github.sofianelecubeur.virtualentities.nms;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public class PacketManager extends TinyProtocol {

    private List<PacketListener> listeners;

    public PacketManager(Plugin plugin) {
        super(plugin);
        this.listeners = new ArrayList<>();
    }

    public void addListener(PacketListener listener){
        listeners.add(listener);
    }

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        boolean cancelled = false;
        for (PacketListener listener : listeners) {
            if(cancelled) break;
            cancelled = listener.onPacketInAsync(sender, packet);
        }
        return cancelled ? null : super.onPacketInAsync(sender, channel, packet);
    }

    @Override
    public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
        boolean cancelled = false;
        for (PacketListener listener : listeners) {
            if(cancelled) break;
            cancelled = listener.onPacketOutAsync(receiver, packet);
        }
        return cancelled ? null : super.onPacketOutAsync(receiver, channel, packet);
    }

    public interface PacketListener {
        boolean onPacketInAsync(Player sender, Object packet);
        boolean onPacketOutAsync(Player receiver, Object packet);
    }
}