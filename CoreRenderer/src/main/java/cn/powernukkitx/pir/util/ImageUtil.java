package cn.powernukkitx.pir.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

public final class ImageUtil {
    private ImageUtil() {
        throw new UnsupportedOperationException();
    }

    public static final Vector2i FALLBACK_TEXTURE_SIZE = new Vector2i(1, 1);
    public static final int[] FALLBACK_TEXTURE = new int[]{0x80808080};

    private static final ConcurrentHashMap<BufferedImage, int[]> PIXEL_CACHE = new ConcurrentHashMap<>();

    @Contract("null -> !null; !null -> !null")
    public static int[] getPixels(@Nullable BufferedImage image) {
        if (image == null) return FALLBACK_TEXTURE;
        return PIXEL_CACHE.computeIfAbsent(image, img -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int[] pixels = new int[width * height];
            img.getRGB(0, 0, width, height, pixels, 0, width);
            return pixels;
        });
    }

    private static final ConcurrentHashMap<int[], int[]> TRANSPOSE_CACHE = new ConcurrentHashMap<>();

    @Contract(pure = true)
    public static int @NotNull [] transpose(int[] pixels, int width, int height) {
        return TRANSPOSE_CACHE.computeIfAbsent(pixels, p -> {
            int[] result = new int[pixels.length];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    result[i * height + j] = pixels[j * width + i];
                }
            }
            return result;
        });
    }

    @Contract(pure = true)
    public static int @NotNull [] transpose(int[] pixels, @NotNull Vector2i size) {
        return transpose(pixels, size.x, size.y);
    }

    @Contract("null ->!null; !null -> !null")
    public static Vector2i getImageSize(@Nullable BufferedImage image) {
        if (image == null) return FALLBACK_TEXTURE_SIZE;
        return new Vector2i(image.getWidth(), image.getHeight());
    }

    private static final ConcurrentHashMap<int[], int[]> CENTRAL_SYMMETRY_CACHE = new ConcurrentHashMap<>();

    @Contract(pure = true)
    public static int @NotNull [] centralSymmetry(int[] pixels, int width, int height) {
        return CENTRAL_SYMMETRY_CACHE.computeIfAbsent(pixels, p -> {
            int[] result = new int[pixels.length];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    result[i * height + j] = pixels[(width - i - 1) * height + (height - j - 1)];
                }
            }
            return result;
        });
    }

    @Contract(pure = true)
    public static int @NotNull [] centralSymmetry(int[] pixels, @NotNull Vector2i size) {
        return centralSymmetry(pixels, size.x, size.y);
    }

    private static final ConcurrentHashMap<int[], int[]> LEFT_RIGHT_MIRROR_CACHE = new ConcurrentHashMap<>();

    @Contract(pure = true)
    public static int @NotNull [] leftRightMirror(int[] pixels, int width, int height) {
        return LEFT_RIGHT_MIRROR_CACHE.computeIfAbsent(pixels, p -> {
            int[] result = new int[pixels.length];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    result[i + j * width] = pixels[(width - i - 1) + j * width];
                }
            }
            return result;
        });
    }

    @Contract(pure = true)
    public static int @NotNull [] leftRightMirror(int[] pixels, @NotNull Vector2i size) {
        return leftRightMirror(pixels, size.x, size.y);
    }

    private static final ConcurrentHashMap<int[], int[]> TOP_BOTTOM_MIRROR_CACHE = new ConcurrentHashMap<>();

    @Contract(pure = true)
    public static int @NotNull [] topBottomMirror(int[] pixels, int width, int height) {
        return TOP_BOTTOM_MIRROR_CACHE.computeIfAbsent(pixels, p -> {
            int[] result = new int[pixels.length];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    result[i + j * width] = pixels[i + (height - j - 1) * width];
                }
            }
            return result;
        });
    }

    @Contract(pure = true)
    public static int @NotNull [] topBottomMirror(int[] pixels, @NotNull Vector2i size) {
        return topBottomMirror(pixels, size.x, size.y);
    }

    record SliceCacheKey(
            int[] pixels,
            int x,
            int y,
            int w,
            int h
    ) {
    }

    private static final ConcurrentHashMap<SliceCacheKey, int[]> SLICE_CACHE = new ConcurrentHashMap<>();

    public static int @NotNull [] slice(@NotNull BufferedImage texture, int x, int y, int w, int h) {
        return slice(getPixels(texture), texture.getWidth(), texture.getHeight(), x, y, w, h);
    }

    public static int @NotNull [] slice(int[] pixels, int width, int height, int x, int y, int w, int h) {
        return SLICE_CACHE.computeIfAbsent(new SliceCacheKey(pixels, x, y, w, h), key -> {
            int[] result = new int[w * h];
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    if (i + x >= width || j + y >= height) {
                        result[i + j * w] = ColorUtil.TRANSPARENT_INT;
                    } else {
                        result[i + j * w] = pixels[(i + x) + (j + y) * width];
                    }
                }
            }
            return result;
        });
    }

    @Contract(pure = true)
    public static int @NotNull [] doNothing(int[] pixels, @NotNull Vector2i size) {
        return pixels;
    }
}
