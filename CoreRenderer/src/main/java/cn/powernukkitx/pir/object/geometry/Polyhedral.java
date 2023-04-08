package cn.powernukkitx.pir.object.geometry;

import cn.powernukkitx.pir.object.ObjectType;
import cn.powernukkitx.pir.object.SceneObject;
import org.jetbrains.annotations.NotNull;

/**
 * A polyhedral is a shape that can be divided into triangles.
 */
public interface Polyhedral extends SceneObject {
    /**
     * @return The triangles of this polyhedral.
     */
    Triangle[] triangles();

    @Override
    @NotNull default ObjectType type() {
        return ObjectType.GEOMETRY;
    }
}
