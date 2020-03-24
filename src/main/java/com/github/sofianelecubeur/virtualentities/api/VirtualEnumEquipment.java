package com.github.sofianelecubeur.virtualentities.api;

import com.github.sofianelecubeur.virtualentities.nms.Reflection;

/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public enum VirtualEnumEquipment {
    HELD("mainhand", 0, 0),
    OFF_HAND("offhand", 0, 1),
    BOOTS("feet", 1, 2),
    LEGGINGS("legs", 2, 3),
    CHESTPLATE("chest", 3, 4),
    HELMET("head", 4, 5);

    private final String name;
    private final int slotVersion1_8;
    private final int slotVersion1_9;

    VirtualEnumEquipment(String name, int slotVersion1_8, int slotVersion1_9) {
        this.name = name;
        this.slotVersion1_8 = slotVersion1_8;
        this.slotVersion1_9 = slotVersion1_9;
    }

    public int getSlotId1_8() {
        return slotVersion1_8;
    }

    public int getSlotId1_9() {
        return slotVersion1_9;
    }

    public int getSlotId(){
        return Reflection.getServerProtocolVersion().startsWith("1.8") ? this.slotVersion1_8 : this.slotVersion1_9;
    }

    public String getName() {
        return name;
    }
}