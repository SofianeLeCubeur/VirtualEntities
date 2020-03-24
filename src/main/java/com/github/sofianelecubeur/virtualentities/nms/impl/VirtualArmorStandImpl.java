package com.github.sofianelecubeur.virtualentities.nms.impl;

import com.github.sofianelecubeur.virtualentities.api.entities.living.VirtualArmorStand;
import com.github.sofianelecubeur.virtualentities.nms.Reflection;
import org.bukkit.util.EulerAngle;


/**
 * Created by Sofiane on 22/07/2018.
 *
 * @author Sofiane
 */
public class VirtualArmorStandImpl extends VirtualEntityLivingImpl implements VirtualArmorStand {

    private static final Class<?> entityArmorStandClass = Reflection.getMinecraftClass("EntityArmorStand");
    private static final Class<?> vector3fClass = Reflection.getMinecraftClass("Vector3f");
    private static final Reflection.MethodInvoker setSmallMethod = Reflection.getMethod(entityArmorStandClass, "setSmall", Boolean.TYPE);
    private static final Reflection.MethodInvoker setHeadPoseMethod = Reflection.getMethod(entityArmorStandClass, "setHeadPose", vector3fClass);
    private static final Reflection.MethodInvoker setArmsMethod = Reflection.getMethod(entityArmorStandClass, "setArms", Boolean.TYPE);
    private static final Reflection.MethodInvoker setBasePlateMethod = Reflection.getMethod(entityArmorStandClass, "setBasePlate", Boolean.TYPE);
    private static Reflection.MethodInvoker setGravityMethod, setMarkerMethod;
    private static String getMarkerMethod = "isMarker";
    private static final Reflection.ConstructorInvoker vector3fConstructor = Reflection.getConstructor(vector3fClass, Float.TYPE, Float.TYPE, Float.TYPE);
    private static final Reflection.FieldAccessor<?> headPoseField = Reflection.getField(entityArmorStandClass, "headPose", vector3fClass);

    public VirtualArmorStandImpl(VirtualWorldImpl vworld) {
        super(vworld);
    }

    @Override
    protected void entityInit() {
        this.handle = createEntity("EntityArmorStand");
        super.entityInit();

        try {
            setGravityMethod = Reflection.getMethod(entityArmorStandClass, "setGravity", Boolean.TYPE);
        } catch (Exception e) {
            System.err.println(this.buildExceptionMessage("setGravity", "ArmorStand", e));
        }
        try {
            setMarkerMethod = Reflection.getMethod(entityArmorStandClass, "setMarker", boolean.class);
        } catch (Exception ignored) {
           try {
               setMarkerMethod = Reflection.getMethod(entityArmorStandClass, "n", boolean.class);
               getMarkerMethod = null;
           } catch (Exception e) {
               System.err.println(this.buildExceptionMessage("setMarker", "ArmorStand", null));
           }
        }
    }

    @Override
    public boolean isSmall() {
        return (boolean) invokeGetter("isSmall");
    }

    @Override
    public void setSmall(boolean small) {
        setSmallMethod.invoke(this.handle, small);
        this.vworld.notifyDataUpdated(this);
    }

    @Override
    public EulerAngle getHeadPose() {
        Object vector3f = headPoseField.get(this.handle);
        return toEulerAngle(vector3f);
    }

    @Override
    public void setHeadPose(EulerAngle headPose) {
        EulerAngle angle = convert(headPose);
        setHeadPoseMethod.invoke(this.handle, vector3fConstructor.invoke((float) angle.getX(), (float) angle.getY(), (float) angle.getZ()));
        this.vworld.notifyDataUpdated(this);
    }

    @Override
    public boolean hasArms() {
        return (boolean) invokeGetter("hasArms");
    }

    @Override
    public void setArms(boolean arms) {
        setArmsMethod.invoke(this.handle, arms);
        this.vworld.notifyDataUpdated(this);
    }

    @Override
    public boolean hasGravity() {
        return (boolean) invokeGetter("hasGravity");
    }

    @Override
    public void setGravity(boolean gravity) {
        if(setGravityMethod != null) {
            setGravityMethod.invoke(this.handle, gravity);
            this.vworld.notifyDataUpdated(this);
        }
    }

    @Override
    public boolean hasBasePlate() {
        return (boolean) invokeGetter("hasBasePlate");
    }

    @Override
    public void setBasePlate(boolean showBasePlate) {
        setBasePlateMethod.invoke(this.handle, showBasePlate);
        this.vworld.notifyDataUpdated(this);
    }

    @Override
    public boolean hasMarker() { // s() => 1.8 | hasMarker() => 1.9
        if(getMarkerMethod == null){
            Reflection.MethodInvoker sMethod = Reflection.getTypedMethod(entityArmorStandClass, "s", boolean.class);
            return (boolean) sMethod.invoke(this.handle);
        } else {
            return (boolean) invokeGetter(getMarkerMethod);
        }
    }

    @Override
    public void setMarker(boolean marker) { // n(bool?=Marker) => 1.8 | setMarker(bool?=Marker) => 1.9
        if(setMarkerMethod != null){
            setMarkerMethod.invoke(this.handle, marker);
            this.vworld.notifyDataUpdated(this);
        } else throw new UnsupportedOperationException("setMarker method is not available");
    }

    private EulerAngle convert(EulerAngle old) {
        return new EulerAngle((float) Math.toDegrees(old.getX()), (float) Math.toDegrees(old.getY()), (float) Math.toDegrees(old.getZ()));
    }

    private EulerAngle toEulerAngle(Object vector3f){
        return new EulerAngle((float) invokeGetter(vector3f, "getX"), (float) invokeGetter(vector3f, "getY"), (float) invokeGetter(vector3f, "getZ"));
    }
}