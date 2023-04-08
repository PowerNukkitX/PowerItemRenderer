package cn.powernukkitx.pir.util;

import cn.powernukkitx.pir.object.geometry.Plane;
import cn.powernukkitx.pir.object.geometry.Triangle;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class MathUtil {
    public static final float EPSILON = 1e-6f;
    public static final float EPSILON_NEG = -EPSILON;
    public static final float EPSILON_1X = 1f + EPSILON;
    public static final Vector3f INVALID_VEC3F = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
    public static final Vector4f INVALID_VEC4F = new Vector4f(Float.NaN, Float.NaN, Float.NaN, Float.NaN);

    private MathUtil() {
        throw new UnsupportedOperationException();
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float distance(@NotNull Vector3f pos, @NotNull Vector3f planeVector1, @NotNull Vector3f planeVector2) {
        var normal = new Vector3f(planeVector1).cross(planeVector2);
        var distance = new Vector3f(planeVector1).dot(normal);
        return Math.abs(normal.dot(pos) - distance) / normal.length();
    }

    public static float distance(@NotNull Vector3f pos, @NotNull Triangle triangle) {
        return distance(pos, triangle.edge1(), triangle.edge2());
    }

    public static float distance(@NotNull Vector3f pos, @NotNull Plane plane) {
        var planeVector = plane.planeVector();
        return Math.abs(planeVector.x * pos.x + planeVector.y * pos.y + planeVector.z * pos.z + planeVector.w) / new Vector3f(planeVector.x, planeVector.y, planeVector.z).length();
    }

    public static boolean isInvalidVector(@NotNull Vector3f vector) {
        return vector == INVALID_VEC3F || Float.isNaN(vector.x) || Float.isNaN(vector.y) || Float.isNaN(vector.z);
    }

    public static boolean isInvalidVector(@NotNull Vector4f vector) {
        return vector == INVALID_VEC4F || Float.isNaN(vector.x) || Float.isNaN(vector.y) || Float.isNaN(vector.z) || Float.isNaN(vector.w);
    }
}
