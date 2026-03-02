package com.jlibrosa.audio;
import org.apache.commons.math3.complex.Complex;
import com.jlibrosa.audio.core.Spectrum;

public class TestSTFTSENO {
    public static void main(String[] args) {
        try {
            
            int n_fft = 2048;
            int hop_length = 512;
            double sr = 22050.0;

            // 1️⃣ Criar sinal de teste (SENO 440 Hz)
            int length = 4096;
            double[] yOk = new double[length];

            double freq = 440.0;
            for (int i = 0; i < length; i++) {
                yOk[i] = Math.sin(2 * Math.PI * freq * i / sr);
            }

            // 2️⃣ Chamar sua STFT
            Complex[][] stftMatrix = Spectrum.stft(
                yOk,
                n_fft,
                hop_length,
                n_fft,
                "hann",
                true,
                "constant"
            );

            // 3️⃣ TESTE DO BIN ESPERADO
            int expectedBin = (int)Math.round(freq * n_fft / sr);
            System.out.println("Bin esperado: " + expectedBin);

            int frameToInspect = stftMatrix[0].length / 2; // frame central

            for (int k = expectedBin - 2; k <= expectedBin + 2; k++) {
                double mag = stftMatrix[k][frameToInspect].abs();
                System.out.printf("Bin %d: %.6f%n", k, mag);
            }
        } catch (Exception e) {
            System.err.println("Erro geral: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

