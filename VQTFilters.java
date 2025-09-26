import java.util.Arrays;
import org.apache.commons.math3.complex.Complex;


public class VQTFilters {

    public static class FilterResult {
        public Complex[][] fftBasis;
        public int nFft;
        public double[] lengths;

        public FilterResult(Complex[][] fftBasis, int nFft, double[] lengths) {
            this.fftBasis = fftBasis;
            this.nFft = nFft;
            this.lengths = lengths;
        }
    }

    public static FilterResult vqtFilterFFT(
            int sr,
            double[] freqs,
            double filterScale,
            double norm,
            double sparsity,
            Integer hopLength,
            String window,
            double gamma,
            Double alpha
    ) {
        // 1. Gera filtros no tempo (wavelets)
        WaveletResult wavelet = Filters.wavelet(
                freqs, sr, filterScale, norm, true, window, gamma, alpha
        );

        double[][] basis = wavelet.basis;  // filtros no tempo
        double[] lengths = wavelet.lengths;

        int nFft = basis[0].length;

        // 2. Ajusta tamanho do FFT se hop_length exigir
        if (hopLength != null) {
            int minNfft = 1 << (int) Math.ceil(Math.log(hopLength) / Math.log(2) + 1);
            if (nFft < minNfft) {
                nFft = minNfft;
            }
        }

        // 3. Re-normaliza
        for (int i = 0; i < basis.length; i++) {
            for (int j = 0; j < basis[i].length; j++) {
                basis[i][j] *= lengths[i] / (double) nFft;
            }
        }

        // 4. FFT dos filtros
        Complex[][] fftBasis = new Complex[basis.length][nFft / 2 + 1];
        for (int i = 0; i < basis.length; i++) {
            Complex[] fftRow = FFT.fft(basis[i], nFft);  // FFT linha
            fftBasis[i] = Arrays.copyOfRange(fftRow, 0, nFft / 2 + 1);
        }

        // 5. Sparsificação (opcional, pode pular na primeira versão)
        fftBasis = Utils.sparsifyRows(fftBasis, sparsity);

        return new FilterResult(fftBasis, nFft, lengths);
    }
}
