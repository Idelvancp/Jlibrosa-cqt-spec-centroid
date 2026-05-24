package com.jlibrosa.audio.core;
import com.jlibrosa.audio.core.Convert;
import com.jlibrosa.audio.core.Pitch;
import com.jlibrosa.audio.core.IntervalFrequencies;
import com.jlibrosa.audio.util.Utils;
import com.jlibrosa.audio.Filters;
import org.apache.commons.math3.complex.Complex;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class Constantq {

	private static final Map<Integer, double[]> BW_CACHE = new HashMap<>();

	private static double[] etRelativeBw(int binsPerOctave) {
		return BW_CACHE.computeIfAbsent(binsPerOctave, b -> {
			double r = Math.pow(2.0, 1.0 / b);
			double alpha = (Math.pow(r, 2) - 1.0) / (Math.pow(r, 2) + 1.0);
			return new double[]{alpha};
		});
	}

		    /**
     * Retorna quantas vezes o inteiro x pode ser dividido por 2.
     * Retorna 0 para inteiros não positivos.
     */
    public static int numTwoFactors(int x) {
        if (x <= 0) {
            return 0;
        }

        int numTwos = 0;

        while (x % 2 == 0) {
            numTwos++;
            x /= 2;
        }

        return numTwos;
    }


    /**
     * Equivalente da função Python:
     *
     * def __early_downsample_count(nyquist, filter_cutoff, hop_length, n_octaves):
     */
    public static int earlyDownsampleCount(
            double nyquist,
            double filterCutoff,
            int hopLength,
            int nOctaves
    ) {

        int downsampleCount1 = Math.max(
                0,
                (int) (Math.ceil(
                        Math.log(nyquist / filterCutoff) / Math.log(2)
                ) - 1) - 1
        );

        int numTwos = numTwoFactors(hopLength);

        int downsampleCount2 = Math.max(
                0,
                numTwos - nOctaves + 1
        );

        return Math.min(downsampleCount1, downsampleCount2);
    }

    /**
     * Classe para retornar múltiplos valores,
     * equivalente ao:
     *
     * return y, sr, hop_length
     */
    public static class EarlyDownsampleResult {

        public final double[] y;
        public final double sr;
        public final int hopLength;

        public EarlyDownsampleResult(
                double[] y,
                double sr,
                int hopLength
        ) {
            this.y = y;
            this.sr = sr;
            this.hopLength = hopLength;
        }
    }

    /**
     * Equivalente ao:
     * __early_downsample
     */
    public static EarlyDownsampleResult earlyDownsample(
            double[] y,
            double sr,
            int hopLength,
            String resType,
            int nOctaves,
            double nyquist,
            double filterCutoff,
            boolean scale
    ) {

        int downsampleCount = earlyDownsampleCount(
                nyquist,
                filterCutoff,
                hopLength,
                nOctaves
        );

        if (downsampleCount > 0) {

            int downsampleFactor =
                    (int) Math.pow(2, downsampleCount);

            // hop_length //= downsample_factor
            hopLength /= downsampleFactor;

            // y.shape[-1] < downsample_factor
            if (y.length < downsampleFactor) {

                throw new Utils.ParameterError(
                        "Input signal length="
                                + y.length
                                + " is too short for "
                                + nOctaves
                                + "-octave CQT"
                );
            }

            // new_sr = sr / float(downsample_factor)
            double newSr =
                    sr / (double) downsampleFactor;

            /**
             * Python:
             *
             * y = audio.resample(
             *     y,
             *     orig_sr=downsample_factor,
             *     target_sr=1,
             *     res_type=res_type,
             *     scale=True
             * )
             */
            y = Audio.resample(
                    y,
                    downsampleFactor,
                    1,
                    resType,
                    true,
                    true
            );

            /**
             * if not scale:
             *     y *= np.sqrt(downsample_factor)
             */
            if (!scale) {

                double factor =
                        Math.sqrt(downsampleFactor);

                for (int i = 0; i < y.length; i++) {
                    y[i] *= factor;
                }
            }

            sr = newSr;
        }

        return new EarlyDownsampleResult(
                y,
                sr,
                hopLength
        );
    }

	public static Complex[][] cqt(
	    double[] y,
	    int sr,
	    int hopLength,
	    Double fmin,
	    int nBins,
	    int binsPerOctave,
	    Double tuning,
	    double filterScale,
	    Double norm,
	    double sparsity,
	    String window,
	    boolean scale,
	    String padMode,
	    String resType,
	    Class<?> dtype
	) {
	    return null;
	}

	public static Complex[][] vqt(
	    double[] y,
	    int sr,
	    int hopLength,
	    Double fmin,
	    int nBins,
	    Object intervals,
	    Double gamma,
	    int binsPerOctave,
	    Double tuning,
	    double filterScale,
	    Double norm,
	    double sparsity,
	    String window,
	    boolean scale,
	    String padMode,
	    String resType,
	    String dtype
	) {
		

	    if (intervals instanceof Collection<?>) {
		    binsPerOctave = ((Collection<?>) intervals).size();
	    } else if (!(intervals instanceof String)) {
		    throw new IllegalArgumentException("intervals must be String or Collection");
	    }

	    int nOctaves = (int) Math.ceil((double) nBins / binsPerOctave);
	    int nFilters = Math.min(binsPerOctave, nBins);
	    if (fmin == null) {
   		 // C1 por padrão
   		 fmin = Convert.noteToHz("C1", true);
	    }
	    
	    if (tuning == null) {
            tuning = Pitch.estimateTuning(y, sr, binsPerOctave);
        }


		if (dtype == null) {
			dtype = Utils.dtypeR2C(Double.TYPE);

		}

		fmin = fmin * Math.pow(2.0, tuning / (double) binsPerOctave);

		double[] freqs = IntervalFrequencies.intervalFrequencies(
        nBins,          // int
        fmin,           // double
        intervals,      // String ou double[]
        binsPerOctave,  // int
        0.0,            // tuning (no Python default = 0)
        true            // sort
		);
	    
		if (freqs.length < binsPerOctave) {
			throw new IllegalArgumentException("freqs menor que binsPerOctave");
		}

		double fmaxT = Double.NEGATIVE_INFINITY;

		for (int i = freqs.length - binsPerOctave; i < freqs.length; i++) {
			if (freqs[i] > fmaxT) {
				fmaxT = freqs[i];
			}
		}

		double[] alpha;

		if (nBins == 1) {
			alpha = etRelativeBw(binsPerOctave);
		} else {
			alpha = Filters.relativeBandwidth(freqs);
		}

		Filters.WaveletLengthsResult result =
        Filters.waveletLengths(
                freqs,
                sr,
                window,
                filterScale,
                gamma,
                alpha
        );

		double[] lengths = result.lengths;
		double filterCutoff = result.fCutoff;

		// =====================================================
		// Nyquist check
		// =====================================================

		double nyquist = sr / 2.0;

		if (filterCutoff > nyquist) {

			throw new IllegalArgumentException(
					"Wavelet basis with max frequency="
					+ filterCutoff
					+ " would exceed the Nyquist frequency="
					+ nyquist
					+ ". Try reducing the number of frequency bins."
			);
		}

		if (resType == null) {
			resType = "soxr_hq";
		}
		return null;
	}




}
