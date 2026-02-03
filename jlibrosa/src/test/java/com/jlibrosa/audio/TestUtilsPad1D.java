package com.jlibrosa.audio;
import com.jlibrosa.audio.util.Utils;
public class TestUtilsPad1D {
    public static void main(String[] args) {
        double[] x = {1, 2, 3, 4};

        double[] y = Utils.pad1D(x, 2, 3, 0.0);

        for (double v : y) {
            System.out.print(v + " ");
        }
    }
}

