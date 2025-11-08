package com.jlibrosa.audio.core;

import com.jlibrosa.audio.JLibrosa;
import org.apache.commons.math3.complex.Complex;
import com.jlibrosa.audio.util.Utils;
import com.jlibrosa.audio.core.Spectrum;
import java.util.Arrays;


public class Spectrum {

	/**
	 * Retrieve a magnitude or power spectrogram using JLibrosa.
	 *
	 * @param y           Audio time series (double[])
	 * @param n_fft       FFT window size
	 * @param hop_length  Hop length (currently unused in JLibrosa)
	 * @param power       Exponent for the magnitude spectrogram (1=magnitude, 2=power)
	 * @param sampleRate  Sample rate of the audio
	 * @return double[][] Spectrogram matrix
	 * @throws Exception
	 */
	public static double[][] spectrogram(double[] y,
			int n_fft,
			int hop_length,
			double power,
			int sampleRate) throws Exception {

		if (y == null) {
			throw new IllegalArgumentException("Input signal (y) must be provided.");
		}

		// Converter de double[] para float[], exigido pelo JLibrosa
		float[] yFloat = new float[y.length];
		for (int i = 0; i < y.length; i++) {
			yFloat[i] = (float) y[i];
		}

		JLibrosa jLibrosa = new JLibrosa();
		Complex[][] stftComplex = jLibrosa.generateSTFTFeatures(yFloat, sampleRate, n_fft);

		int rows = stftComplex.length;
		int cols = stftComplex[0].length;
		double[][] specOut = new double[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double mag = stftComplex[i][j].abs(); // magnitude
				if (power == 1.0) {
					specOut[i][j] = mag; // magnitude
				} else if (power == 2.0) {
					specOut[i][j] = mag * mag; // power spectrum
				} else {
					specOut[i][j] = Math.pow(mag, power);
				}
			}
		}

		return specOut;
	}

	/**
	 * Short-time Fourier transform (STFT) — conversão passo-a-passo de uma função
	 * similar à `librosa.stft` (Python) para Java.
	 *
	 * Observações:
	 * - Esta implementação inicial aceita sinal mono (double[]). Para multicanal,
	 *   precisamos adaptar o método para double[][] ou sobrecarregar a função.
	 * - Alguns parâmetros do Python (dtype, out) não têm correspondência direta em Java
	 *   e serão ignorados/ajustados conforme necessário.
	 *
	 * Próximos passos: calcular janela, padding, framing e aplicar FFT (rfft).
	 */
	public static Complex[][] stft(
			double[] y,
			Integer n_fft,                // tamanho da FFT (padrão em librosa: 2048)
			Integer hop_length,       // número de amostras entre colunas de STFT (nullable)
			Integer win_length,       // comprimento da janela (nullable)
			String window,            // especificação da janela (ex: "hann")
			boolean center,           // se true, centraliza frames com padding
			String pad_mode           // modo de padding (ex: "constant")
			) throws IllegalArgumentException {
		// ------------- validações iniciais e defaults ---------------

//		if (y == null) {
//			throw new IllegalArgumentException("Input signal (y) must be provided (non-null).");
//		}

		    // Definição do padrão idêntico ao librosa:
   		if (n_fft == null || n_fft <= 0) {
   		     n_fft = 2048;
   		 }

		// default: win_length = n_fft (se null)
		if (win_length == null) {
			win_length = n_fft;
		} else {
			if (win_length <= 0 || win_length > n_fft) {
				throw new IllegalArgumentException("win_length must be > 0 and <= n_fft");
			}
		}

		// default: hop_length = win_length // 4 (se null)
		if (hop_length == null) {
			hop_length = Math.max(1, win_length / 4);
		} else {
			if (!Utils.isPositiveInt(hop_length)) {
				throw new IllegalArgumentException("hop_length must be a positive integer");
			}
		}
		
		

		if (window == null) {
			window = "hann";
		}

		if (pad_mode == null) {
			pad_mode = "constant";
		}

		Utils.validAudio(y);

		double[] fft_window = Utils.getWindow(window, win_length, true);
		System.out.println("fft_window gerado com sucesso! Tamanho = " + fft_window.length);
		System.out.println("Primeiros valores: " + Arrays.toString(Arrays.copyOf(fft_window, Math.min(5, fft_window.length))));

		// Avisos análogos ao librosa:
		if (center && (pad_mode.equals("wrap") || pad_mode.equals("maximum")
					|| pad_mode.equals("mean") || pad_mode.equals("median") || pad_mode.equals("minimum"))) {
			throw new IllegalArgumentException("pad_mode='" + pad_mode + "' is not supported by this STFT implementation.");
					}

		if (center && n_fft > y.length) {
		    System.err.printf("Warning: n_fft=%d is larger than input length=%d%n", n_fft, y.length);
		} else if (!center && n_fft > y.length) {
    			throw new IllegalArgumentException(String.format( "n_fft=%d is too large for uncentered analysis of input signal of length=%d", n_fft, y.length));
		}


		// Preparação: converter double[] para float[] caso necessário por libs externas.
		// float[] yFloat = new float[y.length];
		// for (int i = 0; i < y.length; i++) yFloat[i] = (float) y[i];

		// ------------- placeholders / próxima etapa ---------------
		// A seguir: calcular a janela (fft_window = getWindow(window, win_length)),
		// pad_center(fft_window, size=n_fft), expand_to(fft_window,...),
		// se center == true -> aplicar padding ao sinal y,
		// construir frames: frame(y[..., start:], frame_length=n_fft, hop_length=hop_length),
		// alocar matriz Complex[freqBins][nFrames] e preencher usando FFT real (rfft).
		//
		// Irei implementar cada uma dessas etapas em sequência (janela, padding, framing, FFT).
		//

		// Por enquanto, retornamos null como placeholder — na próxima mensagem eu implemento
		// a geração da janela e o framing, e então a FFT.
		return null;
	}

}

