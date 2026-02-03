package com.jlibrosa.audio;
import com.jlibrosa.audio.util.Utils;
public class TestUtilsPad2D {
    public static void main(String[] args) {
        double[][] X = {
            {1, 2, 3},
            {4, 5, 6}
        };

        int padTop = 1;
        int padBottom = 2;
        int padLeft = 2;
        int padRight = 1;

        double[][] Y = Utils.pad2D(
                X,
                padTop,
                padBottom,
                padLeft,
                padRight,
                0.0
        );

        // Imprime matriz
        for (double[] row : Y) {
            for (double v : row) {
                System.out.printf("%.1f ", v);
            }
            System.out.println();
        }
    }
}

