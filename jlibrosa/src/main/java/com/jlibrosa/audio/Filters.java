package com.jlibrosa.audio;
import java.util.Arrays;


public class Filters {

    public static double[] relativeBandwidth(double[] freqs) {
        if (freqs == null || freqs.length <= 1) {
            throw new IllegalArgumentException(
                "2 or more frequencies are required to compute bandwidths. Given freqs="
                    + java.util.Arrays.toString(freqs)
            );
        }

        int n = freqs.length;

        double[] bpo = new double[n];
        double[] logf = new double[n];

        // log2(freqs)
        for (int i = 0; i < n; i++) {
            logf[i] = log2(freqs[i]);
        }

        // Extremidades (reflexão)
        bpo[0] = 1.0 / (logf[1] - logf[0]);
        bpo[n - 1] = 1.0 / (logf[n - 1] - logf[n - 2]);

        // Diferença central
        for (int i = 1; i < n - 1; i++) {
            bpo[i] = 2.0 / (logf[i + 1] - logf[i - 1]);
        }

        // Cálculo do alpha
        double[] alpha = new double[n];
        for (int i = 0; i < n; i++) {
            double exp = Math.pow(2.0, 2.0 / bpo[i]);
            alpha[i] = (exp - 1.0) / (exp + 1.0);
        }

        return alpha;
    }

     // =============================================================
    // Wavelet lengths
    // =============================================================

    public static class WaveletLengthsResult {

        public final double[] lengths;
        public final double fCutoff;

        public WaveletLengthsResult(
                double[] lengths,
                double fCutoff
        ) {
            this.lengths = lengths;wavelet_lengths
            this.fCutoff = fCutoff;
        }
    }

    public static WaveletLengthsResult waveletLengths(
            double[] freqs,
            int sr,
            String window,
            double filterScale,
            Double gamma,
            double[] alpha
    ) {

        // =========================================================
        // Validações
        // =========================================================

        if (filterScale <= 0) {
            throw new IllegalArgumentException(
                    "filter_scale must be positive"
            );
        }

        if (gamma != null && gamma < 0) {
            throw new IllegalArgumentException(
                    "gamma must be non-negative"
            );
        }

        for (double f : freqs) {

            if (f <= 0) {
                throw new IllegalArgumentException(
                        "frequencies must be strictly positive"
                );
            }
        }

        for (int i = 0; i < freqs.length - 1; i++) {

            if (freqs[i] > freqs[i + 1]) {

                throw new IllegalArgumentException(
                        "frequencies must be in ascending order"
                );
            }
        }

        // =========================================================
        // Alpha
        // =========================================================

        if (alpha == null) {
            alpha = relativeBandwidth(freqs);
        }

        // =========================================================
        // Gamma
        // =========================================================

        double[] gammaArr;

        if (gamma == null) {

            gammaArr = new double[alpha.length];

            for (int i = 0; i < alpha.length; i++) {
                gammaArr[i] = alpha[i] * 24.7 / 0.108;
            }

        } else {

            gammaArr = new double[freqs.length];
            Arrays.fill(gammaArr, gamma);
        }

        // =========================================================
        // Q
        // =========================================================

        double[] Q = new double[alpha.length];

        for (int i = 0; i < alpha.length; i++) {
            Q[i] = filterScale / alpha[i];
        }

        // =========================================================
        // f_cutoff
        // =========================================================

        double windowBW = windowBandwidth(window);

        double maxFc = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < freqs.length; i++) {

            double fc =
                    freqs[i]
                    * (1.0 + 0.5 * windowBW / Q[i])
                    + 0.5 * gammaArr[i];

            if (fc > maxFc) {
                maxFc = fc;
            }
        }

        // =========================================================
        // lengths
        // =========================================================

        double[] lengths = new double[freqs.length];

        for (int i = 0; i < freqs.length; i++) {

            lengths[i] =
                    Q[i]
                    * sr
                    / (freqs[i] + gammaArr[i] / alpha[i]);
        }

        return new WaveletLengthsResult(
                lengths,
                maxFc
        );
    }

    // =============================================================
    // Window bandwidths
    // =============================================================

    private static double windowBandwidth(String window) {

        switch (window.toLowerCase()) {

            case "hann":
                return 1.50018310546875;

            case "hamming":
                return 1.3629455320350348;

            case "blackman":
                return 1.7269681554262326;

            case "rect":
            case "boxcar":
                return 1.0;

            default:
                throw new IllegalArgumentException(
                        "Unsupported window: " + window
                );
        }
    }

    // =============================================================
    // Utils
    // =============================================================

    private static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

        
}