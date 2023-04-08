package cn.powernukkitx.pir.object.geometry;

import cn.powernukkitx.pir.object.ObjectType;
import cn.powernukkitx.pir.object.SceneObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public interface Plane extends SceneObject {
    /**
     * @return A matrix that presents the plane of the triangle using equation Ax + By + Cz + D = 0
     */
    @Contract("-> new") @NotNull default Vector4f planeVector() {
        return planeVector(new Vector4f());
    }

    /**
     * @return A matrix that presents the plane of the triangle using equation Ax + By + Cz + D = 0
     */
    @Contract("!null -> param1") @NotNull Vector4f planeVector(Vector4f ref);

    @Override
    @NotNull default ObjectType type() {
        return ObjectType.GEOMETRY;
    }
}
