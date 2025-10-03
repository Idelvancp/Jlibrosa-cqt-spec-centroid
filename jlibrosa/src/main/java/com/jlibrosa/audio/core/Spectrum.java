package com.jlibrosa.audio.core;

import com.jlibrosa.audio.JLibrosa;
import org.apache.commons.math3.complex.Complex;

public class Spectrum {

    /**
     * Retrieve a magnitude or power spectrogram using JLibrosa.
     *
     * @param y           Audio time series (double[])
     * @param n_fft       FFT window size
     * @param hop_length  Hop length (currently unused in JLibrosa)
     * @param power       Exponent for the magnitude spectrogram (1=magnitude, 2=power)
     * @param sampleRate  Sample rate of the audio
     * @return double[][] Spectrogram matrix
     * @throws Exception
     */
    public static double[][] spectrogram(double[] y,
                                         int n_fft,
                                         int hop_length,
                                         double power,
                                         int sampleRate) throws Exception {

        if (y == null) {
            throw new IllegalArgumentException("Input signal (y) must be provided.");
        }

        // Converter de double[] para float[], exigido pelo JLibrosa
        float[] yFloat = new float[y.length];
        for (int i = 0; i < y.length; i++) {
            yFloat[i] = (float) y[i];
        }

        JLibrosa jLibrosa = new JLibrosa();
        Complex[][] stftComplex = jLibrosa.generateSTFTFeatures(yFloat, sampleRate, n_fft);

        int rows = stftComplex.length;
        int cols = stftComplex[0].length;
        double[][] specOut = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double mag = stftComplex[i][j].abs(); // magnitude
                if (power == 1.0) {
                    specOut[i][j] = mag; // magnitude
                } else if (power == 2.0) {
                    specOut[i][j] = mag * mag; // power spectrum
                } else {
                    specOut[i][j] = Math.pow(mag, power);
                }
            }
        }

        return specOut;
    }
}

