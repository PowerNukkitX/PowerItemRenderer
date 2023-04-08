package cn.powernukkitx.pir.functor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface TriangleFunctor extends ShapeFunctor {
    TriangleFunctor CURRENT_FUNCTOR = new JavaTriangleFunctor();

    static TriangleFunctor current() {
        return CURRENT_FUNCTOR;
    }

    /**
     * If the triangle contains the pos.
     *
     * @param pos pos
     * @return true if the triangle contains the pos
     */
    boolean contains(@NotNull Matrix3f triangle, @NotNull Vector3f pos);

    /**
     * If the triangle contains the pos.
     *
     * @param pos pos
     * @return for each element true if the triangle contains the pos, otherwise false
     */
    default boolean @NotNull [] contains(@NotNull Matrix3f @NotNull [] triangles, @NotNull Vector3f @NotNull [] pos) {
        boolean[] result = new boolean[triangles.length];
        for (int i = 0; i < triangles.length; i++) {
            result[i] = contains(triangles[i], pos[i]);
        }
        return result;
    }

    /**
     * @return A matrix that presents the plane of the triangle using equation Ax + By + Cz + D = 0
     */
    @Contract("!null, !null -> param2")
    @NotNull Vector4f planeVector(Matrix3f triangle, Vector4f ref);

    /**
     * @return A matrix that presents the plane of the triangle using equation Ax + By + Cz + D = 0
     */
    @Contract("_ -> new")
    @NotNull
    default Vector4f @NotNull [] planeVector(@NotNull Matrix3f @NotNull [] triangles) {
        Vector4f[] result = new Vector4f[triangles.length];
        for (int i = 0; i < triangles.length; i++) {
            result[i] = planeVector(triangles[i], new Vector4f());
        }
        return result;
    }

    /**
     * find the intersection pos with given ray
     *
     * @param triangle  the triangle
     * @param pos       the result pos
     * @param direction the direction of the ray
     * @return the position of the intersection, {@link cn.powernukkitx.pir.util.MathUtil#INVALID_VEC3F} if no intersection
     */
    @NotNull
    Vector3f intersects(@NotNull Matrix3f triangle, @NotNull Vector3f pos, @NotNull Vector3f direction, @NotNull Vector3f ref);

    /**
     * find the intersections poses with given rays
     *
     * @param pos       the result pos
     * @param direction the direction of the ray
     * @return each element is the position of the intersection, {@link cn.powernukkitx.pir.util.MathUtil#INVALID_VEC3F} element if no intersection
     */
    @NotNull
    default Vector3f @NotNull [] intersects(@NotNull Matrix3f @NotNull [] triangle,
                                            @NotNull Vector3f pos,
                                            @NotNull Vector3f direction) {
        var result = new Vector3f[triangle.length];
        for (int i = 0; i < triangle.length; i++) {
            result[i] = intersects(triangle[i], pos, direction, new Vector3f());
        }
        return result;
    }

    /**
     * find the intersections poses with given rays
     *
     * @param pos       the result pos
     * @param direction the direction of the ray
     * @param refBuffer results will be written into the elements of the buffer. Each element is the position of the intersection, {@link cn.powernukkitx.pir.util.MathUtil#INVALID_VEC3F} element if no intersection
     */
    default void intersects(@NotNull Matrix3f @NotNull [] triangle,
                            @NotNull Vector3f pos,
                            @NotNull Vector3f direction,
                            @NotNull Vector3f @NotNull [] refBuffer) {
        for (int i = 0; i < triangle.length; i++) {
            intersects(triangle[i], pos, direction, refBuffer[i]);
        }
    }

    /**
     * find the intersections poses with given rays
     *
     * @param pos       [rayIndex] the result pos
     * @param direction [rayIndex] the direction of the ray
     * @param refBuffer [rayIndex][triangleIndex] results will be written into the elements of the buffer. Each element is the position of the intersection, {@link cn.powernukkitx.pir.util.MathUtil#INVALID_VEC3F} element if no intersection
     */
    default void intersects(@NotNull Matrix3f @NotNull [] triangle,
                            @NotNull Vector3f @NotNull [] pos,
                            @NotNull Vector3f @NotNull [] direction,
                            @NotNull Vector3f @NotNull [] @NotNull[] refBuffer) {
        for (int i = 0, len1 = pos.length; i < len1; i++) {
            for (int j = 0, len2 = triangle.length; j < len2; j++) {
                intersects(triangle[j], pos[i], direction[i], refBuffer[i][j]);
            }
        }
    }

    /**
     * calculate the normal vector
     *
     * @return P<sub>1</sub>->P<sub>2</sub> × P<sub>1</sub>->P<sub>3</sub>
     */
    @NotNull
    Vector3f normalVector(@NotNull Matrix3f triangle, @NotNull Vector3f ref);

    /**
     * calculate the normal vector
     *
     * @return P<sub>1</sub>->P<sub>2</sub> × P<sub>1</sub>->P<sub>3</sub>
     */
    default @NotNull Vector3f @NotNull [] normalVector(@NotNull Matrix3f @NotNull [] triangle) {
        var result = new Vector3f[triangle.length];
        for (int i = 0; i < triangle.length; i++) {
            result[i] = normalVector(triangle[i], new Vector3f());
        }
        return result;
    }

    /**
     * get the UV for the texture on the shape at given pos
     *
     * @param triangle    the triangle
     * @param pos         the pos
     * @param textureSize the size of the texture
     * @return the UV for the texture on the shape at given pos
     */
    @NotNull Vector2i getTextureUV(@NotNull Matrix3f triangle, @NotNull Vector3f pos, @NotNull Vector2i textureSize, @NotNull Vector2i ref);

    /**
     * get the UV for the texture on the shape at given pos
     *
     * @param triangle    the triangle
     * @param pos         the pos
     * @param textureSize the size of the texture
     * @return the UV for the texture on the shape at given pos
     */
    default @NotNull Vector2i @NotNull [] getTextureUV(@NotNull Matrix3f @NotNull [] triangle,
                                                       @NotNull Vector3f @NotNull [] pos,
                                                       @NotNull Vector2i @NotNull [] textureSize) {
        var result = new Vector2i[pos.length];
        for (int i = 0; i < pos.length; i++) {
            result[i] = getTextureUV(triangle[i], pos[i], textureSize[i], new Vector2i());
        }
        return result;
    }
}
