package com.jlibrosa.audio.core;
import com.jlibrosa.audio.core.Spectrum;


public class Pitch {

    /**
     * Estimate the tuning of an audio time series or spectrogram input.
     *
     * @param y               Audio signal (can be null if S is provided)
     * @param sr              Sample rate of y
     * @param S               Magnitude or power spectrogram (can be null if y is provided)
     * @param nFft            FFT size (used when y is provided)
     * @param resolution      Resolution of tuning estimation (fraction of a bin)
     * @param binsPerOctave   Number of bins per octave
     * @param kwargs          Additional optional parameters (similar to **kwargs in Python)
     * @return tuning deviation in range [-0.5, 0.5)
     */

    public static double estimateTuning(
            double[] y,
            double sr,
            int binsPerOctave
    ) {
        return estimateTuning(
                y,              // y
                sr,             // sr
                null,           // S
                2048,           // n_fft default librosa
                0.01,           // resolution default librosa
                binsPerOctave,
                new java.util.HashMap<>()
        );
    }
    public static double estimateTuning(
            double[] y,
            double sr,
            double[][] S,
            Integer nFft,
            double resolution,
            int binsPerOctave,
            java.util.Map<String, Object> kwargs
    ) {
        // implementação aqui
        return 0.0;
    }

    public static class PiptrackResult {
        public double[][] pitches;
        public double[][] magnitudes;

        public PiptrackResult(double[][] pitches, double[][] magnitudes) {
            this.pitches = pitches;
            this.magnitudes = magnitudes;
        }
    }

    public static PiptrackResult piptrack(
            double[] y,
            double sr,
            double[][] S,
            Integer nFft,
            Integer hopLength,
            double fmin,
            double fmax,
            double threshold,
            Integer winLength,
            String window,
            boolean center,
            String padMode,
            Object ref
    ) {

        if (S == null && y == null) {
            throw new IllegalArgumentException("Either y or S must be provided");
        }

        if (nFft == null) {
            nFft = 2048;
        }

        if (hopLength == null) {
            hopLength = nFft / 4;
        }

        if (winLength == null) {
            winLength = nFft;
        }

            // equivalente ao _spectrogram
        // ============================
        Spectrum.SpectrogramResult spec = Spectrum.spectrogram(
                y,
                S,
                nFft,
                hopLength,
                1.0,
                winLength,
                window,
                center,
                padMode
        );

        S = spec.S;
        nFft = spec.n_fft;

        // daqui pra frente usa S normalmente
        int rows = S.length;
        int cols = S[0].length;

        double[][] pitches = new double[0][0];
        double[][] magnitudes = new double[0][0];

        return new PiptrackResult(pitches, magnitudes);
    }
}
