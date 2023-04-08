package cn.powernukkitx.pir.object.geometry;

import cn.powernukkitx.pir.functor.TriangleFunctor;
import cn.powernukkitx.pir.util.ColorUtil;
import cn.powernukkitx.pir.util.ImageUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.image.BufferedImage;

import static cn.powernukkitx.pir.util.ImageUtil.FALLBACK_TEXTURE;
import static cn.powernukkitx.pir.util.ImageUtil.FALLBACK_TEXTURE_SIZE;

public record Triangle(@NotNull Matrix3f posMat, int @NotNull [] texture,
                       @NotNull Vector2i textureSize) implements Shape, Plane {
    public Triangle(@NotNull Matrix3f posMat) {
        this(posMat, FALLBACK_TEXTURE, FALLBACK_TEXTURE_SIZE);
    }

    public Triangle(@NotNull Vector3f pos1, @NotNull Vector3f pos2, @NotNull Vector3f pos3) {
        this(new Matrix3f(pos1, pos2, pos3), FALLBACK_TEXTURE, FALLBACK_TEXTURE_SIZE);
    }

    public Triangle(@NotNull Vector3f pos1, @NotNull Vector3f pos2, @NotNull Vector3f pos3, @Nullable BufferedImage texture) {
        this(new Matrix3f(pos1, pos2, pos3), ImageUtil.getPixels(texture), ImageUtil.getImageSize(texture));
    }

    public Triangle(@NotNull Vector3f pos1, @NotNull Vector3f pos2, @NotNull Vector3f pos3, int @NotNull [] texture, @NotNull Vector2i textureSize) {
        this(new Matrix3f(pos1, pos2, pos3), texture, textureSize);
    }

    public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
        this(new Matrix3f(x1, y1, z1, x2, y2, z2, x3, y3, z3), FALLBACK_TEXTURE, FALLBACK_TEXTURE_SIZE);
    }

    public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, @Nullable BufferedImage texture) {
        this(new Matrix3f(x1, y1, z1, x2, y2, z2, x3, y3, z3), ImageUtil.getPixels(texture), ImageUtil.getImageSize(texture));
    }

    @Contract("-> new")
    public Vector3f pos1() {
        return posMat.getColumn(0, new Vector3f());
    }

    @Contract("!null -> param1")
    public Vector3f pos1(Vector3f ref) {
        return posMat.getColumn(0, ref);
    }

    @Contract("-> new")
    public Vector3f pos2() {
        return posMat.getColumn(1, new Vector3f());
    }

    @Contract("!null -> param1")
    public Vector3f pos2(Vector3f ref) {
        return posMat.getColumn(1, ref);
    }

    @Contract("-> new")
    public Vector3f pos3() {
        return posMat.getColumn(2, new Vector3f());
    }

    @Contract("!null -> param1")
    public Vector3f pos3(Vector3f ref) {
        return posMat.getColumn(2, ref);
    }

    /**
     * @return pos1 -> pos2
     */
    @Contract("-> new")
    public Vector3f edge1() {
        var tmp = new Vector3f();
        return new Vector3f(pos2(tmp)).sub(pos1(tmp));
    }

    /**
     * @return pos1 -> pos2
     */
    @Contract("_ -> param1")
    public Vector3f edge1(@NotNull Vector3f ref) {
        var tmp = new Vector3f();
        return ref.set(pos2(tmp)).sub(pos1(tmp));
    }

    /**
     * @return pos2 -> pos3
     */
    @Contract("-> new")
    public Vector3f edge2() {
        var tmp = new Vector3f();
        return new Vector3f(pos3(tmp)).sub(pos2(tmp));
    }

    /**
     * @return pos2 -> pos3
     */
    @Contract("_ -> param1")
    public Vector3f edge2(@NotNull Vector3f ref) {
        var tmp = new Vector3f();
        return ref.set(pos3(tmp)).sub(pos2(tmp));
    }

    /**
     * @return pos3 -> pos1
     */
    @Contract("-> new")
    public Vector3f edge3() {
        var tmp = new Vector3f();
        return new Vector3f(pos1(tmp)).sub(pos3(tmp));
    }

    /**
     * @return A matrix that presents the plane of the triangle using equation Ax + By + Cz + D = 0
     */
    @Contract("_ -> param1")
    @Override
    public @NotNull Vector4f planeVector(@NotNull Vector4f ref) {
        return TriangleFunctor.current().planeVector(this.posMat, ref);
    }

    /**
     * @return P<sub>1</sub>->P<sub>2</sub> × P<sub>1</sub>->P<sub>3</sub>
     */
    @Override
    public @NotNull Vector3f normalVector(Vector3f ref) {
        return TriangleFunctor.current().normalVector(this.posMat, ref);
    }

    @Override
    public boolean contains(@NotNull Vector3f pos) {
        return TriangleFunctor.current().contains(this.posMat, pos);
    }

    /**
     * Calculate the intersection of a ray and the triangle using Möller-Trumbore algorithm
     *
     * @param pos       the result pos
     * @param direction the direction of the ray
     * @return the position of the intersection, {@link cn.powernukkitx.pir.util.MathUtil#INVALID_VEC3F} if no intersection
     */
    @Override
    public @NotNull Vector3f intersects(@NotNull Vector3f pos, @NotNull Vector3f direction, @NotNull Vector3f ref) {
        return TriangleFunctor.current().intersects(this.posMat, pos, direction, ref);
    }

    @Override
    public boolean hasTexture() {
        return texture != FALLBACK_TEXTURE;
    }

    @Override
    public @NotNull Vector4f getTextureColor(@NotNull Vector3f pos, @NotNull Vector4f ref) {
        if (hasTexture()) {
            var uv = TriangleFunctor.current().getTextureUV(this.posMat, pos, textureSize, new Vector2i());
            return ColorUtil.toColorVectorFloat(texture[uv.x + uv.y * textureSize.x], ref);
        } else {
            return ref.set(0.5f, 0.5f, 0.5f, 0.5f);
        }
    }
}
