package com.jlibrosa.audio.core;

import com.jlibrosa.audio.JLibrosa;
import org.apache.commons.math3.complex.Complex;
import com.jlibrosa.audio.util.Utils;
import com.jlibrosa.audio.core.Spectrum;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


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

		//Pad the window out to n_fft size
		fft_window = Utils.padCenter(fft_window, n_fft);
		System.out.println("Novo tamanho do fft_window = " + fft_window.length);

		fft_window = Utils.padCenter(fft_window, n_fft);
		

		
		if (center) {

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
			
			int ndim = 1; // hoje é mono, mas pode virar 2 depois
			List<int[]> padding = new ArrayList<>();

			for (int i = 0; i < ndim; i++) {
				padding.add(new int[]{0, 0});
			}

			int start_k = (int) Math.ceil((double) (n_fft / 2) / hop_length);

			int tail_k = (y.length + (n_fft / 2) - n_fft) / hop_length + 1;

			
			if (tail_k <= start_k) {
				// Caso simples: padding simétrico total (equivalente ao np.pad do librosa)
				int start;
			    int extra;
				start = 0;
				extra = 0;
				y = Utils.pad1D(y, n_fft / 2, n_fft / 2, 0.0);

         		// 🔍 DEBUG TEMPORÁRIO
				System.out.println("=== DEBUG PAD1D ===");
				System.out.println("Original length (sem pad): " + (y.length - n_fft));
				System.out.println("Padded length: " + y.length);
				System.out.println("Pad left/right: " + (n_fft / 2));

				System.out.println("Primeiros 10 valores:");
				for (int i = 0; i < 10; i++) {
					System.out.printf("%.6f ", y[i]);
				}
				System.out.println();

				System.out.println("Últimos 10 valores:");
				for (int i = y.length - 10; i < y.length; i++) {
					System.out.printf("%.6f ", y[i]);
				}
				System.out.println();
			}else {
				// Caso geral: padding parcial (librosa otimizado)
				System.out.println("=== DEBUG PAD1D Else início ===");


				// "Middle" do sinal começa aqui
				int start;
				start = start_k * hop_length - n_fft / 2;

				// Calcula o fim do trecho inicial
				int end = (start_k - 1) * hop_length - n_fft / 2 + n_fft + 1;

				// Recorta o início do sinal
				double[] yPreRaw = Arrays.copyOfRange(y, 0, end);

				// Padding somente à esquerda
				double[] y_pre = Utils.pad1D(yPreRaw, n_fft / 2, 0, 0.0);

				// y_pre será usado para gerar os primeiros frames
				// 🔍 DEBUG ELSE (equivalente ao debug do librosa)
				System.out.println("=== DEBUG ELSE (partial padding) ===");
				System.out.println("y.length (original): " + y.length);
				System.out.println("start_k: " + start_k);
				System.out.println("hop_length: " + hop_length);
				System.out.println("n_fft: " + n_fft);

				System.out.println("start (offset): " + start);
				System.out.println("end (raw slice): " + end);

				System.out.println("yPreRaw.length: " + yPreRaw.length);
				System.out.println("y_pre.length (after pad): " + y_pre.length);
				System.out.println("Expected y_pre.length: " + (yPreRaw.length + n_fft / 2));

				System.out.println("Pad left: " + (n_fft / 2) + " | Pad right: 0");

				System.out.println("Primeiros 10 valores de y_pre:");
				for (int i = 0; i < 10; i++) {
					System.out.printf("%.6f ", y_pre[i]);
				}
				System.out.println();

				System.out.println("Valores em torno da transição pad → sinal:");
				int t = n_fft / 2;
				for (int i = t - 5; i < t + 5; i++) {
					System.out.printf("%.6f ", y_pre[i]);
				}
				System.out.println();

				// ===============================
				// Continuação do ELSE (Librosa)
				// ===============================

				// Framing do sinal parcialmente padded
				double[][] yFramesPre = Utils.frame(y_pre, n_fft, hop_length);

				// Trim para manter apenas os primeiros start_k frames
				int framesToKeep = Math.min(start_k, yFramesPre.length);
				double[][] yFramesPreTrim = new double[framesToKeep][n_fft];

				for (int i = 0; i < framesToKeep; i++) {
					yFramesPreTrim[i] = yFramesPre[i];
				}

				// Quantidade de frames extras vindos do "head"
				int extra = yFramesPreTrim.length;

				// ===============================
				// DEBUG — equivalente ao Librosa
				// ===============================
				System.out.println("=== DEBUG ELSE (framing pre) ===");
				System.out.println("y_pre.length: " + y_pre.length);
				System.out.println("n_fft: " + n_fft);
				System.out.println("hop_length: " + hop_length);
				System.out.println("Total frames (before trim): " + yFramesPre.length);
				System.out.println("start_k: " + start_k);
				System.out.println("Frames kept (after trim): " + yFramesPreTrim.length);
				System.out.println("extra: " + extra);

				System.out.println("Primeiro frame (primeiros 10 valores):");
				for (int i = 0; i < 10; i++) {
					System.out.printf("%.6f ", yFramesPreTrim[0][i]);
				}
				System.out.println();

				System.out.println("Último frame (últimos 10 valores):");
				for (int i = n_fft - 10; i < n_fft; i++) {
					System.out.printf("%.6f ", yFramesPreTrim[framesToKeep - 1][i]);
				}
				System.out.println();


			}
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

	public static Complex[][][] stft(
	        double[][] y,
	        Integer n_fft,
	        Integer hop_length,
	        Integer win_length,
	        String window,
	        boolean center,
	        String pad_mode
	) {
	    if (y == null || y.length == 0)
	        throw new IllegalArgumentException("Audio must have at least 1 channel");
	
	    int channels = y.length;
	
	    // Validação: todos os canais devem ter o mesmo tamanho
	    int n_samples = y[0].length;
	    for (int c = 1; c < channels; c++) {
	        if (y[c].length != n_samples) {
	            throw new IllegalArgumentException("All channels must have the same length");
	        }
	    }
	
	    Complex[][][] out = new Complex[channels][][];
	
	    // Para cada canal, chama a versão mono
	    for (int c = 0; c < channels; c++) {
	        out[c] = stft(
	                y[c],
	                n_fft,
	                hop_length,
	                win_length,
	                window,
	                center,
	                pad_mode
	        );
	    }
	
	    return out;
	}


}

