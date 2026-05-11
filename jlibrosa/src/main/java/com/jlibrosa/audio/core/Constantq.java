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
	    Object window,
	    boolean scale,
	    String padMode,
	    String resType,
	    Class<?> dtype
	) {
	    return null;
	}

	public static Complex[][] vqt(
	    double[] y,
	    double sr,
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
	    Object window,
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
