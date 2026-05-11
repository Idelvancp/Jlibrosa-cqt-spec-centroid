package com.jlibrosa.audio;
import org.apache.commons.math3.complex.Complex;
import com.jlibrosa.audio.core.Spectrum;
import com.jlibrosa.audio.Filters;
import java.util.Arrays;

public class TestwaveletLengths {

    public static void main(String[] args) {

        // =====================================================
        // Parâmetros iguais ao librosa
        // =====================================================

        int sr = 22050;

        int nBins = 84;

        double fmin = 32.70319566257483; // C1

        int binsPerOctave = 12;

        String window = "hann";

        double filterScale = 1.0;

        Double gamma = null;

        // =====================================================
        // Frequências iguais ao librosa.cqt_frequencies
        // =====================================================

        double[] freqs = new double[nBins];

        for (int i = 0; i < nBins; i++) {

            freqs[i] =
                    fmin
                    * Math.pow(
                            2.0,
                            (double) i / binsPerOctave
                    );
        }

        // =====================================================
        // Alpha
        // =====================================================

        double[] alpha = Filters.relativeBandwidth(freqs);

        // =====================================================
        // Wavelet lengths
        // =====================================================

        Filters.WaveletLengthsResult result =
                Filters.waveletLengths(
                        freqs,
                        sr,
                        window,
                        filterScale,
                        gamma,
                        alpha
                );

        // =====================================================
        // Impressão
        // =====================================================

        System.out.println("=================================================");
        System.out.println("FREQUENCIES");
        System.out.println("=================================================");

        printArray(freqs);

        System.out.println("\n=================================================");
        System.out.println("ALPHA");
        System.out.println("=================================================");

        printArray(alpha);

        System.out.println("\n=================================================");
        System.out.println("LENGTHS");
        System.out.println("=================================================");

        printArray(result.lengths);

        System.out.println("\n=================================================");
        System.out.println("F_CUTOFF");
        System.out.println("=================================================");

        System.out.println(result.fCutoff);
    }

    // =========================================================
    // Pretty print
    // =========================================================

    private static void printArray(double[] arr) {

        for (int i = 0; i < arr.length; i++) {

            System.out.printf(
                    "[%03d] %.15f%n",
                    i,
                    arr[i]
            );
        }
    }
}