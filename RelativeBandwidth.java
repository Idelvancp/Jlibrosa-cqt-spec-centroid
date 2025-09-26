import java.util.Arrays;

public class RelativeBandwidth {

    public static double[] compute(double[] freqs) {
        if (freqs.length <= 1) {
            throw new IllegalArgumentException(
                "2 ou mais frequências são necessárias para calcular a largura de banda. Recebido freqs=" + Arrays.toString(freqs)
            );
        }

        double[] bpo = new double[freqs.length];
        double[] logf = new double[freqs.length];

        // log base 2 de cada frequência
        for (int i = 0; i < freqs.length; i++) {
            logf[i] = Math.log(freqs[i]) / Math.log(2.0);
        }

        // refletir na menor e maior frequência
        bpo[0] = 1.0 / (logf[1] - logf[0]);
        bpo[freqs.length - 1] = 1.0 / (logf[freqs.length - 1] - logf[freqs.length - 2]);

        // diferenças centradas para os valores internos
        for (int i = 1; i < freqs.length - 1; i++) {
            bpo[i] = 2.0 / (logf[i + 1] - logf[i - 1]);
        }

        // calcular alpha
        double[] alpha = new double[freqs.length];
        for (int i = 0; i < freqs.length; i++) {
            double exp = Math.pow(2.0, 2.0 / bpo[i]);
            alpha[i] = (exp - 1.0) / (exp + 1.0);
        }

        return alpha;
    }

    // Teste rápido
    public static void main(String[] args) {
        double[] freqs = {55.0, 110.0, 220.0, 440.0};
        double[] result = compute(freqs);
        System.out.println(Arrays.toString(result));
    }
}

