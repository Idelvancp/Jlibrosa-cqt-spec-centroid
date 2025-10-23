package com.jlibrosa.audio.util;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    
    // Exceção personalizada (equivalente a ParameterError)
    public static class ParameterError extends RuntimeException {
        public ParameterError(String message) {
            super(message);
        }
    }

    /**
     * Verifica se um array contém dados de áudio válidos.
     *
     * Regras:
     * - O objeto deve ser um array de double (1D ou 2D)
     * - Não pode ser nulo
     * - Nenhum valor pode ser NaN ou infinito
     *
     * @param y array de áudio (double[] ou double[][])
     * @return true se o áudio for válido
     * @throws ParameterError se qualquer validação falhar
     */
    public static boolean validAudio(Object y) {
        if (y == null) {
            throw new ParameterError("Audio data must not be null");
        }

        // Aceita double[] (mono) ou double[][] (multi-canal)
        if (y instanceof double[]) {
            double[] data = (double[]) y;

            if (data.length == 0) {
                throw new ParameterError("Audio data must have at least one dimension (length > 0)");
            }

            for (double v : data) {
                if (Double.isNaN(v) || Double.isInfinite(v)) {
                    throw new ParameterError("Audio buffer is not finite everywhere");
                }
            }

        } else if (y instanceof double[][]) {
            double[][] data = (double[][]) y;

            if (data.length == 0 || data[0].length == 0) {
                throw new ParameterError("Audio data must have at least one dimension (non-empty)");
            }

            for (double[] row : data) {
                for (double v : row) {
                    if (Double.isNaN(v) || Double.isInfinite(v)) {
                        throw new ParameterError("Audio buffer is not finite everywhere");
                    }
                }
            }

        } else {
            throw new ParameterError("Audio data must be a double[] or double[][] array");
        }

        return true;
    }

    /**
     * Verifica se um objeto representa um número inteiro positivo (> 0).
     *
     * @param x objeto a verificar (pode ser Integer, Long, etc.)
     * @return true se for um inteiro positivo; false caso contrário
     */
    public static boolean isPositiveInt(Object x) {
        if (x == null) {
            return false;
        }

        if (x instanceof Integer) {
            return ((Integer) x) > 0;
        }

        if (x instanceof Long) {
            return ((Long) x) > 0;
        }

        // Comportamento igual ao do Librosa: floats não são aceitos
        return false;
    }

    // --- padCenter para vetores 1D ---
    public static double[] padCenter(double[] data, int size) {
        return padCenter(data, size, "constant", 0.0);
    }

    public static double[] padCenter(double[] data, int size, String mode, double padValue) {
        int n = data.length;
        int lpad = (size - n) / 2;

        if (lpad < 0) {
            throw new ParameterError(
                String.format("Target size (%d) must be at least input size (%d)", size, n)
            );
        }

        double[] padded = new double[size];
        Arrays.fill(padded, padValue);

        // Copia os dados para o centro
        System.arraycopy(data, 0, padded, lpad, n);

        return padded;
    }

    // --- padCenter para matrizes 2D (axis = 0 ou 1) ---
    public static double[][] padCenter(double[][] data, int size, int axis) {
        int dim0 = data.length;
        int dim1 = data[0].length;

        if (axis == 0) {
            if (size < dim0) {
                throw new ParameterError(
                    String.format("Target size (%d) must be at least input size (%d)", size, dim0)
                );
            }

            double[][] padded = new double[size][dim1];
            int lpad = (size - dim0) / 2;
            for (int i = 0; i < dim0; i++) {
                padded[lpad + i] = Arrays.copyOf(data[i], dim1);
            }
            return padded;

        } else if (axis == 1) {
            if (size < dim1) {
                throw new ParameterError(
                    String.format("Target size (%d) must be at least input size (%d)", size, dim1)
                );
            }

            double[][] padded = new double[dim0][size];
            int lpad = (size - dim1) / 2;

            for (int i = 0; i < dim0; i++) {
                Arrays.fill(padded[i], 0.0);
                System.arraycopy(data[i], 0, padded[i], lpad, dim1);
            }
            return padded;

        } else {
            throw new ParameterError("Axis must be 0 or 1");
        }
    }

    /**
     * Expande as dimensões de um array 1D ou 2D para um número maior de dimensões,
     * preservando os eixos especificados.
     *
     * @param x     Array de entrada (1D ou 2D)
     * @param ndim  Número total de dimensões desejadas (>= x.ndim)
     * @param axes  Eixos a preservar (ex: [0], [1], [1, 2])
     * @return Novo array expandido com dimensões de tamanho 1 nos eixos extras
     */
    public static double[][][] expandTo(double[] x, int ndim, int[] axes) {
        // --- validações ---
        if (axes.length != 1) {
            throw new ParameterError(
                String.format("Shape mismatch between axes=%s and input x.shape=(%d)",
                              Arrays.toString(axes), x.length)
            );
        }

        if (ndim < 1) {
            throw new ParameterError(
                String.format("Cannot expand x.shape=(%d,) to fewer dimensions ndim=%d", x.length, ndim)
            );
        }

        // Cria forma destino (shape)
        int[] shape = new int[ndim];
        Arrays.fill(shape, 1);
        shape[axes[0]] = x.length;

        // Caso típico: expandir 1D -> 2D
        if (ndim == 2) {
            if (axes[0] == 0) {  // (n,1)
                double[][] expanded = new double[x.length][1];
                for (int i = 0; i < x.length; i++) expanded[i][0] = x[i];
                return new double[][][] { expanded };
            } else if (axes[0] == 1) {  // (1,n)
                double[][] expanded = new double[1][x.length];
                for (int i = 0; i < x.length; i++) expanded[0][i] = x[i];
                return new double[][][] { expanded };
            } else {
                throw new ParameterError("Invalid axis for 1D expansion");
            }
        }

        // Em casos mais complexos (ex: ndim=4), retornamos 3D com placeholders
        double[][][] expanded = new double[1][x.length][1];
        for (int i = 0; i < x.length; i++) expanded[0][i][0] = x[i];
        return expanded;
    }

    // Sobrecarga para 2D -> 3D ou 4D
    public static double[][][][] expandTo(double[][] x, int ndim, int[] axes) {
        int n = x.length;
        int m = x[0].length;

        if (axes.length != 2) {
            throw new ParameterError(
                String.format("Shape mismatch between axes=%s and input x.shape=(%d,%d)",
                              Arrays.toString(axes), n, m)
            );
        }

        if (ndim < 2) {
            throw new ParameterError(
                String.format("Cannot expand x.shape=(%d,%d) to fewer dimensions ndim=%d", n, m, ndim)
            );
        }

        // Cria forma destino
        int[] shape = new int[ndim];
        Arrays.fill(shape, 1);
        shape[axes[0]] = n;
        shape[axes[1]] = m;

        // Exemplo: (n,m) → (1,n,m,1)
        double[][][][] expanded = new double[shape[0]][shape[1]][shape[2]][shape[3]];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                expanded[0][i][j][0] = x[i][j];
            }
        }

        return expanded;
    }

    /**
     * Divide um vetor de amostras em janelas (frames) sobrepostas.
     *
     * Exemplo: x = [0,1,2,3,4,5,6], frame_length=3, hop_length=2:
     * Retorna:
     * [[0,1,2],
     *  [2,3,4],
     *  [4,5,6]]
     *
     * @param x vetor de entrada
     * @param frameLength tamanho da janela
     * @param hopLength passo entre janelas consecutivas
     * @return matriz (n_frames x frame_length)
     */
    public static double[][] frame(double[] x, int frameLength, int hopLength) {
        return frame(x, frameLength, hopLength, false);
    }

    public static double[][] frame(double[] x, int frameLength, int hopLength, boolean strict) {
        if (x == null || x.length == 0)
            throw new ParameterError("Input array cannot be null or empty");

        if (frameLength <= 0)
            throw new ParameterError("frame_length must be positive");

        if (hopLength < 1)
            throw new ParameterError("Invalid hop_length: " + hopLength);

        if (x.length < frameLength)
            throw new ParameterError(
                String.format("Input is too short (n=%d) for frame_length=%d", x.length, frameLength)
            );

        // Número total de janelas possíveis
        int nFrames = 1 + (x.length - frameLength) / hopLength;

        // Caso strict = true → exige alinhamento perfeito
        if (strict && (x.length - frameLength) % hopLength != 0)
            throw new ParameterError("Input length is not perfectly divisible by hop_length");

        double[][] frames = new double[nFrames][frameLength];

        for (int i = 0; i < nFrames; i++) {
            int start = i * hopLength;
            for (int j = 0; j < frameLength; j++) {
                frames[i][j] = x[start + j];
            }
        }

        return frames;
    }

    /**
     * Versão 2D: aplica framing ao longo do eixo temporal (último eixo).
     *
     * Útil para sinais estéreo, espectrogramas, etc.
     */
    public static double[][][] frame(double[][] x, int frameLength, int hopLength) {
        if (x.length == 0 || x[0].length < frameLength)
            throw new ParameterError("Input too short for frame_length");

        int nChannels = x.length;
        int nSamples = x[0].length;
        int nFrames = 1 + (nSamples - frameLength) / hopLength;

        double[][][] frames = new double[nChannels][nFrames][frameLength];

        for (int ch = 0; ch < nChannels; ch++) {
            for (int i = 0; i < nFrames; i++) {
                int start = i * hopLength;
                for (int j = 0; j < frameLength; j++) {
                    frames[ch][i][j] = x[ch][start + j];
                }
            }
        }

        return frames;
    }
    
    /**
     * Converte um tipo numérico real em seu tipo complexo correspondente.
     *
     * <p>Serve para manter a precisão numérica ao converter dados reais
     * (float, double, etc.) em dados complexos, como ocorre em transformadas de Fourier.</p>
     *
     * Exemplos:
     * <pre>
     * Utils.dtypeR2C(Float.TYPE)  -> "complex64"
     * Utils.dtypeR2C(Double.TYPE) -> "complex128"
     * Utils.dtypeR2C(Integer.TYPE) -> "complex64"
     * </pre>
     *
     * @param realType Classe representando o tipo real (ex: Float.TYPE, Double.TYPE, Integer.TYPE)
     * @param defaultType Tipo complexo padrão (ex: "complex64")
     * @return Uma string representando o tipo complexo correspondente
     */
    public static String dtypeR2C(Class<?> realType, String defaultType) {
        Map<Class<?>, String> mapping = new HashMap<>();
        mapping.put(Float.TYPE, "complex64");   // float32 → complex64
        mapping.put(Double.TYPE, "complex128"); // float64 → complex128
        mapping.put(Float.class, "complex64");
        mapping.put(Double.class, "complex128");

        // Se já for um tipo complexo, retorna ele mesmo
        if (realType.getSimpleName().toLowerCase().contains("complex")) {
            return realType.getSimpleName();
        }

        // Busca no mapa; se não encontrar, retorna o default
        return mapping.getOrDefault(realType, defaultType);
    }

    /**
     * Sobrecarga que usa "complex64" como valor padrão.
     */
    public static String dtypeR2C(Class<?> realType) {
        return dtypeR2C(realType, "complex64");
    }
}
