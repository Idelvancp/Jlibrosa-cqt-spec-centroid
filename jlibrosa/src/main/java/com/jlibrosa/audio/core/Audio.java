package com.jlibrosa.audio.core;
import com.jlibrosa.audio.util.Utils;

public class Audio {

    /**
     * Resample com parâmetros padrão, considerando apenas áudios mono, por enquanto:
     * resType = "soxr_hq"
     * fix = true
     * scale = false
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

    /**
     * Resample com tipo customizado.
     */
    public static double[] resample(
            double[] y,
            double origSr,
            double targetSr,
            String resType
    ) {
        return resample(
                y,
                origSr,
                targetSr,
                resType,
                true,
                false
        );
    }

    /**
     * Implementação principal do resample.
     */
    public static double[] resample(
            double[] y,
            double origSr,
            double targetSr,
            String resType,
            boolean fix,
            boolean scale
    ) {

        // Validação do áudio
        Utils.validAudio(y);

        // Validação dos sample rates
        if (origSr <= 0) {
            throw new Utils.ParameterError(
                    "origSr must be positive"
            );
        }

        if (targetSr <= 0) {
            throw new Utils.ParameterError(
                    "targetSr must be positive"
            );
        }

        // Evita processamento desnecessário
        if (origSr == targetSr) {
            return y.clone();
        }

        // Razão de conversão
        double ratio = targetSr / origSr;

        int nSamples = (int) Math.ceil(y.length * ratio);

        return y;
    }



    // Implementação equivalente ao Soxr 
    // Número de amostras usadas no kernel sinc
    // Maior = melhor qualidade e mais CPU
    private static final int FILTER_WIDTH = 16;

    /**
     * Resample estilo soxr_hq simplificado.
     */
    public static double[] resample(
            double[] input,
            double inRate,
            double outRate,
            String quality
    ) {

        if (input == null || input.length == 0) {
            return new double[0];
        }

        if (inRate <= 0 || outRate <= 0) {
            throw new IllegalArgumentException(
                    "Sample rates must be positive"
            );
        }

        double ratio = outRate / inRate;

        int outputLength = (int) Math.ceil(input.length * ratio);

        double[] output = new double[outputLength];

        // Ajusta largura do filtro conforme qualidade
        int filterWidth = getFilterWidth(quality);

        for (int i = 0; i < outputLength; i++) {

            // posição no sinal original
            double sourceIndex = i / ratio;

            int center = (int) Math.floor(sourceIndex);

            double sum = 0.0;
            double norm = 0.0;

            // sinc windowed interpolation
            for (int k = -filterWidth; k <= filterWidth; k++) {

                int sampleIndex = center + k;

                if (sampleIndex < 0 || sampleIndex >= input.length) {
                    continue;
                }

                double distance = sourceIndex - sampleIndex;

                // sinc
                double sinc = sinc(distance);

                // janela Hann
                double window = hannWindow(
                        distance,
                        filterWidth
                );

                double weight = sinc * window;

                sum += input[sampleIndex] * weight;
                norm += weight;
            }

            if (norm != 0.0) {
                output[i] = sum / norm;
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