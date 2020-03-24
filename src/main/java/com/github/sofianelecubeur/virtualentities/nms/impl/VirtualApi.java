package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.VirtualEntity;
import com.github.sofianelecubeur.virtualentities.api.VirtualWorld;
import com.github.sofianelecubeur.virtualentities.nms.PacketManager;
import com.github.sofianelecubeur.virtualentities.nms.TinyProtocol;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public class VirtualApi {

    protected TinyProtocol protocol;
    private Set<VirtualWorld> worlds;

    public VirtualApi(TinyProtocol protocol) {
        this.worlds = new HashSet<>();
        this.protocol = protocol;
    }

    public VirtualWorld createVWorld(String name, World referer){
        if(referer == null) throw new NullPointerException("Referer world can't be null.");

        VirtualWorldImpl world = new VirtualWorldImpl(name, referer, this);
        this.worlds.add(world);
        ((PacketManager)this.protocol).addListener(new PacketPlayInListener(world));
        return world;
    }

    public VirtualWorld getWorld(World base){
        return this.worlds.stream().filter(vWorld -> vWorld.getRealReferer().equals(base)).findFirst().orElse(createVWorld(base.getName(), base));
    }

    public void destroy(){
        this.worlds.forEach(VirtualWorld::destroy);
    }

}