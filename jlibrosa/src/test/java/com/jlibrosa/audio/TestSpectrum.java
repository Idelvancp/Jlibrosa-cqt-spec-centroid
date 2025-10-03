package com.jlibrosa.audio;

import com.jlibrosa.audio.core.Spectrum;
import java.util.Locale;

public class TestSpectrum {
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US); // força o ponto como separador decimal

        // Caminho do arquivo WAV (mude para o seu arquivo de teste)
        String wavPath = "audioFiles/1995-1826-0003.wav";

        // Instância do JLibrosa
        JLibrosa jLibrosa = new JLibrosa();

        // Carregar o áudio
        int sr = 22050; // taxa de amostragem padrão do librosa
        float[] yFloat = jLibrosa.loadAndRead(wavPath, sr, -1);

        // Converter para double[]
        double[] y = new double[yFloat.length];
        for (int i = 0; i < yFloat.length; i++) {
            y[i] = yFloat[i];
        }

        // Parâmetros iguais aos do Librosa
        int n_fft = 2048;
        int hop_length = 512;
        double power = 2.0; // espectrograma de potência (|X|^2)

        // Calcular espectrograma
        double[][] spec = Spectrum.spectrogram(y, n_fft, hop_length, power, sr);

        // Mostrar informações
        System.out.println("Espectrograma gerado:");
        System.out.println("Dimensões: " + spec.length + " x " + spec[0].length);
        System.out.println("Primeiros valores:");
        for (int i = 0; i < Math.min(5, spec.length); i++) {
            for (int j = 0; j < Math.min(5, spec[i].length); j++) {
                System.out.printf("%.6f ", spec[i][j]);
            }
            System.out.println();
        }
    }
}

