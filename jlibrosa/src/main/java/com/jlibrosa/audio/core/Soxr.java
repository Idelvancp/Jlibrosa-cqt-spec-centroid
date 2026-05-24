package com.jlibrosa.audio.core;

public class Soxr {

    /*
     * Número de fases polyphase.
     *
     * 1024 ficou mais próximo do soxr_hq
     * nos testes realizados.
     */
    private static final int PHASES = 1024;

    /*
     * Cutoff do low-pass filter.
     */
    private static final double CUTOFF_SCALE = 0.95;

    /**
     * Resampler estilo soxr_hq em Java.
     *
     * Implementa:
     * - Polyphase FIR
     * - Bandlimited sinc
     * - Kaiser window
     * - Low-pass filtering
     * - Pré-computação de kernels
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

        /*
         * Comprimento alinhado ao comportamento
         * do librosa/soxr.
         */
        int outputLength = (int)(
                input.length * ratio
        );

        if (outputLength <= 0) {
            return new double[0];
        }

        double[] output = new double[outputLength];

        int taps = getFilterTaps(quality);

        /*
         * Low-pass cutoff.
         */
        double cutoff =
                CUTOFF_SCALE
                        * Math.min(1.0, ratio);

        /*
         * Banco polyphase FIR.
         */
        double[][] polyphaseBank =
                buildPolyphaseBank(
                        taps,
                        cutoff
                );

        for (int i = 0; i < outputLength; i++) {

            /*
             * Posição fracionária no sinal original.
             */
            double sourcePos = i / ratio;

            /*
             * floor() gera melhor alinhamento
             * que round().
             */
            int center =
                    (int)Math.floor(sourcePos);

            double frac =
                    sourcePos - center;

            /*
             * Corrige fase negativa.
             */
            if (frac < 0.0) {
                frac += 1.0;
            }

            int phase =
                    (int)(frac * PHASES);

            if (phase >= PHASES) {
                phase = PHASES - 1;
            }

            double[] kernel =
                    polyphaseBank[phase];

            double sum = 0.0;

            for (int t = 0; t < taps; t++) {

                int sampleIndex =
                        center
                        + t
                        - taps / 2;

                if (sampleIndex < 0
                        || sampleIndex >= input.length) {
                    continue;
                }

                sum += input[sampleIndex]
                        * kernel[t];
            }

            output[i] = sum;
        }

        return output;
    }

    /**
     * Cria banco de kernels polyphase.
     */
    private static double[][] buildPolyphaseBank(
            int taps,
            double cutoff
    ) {

        double[][] bank =
                new double[PHASES][taps];

        for (int phase = 0;
             phase < PHASES;
             phase++) {

            double frac =
                    (double)phase / PHASES;

            double norm = 0.0;

            for (int i = 0;
                 i < taps;
                 i++) {

                /*
                 * Centro do kernel.
                 */
                double x =
                        i
                        - taps / 2.0
                        - frac;

                /*
                 * sinc bandlimited.
                 */
                double sinc =
                        sinc(x * cutoff)
                                * cutoff;

                /*
                 * Kaiser window.
                 */
                double window =
                        kaiserWindow(
                                i,
                                taps,
                                8.6
                        );

                double coeff =
                        sinc * window;

                bank[phase][i] =
                        coeff;

                norm += coeff;
            }

            /*
             * Normalização FIR.
             */
            if (norm != 0.0) {

                for (int i = 0;
                     i < taps;
                     i++) {

                    bank[phase][i] /= norm;
                }
            }
        }

        return bank;
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
     * Janela Kaiser.
     */
    private static double kaiserWindow(
            int n,
            int taps,
            double beta
    ) {

        double ratio =
                (2.0 * n)
                        / (taps - 1.0)
                        - 1.0;

        double value =
                beta
                        * Math.sqrt(
                        1.0
                                - ratio * ratio
                );

        return besselI0(value)
                / besselI0(beta);
    }

    /**
     * Aproximação da função Bessel I0.
     */
    private static double besselI0(double x) {

        double sum = 1.0;
        double term = 1.0;

        double xsq =
                (x * x) / 4.0;

        for (int k = 1;
             k < 30;
             k++) {

            term *=
                    xsq / (k * k);

            sum += term;
        }

        return sum;
    }

    /**
     * Número de taps FIR por qualidade.
     */
    private static int getFilterTaps(
            String quality
    ) {

        if (quality == null) {
            return 64;
        }

        switch (quality.toLowerCase()) {

            case "soxr_qq":
                return 16;

            case "soxr_lq":
                return 32;

            case "soxr_mq":
                return 48;

            case "soxr_hq":
                return 64;

            case "soxr_vhq":
                return 128;

            default:
                return 64;
        }
    }
}
