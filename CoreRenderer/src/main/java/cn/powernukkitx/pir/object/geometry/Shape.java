package cn.powernukkitx.pir.object.geometry;

import cn.powernukkitx.pir.object.Ray;
import cn.powernukkitx.pir.object.SceneObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A shape is a primitive 3D object that cannot be divided into multi parts and can be intersected by a ray.
 */
public interface Shape extends SceneObject {
    boolean contains(@NotNull Vector3f pos);

    /**
     * find the intersection pos with given ray
     *
     * @param pos       the result pos
     * @param direction the direction of the ray
     * @return the position of the intersection, nan if no intersection
     */
    @NotNull
    Vector3f intersects(@NotNull Vector3f pos, @NotNull Vector3f direction, @NotNull Vector3f ref);

    /**
     * find the intersection pos with given ray
     *
     * @param ray the ray
     * @return the position of the intersection, nan if no intersection
     */
    default @NotNull Vector3f intersects(@NotNull Ray ray, @NotNull Vector3f ref) {
        return intersects(ray.pos(), ray.direction(), ref);
    }

    /**
     * find the intersection pos with given ray
     *
     * @param ray the ray
     * @return the position of the intersection, nan if no intersection
     */
    default @NotNull Vector3f intersects(@NotNull Ray ray) {
        return intersects(ray.pos(), ray.direction(), new Vector3f());
    }

    /**
     * calculate the normal vector
     *
     * @return P<sub>1</sub>->P<sub>2</sub> × P<sub>1</sub>->P<sub>3</sub>
     */
    Vector3f normalVector(Vector3f ref);

    /**
     * calculate the normal vector
     *
     * @return P<sub>1</sub>->P<sub>2</sub> × P<sub>1</sub>->P<sub>3</sub>
     */
    default Vector3f normalVector() {
        return normalVector(new Vector3f());
    }

    boolean hasTexture();

    /**
     * get the color on the shape at given pos
     *
     * @param pos the pos
     * @return the color
     */
    @NotNull Vector4f getTextureColor(@NotNull Vector3f pos, @NotNull Vector4f ref);

    /**
     * get the color on the shape at given pos
     *
     * @param pos the pos
     * @return the color
     */
    default @NotNull Vector4f getTextureColor(@NotNull Vector3f pos) {
        return getTextureColor(pos, new Vector4f());
    }
}
