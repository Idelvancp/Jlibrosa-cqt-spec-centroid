package com.jlibrosa.audio;

import com.jlibrosa.audio.core.Constantq;
import com.jlibrosa.audio.util.Utils;

public class TestEarlyDownsample {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("TESTES earlyDownsample");
        System.out.println("========================================");

        try {

            testNumTwoFactors();

            testEarlyDownsampleCount();

            testEarlyDownsampleCountZero();

            testEarlyDownsampleChangesSrAndHopLength();

            testEarlyDownsampleThrowsWhenSignalTooShort();

            testEarlyDownsampleWithoutScale();

            System.out.println("\n========================================");
            System.out.println("TODOS OS TESTES PASSARAM");
            System.out.println("========================================");

        } catch (Exception e) {

            System.err.println("\n========================================");
            System.err.println("FALHA NOS TESTES");
            System.err.println("========================================");

            e.printStackTrace();
        }
    }

    // =====================================================
    // numTwoFactors
    // =====================================================

    private static void testNumTwoFactors() {

        System.out.println("\n[TEST] numTwoFactors");

        assertEquals(
                0,
                Constantq.numTwoFactors(0),
                "numTwoFactors(0)"
        );

        assertEquals(
                0,
                Constantq.numTwoFactors(-4),
                "numTwoFactors(-4)"
        );

        assertEquals(
                0,
                Constantq.numTwoFactors(3),
                "numTwoFactors(3)"
        );

        assertEquals(
                1,
                Constantq.numTwoFactors(2),
                "numTwoFactors(2)"
        );

        assertEquals(
                2,
                Constantq.numTwoFactors(4),
                "numTwoFactors(4)"
        );

        assertEquals(
                3,
                Constantq.numTwoFactors(8),
                "numTwoFactors(8)"
        );

        assertEquals(
                4,
                Constantq.numTwoFactors(16),
                "numTwoFactors(16)"
        );

        assertEquals(
                3,
                Constantq.numTwoFactors(40),
                "numTwoFactors(40)"
        );

        System.out.println("[OK] numTwoFactors");
    }

    // =====================================================
    // earlyDownsampleCount
    // =====================================================

    private static void testEarlyDownsampleCount() {

        System.out.println("\n[TEST] earlyDownsampleCount");

        double nyquist = 11025.0;
        double filterCutoff = 2000.0;

        int hopLength = 512;
        int nOctaves = 6;

        int result = Constantq.earlyDownsampleCount(
                nyquist,
                filterCutoff,
                hopLength,
                nOctaves
        );

        System.out.println("Resultado: " + result);

        assertEquals(
                1,
                result,
                "earlyDownsampleCount"
        );

        System.out.println("[OK] earlyDownsampleCount");
    }

    private static void testEarlyDownsampleCountZero() {

        System.out.println("\n[TEST] earlyDownsampleCountZero");

        double nyquist = 11025.0;
        double filterCutoff = 10000.0;

        int hopLength = 32;
        int nOctaves = 8;

        int result = Constantq.earlyDownsampleCount(
                nyquist,
                filterCutoff,
                hopLength,
                nOctaves
        );

        System.out.println("Resultado: " + result);

        assertEquals(
                0,
                result,
                "earlyDownsampleCountZero"
        );

        System.out.println("[OK] earlyDownsampleCountZero");
    }

    // =====================================================
    // earlyDownsample
    // =====================================================

    private static void testEarlyDownsampleChangesSrAndHopLength() {

        System.out.println("\n[TEST] earlyDownsampleChangesSrAndHopLength");

        double[] y = new double[8192];

        double sr = 22050.0;
        int hopLength = 512;

        int nOctaves = 6;

        double nyquist = sr / 2.0;
        double filterCutoff = 2000.0;

        Constantq.EarlyDownsampleResult result =
                Constantq.earlyDownsample(
                        y,
                        sr,
                        hopLength,
                        "soxr_hq",
                        nOctaves,
                        nyquist,
                        filterCutoff,
                        true
                );

        System.out.println("Novo SR: " + result.sr);

        System.out.println("Novo hopLength: " + result.hopLength);

        System.out.println("Novo tamanho y: " + result.y.length);

        assertEqualsDouble(
                22050.0 / 2.0,
                result.sr,
                0.0001,
                "sample rate"
        );

        assertEquals(
                512 / 2,
                result.hopLength,
                "hopLength"
        );

        assertTrue(
                result.y.length > 0,
                "audio resultante vazio"
        );

        System.out.println("[OK] earlyDownsampleChangesSrAndHopLength");
    }

    private static void testEarlyDownsampleThrowsWhenSignalTooShort() {

        System.out.println("\n[TEST] earlyDownsampleThrowsWhenSignalTooShort");

        double[] y = new double[1];

        double sr = 22050.0;
        int hopLength = 512;

        int nOctaves = 6;

        double nyquist = sr / 2.0;
        double filterCutoff = 2000.0;

        boolean exceptionThrown = false;

        try {

            Constantq.earlyDownsample(
                    y,
                    sr,
                    hopLength,
                    "soxr_hq",
                    nOctaves,
                    nyquist,
                    filterCutoff,
                    true
            );

        } catch (Utils.ParameterError e) {

            exceptionThrown = true;

            System.out.println("Exceção esperada capturada:");
            System.out.println(e.getMessage());
        }

        assertTrue(
                exceptionThrown,
                "esperava ParameterError"
        );

        System.out.println("[OK] earlyDownsampleThrowsWhenSignalTooShort");
    }

    private static void testEarlyDownsampleWithoutScale() {

        System.out.println("\n[TEST] earlyDownsampleWithoutScale");

        double[] y = new double[8192];

        double sr = 22050.0;

        double freq = 440.0;

        for (int i = 0; i < y.length; i++) {

            y[i] = Math.sin(
                    2.0 * Math.PI * freq * i / sr
            );
        }

        int hopLength = 512;

        int nOctaves = 6;

        double nyquist = sr / 2.0;
        double filterCutoff = 2000.0;

        double rmsOriginal = computeRMS(y);

        Constantq.EarlyDownsampleResult result =
                Constantq.earlyDownsample(
                        y,
                        sr,
                        hopLength,
                        "soxr_hq",
                        nOctaves,
                        nyquist,
                        filterCutoff,
                        false
                );

        double rmsResampled =
                computeRMS(result.y);

        double peakOriginal =
                computePeak(y);

        double peakResampled =
                computePeak(result.y);

        System.out.println(
                "Primeira amostra: "
                        + result.y[0]
        );

        System.out.println(
                "RMS original: "
                        + rmsOriginal
        );

        System.out.println(
                "RMS resampled: "
                        + rmsResampled
        );

        System.out.println(
                "Pico original: "
                        + peakOriginal
        );

        System.out.println(
                "Pico resampled: "
                        + peakResampled
        );

        assertTrue(
                result.y.length > 0,
                "áudio vazio"
        );

        // quando scale=false
        // o ganho sqrt(fator)
        // deve aumentar a energia do sinal

        assertTrue(
                rmsResampled > rmsOriginal,
                "ganho RMS não aplicado"
        );

        assertTrue(
                peakResampled > peakOriginal,
                "ganho de pico não aplicado"
        );

        System.out.println("[OK] earlyDownsampleWithoutScale");
    }

    // =====================================================
    // AUXILIARES DSP
    // =====================================================

    private static double computeRMS(
            double[] signal
    ) {

        double sum = 0.0;

        for (double v : signal) {
            sum += v * v;
        }

        return Math.sqrt(
                sum / signal.length
        );
    }

    private static double computePeak(
            double[] signal
    ) {

        double peak = 0.0;

        for (double v : signal) {

            double abs = Math.abs(v);

            if (abs > peak) {
                peak = abs;
            }
        }

        return peak;
    }

    // =====================================================
    // ASSERTS AUXILIARES
    // =====================================================

    private static void assertEquals(
            int expected,
            int actual,
            String message
    ) {

        if (expected != actual) {

            throw new RuntimeException(
                    "[FAIL] "
                            + message
                            + " | esperado="
                            + expected
                            + " atual="
                            + actual
            );
        }
    }

    private static void assertEqualsDouble(
            double expected,
            double actual,
            double tolerance,
            String message
    ) {

        if (Math.abs(expected - actual) > tolerance) {

            throw new RuntimeException(
                    "[FAIL] "
                            + message
                            + " | esperado="
                            + expected
                            + " atual="
                            + actual
            );
        }
    }

    private static void assertTrue(
            boolean condition,
            String message
    ) {

        if (!condition) {

            throw new RuntimeException(
                    "[FAIL] " + message
            );
        }
    }
}