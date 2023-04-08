package cn.powernukkitx.pir.util;


import org.jetbrains.annotations.NotNull;

public final class SortUtil {
    private SortUtil() {
        throw new UnsupportedOperationException();
    }

    public interface IntComparator {
        int compare(int o1, int o2);
    }

    // custom primitive int sort using TimSort
    public static void sort(int[] array, int fromIndex, int toIndex, IntComparator c) {
        int remaining = toIndex - fromIndex;
        if (remaining < 2) {
            return;
        }
        int runLength = countRunAndMakeAscending(array, fromIndex, toIndex, c);
        binarySort(array, fromIndex, toIndex, fromIndex + runLength, c);
    }

    private static void swap(int @NotNull [] array, int x, int y) {
        int v = array[x];
        array[x] = array[y];
        array[y] = v;
    }

    private static void binarySort(int[] a, int lo, int hi, int start, IntComparator c) {
        assert lo <= start && start <= hi;
        if (start == lo)
            start++;
        for (; start < hi; start++) {
            int pivot = a[start];

            // Set left (and right) to the index where a[start] (pivot) belongs
            int left = lo;
            int right = start;
            assert left <= right;
            /*
             * Invariants:
             *   pivot >= all in [lo, left).
             *   pivot <  all in [right, start).
             */
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (c.compare(pivot, a[mid]) < 0)
                    right = mid;
                else
                    left = mid + 1;
            }
            assert left == right;

            /*
             * The invariants still hold: pivot >= all in [lo, left) and
             * pivot < all in [left, start), so pivot belongs at left.  Note
             * that if there are elements equal to pivot, left points to the
             * first slot after them -- that's why this sort is stable.
             * Slide elements over to make room for pivot.
             */
            int n = start - left;  // The number of elements to move
            // Switch is just an optimization for arraycopy in default case
            switch (n) {
                case 2:
                    a[left + 2] = a[left + 1];
                case 1:
                    a[left + 1] = a[left];
                    break;
                default:
                    System.arraycopy(a, left, a, left + 1, n);
            }
            a[left] = pivot;
        }
    }

    private static int countRunAndMakeAscending(int[] a, int lo, int hi, IntComparator c) {
        assert lo < hi;
        int runHi = lo + 1;
        if (runHi == hi)
            return 1;

        // Find end of run, and reverse range if descending
        if (c.compare(a[runHi++], a[lo]) < 0) { // Descending
            while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) < 0)
                runHi++;
            reverseRange(a, lo, runHi);
        } else {                              // Ascending
            while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) >= 0)
                runHi++;
        }

        return runHi - lo;
    }

    private static void reverseRange(int[] a, int lo, int hi) {
        hi--;
        while (lo < hi) {
            int t = a[lo];
            a[lo++] = a[hi];
            a[hi--] = t;
        }
    }
}
