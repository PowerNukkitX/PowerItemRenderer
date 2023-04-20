package cn.powernukkitx.pir.worker;

import cn.powernukkitx.pir.functor.TriangleFunctor;
import cn.powernukkitx.pir.object.Ray;
import cn.powernukkitx.pir.object.geometry.Shape;
import cn.powernukkitx.pir.object.geometry.Triangle;
import cn.powernukkitx.pir.object.light.Light;
import cn.powernukkitx.pir.util.ColorUtil;
import cn.powernukkitx.pir.util.MathUtil;
import cn.powernukkitx.pir.util.SortUtil;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * SimpleRayTraceWorker supports ONLY Triangles !!!
 */
public class SimpleRayTraceWorker implements RayTraceWorker {
    private static final ThreadLocal<Map<Long, Vector3f[][]>> IntersectionsGlobalBufferCache = ThreadLocal.withInitial(WeakHashMap::new);
    private static final ThreadLocal<float[]> HsbTmpBufferCache = ThreadLocal.withInitial(() -> new float[3]);
    private static final ThreadLocal<Map<Integer, Matrix3f[]>> TriangleMatsBufferCache = ThreadLocal.withInitial(WeakHashMap::new);
    private static final ThreadLocal<Map<Integer, Vector3f[]>> RayPositionsBufferCache = ThreadLocal.withInitial(WeakHashMap::new);
    private static final ThreadLocal<Map<Integer, Vector3f[]>> RayDirectionsBufferCache = ThreadLocal.withInitial(WeakHashMap::new);

    public final boolean singleSided;

    public SimpleRayTraceWorker(boolean singleSided) {
        this.singleSided = singleSided;
    }

    public SimpleRayTraceWorker() {
        this.singleSided = false;
    }

    private @NotNull Vector3f @NotNull [] @NotNull [] makeIntersectionsGlobalBuffer(int raysLength, int trianglesLength) {
        var intersectionsGlobalBuffer = new Vector3f[raysLength][trianglesLength];
        for (int i = 0; i < raysLength; i++) {
            for (int j = 0; j < trianglesLength; j++) {
                intersectionsGlobalBuffer[i][j] = new Vector3f();
            }
        }
        return intersectionsGlobalBuffer;
    }

    private @NotNull Matrix3f @NotNull [] makeTriangleMatsBuffer(int trianglesLength) {
        var triangleMatsBuffer = new Matrix3f[trianglesLength];
        for (int i = 0; i < trianglesLength; i++) {
            triangleMatsBuffer[i] = new Matrix3f();
        }
        return triangleMatsBuffer;
    }

    @Override
    public @NotNull Vector4f @NotNull [] rayTrace(@NotNull Ray @NotNull [] rays,
                                                  @NotNull Triangle @NotNull [] triangles,
                                                  @NotNull Shape @NotNull [] otherShapes,
                                                  @NotNull Light @NotNull [] lights,
                                                  @NotNull Vector4f defaultColor) {
        var rayLength = rays.length;
        var rayPositionBuffer = RayPositionsBufferCache.get().computeIfAbsent(rayLength, len -> {
            var buffer = new Vector3f[len];
            for (var i = 0; i < len; i++) {
                buffer[i] = new Vector3f();
            }
            return buffer;
        });
        var rayDirectionBuffer = RayDirectionsBufferCache.get().computeIfAbsent(rayLength, len -> {
            var buffer = new Vector3f[len];
            for (var i = 0; i < len; i++) {
                buffer[i] = new Vector3f();
            }
            return buffer;
        });
        return rayTrace(rayPositionBuffer, rayDirectionBuffer, triangles, otherShapes, lights, defaultColor);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public @NotNull Vector4f @NotNull [] rayTrace(@NotNull Vector3f @NotNull [] rayPositions,
                                                  @NotNull Vector3f @NotNull [] rayDirections,
                                                  @NotNull Triangle @NotNull [] triangles,
                                                  @NotNull Shape @NotNull [] otherShapes,
                                                  @NotNull Light @NotNull [] lights,
                                                  @NotNull Vector4f defaultColor) {
        var results = new Vector4f[rayPositions.length];

        // tmp vars
        var hsbTmpBuffer = HsbTmpBufferCache.get();
        var raysLength = rayPositions.length;
        var trianglesLength = triangles.length;
        var intersectionsGlobalBuffer = IntersectionsGlobalBufferCache.get().computeIfAbsent((((long) raysLength << 32) | trianglesLength),
                key -> makeIntersectionsGlobalBuffer(raysLength, trianglesLength));
        // build an array of all the triangle matrices
        var triangleMats = TriangleMatsBufferCache.get().computeIfAbsent(trianglesLength, this::makeTriangleMatsBuffer);
        for (int i = 0; i < trianglesLength; i++) {
            var triangle = triangles[i];
            triangleMats[i].set(triangle.posMat());
        }
        // calculate the normalVectors for all triangles
        var normalVectors = TriangleFunctor.current().normalVector(triangleMats);
        // ray tracing
        // calculate all intersections
        if (this.singleSided) {
            TriangleFunctor.current().intersectsSingleSided(triangleMats, normalVectors, rayPositions, rayDirections, intersectionsGlobalBuffer);
        } else {
            TriangleFunctor.current().intersects(triangleMats, rayPositions, rayDirections, intersectionsGlobalBuffer);
        }
        var intersectionsIdArray = new int[2];
        var intersectionCount = 0;
        for (var i = 0; i < raysLength; i++) {
            // find the intersection points of the ray and the triangles
            var intersectionsBuffer = intersectionsGlobalBuffer[i];
            for (int j = 0, intersectionsLength = intersectionsBuffer.length; j < intersectionsLength; j++) {
                if (!MathUtil.isInvalidVector(intersectionsBuffer[j])) {
                    if (intersectionCount + 1 == intersectionsIdArray.length) {
                        var newArr = new int[(intersectionsIdArray.length << 1)];
                        System.arraycopy(intersectionsIdArray, 0, newArr, 0, intersectionsIdArray.length);
                        intersectionsIdArray = newArr;
                    }
                    intersectionsIdArray[intersectionCount++] = j;
                }
            }
            // if no intersections, use the default color
            if (intersectionCount == 0) {
                results[i] = defaultColor;
                continue;
            }
            // sort with distance
            var currentRayPosition = rayPositions[i];
            SortUtil.sort(intersectionsIdArray, 0, intersectionCount,
                    (o1, o2) -> Float.compare(intersectionsBuffer[o1].distanceSquared(currentRayPosition), intersectionsBuffer[o2].distanceSquared(currentRayPosition)));
            // convert the data into arrays
            var intersectionPointArray = new Vector3f[intersectionCount];
            var triangleMatArray = new Matrix3f[intersectionCount];
            var textureSizeArray = new Vector2i[intersectionCount];
            var normalVectorArray = new Vector3f[intersectionCount];
            for (int j = 0; j < intersectionCount; j++) {
                intersectionPointArray[j] = intersectionsBuffer[intersectionsIdArray[j]];
                triangleMatArray[j] = triangleMats[intersectionsIdArray[j]];
                textureSizeArray[j] = triangles[intersectionsIdArray[j]].textureSize();
                normalVectorArray[j] = normalVectors[intersectionsIdArray[j]];
            }
            // calculate the lights
            var lightIntensities = new float[triangleMatArray.length];
            for (var light : lights) {
                light.calculateIntensity(lightIntensities, normalVectorArray);
            }
            // calculate the texture coordinates
            var textureUVArray = TriangleFunctor.current().getTextureUV(triangleMatArray, intersectionPointArray, textureSizeArray);
            // calculate the color
            Vector4f color = null;
            for (int j = 0; j < intersectionCount; j++) {
                var triangle = triangles[intersectionsIdArray[j]];
                var uv = textureUVArray[j];
                var textureSize = textureSizeArray[j];
                var texture = triangle.texture();
                var tmpColor = ColorUtil.toColorVectorFloat(texture[uv.x + uv.y * textureSize.x], new Vector4f());
                ColorUtil.setLightIntensityInHSB(tmpColor, lightIntensities[j], hsbTmpBuffer);
                if (color == null) color = tmpColor;
                else ColorUtil.combineRefractionColor(color, tmpColor);
            }
            Objects.requireNonNull(color);
            results[i] = color;
            intersectionCount = 0;
        }
        return results;
    }
}
