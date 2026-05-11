package com.jlibrosa.audio;

import com.jlibrosa.audio.Filters;
import java.util.Arrays;

public class TestRelativeBandwidth {

    public static void main(String[] args) {

        // Frequências de teste (mesmas que você usará no Python)
        double[] freqs = new double[] {
            32.70, 65.41, 130.81, 261.63, 523.25, 1046.50
        };

        double[] alpha = Filters.relativeBandwidth(freqs);

        System.out.println("Frequencies:");
        System.out.println(Arrays.toString(freqs));

        System.out.println("\nAlpha (Java):");
        System.out.println(Arrays.toString(alpha));

        // Valores esperados (cole aqui depois de rodar no Python)
        double[] expected = new double[] {
            // COLE AQUI OS VALORES DO PYTHON
        };

        // Comparação com tolerância
        if (expected.length > 0) {
            System.out.println("\nComparação com Python:");

            for (int i = 0; i < alpha.length; i++) {
                double diff = Math.abs(alpha[i] - expected[i]);

                System.out.printf(
                    "i=%d | Java=%.10f | Python=%.10f | Diff=%.10f%n",
                    i, alpha[i], expected[i], diff
                );
            }
        }
    }
}