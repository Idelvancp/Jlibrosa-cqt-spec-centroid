package com.jlibrosa.audio;
import com.jlibrosa.audio.util.Utils;

public class TesteUtilsGetWindow {
    public static void main(String[] args) {
        int Nx = 16;

        double[] hann = Utils.getWindow("hann", Nx, true);
        double[] hamming = Utils.getWindow("hamming", Nx, true);
        double[] kaiser = Utils.getWindow(new Object[]{"kaiser", 14.0}, Nx, true);

        System.out.println("Hann:");
        printArray(hann);
        System.out.println("\nHamming:");
        printArray(hamming);
        System.out.println("\nKaiser:");
        printArray(kaiser);
    }

    private static void printArray(double[] arr) {
        for (double v : arr) {
            System.out.printf("%.6f ", v);
        }
        System.out.println();
    }
}

