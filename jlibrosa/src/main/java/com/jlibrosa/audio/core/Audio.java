package com.jlibrosa.audio.core;
import com.jlibrosa.audio.util.Utils;

public class Audio {

    /**
     * Resample com parâmetros padrão, considerando apenas áudios mono, por enquanto:
     * resType = "soxr_hq"
     * fix = true
     * scale = false
     */
     /**
     * Resample com parâmetros padrão.
     */
    public static double[] resample(
            double[] y,
            double origSr,
            double targetSr
    ) {

        return resample(
                y,
                origSr,
                targetSr,
                "soxr_hq",
                true,
                false
        );
    }

    // =========================================================
    // Implementação principal
    // =========================================================

    /**
     * Implementação principal do resample.
     *
     * Equivalente aproximado ao:
     *
     * librosa.audio.resample(...)
     */
    public static double[] resample(
            double[] input,
            double inRate,
            double outRate,
            String quality,
            boolean fix,
            boolean scale
    ) {

        // -----------------------------------------
        // Validação
        // -----------------------------------------

        Utils.validAudio(input);

        if (inRate <= 0) {
            throw new Utils.ParameterError(
                    "origSr must be positive"
            );
        }

        if (outRate <= 0) {
            throw new Utils.ParameterError(
                    "targetSr must be positive"
            );
        }

        if (input == null || input.length == 0) {
            return new double[0];
        }

        // -----------------------------------------
        // Sem necessidade de resample
        // -----------------------------------------

        if (inRate == outRate) {
            return input.clone();
        }

        // -----------------------------------------
        // Razão de conversão
        // -----------------------------------------

        double ratio = outRate / inRate;

        int outputLength;

        if (fix) {
            outputLength =
                    (int) Math.ceil(input.length * ratio);
        } else {
            outputLength =
                    (int) (input.length * ratio);
        }

        if (outputLength <= 0) {
            outputLength = 1;
        }

        double[] output = new double[outputLength];

        // -----------------------------------------
        // Ajusta qualidade
        // -----------------------------------------

        int filterWidth = getFilterWidth(quality);

        // -----------------------------------------
        // Windowed sinc interpolation
        // -----------------------------------------

        for (int i = 0; i < outputLength; i++) {

            // posição correspondente no sinal original
            double sourceIndex = i / ratio;

            int center = (int) Math.floor(sourceIndex);

            double sum = 0.0;
            double norm = 0.0;

            for (int k = -filterWidth; k <= filterWidth; k++) {

                int sampleIndex = center + k;

                if (sampleIndex < 0
                        || sampleIndex >= input.length) {
                    continue;
                }

                double distance =
                        sourceIndex - sampleIndex;

                // sinc
                double sincValue = sinc(distance);

                // janela Hann
                double window =
                        hannWindow(distance, filterWidth);

                double weight =
                        sincValue * window;

                sum += input[sampleIndex] * weight;
                norm += weight;
            }

            if (norm != 0.0) {
                output[i] = sum / norm;
            }
        }

        // -----------------------------------------
        // scale=True no librosa
        // -----------------------------------------

        if (scale) {

            double gain =
                    Math.sqrt(outRate / inRate);

            for (int i = 0; i < output.length; i++) {
                output[i] *= gain;
            }
        }

        return output;
    }

    /**
     * sinc(x) = sin(pi*x)/(pi*x)
     */
    private static double sinc(double x) {

        if (x == 0.0) {
            return 1.0;
        }

        double pix = Math.PI * x;

        return Math.sin(pix) / pix;
    }

    /**
     * Janela Hann
     */
    private static double hannWindow(
            double x,
            int filterWidth
    ) {

        double normalized = Math.abs(x) / filterWidth;

        if (normalized > 1.0) {
            return 0.0;
        }

        return 0.5 * (
                1.0 + Math.cos(Math.PI * normalized)
        );
    }

    /**
     * Aproxima níveis de qualidade do soxr.
     */
    private static int getFilterWidth(String quality) {

        if (quality == null) {
            return 16;
        }

        switch (quality.toLowerCase()) {

            case "soxr_qq":
                return 4;

            case "soxr_lq":
                return 8;

            case "soxr_mq":
                return 12;

            case "soxr_hq":
                return 16;

            case "soxr_vhq":
                return 24;

            default:
                return 16;
        }
    }


}