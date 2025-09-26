import java.util.Arrays;

public class WaveletLengths {

    public static class Result {
        public double[] lengths;
        public double fCutoff;

        public Result(double[] lengths, double fCutoff) {
            this.lengths = lengths;
            this.fCutoff = fCutoff;
        }
    }

    public static Result compute(
            double[] freqs,
            int sr,
            String window,
            double filterScale,
            Double gamma,
            double[] alpha
    ) {
        // 1. Checagens
        if (filterScale <= 0) throw new IllegalArgumentException("filter_scale must be positive");
        if (gamma != null && gamma < 0) throw new IllegalArgumentException("gamma must be non-negative");
        for (double f : freqs) if (f <= 0) throw new IllegalArgumentException("frequencies must be positive");
        for (int i = 0; i < freqs.length - 1; i++)
            if (freqs[i] >= freqs[i + 1]) throw new IllegalArgumentException("frequencies must be ascending");

        // 2. alpha
        if (alpha == null) alpha = relativeBandwidth(freqs);

        // 3. gamma
        double[] gammaArr;
        if (gamma == null) {
            gammaArr = new double[alpha.length];
            for (int i = 0; i < alpha.length; i++) {
                gammaArr[i] = alpha[i] * 24.7 / 0.108;
            }
        } else {
            gammaArr = new double[freqs.length];
            Arrays.fill(gammaArr, gamma);
        }

        // 4. Q
        double[] Q = new double[alpha.length];
        for (int i = 0; i < alpha.length; i++) {
            Q[i] = filterScale / alpha[i];
        }

        // 5. Frequência de corte
        double maxFc = Double.NEGATIVE_INFINITY;
        double windowBW = windowBandwidth(window);
        for (int i = 0; i < freqs.length; i++) {
            double fc = freqs[i] * (1 + 0.5 * windowBW / Q[i]) + 0.5 * gammaArr[i];
            if (fc > maxFc) maxFc = fc;
        }

        // 6. Comprimento do filtro
        double[] lengths = new double[freqs.length];
        for (int i = 0; i < freqs.length; i++) {
            lengths[i] = Q[i] * sr / (freqs[i] + gammaArr[i] / alpha[i]);
        }

        return new Result(lengths, maxFc);
    }

    // Função auxiliar: largura relativa da janela
    private static double windowBandwidth(String window) {
        switch (window.toLowerCase()) {
            case "hann": return 1.5; // valor aproximado
            case "hamming": return 1.36;
            default: return 1.0;
        }
    }

    // Função auxiliar: calcula alpha
    private static double[] relativeBandwidth(double[] freqs) {
   	 int n = freqs.length;
   	 if (n <= 1) throw new IllegalArgumentException("Need at least 2 frequencies");

   	 double[] bpo = new double[n];
   	 double[] logf = new double[n];
   	 for (int i = 0; i < n; i++) {
   	     logf[i] = Math.log(freqs[i]) / Math.log(2.0); // log base 2
   	 }

   	 // extremos
   	 bpo[0] = 1.0 / (logf[1] - logf[0]);
   	 bpo[n - 1] = 1.0 / (logf[n - 1] - logf[n - 2]);

   	 // valores intermediários
   	 for (int i = 1; i < n - 1; i++) {
   	     bpo[i] = 2.0 / (logf[i + 1] - logf[i - 1]);
   	 }

   	 // converte bpo → alpha
   	 double[] alpha = new double[n];
   	 for (int i = 0; i < n; i++) {
   	     double val = Math.pow(2.0, 2.0 / bpo[i]);
   	     alpha[i] = (val - 1) / (val + 1);
   	 }

   	 return alpha;
    }

}
