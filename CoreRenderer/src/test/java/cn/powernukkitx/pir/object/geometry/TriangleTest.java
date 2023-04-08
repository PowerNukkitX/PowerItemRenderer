package cn.powernukkitx.pir.object.geometry;

import cn.powernukkitx.pir.object.Ray;
import cn.powernukkitx.pir.util.MathUtil;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TriangleTest {
    @Test
    public void contain1() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 0,
                1, 0, 0
        );
        var pos = new Vector3f(0.5f, 0.5f, 0);
        Assertions.assertTrue(triangle.contains(pos));
    }

    @Test
    public void contain2() {
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 0,
                1, 0, 0
        );
        var pos = new Vector3f(0.5f, 0.5f, 1f);
        Assertions.assertFalse(triangle.contains(pos));
    }

    @Test
    public void contain3() {
        var triangle = new Triangle(
                1, 0, 1,
                0, 1, 1,
                0, 0, 0
        );
        var pos = new Vector3f(1, 1, 1);
        Assertions.assertFalse(triangle.contains(pos));
    }

    @Test
    public void contain4() {
        var triangle = new Triangle(
                1, 0, 1,
                0, 1, 1,
                0, 0, 0
        );
        var pos = new Vector3f(0.5f, 0.5f, 1);
        Assertions.assertTrue(triangle.contains(pos));
    }

    @Test
    public void contain5() {
        var triangle = new Triangle(
                1, 0, 1,
                0, 1, 1,
                0, 0, 0
        );
        var pos = new Vector3f(0.4999f, 0.5001f, 1);
        Assertions.assertFalse(triangle.contains(pos));
    }

    @Test
    public void intersect1() {
        var ray = new Ray(1, 1, 1, -1f, -1f, -1f);
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 0,
                1, 0, 0
        );
        var pos = triangle.intersects(ray);
        System.out.println(pos);
        Assertions.assertNotNull(pos);
    }

    @Test
    public void intersect2() {
        var ray = new Ray(0.5f, 0.5f, 0.5f, -0.02f, -0.02f, -1f);
        var triangle = new Triangle(
                0, 0, 0,
                0, 1, 0,
                1, 0, 0
        );
        var pos = triangle.intersects(ray);
        System.out.println(pos);
        Assertions.assertNotNull(pos);
    }

    @Test
    public void testNormal1() {
        var triangle = new Triangle(
                0, 0, 0,
                1, 0, 0,
                0, 1, 0
        );
        var nor = triangle.normalVector();
        Assertions.assertEquals(0, nor.x);
        Assertions.assertEquals(0, nor.y);
        Assertions.assertEquals(1, nor.z);
        Assertions.assertEquals(1, nor.length());
    }

    @Test
    public void testNormal2() {
        var triangle = new Triangle(
                0.9822f, 2.234f, -0.4322f,
                12.21f, 0.72f, 7.83f,
                -7.7235f, -3.3417f, 0.992f
        );
        var nor = triangle.normalVector();
        System.out.println(nor);
        Assertions.assertEquals(1, nor.length(), MathUtil.EPSILON);
    }
}
