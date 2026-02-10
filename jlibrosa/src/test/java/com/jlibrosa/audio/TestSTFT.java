package com.jlibrosa.audio;

import com.jlibrosa.audio.core.Spectrum;

public class TestSTFT {
    public static void main(String[] args) {
        try {
            // Cria um sinal de áudio simples (exemplo: uma senoide)
            int sampleRate = 22050;
            double duration = 0.3; // 0.5 segundo
            double freq = 440.0;   // tom de 440 Hz (Lá)
            int nSamples = (int) (sampleRate * duration);
            double[] y = new double[nSamples];

            for (int i = 0; i < nSamples; i++) {
                y[i] = Math.sin(2 * Math.PI * freq * i / sampleRate);
            }

            // sinal MUITO curto
            double[] yCurto = new double[500];
            for (int i = 0; i < yCurto.length; i++) {
                yCurto[i] = Math.sin(2 * Math.PI * 440 * i / sampleRate);
            }

            double[] yOk = new double[4096];
            for (int i = 0; i < yOk.length; i++) {
                yOk[i] = Math.sin(2 * Math.PI * 440 * i / sampleRate);
            }


            // Teste 1: parâmetros válidos
            System.out.println("=== Teste 1: parâmetros válidos ===");
            System.out.println("Comprimento original do sinal: " + yOk.length);
            Spectrum.stft(yOk, 2048, null, null, "hann", true, "constant");
            System.out.println("Passou! Nenhum erro de validação.");

         /*   // Teste 2: hop_length inválido
            System.out.println("\n=== Teste 2: hop_length inválido ===");
            try {
                Spectrum.stft(y, 2048, -10, null, "hann", true, "constant");
                System.out.println("ERRO: deveria ter lançado exceção!");
            } catch (IllegalArgumentException e) {
                System.out.println("Capturado corretamente: " + e.getMessage());
            }

            // Teste 3: sinal nulo
            System.out.println("\n=== Teste 3: sinal nulo ===");
            try {
                Spectrum.stft(null, 2048, 512, null, "hann", true, "constant");
                System.out.println("ERRO: deveria ter lançado exceção!");
            } catch (IllegalArgumentException e) {
                System.out.println("Capturado corretamente: " + e.getMessage());
            }
*/
        } catch (Exception e) {
            System.err.println("Erro geral: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

