package com.jlibrosa.audio.core;
import com.jlibrosa.audio.core.Convert;
import com.jlibrosa.audio.core.Pitch;
import com.jlibrosa.audio.util.Utils;
import org.apache.commons.math3.complex.Complex;
import java.util.Collection;

public class Constantq {
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
	    return null;
	}

}
