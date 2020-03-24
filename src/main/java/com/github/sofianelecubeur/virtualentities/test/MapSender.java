package com.github.sofianelecubeur.virtualentities.test;

import com.github.sofianelecubeur.virtualentities.VirtualEntities;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class MapSender {

	private static Class<?> nmsPacketPlayOutMap = Reflection.getMinecraftClass("PacketPlayOutMap");

	public static void sendMap(int id, ArrayImage data, Collection<Player> viewers){
        VirtualEntities.getInstance().getProtocol().sendPacket(viewers, constructPacket(id, data));
    }

	public static Object constructPacket(int id, ArrayImage data) {
		Object packet = null;

		if (Reflection.getServerProtocolVersion().startsWith("1.8")) {
			try {
				packet = constructPacket_1_8(id, data);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		} else  {
			try {
				packet = constructPacket_1_9(id, data);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}

		return packet;
	}

	private static Object constructPacket_1_8(int id, ArrayImage data) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
		Object packet = nmsPacketPlayOutMap//
				.getConstructor(int.class, byte.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class)//
				.newInstance(id,// ID
						(byte) 0,// Scale
						new ArrayList<>(),// Icons
						data.generatePacketData(),// Data
						data.minX,// X-position
						data.minY,// Y-position
						data.maxX,// X-Size (or 2nd X-position)
						data.maxY// Y-Size (or 2nd Y-position)
				);
		return packet;
	}

	private static Object constructPacket_1_9(int id, ArrayImage data) throws ReflectiveOperationException {
		Object packet = nmsPacketPlayOutMap//
				.getConstructor(int.class, byte.class, boolean.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class)//
				.newInstance(id,//ID
						(byte) 0,//Scale
						false,//????
						new ArrayList<>(),//Icons
						data.generatePacketData(),//Data
						data.minX,// X-position
						data.minY,// Y-position
						data.maxX,// X-Size (or 2nd X-position)
						data.maxY// Y-Size (or 2nd Y-position)
				);
		return packet;
	}
}