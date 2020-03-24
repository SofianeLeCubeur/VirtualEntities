package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.VirtualDroppedItem;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import com.github.sofianelecubeur.virtualentities.nms.impl.VirtualEntityObjectImpl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public class VirtualDroppedItemImpl extends VirtualEntityObjectImpl implements VirtualDroppedItem {

    private static final Class<?> entityItemClass = Reflection.getMinecraftClass("EntityItem");
    private static final Reflection.MethodInvoker setItemStackMethod = Reflection.getMethod(entityItemClass, "setItemStack", itemStackClass);

    public VirtualDroppedItemImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = createEntity("EntityItem");
        super.entityInit();
    }

    @Override
    public ItemStack getItem() {
        return (ItemStack) asBukkitCopyMethod.invoke(null, this.invokeGetter("getItemStack"));
    }

    @Override
    public void setItem(ItemStack item) {
        setItemStackMethod.invoke(this.handle, asNMSCopyMethod.invoke(null, item));
        this.vworld.notifyDataUpdated(this);
    }

    @Override
    public void collect(Player player) {
        this.vworld.notifyItemCollected(this, player);
    }
}