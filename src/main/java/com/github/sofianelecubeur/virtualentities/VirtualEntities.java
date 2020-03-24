package com.github.sofianelecubeur.virtualentities;

import com.github.sofianelecubeur.virtualentities.test.listeners.PlayerListener;
import com.github.sofianelecubeur.virtualentities.nms.PacketManager;
import com.github.sofianelecubeur.virtualentities.nms.TinyProtocol;
import com.github.sofianelecubeur.virtualentities.nms.impl.VirtualApi;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Sofiane on 31/12/2017.
 * Tested versions: 1.8 (1.8.8), 1.9, 1.11 (1.11.2), 1.12 (1.12.2)
 *
 * @author Sofiane
 */
public class VirtualEntities extends JavaPlugin {

    private TinyProtocol protocol;
    private VirtualApi vApi;

    public static VirtualEntities getInstance(){
        return getPlugin(VirtualEntities.class);
    }

    @Override
    public void onEnable() {
        this.protocol = new PacketManager(this);
        this.vApi = new VirtualApi(protocol);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        this.vApi.destroy();
    }

    public VirtualApi getApi() {
        return vApi;
    }

    public TinyProtocol getProtocol() {
        return protocol;
    }
}