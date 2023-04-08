package cn.powernukkitx.pir.functor;

import cn.powernukkitx.pir.util.MathUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class JavaTriangleFunctor implements TriangleFunctor {
    @Override
    public boolean contains(@NotNull Matrix3f triangle, @NotNull Vector3f pos) {
        var tmp = new Vector3f();
        if (pos.equals(pos1(triangle, tmp)) || pos.equals(pos2(triangle, tmp)) || pos.equals(pos3(triangle, tmp))) {
            return true;
        }
        // not in the same plane
        if (MathUtil.distance(pos, pos2(triangle).sub(pos1(triangle, tmp)), pos3(triangle).sub(tmp)) > MathUtil.EPSILON) {
            return false;
        }
        // 3 vectors: pos to A, B and C
        var v1 = pos.sub(pos1(triangle, tmp), new Vector3f());
        var v2 = pos.sub(pos2(triangle, tmp), new Vector3f());
        var v3 = pos.sub(pos3(triangle, tmp), new Vector3f());
        // 3 cross vector
        var t1 = v1.cross(v2, new Vector3f());
        var t2 = v2.cross(v3, new Vector3f());
        var t3 = v3.cross(v1, new Vector3f());
        // if they have the same sign, then the point is in the triangle
        return t1.dot(t2) >= 0 && t2.dot(t3) >= 0 && t3.dot(t1) >= 0;
    }

    @Override
    public @NotNull Vector4f planeVector(Matrix3f triangle, Vector4f ref) {
        var pos1 = pos1(triangle);
        var pos2 = pos2(triangle);
        var pos3 = pos3(triangle);
        var normal = new Vector3f(pos2).sub(pos1).cross(new Vector3f(pos3).sub(pos1));
        var distance = new Vector3f(pos1).dot(normal);
        return ref.set(normal, distance);
    }

    @Override
    public @NotNull Vector3f intersects(@NotNull Matrix3f triangle, @NotNull Vector3f pos, @NotNull Vector3f direction, @NotNull Vector3f ref) {
        var tmp = new Vector3f();
        var tmp2 = new Vector3f();
        var e1 = pos2(triangle, tmp2).sub(pos1(triangle, tmp), new Vector3f()); // edge 1
        var e2 = pos3(triangle, tmp2).sub(tmp, new Vector3f()); // edge 2
        var p = direction.cross(e2, new Vector3f()); // p = D x E2
        var det = e1.dot(p); // det = E1 x P
        if (det > MathUtil.EPSILON_NEG && det < MathUtil.EPSILON) { // if det is near zero, ray lies in plane of triangle
            return ref.set(Float.NaN);
        }
        var invDet = 1.0f / det; // calculate distance from V1 to ray origin
        var tVec = pos.sub(tmp, tmp2); // T = O - V1
        var u = tVec.dot(p) * invDet; // calculate U parameter and test bounds
        if (u < MathUtil.EPSILON_NEG || u > MathUtil.EPSILON_1X) { // if U is not in range (0, 1), the intersection lies outside the triangle
            return ref.set(Float.NaN);
        }
        var q = tVec.cross(e1); // Q = T x E1, now q == tmp2
        var v = direction.dot(q) /* tmp2 free */ * invDet; // calculate V parameter and test bounds
        if (v < MathUtil.EPSILON_NEG || u + v > MathUtil.EPSILON_1X) { // if V is not in range (0, 1) or u + v is gt than 1, the intersection lies outside the triangle
            return ref.set(Float.NaN);
        }
        var t = 1 - u - v; // calculate T parameter, ray intersects triangle
        if (t > MathUtil.EPSILON_NEG) { // ray intersection
            return ref.set(pos1(triangle, tmp).mul(t).add(pos2(triangle, tmp2).mul(u)).add(pos3(triangle).mul(v))); // return the intersection point
        }
        // this means that there is a line intersecting the plane of the triangle, but not the triangle itself
        return ref.set(Float.NaN);
    }

    @Override
    public @NotNull Vector3f normalVector(@NotNull Matrix3f triangle, @NotNull Vector3f ref) {
        var tmp = new Vector3f();
        return ref.set(pos2(triangle, tmp)).sub(pos1(triangle, tmp)).cross(pos3(triangle).sub(pos1(triangle, tmp))).normalize();
    }

    @Override
    public @NotNull Vector2i getTextureUV(@NotNull Matrix3f triangle, @NotNull Vector3f pos, @NotNull Vector2i textureSize, @NotNull Vector2i ref) {
        var tmp = new Vector3f();
        var e12 = new Vector3f(pos2(triangle, tmp)).sub(pos1(triangle, tmp));
        var e13 = new Vector3f(pos3(triangle, tmp)).sub(pos1(triangle, tmp));
        var pos1 = pos1(triangle);
        var u = pos.sub(pos1, new Vector3f()).dot(e12) / e12.lengthSquared();
        var v = pos.sub(pos1, new Vector3f()).dot(e13) / e13.lengthSquared();
        return ref.set((int) (u * textureSize.x), (int) (v * textureSize.y));
    }

    @Contract("_ -> new")
    public Vector3f pos1(@NotNull Matrix3f triangle) {
        return triangle.getColumn(0, new Vector3f());
    }

    @Contract("_, !null -> param2")
    public Vector3f pos1(@NotNull Matrix3f triangle, Vector3f ref) {
        return triangle.getColumn(0, ref);
    }

    @Contract("_ -> new")
    public Vector3f pos2(@NotNull Matrix3f triangle) {
        return triangle.getColumn(1, new Vector3f());
    }

    @Contract("_, !null -> param2")
    public Vector3f pos2(@NotNull Matrix3f triangle, Vector3f ref) {
        return triangle.getColumn(1, ref);
    }

    @Contract("_ -> new")
    public Vector3f pos3(@NotNull Matrix3f triangle) {
        return triangle.getColumn(2, new Vector3f());
    }

    @Contract("_, !null -> param2")
    public Vector3f pos3(@NotNull Matrix3f triangle, Vector3f ref) {
        return triangle.getColumn(2, ref);
    }
}
