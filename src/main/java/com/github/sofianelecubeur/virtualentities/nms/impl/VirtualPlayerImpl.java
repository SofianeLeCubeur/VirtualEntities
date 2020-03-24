package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.VirtualEntities;
import com.github.sofianelecubeur.virtualentities.api.entities.living.player.VirtualPlayer;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import com.github.sofianelecubeur.virtualentities.test.Skin;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.List;
import java.util.UUID;

public class VirtualPlayerImpl extends VirtualEntityLivingImpl implements VirtualPlayer {

    private final Class<?> packetSpawnNamedEntityClass = Reflection.getMinecraftClass("PacketPlayOutNamedEntitySpawn");
    private final Class<?> packetPlayerInfoClass = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo");
    private final Class<?> enumPlayerInfoClass = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
    private final Class<?> entityPlayerClass = Reflection.getMinecraftClass("EntityPlayer");
    private final Class<?> playerInteractManagerClass = Reflection.getMinecraftClass("PlayerInteractManager");
    private final Reflection.MethodInvoker setSneakingMethod = Reflection.getMethod(entityClass, "setSneaking", boolean.class);
    public static final Reflection.MethodInvoker getHandleWorldMethod = Reflection.getMethod("{obc}.CraftWorld", "getHandle", World.class);
    private final Reflection.MethodInvoker setLocationMethod = Reflection.getMethod(entityClass, "setLocation", Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE);
    private final Reflection.MethodInvoker valueOfMethod = Reflection.getMethod(enumPlayerInfoClass, "valueOf", String.class);
    private final Reflection.ConstructorInvoker packetPlayerInfoConstructor = Reflection.getConstructor(packetPlayerInfoClass,
            enumPlayerInfoClass, Reflection.getArrayClass("{nms}.EntityPlayer", 1));
    private final Reflection.ConstructorInvoker packetSpawnNamedEntityConstructor = Reflection.getConstructor(packetSpawnNamedEntityClass,
            Reflection.getMinecraftClass("EntityHuman"));
    private Reflection.ConstructorInvoker entityPlayerConstructor = Reflection.getConstructor(entityPlayerClass,
            Reflection.getMinecraftClass("MinecraftServer"), Reflection.getMinecraftClass("WorldServer"), GameProfile.class,
            Reflection.getMinecraftClass("PlayerInteractManager"));
    private final Reflection.ConstructorInvoker playerInteractManagerConstructor = Reflection.getConstructor(playerInteractManagerClass,
            Reflection.getMinecraftClass("World"));

    private String name;
    private GameProfile gameProfile;

    public VirtualPlayerImpl(VirtualWorldImpl vworld) {
        super(vworld);
        this.gameProfile = Skin.BOT.get();
    }

    public VirtualPlayerImpl(VirtualWorldImpl vworld, GameProfile gp) {
        this(vworld);
        this.gameProfile = gp;
    }

    @Override
    protected void entityInit() {
        System.out.println("Is real: " + getRealReferer() != null);
        if(getRealReferer() == null){
            Reflection.MethodInvoker nmsServerMethod = Reflection.getMethod(Bukkit.getServer().getClass(), "getServer");
            Object nmsServer = nmsServerMethod.invoke(Bukkit.getServer());

            Object nmsWorld = getHandleWorldMethod.invoke(this.location.getWorld());
            Object playerInterractManager = this.playerInteractManagerConstructor.invoke(nmsWorld);

            this.handle = entityPlayerConstructor.invoke(nmsServer, nmsWorld, this.gameProfile, playerInterractManager);
            setLocationMethod.invoke(this.handle, this.location.getX(), this.location.getY(), this.location.getZ(), this.location.getYaw(), this.location.getPitch());
            super.entityInit();

            Object[] array = (Object[]) Array.newInstance(Reflection.getMinecraftClass("EntityPlayer"), 1);
            array[0] = this.handle;

            Object enumPlayerInfoAdd = valueOfMethod.invoke(enumPlayerInfoClass, "ADD_PLAYER");
            System.out.println(Reflection.getMethod(enumPlayerInfoClass, "name").invoke(enumPlayerInfoAdd));
            Object packetPlayerInfo = packetPlayerInfoConstructor.invoke(enumPlayerInfoAdd, array);
            Object packetSpawn = packetSpawnNamedEntityConstructor.invoke(this.handle);

            this.vworld.notifySendPacket(packetPlayerInfo);
            Bukkit.getScheduler().runTaskLaterAsynchronously(VirtualEntities.getInstance(), () -> this.vworld.notifySendPacket(packetSpawn), 10);
        }
    }

    @Override
    protected void importParams(Entity referer) {
        super.importParams(referer);
        this.name = referer.getName();
        this.gameProfile = (GameProfile) invokeGetter("getProfile");
    }

    public String getName() {
        return name;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    @Override
    public void setSleepingAt(Location bedLocation) {

    }

    @Override
    public void setNameTagVisibility(boolean isVisible) {

    }

    @Override
    public void setDisplayName(String displayName) {

    }

    @Override
    public void setTablistVisibility(boolean isVisible) {
        Object[] array = (Object[]) Array.newInstance(Reflection.getMinecraftClass("EntityPlayer"), 1);
        array[0] = this.handle;

        Object packetPlayerInfo;
        if(isVisible){
            Object enumPlayerInfoAdd = valueOfMethod.invoke(enumPlayerInfoClass, "ADD_PLAYER");
            packetPlayerInfo = packetPlayerInfoConstructor.invoke(enumPlayerInfoAdd, array);
        } else {
            Object enumPlayerInfoRemove = valueOfMethod.invoke(enumPlayerInfoClass, "REMOVE_PLAYER");
            packetPlayerInfo = packetPlayerInfoConstructor.invoke(enumPlayerInfoRemove, array);
        }

        this.vworld.notifySendPacket(packetPlayerInfo);
    }

    @Override
    public void setSneaking(boolean isSneaking) {
        setSneakingMethod.invoke(this.handle, isSneaking);
        this.vworld.notifyDataUpdated(this);
    }

    @Override
    public boolean isSneaking() {
        return (boolean) invokeGetter("isSneaking");
    }

    @Override
    public int getPing() {
        Reflection.FieldAccessor<Integer> ping = Reflection.getField(entityPlayerClass, "ping", Integer.TYPE);
        return ping.get(this.handle);
    }

    @Override
    public String getDisplayName() {
        return getRealReferer() == null ? (String) invokeGetter("getName") : ((Player) getRealReferer()).getDisplayName();
    }

    @Override
    public void setVirtualGameMode(GameMode gameMode) {
        try {
            Class<?> packetPlayerInfoClass = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo");
            Object playerInfoPacket = packetPlayerInfoClass.newInstance();

            Class<?> enumPlayerInfoActionClass = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            Reflection.FieldAccessor<Object> aField = Reflection.getField(packetPlayerInfoClass, "a", Object.class);
            aField.set(playerInfoPacket, enumPlayerInfoActionClass.getEnumConstants()[1]);

            Reflection.FieldAccessor<Object> bField = Reflection.getField(packetPlayerInfoClass, "b", Object.class);
            List<Object> dataList = (List<Object>) bField.get(playerInfoPacket);
            dataList.add(buildData(gameMode));
            this.vworld.notifySendPacket(playerInfoPacket);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private Object buildData(GameMode mode){
        try {
            Class<?> enumGamemode = Reflection.getMinecraftClass("WorldSettings$EnumGamemode");
            Reflection.MethodInvoker getByIdMethod = Reflection.getMethod(enumGamemode, "getById", Integer.TYPE);
            Object gMode = getByIdMethod.invoke(null, mode.ordinal());

            Class<?> gameProfileClass = Reflection.getClass("com.mojang.authlib.GameProfile");
            Class<?> packetPlayerInfoClass = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo");
            Class<?> playerInfoDataClass = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo$PlayerInfoData");
            Reflection.ConstructorInvoker playerInfoDataConstructor =
                    Reflection.getConstructor(playerInfoDataClass, packetPlayerInfoClass, gameProfileClass, Integer.TYPE, enumGamemode, Reflection.getMinecraftClass("IChatBaseComponent"));
            return playerInfoDataConstructor.invoke(null, gameProfile, getPing(), gMode, serializeChat(String.format("{\"text\":\"%s\"}", getDisplayName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GameProfile clone(GameProfile original){
        GameProfile gp = new GameProfile(UUID.randomUUID(), original.getName());
        gp.getProperties().putAll(original.getProperties());
        return gp;
    }

    public static Object serializeChat(String msg) {
        try {
            Class<?> chatSerializerClass = Reflection.getMinecraftClass("IChatBaseComponent$ChatSerializer");
            return chatSerializerClass.getDeclaredMethod("a", String.class).invoke(null, msg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
