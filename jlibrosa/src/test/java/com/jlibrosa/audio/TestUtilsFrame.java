package com.jlibrosa.audio;
import com.jlibrosa.audio.util.Utils;
import java.util.Arrays;

public class TestUtilsFrame {
    public static void main(String[] args) {
        double[] x = {0, 1, 2, 3, 4, 5, 6};

        double[][] frames = Utils.frame(x, 3, 2);

        System.out.println("Frames:");
        for (double[] f : frames)
            System.out.println(Arrays.toString(f));

        // Caso estéreo
        double[][] stereo = {
            {0, 1, 2, 3, 4, 5, 6},
            {10, 11, 12, 13, 14, 15, 16}
        };
        double[][][] stereoFrames = Utils.frame(stereo, 3, 2);

        System.out.println("\nFrames estéreo:");
        for (int ch = 0; ch < stereoFrames.length; ch++) {
            System.out.println("Canal " + ch + ":");
            for (double[] f : stereoFrames[ch]) {
                System.out.println(Arrays.toString(f));
            }
        }
    }
 }
