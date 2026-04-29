package com.jlibrosa.audio.core;
import com.jlibrosa.audio.core.Spectrum;
import java.util.Arrays;
import java.util.Collection;

public class IntervalFrequencies {

    /**
     * Constrói frequências a partir de um conjunto de intervalos
     *
     * @param nBins número de frequências desejadas
     * @param fmin frequência mínima (>0)
     * @param intervals nome do sistema ("equal", "pythagorean", "ji3", "ji5", "ji7")
     *                  ou array double[] com razões em [1,2)
     * @param binsPerOctave divisões por oitava
     * @param tuning ajuste fracional
     * @param sort ordenar resultado
     * @return array de frequências
     */
    public static double[] intervalFrequencies(
            int nBins,
            double fmin,
            Object intervals,
            int binsPerOctave,
            double tuning,
            boolean sort
    ) {

        double[] ratios;

        if (intervals instanceof String) {

            String mode = (String) intervals;

            switch (mode) {
                case "equal":
                    ratios = new double[binsPerOctave];
                    for (int i = 0; i < binsPerOctave; i++) {
                        ratios[i] = Math.pow(
                                2.0,
                                (tuning + i) / binsPerOctave
                        );
                    }
                    break;

                case "pythagorean":
                    ratios = pythagoreanIntervals(binsPerOctave, sort);
                    break;

                case "ji3":
                    ratios = plimitIntervals(
                            new int[]{3},
                            binsPerOctave,
                            sort
                    );
                    break;

                case "ji5":
                    ratios = plimitIntervals(
                            new int[]{3, 5},
                            binsPerOctave,
                            sort
                    );
                    break;

                case "ji7":
                    ratios = plimitIntervals(
                            new int[]{3, 5, 7},
                            binsPerOctave,
                            sort
                    );
                    break;

                default:
                    throw new IllegalArgumentException("intervals inválido: " + mode);
            }

        } else if (intervals instanceof double[]) {

            ratios = (double[]) intervals;
            binsPerOctave = ratios.length;

        } else {
            throw new IllegalArgumentException("intervals deve ser String ou double[]");
        }

        // ceil(nBins / binsPerOctave)
        int nOctaves = (int) Math.ceil((double) nBins / binsPerOctave);

        double[] allRatios = new double[nOctaves * binsPerOctave];
        int idx = 0;

        for (int octave = 0; octave < nOctaves; octave++) {
            double factor = Math.pow(2.0, octave);

            for (double ratio : ratios) {
                allRatios[idx++] = factor * ratio;
            }
        }

        // corta para nBins
        allRatios = Arrays.copyOf(allRatios, nBins);

        if (sort) {
            Arrays.sort(allRatios);
        }

        // multiplica por fmin
        for (int i = 0; i < allRatios.length; i++) {
            allRatios[i] *= fmin;
        }

        return allRatios;
    }

    // ---------------------------------------------------
    // STUBS - implementar igual ao librosa
    // ---------------------------------------------------

    public static double[] pythagoreanIntervals(int binsPerOctave, boolean sort) {
        throw new UnsupportedOperationException("Implementar pythagoreanIntervals()");
    }





        // ---------------- CACHE ----------------

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    // ---------------- API PRINCIPAL ----------------

    /**
     * Equivalente a:
     *
     * librosa.pythagorean_intervals(
     *      bins_per_octave=12,
     *      sort=true,
     *      return_factors=false
     * )
     */
    public static Object pythagoreanIntervals(
            int binsPerOctave,
            boolean sort,
            boolean returnFactors
    ) {

        if (binsPerOctave <= 0) {
            throw new IllegalArgumentException(
                    "binsPerOctave must be > 0"
            );
        }

        String key = binsPerOctave + "_" + sort + "_" + returnFactors;

        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }

        int n = binsPerOctave;

        int[] pow3 = new int[n];
        double[] logRatios = new double[n];
        int[] pow2 = new int[n];

        double log2_3 = log2(3.0);

        // ---------------- np.arange + np.modf ----------------
        for (int i = 0; i < n; i++) {

            pow3[i] = i;

            double x = i * log2_3;

            double intPart = Math.floor(x);
            double fracPart = x - intPart;

            logRatios[i] = fracPart;
            pow2[i] = (int) intPart;

            // compatibilidade librosa
            if (logRatios[i] < 0) {
                logRatios[i] += 1.0;
                pow2[i] += 1;
            }
        }

        // ---------------- índices ----------------
        int[] idx = createIndex(n);

        if (sort) {
            sortByValues(idx, logRatios);
        }

        Object result;

        // ---------------- return_factors ----------------
        if (returnFactors) {

            List<Map<Integer, Integer>> factors = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                int j = idx[i];

                Map<Integer, Integer> map = new LinkedHashMap<>();
                map.put(2, -pow2[j]);
                map.put(3, pow3[j]);

                factors.add(map);
            }

            result = factors;

        } else {

            double[] values = new double[n];

            for (int i = 0; i < n; i++) {
                values[i] = Math.pow(2.0, logRatios[idx[i]]);
            }

            result = values;
        }

        CACHE.put(key, result);

        return result;
    }

    // ---------------- AUXILIARES ----------------

    private static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    private static int[] createIndex(int n) {
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        return idx;
    }

    private static void sortByValues(int[] idx, double[] values) {

        Integer[] boxed = new Integer[idx.length];

        for (int i = 0; i < idx.length; i++) {
            boxed[i] = idx[i];
        }

        Arrays.sort(boxed, Comparator.comparingDouble(i -> values[i]));

        for (int i = 0; i < idx.length; i++) {
            idx[i] = boxed[i];
        }
    }

    public static double[] plimitIntervals(
            int[] primes,
            int binsPerOctave,
            boolean sort
    ) {
        throw new UnsupportedOperationException("Implementar plimitIntervals()");
    }


}