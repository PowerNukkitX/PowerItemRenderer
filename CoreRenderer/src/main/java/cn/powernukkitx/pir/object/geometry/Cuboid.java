package cn.powernukkitx.pir.object.geometry;

import cn.powernukkitx.pir.util.ImageUtil;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.util.Objects;

public record Cuboid(
        @NotNull Vector3f center, float xLength, float yLength, float zLength,
        int[][] textures,
        // the first dimension is the face, the second dimension is the texture, Down Right Back Left Front Up
        Vector2i[] textureSizes
) implements Polyhedral {
    @Override
    public Triangle[] triangles() {
        float halfXLength = xLength / 2;
        float halfYLength = yLength / 2;
        float halfZLength = zLength / 2;
        var vertices = new Vector3f[]{
                new Vector3f(center.x - halfXLength, center.y - halfYLength, center.z - halfZLength),
                new Vector3f(center.x + halfXLength, center.y - halfYLength, center.z - halfZLength),
                new Vector3f(center.x + halfXLength, center.y + halfYLength, center.z - halfZLength),
                new Vector3f(center.x - halfXLength, center.y + halfYLength, center.z - halfZLength),
                new Vector3f(center.x - halfXLength, center.y - halfYLength, center.z + halfZLength),
                new Vector3f(center.x + halfXLength, center.y - halfYLength, center.z + halfZLength),
                new Vector3f(center.x + halfXLength, center.y + halfYLength, center.z + halfZLength),
                new Vector3f(center.x - halfXLength, center.y + halfYLength, center.z + halfZLength)
        };
        return new Triangle[]{
                new Triangle(vertices[0], vertices[3], vertices[1], ImageUtil.centralSymmetry(textures[0], textureSizes[0]), textureSizes[0]),
                new Triangle(vertices[2], vertices[1], vertices[3], textures[0], textureSizes[0]),
                new Triangle(vertices[2], vertices[3], vertices[6], ImageUtil.centralSymmetry(textures[1], textureSizes[1]), textureSizes[1]),
                new Triangle(vertices[7], vertices[6], vertices[3], textures[1], textureSizes[1]),
                new Triangle(vertices[3], vertices[0], vertices[7], ImageUtil.centralSymmetry(textures[2], textureSizes[2]), textureSizes[2]),
                new Triangle(vertices[4], vertices[7], vertices[0], textures[2], textureSizes[2]),
                new Triangle(vertices[0], vertices[1], vertices[4], ImageUtil.centralSymmetry(textures[3], textureSizes[3]), textureSizes[3]),
                new Triangle(vertices[5], vertices[4], vertices[1], textures[3], textureSizes[3]),
                new Triangle(vertices[1], vertices[2], vertices[5], ImageUtil.centralSymmetry(textures[4], textureSizes[4]), textureSizes[4]),
                new Triangle(vertices[6], vertices[5], vertices[2], textures[4], textureSizes[4]),
                new Triangle(vertices[4], vertices[5], vertices[7], ImageUtil.centralSymmetry(textures[5], textureSizes[5]), textureSizes[5]),
                new Triangle(vertices[6], vertices[7], vertices[5], textures[5], textureSizes[5])
        };
    }

    public static final class UVDetail {
        private int x;
        private int y;
        private int w;
        private int h;

        public UVDetail(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public int w() {
            return w;
        }

        public int h() {
            return h;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setW(int w) {
            this.w = w;
        }

        public void setH(int h) {
            this.h = h;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (UVDetail) obj;
            return this.x == that.x &&
                    this.y == that.y &&
                    this.w == that.w &&
                    this.h == that.h;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, w, h);
        }

        @Override
        public String toString() {
            return "UVDetail[" +
                    "x=" + x + ", " +
                    "y=" + y + ", " +
                    "w=" + w + ", " +
                    "h=" + h + ']';
        }
    }

    public Cuboid(@NotNull Vector3f center, float xLength, float yLength, float zLength) {
        this(center, xLength, yLength, zLength, new int[6][], new Vector2i[6]);
    }

    public Cuboid(float x, float y, float z, float xLength, float yLength, float zLength, @NotNull BufferedImage texture,
                  @NotNull UVDetail @NotNull [] uvDetails) {
        this(new Vector3f(x, y, z), xLength, yLength, zLength, new int[][]{
                makeTexture(texture, uvDetails[0], 0),
                makeTexture(texture, uvDetails[1], 1),
                makeTexture(texture, uvDetails[2], 2),
                makeTexture(texture, uvDetails[3], 3),
                makeTexture(texture, uvDetails[4], 4),
                makeTexture(texture, uvDetails[5], 5)
        }, new Vector2i[]{
                new Vector2i(uvDetails[0].w, uvDetails[0].h),
                new Vector2i(uvDetails[1].w, uvDetails[1].h),
                new Vector2i(uvDetails[2].w, uvDetails[2].h),
                new Vector2i(uvDetails[3].w, uvDetails[3].h),
                new Vector2i(uvDetails[4].w, uvDetails[4].h),
                new Vector2i(uvDetails[5].w, uvDetails[5].h)
        });
    }

    private static int @NotNull [] makeTexture(@NotNull BufferedImage texture, @NotNull UVDetail uvDetail, int index) {
        int[] result;
        if (uvDetail.w < 0 && uvDetail.h < 0) {
            uvDetail.x += uvDetail.w;
            uvDetail.y += uvDetail.h;
            uvDetail.w = -uvDetail.w;
            uvDetail.h = -uvDetail.h;
            result = ImageUtil.slice(texture, uvDetail.x, uvDetail.y, uvDetail.w, uvDetail.h);
//            result = ImageUtil.centralSymmetry(result, uvDetail.w, uvDetail.h);
        } else if (uvDetail.w < 0) {
            uvDetail.x += uvDetail.w;
            uvDetail.w = -uvDetail.w;
            result = ImageUtil.slice(texture, uvDetail.x, uvDetail.y, uvDetail.w, uvDetail.h);
            result = ImageUtil.leftRightMirror(result, uvDetail.w, uvDetail.h);
        } else if (uvDetail.h < 0) {
            uvDetail.y += uvDetail.h;
            uvDetail.h = -uvDetail.h;
            result = ImageUtil.slice(texture, uvDetail.x, uvDetail.y, uvDetail.w, uvDetail.h);
            result = ImageUtil.topBottomMirror(result, uvDetail.w, uvDetail.h);
        } else {
            result = ImageUtil.slice(texture, uvDetail.x, uvDetail.y, uvDetail.w, uvDetail.h);
        }
//        if (index == 1 || index == 5) {
//            result = ImageUtil.centralSymmetry(result, uvDetail.w, uvDetail.h);
//        }
        return result;
    }
}
