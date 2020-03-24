package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.VirtualEntityObject;

/**
 * Created by Sofiane on 23/07/2018.
 *
 * @author Sofiane
 */
public abstract class VirtualEntityObjectImpl extends VirtualEntityImpl implements VirtualEntityObject {

    public VirtualEntityObjectImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    void storeObjectData(int objectData){
    }

    int getObjectData(){
        return 0;
    }
}