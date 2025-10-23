package com.jlibrosa.audio;

import com.jlibrosa.audio.core.Spectrum;
import org.apache.commons.math3.complex.Complex;
import java.util.Locale;

public class TestSpectrum {
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US); // força o ponto como separador decimal

        // Caminho do arquivo WAV (mude para o seu arquivo de teste)
        String wavPath = "audioFiles/1995-1826-0003.wav";

        // Instância do JLibrosa
        JLibrosa jLibrosa = new JLibrosa();

        int sr = 22050;
        float[] yFloat = jLibrosa.loadAndRead(wavPath, sr, -1);

        int n_fft = 2048;
        Complex[][] stft = jLibrosa.generateSTFTFeatures(yFloat, sr, 13, n_fft, 40, 256);        // Carregar o áudio

        // Mostrar informações
	System.out.println("STFT (JLibrosa):");
        System.out.println("Dimensões: " + stft.length + " x " + stft[0].length);
        for (int i = 0; i < Math.min(5, stft.length); i++) {
            for (int j = 0; j < Math.min(5, stft[i].length); j++) {
                Complex c = stft[i][j];
                System.out.printf("(%.6f, %.6f) ", c.getReal(), c.getImaginary());
            }
            System.out.println();
        }
    }
}

