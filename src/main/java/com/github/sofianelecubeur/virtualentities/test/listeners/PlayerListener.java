package com.github.sofianelecubeur.virtualentities.test.listeners;

import com.github.sofianelecubeur.virtualentities.VirtualEntities;
import com.github.sofianelecubeur.virtualentities.api.VirtualEnumEquipment;
import com.github.sofianelecubeur.virtualentities.api.VirtualWorld;
import com.github.sofianelecubeur.virtualentities.api.entities.VirtualDroppedItem;
import com.github.sofianelecubeur.virtualentities.api.entities.hanging.VirtualItemFrame;
import com.github.sofianelecubeur.virtualentities.api.entities.living.VirtualArmorStand;
import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.VirtualEnderman;
import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.VirtualGuardian;
import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.VirtualSlime;
import com.github.sofianelecubeur.virtualentities.api.entities.living.monster.VirtualZombie;
import com.github.sofianelecubeur.virtualentities.api.entities.living.player.VirtualPlayer;
import com.github.sofianelecubeur.virtualentities.nms.event.PlayerInteractVirtualEntityEvent;
import com.github.sofianelecubeur.virtualentities.test.ArrayImage;
import com.github.sofianelecubeur.virtualentities.test.MapSender;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public class PlayerListener implements Listener {

    private VirtualEntities main;

    private VirtualArmorStand armorStand;
    private VirtualSlime slime;
    private VirtualDroppedItem item;
    private VirtualEnderman enderman;
    private ArrayImage map;
    private BukkitTask task;
    private VirtualZombie zombie;
    private VirtualGuardian guardian;

    public PlayerListener(VirtualEntities main) {
        this.main = main;
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                BufferedImage image = ImageIO.read(new File("map_image.png"));
                map = new ArrayImage(image.getSubimage(0, 0, 128, 128));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Location loc = player.getLocation();
        VirtualWorld world = main.getApi().getWorld(player.getWorld());
        world.addViewer(player);

        slime = world.spawn(VirtualSlime.class, loc.clone());
        slime.setInvisible(true);
        slime.setSize(1);
        slime.startRiding(world.getEntityMirror(player));

        VirtualArmorStand as = world.spawn(VirtualArmorStand.class, loc.clone());
        as.setInvisible(true);
        as.setMarker(true);
        as.setCustomName("Cet homme est un §cgénie");
        as.setCustomNameVisible(true);
        as.startRiding(slime);

        VirtualSlime slime = world.spawn(VirtualSlime.class, loc.clone());
        slime.setInvisible(true);
        slime.setSize(1);
        slime.startRiding(as);

        as = world.spawn(VirtualArmorStand.class, loc.clone());
        as.setInvisible(true);
        as.setMarker(true);
        as.setCustomName(ChatColor.YELLOW + "Sofiman" + ChatColor.AQUA + "#2059");
        as.setCustomNameVisible(true);
        as.startRiding(slime);

        VirtualPlayer vp = world.spawn(VirtualPlayer.class, loc.clone());

        //enderman = world.spawn(VirtualEnderman.class, loc.clone());

        /*task = Bukkit.getScheduler().runTaskTimer(main, () -> armorStand.teleport(player.getLocation().clone().add(player.getLocation().getDirection().multiply(1.25D))),
                1, 1);

        item = world.spawnObject(VirtualDroppedItem.class, 0, loc);
        item.setItem(new ItemStack(Material.BREAD));

        zombie = world.spawn(VirtualZombie.class, loc.clone().add(player.getLocation().getDirection().multiply(4.25D)));
        zombie.setEquipment(VirtualEnumEquipment.HELMET, new ItemStack(Material.IRON_HELMET));
        zombie.setBaby(true);

        guardian = world.spawn(VirtualGuardian.class, loc.clone().add(0, 1.85D, 0));
        guardian.setInvisible(true);
        guardian.setLaserTarget(slime);

        int mapId = 1050;
        MapSender.sendMap(mapId, map, Collections.singleton(player));

        Location loc2 = loc.clone();
        loc2.setPitch(0);
        loc2.setYaw(0);
        VirtualItemFrame itemFrame = world.spawnObject(VirtualItemFrame.class, VirtualItemFrame.Facing.fromBlockFace(yawToFace(loc)).toObjectData(), loc.clone().add(0, 1, 0));
        itemFrame.setItem(new ItemStack(Material.MAP, 1, (short) mapId));*/
    }

    public BlockFace yawToFace(Location loc) {
        BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        return axis[Math.round(loc.getYaw() / 90f) & 0x3].getOppositeFace();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (armorStand != null) {
            armorStand.remove();
        }
        if (task != null) task.cancel();
        if (enderman != null) {
            enderman.remove();
        }
        if(zombie != null){
            zombie.remove();
        }
        if(guardian != null){
            guardian.remove();
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking() && item != null) {
            item.collect(e.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractVirtualEntityEvent e) {
        if (e.getUsedEntity().equals(enderman)) {
            enderman.setScreaming(!enderman.isScreaming(), e.getUsedEntity().getWorld().getEntityMirror(e.getPlayer()));
        }
    }

}