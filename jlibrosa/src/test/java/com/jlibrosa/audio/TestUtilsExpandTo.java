package com.jlibrosa.audio;
import com.jlibrosa.audio.util.Utils;
import java.util.Arrays;

public class TestUtilsExpandTo {
    public static void main(String[] args) {
        // --- Caso 1: 1D para (n,1)
        double[] x = {0, 1, 2};
        double[][][] result1 = Utils.expandTo(x, 2, new int[]{0});
        System.out.println("Shape (n,1): " + Arrays.deepToString(result1));

        // --- Caso 2: 1D para (1,n)
        double[][][] result2 = Utils.expandTo(x, 2, new int[]{1});
        System.out.println("Shape (1,n): " + Arrays.deepToString(result2));

        // --- Caso 3: 2D para (1,n,m,1)
        double[][] mtx = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8}
        };
        double[][][][] result3 = Utils.expandTo(mtx, 4, new int[]{1, 2});
        System.out.println("Shape (1,n,m,1): " + Arrays.deepToString(result3));
    }
}

