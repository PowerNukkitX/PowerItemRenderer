package cn.powernukkitx.pir.util;

import cn.powernukkitx.pir.object.geometry.Plane;
import cn.powernukkitx.pir.object.geometry.Triangle;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MathUtilTest {
    @Test
    public void distancePlanar1() {
        var planeVector1 = new Vector3f(0, 1, 0);
        var planeVector2 = new Vector3f(1, 0, 0);
        var pos = new Vector3f(0.5f, 0.5f, 0);
        var distance = MathUtil.distance(pos, planeVector1, planeVector2);
        Assertions.assertEquals(distance, 0);
    }

    @Test
    public void distancePlanar2() {
        var planeVector1 = new Vector3f(0, 1, 0);
        var planeVector2 = new Vector3f(1, 0, 0);
        var pos = new Vector3f(0.5f, 0.5f, 1.22f);
        var distance = MathUtil.distance(pos, planeVector1, planeVector2);
        Assertions.assertEquals(distance, 1.22f);
    }

    @Test
    public void distancePlanar3() {
        var planeVector1 = new Vector3f(0, 1, 1);
        var planeVector2 = new Vector3f(1, 0, 0);
        var pos = new Vector3f(0, 0, 1);
        var distance = MathUtil.distance(pos, planeVector1, planeVector2);
        Assertions.assertEquals(distance, (float) Math.sqrt(2) / 2f);
    }

    @Test
    public void distancePlanar4() {
        var planeVector1 = new Vector3f(0, 1, 1);
        var planeVector2 = new Vector3f(1, 0, 1);
        var pos = new Vector3f(0, 0, 1);
        var distance = MathUtil.distance(pos, planeVector1, planeVector2);
        Assertions.assertEquals(distance, ((float) Math.sqrt(2) / 2f) / (float) Math.sqrt(1f + 0.5f));
    }

    @Test
    public void distancePlanar5() {
        var planeVector1 = new Vector3f(1, 1, 2);
        var planeVector2 = new Vector3f(2, 0, 2);
        var pos = new Vector3f(1, 0, 2);
        var distance = MathUtil.distance(pos, planeVector1, planeVector2);
        Assertions.assertEquals(distance, ((float) Math.sqrt(2) / 2f) / (float) Math.sqrt(1f + 0.5f));
    }

    @Test
    public void distancePlanarTriangle1() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 0,
                1, 0, 0
        );
        var pos = new Vector3f(0.5f, 0.5f, 0);
        var distance = MathUtil.distance(pos, triangle);
        Assertions.assertEquals(distance, 0);
    }

    @Test
    public void distancePlanarTriangle2() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 0,
                1, 0, 0
        );
        var pos = new Vector3f(0.5f, 0.5f, 1.22f);
        var distance = MathUtil.distance(pos, triangle);
        Assertions.assertEquals(distance, 1.22f);
    }

    @Test
    public void distancePlanarTriangle3() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 1,
                1, 0, 0
        );
        var pos = new Vector3f(0, 0, 1);
        var distance = MathUtil.distance(pos, triangle);
        Assertions.assertEquals(distance, (float) Math.sqrt(2) / 2f);
    }

    @Test
    public void distancePlanarTriangle4() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 1,
                1, 0, 1
        );
        var pos = new Vector3f(0, 0, 1);
        var distance = MathUtil.distance(pos, triangle);
        Assertions.assertEquals(distance, ((float) Math.sqrt(2) / 2f) / (float) Math.sqrt(1f + 0.5f));
    }

    @Test
    public void distanceTriangle5() {
        var triangle = new Triangle(
                1, 0, 1,
                1, 1, 2,
                2, 0, 2
        );
        var pos = new Vector3f(1, 0, 2);
        var distance = MathUtil.distance(pos, triangle);
        Assertions.assertEquals(distance, ((float) Math.sqrt(2) / 2f) / (float) Math.sqrt(1f + 0.5f));
    }

    @Test
    public void distancePlanarTriangleAsPlane1() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 0,
                1, 0, 0
        );
        var pos = new Vector3f(0.5f, 0.5f, 0);
        var distance = MathUtil.distance(pos, (Plane) triangle);
        Assertions.assertEquals(distance, 0);
    }

    @Test
    public void distancePlanarTriangleAsPlane2() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 0,
                1, 0, 0
        );
        var pos = new Vector3f(0.5f, 0.5f, 1.22f);
        var distance = MathUtil.distance(pos, (Plane) triangle);
        Assertions.assertEquals(distance, 1.22f);
    }

    @Test
    public void distancePlanarTriangleAsPlane3() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 1,
                1, 0, 0
        );
        var pos = new Vector3f(0, 0, 1);
        var distance = MathUtil.distance(pos, (Plane) triangle);
        Assertions.assertEquals(distance, (float) Math.sqrt(2) / 2f);
    }

    @Test
    public void distancePlanarTriangleAsPlane4() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 1,
                1, 0, 1
        );
        var pos = new Vector3f(0, 0, 1);
        var distance = MathUtil.distance(pos, (Plane) triangle);
        Assertions.assertEquals(distance, ((float) Math.sqrt(2) / 2f) / (float) Math.sqrt(1f + 0.5f));
    }

    @Test
    public void distanceTriangleAsPlane5() {
        var triangle = new Triangle(
                1, 0, 1,
                1, 1, 2,
                2, 0, 2
        );
        var pos = new Vector3f(1, 0, 2);
        var distance = MathUtil.distance(pos, (Plane) triangle);
        Assertions.assertEquals(distance, ((float) Math.sqrt(2) / 2f) / (float) Math.sqrt(1f + 0.5f));
    }
}
