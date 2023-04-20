package cn.powernukkitx.pir.scene;

import cn.powernukkitx.pir.object.Ray;
import cn.powernukkitx.pir.object.*;
import cn.powernukkitx.pir.object.geometry.Polyhedral;
import cn.powernukkitx.pir.object.geometry.Shape;
import cn.powernukkitx.pir.object.geometry.Triangle;
import cn.powernukkitx.pir.object.light.Light;
import cn.powernukkitx.pir.worker.RayTraceWorker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

public final class FrozenScene implements Scene {
    private final Set<SceneObject> rawScene;
    private final Triangle[] triangles;
    private final Shape[] otherShapes;
    private final Light[] lights;

    public FrozenScene(@NotNull Set<SceneObject> rawScene) {
        this.rawScene = Collections.unmodifiableSet(rawScene);
        var triangleList = new ArrayList<Triangle>(rawScene.size());
        var otherShapeList = new ArrayList<Shape>();
        var lightList = new ArrayList<Light>();
        for (var obj : rawScene) {
            if (obj.type() == ObjectType.GEOMETRY) {
                if (obj instanceof Shape shape) {
                    if (shape instanceof Triangle triangle) {
                        triangleList.add(triangle);
                    } else {
                        otherShapeList.add(shape);
                    }
                }
                if (obj instanceof Polyhedral polyhedral) {
                    triangleList.addAll(Arrays.stream(polyhedral.triangles()).filter(e -> {
                        var textureSize = e.textureSize();
                        return textureSize.x != 0 && textureSize.y != 0;
                    }).toList());
                }
            } else if (obj.type() == ObjectType.LIGHT) {
                if (obj instanceof Light light) {
                    lightList.add(light);
                }
            }
        }
        triangles = triangleList.toArray(Triangle[]::new);
        otherShapes = otherShapeList.toArray(Shape[]::new);
        lights = lightList.toArray(Light[]::new);
    }

    @Override
    public boolean isFrozen() {
        return true;
    }

    @Override
    @Contract("-> new")
    public @NotNull Scene freeze() {
        return this;
    }

    @Override
    public @NotNull Vector4f @NotNull [] rayTrace(@NotNull Ray @NotNull [] ray, @NotNull RayTraceWorker rayTraceWorker) {
        return rayTraceWorker.rayTrace(ray, triangles, otherShapes, lights, getDefaultColor());
    }

    @Override
    public @NotNull Vector4f @NotNull [] rayTrace(@NotNull Vector3f @NotNull [] rayPositions, @NotNull Vector3f @NotNull [] rayDirections, @NotNull RayTraceWorker rayTraceWorker) {
        return rayTraceWorker.rayTrace(rayPositions, rayDirections, triangles, otherShapes, lights, getDefaultColor());
    }

    @Override
    public int size() {
        return rawScene.size();
    }

    @Override
    public boolean isEmpty() {
        return rawScene.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return rawScene.contains(o);
    }

    @NotNull
    @Override
    public Iterator<SceneObject> iterator() {
        return rawScene.iterator();
    }

    @NotNull
    @Override
    public SceneObject @NotNull [] toArray() {
        // contact the two arrays
        var result = new SceneObject[triangles.length + otherShapes.length];
        System.arraycopy(triangles, 0, result, 0, triangles.length);
        System.arraycopy(otherShapes, 0, result, triangles.length, otherShapes.length);
        return result;
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        // contact the two arrays
        var result = Arrays.copyOf(a, triangles.length + otherShapes.length);
        System.arraycopy(triangles, 0, result, 0, triangles.length);
        System.arraycopy(otherShapes, 0, result, triangles.length, otherShapes.length);
        return result;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean add(SceneObject shape) {
        return rawScene.add(shape);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean remove(Object o) {
        return rawScene.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return rawScene.containsAll(c);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean addAll(@NotNull Collection<? extends SceneObject> c) {
        return rawScene.addAll(c);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return rawScene.retainAll(c);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return rawScene.removeAll(c);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void clear() {
        rawScene.clear();
    }
}
