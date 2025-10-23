
package com.jlibrosa.audio;
import com.jlibrosa.audio.util.Utils;
import java.util.Arrays;

public class TestUtilsPadCenter {
    public static void main(String[] args) {
        // --- Vetor 1D ---
        double[] data1 = {1, 1, 1, 1, 1};
        double[] padded1 = Utils.padCenter(data1, 10);
        System.out.println("1D: " + Arrays.toString(padded1));

        // --- Matriz 2D (axis = 0) ---
        double[][] data2 = {
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1}
        };
        double[][] padded2 = Utils.padCenter(data2, 7, 0);
        System.out.println("\n2D (axis=0):");
        for (double[] row : padded2)
            System.out.println(Arrays.toString(row));

        // --- Matriz 2D (axis = 1) ---
        double[][] padded3 = Utils.padCenter(data2, 7, 1);
        System.out.println("\n2D (axis=1):");
        for (double[] row : padded3)
            System.out.println(Arrays.toString(row));
    }
}

